package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.constant.enums.EnumElrDltStatus;
import gov.cdc.dataingestion.constant.enums.EnumElrServiceOperation;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.constant.enums.EnumMessageType;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ElrDeadLetterService {
    private static final String CREATED_BY = "DeadLetterService";

    private final IElrDeadLetterRepository dltRepository;
    private final IRawELRRepository rawELRRepository;
    private final IValidatedELRRepository validatedELRRepository;
    private final IHL7ToFHIRRepository fhirRepository;
    private final KafkaProducerService kafkaProducerService;

    @Value("${kafka.validation.topic}")
    private String validatedTopic = "elr_validated";

    @Value("${kafka.fhir-conversion.topic}")
    private String convertedToFhirTopic = "fhir_converted";

    @Value("${kafka.xml-conversion.topic}")
    private String convertedToXmlTopic = "xml_converted";

    @Value("${kafka.xml-conversion-prep.topic}")
    private String prepXmlTopic = "xml_prep";

    @Value("${kafka.fhir-conversion-prep.topic}")
    private String prepFhirTopic = "fhir_prep";
    @Value("${kafka.raw.topic}")
    private String rawTopic = "elr_raw";

    private final String deadLetterIsNullExceptionMessage = "Dead Letter Record Is Null";

    public ElrDeadLetterService(
            IElrDeadLetterRepository dltRepository,
            IRawELRRepository rawELRRepository,
            IValidatedELRRepository validatedELRRepository,
            KafkaProducerService kafkaProducerService,
            IHL7ToFHIRRepository fhirRepository) {
        this.dltRepository = dltRepository;
        this.rawELRRepository = rawELRRepository;
        this.validatedELRRepository = validatedELRRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.fhirRepository = fhirRepository;
    }

    public List<ElrDeadLetterDto> getAllErrorDltRecord() throws DeadLetterTopicException {
        Optional<List<ElrDeadLetterModel>> deadLetterELRModels = dltRepository.findAllDltRecordByDltStatus(EnumElrDltStatus.ERROR.name(), Sort.by(Sort.Direction.DESC, "createdOn"));
        List<ElrDeadLetterDto> results = new ArrayList<>();
        // return empty list if nothing is found
        if (deadLetterELRModels.isPresent()) {
            results = convertModelToDtoList(deadLetterELRModels.get());
        }

        return results;
    }

    public ElrDeadLetterDto getDltRecordById(String id) throws DeadLetterTopicException {
        Optional<ElrDeadLetterModel> model = dltRepository.findById(id);
        if (model.isPresent()) {
            return convertModelToDto(model.get());
        } else {
            throw new DeadLetterTopicException(deadLetterIsNullExceptionMessage);
        }
    }

    public ElrDeadLetterDto updateAndReprocessingMessage(String id, String body) throws DeadLetterTopicException {
        var existingRecord = getDltRecordById(id);
        existingRecord.setDltStatus(EnumElrDltStatus.REINJECTED.name());
        existingRecord.setDltOccurrence(existingRecord.getDltOccurrence());
        existingRecord.setMessage(body);
        if(existingRecord.getErrorMessageSource().equalsIgnoreCase(rawTopic)) {
            var rawRecord = rawELRRepository.findById(existingRecord.getErrorMessageId());
            if (!rawRecord.isPresent()) {
                throw new DeadLetterTopicException(deadLetterIsNullExceptionMessage);
            }
            RawERLModel rawModel = rawRecord.get();
            rawModel.setPayload(body);
            rawModel.setUpdatedOn(Timestamp.from(Instant.now()));

            // persisting data to raw table and dlt table
            rawELRRepository.save(rawModel);
            saveDltRecord(existingRecord);

            kafkaProducerService.sendMessageFromDltController(rawModel.getId(), rawTopic, rawModel.getType(), existingRecord.getDltOccurrence());
        }
        else if(existingRecord.getErrorMessageSource().equalsIgnoreCase(validatedTopic)) {
            var validateRecord = validatedELRRepository.findById(existingRecord.getErrorMessageId());
            if (!validateRecord.isPresent()) {
                throw new DeadLetterTopicException(deadLetterIsNullExceptionMessage);
            }
            ValidatedELRModel validateModel = validateRecord.get();
            validateModel.setRawMessage(body);
            validateModel.setUpdatedOn(Timestamp.from(Instant.now()));

            // persisting data to raw table and dlt table
            validatedELRRepository.save(validateModel);
            saveDltRecord(existingRecord);

            kafkaProducerService.sendMessageFromDltController(validateModel.getId(), validatedTopic, validateModel.getMessageType(), existingRecord.getDltOccurrence());
        }
        else if (  existingRecord.getErrorMessageSource().equalsIgnoreCase(prepFhirTopic) ||
                existingRecord.getErrorMessageSource().equalsIgnoreCase(prepXmlTopic)) {
            var topic = "";
            if (existingRecord.getErrorMessageSource().equalsIgnoreCase(prepFhirTopic)) {
                topic = prepFhirTopic;
            } else {
                topic = prepXmlTopic;
            }

            // persist data to dlt table
            saveDltRecord(existingRecord);

            kafkaProducerService.sendMessageFromDltController(existingRecord.getErrorMessageId(), topic,
                    EnumMessageType.HL7.name(), existingRecord.getDltOccurrence());
        }
        else {
            throw new DeadLetterTopicException("Provided Error Source is not supported");
        }

        return existingRecord;
    }

    public ElrDeadLetterDto saveDltRecord(ElrDeadLetterDto model) {
        model.setUpdatedOn(Timestamp.from(Instant.now()));
        dltRepository.save(convertDtoToModel(model));
        return model;
    }

    private List<ElrDeadLetterDto> convertModelToDtoList(List<ElrDeadLetterModel> models) throws DeadLetterTopicException {
        List<ElrDeadLetterDto>  dtlModels = new ArrayList<>() {};
        for(ElrDeadLetterModel model: models) {
            dtlModels.add(new ElrDeadLetterDto(model, EnumElrServiceOperation.GET_DLT_LIST));
        }
        return dtlModels;
    }

    private ElrDeadLetterDto convertModelToDto(ElrDeadLetterModel model) throws DeadLetterTopicException {
//        String errorMessage;
//        if (model.getErrorMessageSource().equalsIgnoreCase(rawTopic)) {
//            var rawMessageObject = rawELRRepository.findById(model.getErrorMessageId());
//            if (rawMessageObject.isPresent()) {
//                errorMessage = rawMessageObject.get().getPayload();
//            } else {
//                throw new DeadLetterTopicException("DLT record, but parent table record not found");
//            }
//        }
//        else if (model.getErrorMessageSource().equalsIgnoreCase(validatedTopic) ||
//                model.getErrorMessageSource().equalsIgnoreCase(prepXmlTopic) ||
//                model.getErrorMessageSource().equalsIgnoreCase(prepFhirTopic)) {
//            var rawMessageObject = validatedELRRepository.findById(model.getErrorMessageId());
//            if (rawMessageObject.isPresent()) {
//                errorMessage = rawMessageObject.get().getRawMessage();
//            } else {
//                throw new DeadLetterTopicException("DLT record, but parent table record not found");
//            }
//        }
//        else {
//            throw new DeadLetterTopicException("Unsupported Topic");
//        }
        return new ElrDeadLetterDto(model, EnumElrServiceOperation.GET_DLT_BY_ID);
    }

    private ElrDeadLetterModel convertDtoToModel(ElrDeadLetterDto dtoModel) {
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(dtoModel.getErrorMessageId());
        model.setErrorMessageSource(dtoModel.getErrorMessageSource());
        model.setErrorStackTrace(dtoModel.getErrorStackTrace());
        model.setErrorStackTraceShort(dtoModel.getErrorStackTraceShort());
        model.setMessage(dtoModel.getMessage());
        model.setDltOccurrence(dtoModel.getDltOccurrence());
        model.setDltStatus(dtoModel.getDltStatus());
        model.setCreatedOn(dtoModel.getCreatedOn());
        model.setUpdatedOn(dtoModel.getUpdatedOn());
        model.setCreatedBy(dtoModel.getCreatedBy());
        model.setUpdatedBy(dtoModel.getUpdatedBy());
        return model;
    }
}