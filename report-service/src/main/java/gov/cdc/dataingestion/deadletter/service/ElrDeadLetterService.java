package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.model.ElrDltStatus;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7DuplicateValidator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        Optional<List<ElrDeadLetterModel>> deadLetterELRModels = dltRepository.findAllDltRecordByDltStatus(ElrDltStatus.ERROR.name(), Sort.by(Sort.Direction.DESC, "createdOn"));
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
        existingRecord.setDltStatus(ElrDltStatus.REINJECTED.name());
        existingRecord.setDltOccurrence(existingRecord.getDltOccurrence());
        if(existingRecord.getErrorMessageSource().equalsIgnoreCase(rawTopic)) {
            var rawRecord = rawELRRepository.findById(existingRecord.getErrorMessageId());
            if (!rawRecord.isPresent()) {
                throw new DeadLetterTopicException(deadLetterIsNullExceptionMessage);
            }
            RawERLModel rawModel = rawRecord.get();
            rawModel.setPayload(body);
            rawELRRepository.save(rawModel);
            kafkaProducerService.sendMessageFromController(rawModel.getId(), rawTopic, rawModel.getType(), existingRecord.getDltOccurrence());
        }
        else if(existingRecord.getErrorMessageSource().equalsIgnoreCase(validatedTopic) ||
                existingRecord.getErrorMessageSource().equalsIgnoreCase(convertedToFhirTopic) ||
                existingRecord.getErrorMessageSource().equalsIgnoreCase(convertedToXmlTopic) ||
                existingRecord.getErrorMessageSource().equalsIgnoreCase(prepFhirTopic) ||
                existingRecord.getErrorMessageSource().equalsIgnoreCase(prepXmlTopic)) {
            var validateRecord = validatedELRRepository.findById(existingRecord.getErrorMessageId());
            if (!validateRecord.isPresent()) {
                throw new DeadLetterTopicException(deadLetterIsNullExceptionMessage);
            }
            ValidatedELRModel validateModel = validateRecord.get();
            validateModel.setRawMessage(body);
            validatedELRRepository.save(validateModel);
            String topicToBeSent;
            if(existingRecord.getErrorMessageSource().equalsIgnoreCase(validatedTopic)) {
                topicToBeSent = validatedTopic;
            }
            else if(existingRecord.getErrorMessageSource().equalsIgnoreCase(convertedToFhirTopic)) {
                topicToBeSent = convertedToFhirTopic;
            }
            else if(existingRecord.getErrorMessageSource().equalsIgnoreCase(convertedToXmlTopic)) {
                topicToBeSent = convertedToXmlTopic;
            }
            else if(existingRecord.getErrorMessageSource().equalsIgnoreCase(prepFhirTopic)) {
                topicToBeSent = prepFhirTopic;
            }
            else {
                topicToBeSent = prepXmlTopic;
            }
            kafkaProducerService.sendMessageFromController(validateModel.getId(), topicToBeSent, validateModel.getMessageType(), existingRecord.getDltOccurrence());
        }
        else {
            throw new DeadLetterTopicException("Provided Error Source is not supported");
        }

        saveDltRecord(existingRecord);
        return existingRecord;
    }

    public ElrDeadLetterDto saveDltRecord(ElrDeadLetterDto model) {
        dltRepository.save(convertDtoToModel(model));
        return model;
    }

    private List<ElrDeadLetterDto> convertModelToDtoList(List<ElrDeadLetterModel> models) throws DeadLetterTopicException {
        List<ElrDeadLetterDto>  dtlModels = new ArrayList<>() {};
        for(ElrDeadLetterModel model: models) {
            dtlModels.add(convertModelToDto(model));
        }
        return dtlModels;
    }

    private ElrDeadLetterDto convertModelToDto(ElrDeadLetterModel model) throws DeadLetterTopicException {
        String errorMessage;
        if (model.getErrorMessageSource().equalsIgnoreCase(rawTopic)) {
            var rawMessageObject = rawELRRepository.findById(model.getErrorMessageId());
            if (rawMessageObject.isPresent()) {
                errorMessage = rawMessageObject.get().getPayload();
            } else {
                throw new DeadLetterTopicException("DLT record, but parent table record not found");
            }
        } else {
            throw new DeadLetterTopicException("Unsupported Topic");
        }
        return new ElrDeadLetterDto(model, errorMessage);
    }

    private ElrDeadLetterModel convertDtoToModel(ElrDeadLetterDto dtoModel) {
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(dtoModel.getErrorMessageId());
        model.setErrorMessageSource(dtoModel.getErrorMessageSource());
        model.setErrorStackTrace(dtoModel.getErrorStackTrace());
        model.setDltOccurrence(dtoModel.getDltOccurrence());
        model.setDltStatus(dtoModel.getDltStatus());
        model.setCreatedOn(dtoModel.getCreatedOn());
        model.setUpdatedOn(dtoModel.getUpdatedOn());
        model.setCreatedBy(dtoModel.getCreatedBy());
        model.setUpdatedBy(dtoModel.getUpdatedBy());
        return model;
    }
}
