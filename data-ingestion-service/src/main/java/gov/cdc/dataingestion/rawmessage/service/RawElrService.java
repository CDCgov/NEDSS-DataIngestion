package gov.cdc.dataingestion.rawmessage.service;

import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.exception.KafkaProducerException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.rawmessage.dto.RawElrDto;
import gov.cdc.dataingestion.report.repository.IRawElrRepository;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
import gov.cdc.dataingestion.share.helper.HL7BatchSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataingestion.constant.MessageType.HL7_ELR;
import static gov.cdc.dataingestion.constant.MessageType.XML_ELR;
import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawElrService {

    private static final String CREATED_BY = "admin";
    private static final String KAFKA_PRODUCER_FAILED_MSG="Sending event to elr_raw kafka topic failed";
    @Value("${kafka.raw.topic}")
    String topicName;

    @Value("${kafka.raw.xml-topic}")
    String rawXmlTopicName;

    @Value("${service.timezone}")
    private String tz = "UTC";
    @Value("${features.hl7BatchSplitting.enabled}")
    private boolean hl7BatchSplittingEnabled;

    private final IRawElrRepository rawElrRepository;
    private final KafkaProducerService kafkaProducerService;
    private final IElrDeadLetterRepository iElrDeadLetterRepository;

    public String submissionElrXml(RawElrDto rawElrDto) throws KafkaProducerException {
        RawElrModel created = rawElrRepository.save(convert(rawElrDto));
        int dltOccurrence = 0;
        try {
            if(rawElrDto.getType().equalsIgnoreCase(XML_ELR)) {
                kafkaProducerService.sendElrXmlMessageFromController(
                        created.getId(),
                        rawXmlTopicName,
                        rawElrDto.getType(),
                        dltOccurrence,
                        rawElrDto.getPayload(),
                        rawElrDto.getVersion());
            }
        } catch (KafkaProducerException e) {
            iElrDeadLetterRepository.addErrorStatusForRawId(created.getId(), topicName, created.getType(), created.getPayload(), KAFKA_PRODUCER_FAILED_MSG, dltOccurrence + 1);
            throw new KafkaProducerException("Failed publishing message to kafka topic: " + topicName + " with UUID: " + created.getId());
        }
        return created.getId();
    }
    @SuppressWarnings("java:S3776")
    public String submissionElr(RawElrDto rawElrDto) throws KafkaProducerException {
        List<String> rawELRIds = new ArrayList<>();
        if (rawElrDto.getType().equalsIgnoreCase(HL7_ELR)) {
            if (rawElrDto.getCustomMapper() != null && !rawElrDto.getCustomMapper().trim().isEmpty()) {
                rawElrDto.setPayload(hl7MessageCustomMapping(rawElrDto.getPayload(), rawElrDto.getCustomMapper()));
            }
            log.info("HL7Batch splitting feature flag enabled: {}", hl7BatchSplittingEnabled);
            if (hl7BatchSplittingEnabled) {
                //Split the incoming ELR into multiple messages if it is a hl7 batch.
                List<String> hl7Messages = HL7BatchSplitter.splitHL7Batch(rawElrDto.getPayload());
                log.info("Number of messages after batch split:" + hl7Messages.size());
                List<RawElrModel> rawElrModels = createRawElrModelsForBatch(hl7Messages, rawElrDto);
                //JPA batch insert
                List<RawElrModel> savedELRs = rawElrRepository.saveAll(rawElrModels);

                for (RawElrModel rawElrModel : savedELRs) {
                    rawELRIds.add(rawElrModel.getId());
                    int dltOccurrence = 0;
                    try {
                        kafkaProducerService.sendMessageFromController(
                                rawElrModel.getId(),
                                topicName,
                                rawElrDto.getType(),
                                dltOccurrence,
                                rawElrDto.getValidationActive(),
                                rawElrDto.getVersion());
                    } catch (KafkaProducerException e) {
                        iElrDeadLetterRepository.addErrorStatusForRawId(rawElrModel.getId(), topicName, rawElrModel.getType(), rawElrModel.getPayload(), KAFKA_PRODUCER_FAILED_MSG, dltOccurrence + 1);
                        throw new KafkaProducerException("Failed publishing message to kafka topic: " + topicName + " with UUID: " + rawElrModel.getId());
                    }
                }
            } else {
                RawElrModel created = rawElrRepository.save(convert(rawElrDto));
                int dltOccurrence = 0;
                try {
                    if (rawElrDto.getType().equalsIgnoreCase(HL7_ELR)) {
                        kafkaProducerService.sendMessageFromController(
                                created.getId(),
                                topicName,
                                rawElrDto.getType(),
                                dltOccurrence,
                                rawElrDto.getValidationActive(),
                                rawElrDto.getVersion());
                    }
                } catch (KafkaProducerException e) {
                    iElrDeadLetterRepository.addErrorStatusForRawId(created.getId(), topicName, created.getType(), created.getPayload(), KAFKA_PRODUCER_FAILED_MSG, dltOccurrence + 1);
                    throw new KafkaProducerException("Failed publishing message to kafka topic: " + topicName + " with UUID: " + created.getId());
                }
                return created.getId();
            }
        }
        return String.join(",", rawELRIds);
    }
    public void updateRawMessageAfterRetry(RawElrDto rawElrDto, int dltOccurrence) throws KafkaProducerException {
        try {
            if(rawElrDto.getType().equalsIgnoreCase(HL7_ELR)) {
                kafkaProducerService.sendMessageFromController(
                        rawElrDto.getId(),
                        topicName,
                        rawElrDto.getType(),
                        dltOccurrence + 1,
                        rawElrDto.getValidationActive(),
                        rawElrDto.getVersion());
            }
            if(rawElrDto.getType().equalsIgnoreCase(XML_ELR)) {
                kafkaProducerService.sendElrXmlMessageFromController(
                        rawElrDto.getId(),
                        rawXmlTopicName,
                        rawElrDto.getType(),
                        dltOccurrence + 1,
                        rawElrDto.getPayload(),
                        rawElrDto.getVersion());
            }
        } catch (KafkaProducerException e) {
            iElrDeadLetterRepository.updateDltOccurrenceForRawId(rawElrDto.getId(), dltOccurrence + 1, "ERROR");
            throw new KafkaProducerException("Failed publishing message again to kafka topic: " + topicName + " with UUID: " + rawElrDto.getId());
        }
    }

    public RawElrDto getById(String id) {
        RawElrModel rawElrModel = rawElrRepository.getReferenceById(id);
        return convert(rawElrModel);
    }

    private RawElrModel convert(RawElrDto rawElrDto) {
        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setType(rawElrDto.getType());
        rawElrModel.setPayload(rawElrDto.getPayload());
        rawElrModel.setVersion(rawElrDto.getVersion());
        rawElrModel.setCreatedOn(getCurrentTimeStamp(tz));
        rawElrModel.setUpdatedOn(getCurrentTimeStamp(tz));
        rawElrModel.setCreatedBy(CREATED_BY);
        rawElrModel.setUpdatedBy(CREATED_BY);
        return rawElrModel;
    }
    private List<RawElrModel> createRawElrModelsForBatch(List<String> hl7Messages,RawElrDto rawElrDto){
        List<RawElrModel> rawElrModels = new ArrayList<>();
        rawElrDto.setPayload("");
        for(String hl7Message : hl7Messages) {
            RawElrModel rawElrModel=convert(rawElrDto);
            rawElrModel.setPayload(hl7Message);
            rawElrModel.setDataSource(rawElrDto.getDataSource());
            rawElrModels.add(rawElrModel);
        }
        return rawElrModels;
    }
    private RawElrDto convert(RawElrModel rawElrModel) {
        RawElrDto rawElrDto = new RawElrDto();
        rawElrDto.setId(rawElrModel.getId());
        rawElrDto.setType(rawElrModel.getType());
        rawElrDto.setPayload(rawElrModel.getPayload());
        return rawElrDto;
    }
    private String hl7MessageCustomMapping(String message, String customMapper) throws KafkaProducerException {
        if(customMapper==null || customMapper.isEmpty()) {
            return message;
        }
        try{
            String[] formatStrArr = customMapper.split(",");
            for (String formatStr : formatStrArr) {
                String[] keyValuePair = formatStr.split("=");
                if(keyValuePair.length==2) {
                    String oldValue = keyValuePair[0];
                    String newValue = keyValuePair[1];
                    message = message.replaceAll(oldValue, newValue);
                }
            }
        }catch (Exception e) {
            throw new KafkaProducerException("Custom mapping find and replace:Error at parsing/replacing the mapper value:"+e.getMessage());
        }
        return message;
    }
}
