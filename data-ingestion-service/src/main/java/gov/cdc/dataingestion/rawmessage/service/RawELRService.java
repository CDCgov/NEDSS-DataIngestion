package gov.cdc.dataingestion.rawmessage.service;

import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.exception.KafkaProducerException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static gov.cdc.dataingestion.constant.MessageType.HL7_ELR;
import static gov.cdc.dataingestion.constant.MessageType.XML_ELR;
import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawELRService {

    private static final String CREATED_BY = "admin";
    @Value("${kafka.raw.topic}")
    String topicName;

    @Value("${kafka.raw.xml-topic}")
    String rawXmlTopicName;

    @Value("${service.timezone}")
    private String tz = "UTC";

    private final String errorStatus = "Sending event to elr_raw kafka topic failed";

    private final IRawELRRepository rawELRRepository;
    private final KafkaProducerService kafkaProducerService;
    private final IElrDeadLetterRepository iElrDeadLetterRepository;


    public String submission(RawERLDto rawERLDto, String version) throws KafkaProducerException {
        RawERLModel created = rawELRRepository.save(convert(rawERLDto));
        int dltOccurrence = 0;
        try {
            if(rawERLDto.getType().equalsIgnoreCase(HL7_ELR)) {
                kafkaProducerService.sendMessageFromController(
                        created.getId(),
                        topicName,
                        rawERLDto.getType(),
                        dltOccurrence,
                        rawERLDto.getValidationActive(),
                        version);
            }
            if(rawERLDto.getType().equalsIgnoreCase(XML_ELR)) {
                kafkaProducerService.sendElrXmlMessageFromController(
                        created.getId(),
                        rawXmlTopicName,
                        rawERLDto.getType(),
                        dltOccurrence,
                        rawERLDto.getPayload(),
                        version);
            }
        } catch (KafkaProducerException e) {
            iElrDeadLetterRepository.addErrorStatusForRawId(created.getId(), topicName, created.getType(), created.getPayload(), errorStatus, dltOccurrence + 1);
            throw new KafkaProducerException("Failed publishing message to kafka topic: " + topicName + " with UUID: " + created.getId());
        }
        return created.getId();
    }

    public void updateRawMessageAfterRetry(RawERLDto rawElrDto, String version) throws KafkaProducerException {
        rawELRRepository.updateRawMessageWithNewVersion(rawElrDto.getId(), "Reprocessed");
        int dltOccurrence = 1;
        try {
            if(rawElrDto.getType().equalsIgnoreCase(HL7_ELR)) {
                kafkaProducerService.sendMessageFromController(
                        rawElrDto.getId(),
                        topicName,
                        rawElrDto.getType(),
                        dltOccurrence + 1,
                        rawElrDto.getValidationActive(),
                        version);
            }
            if(rawElrDto.getType().equalsIgnoreCase(XML_ELR)) {
                kafkaProducerService.sendElrXmlMessageFromController(
                        rawElrDto.getId(),
                        rawXmlTopicName,
                        rawElrDto.getType(),
                        dltOccurrence + 1,
                        rawElrDto.getPayload(),
                        version);
            }
        } catch (KafkaProducerException e) {
            iElrDeadLetterRepository.updateDltOccurrenceForRawId(rawElrDto.getId(), dltOccurrence + 1);
            throw new KafkaProducerException("Failed publishing message again to kafka topic: " + topicName + " with UUID: " + rawElrDto.getId());
        }
    }

    public RawERLDto getById(String id) {
        RawERLModel rawERLModel = rawELRRepository.getById(id);
//        rawELRRepository.
        return convert(rawERLModel);
    }

    private RawERLModel convert(RawERLDto rawERLDto) {

        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setType(rawERLDto.getType());
        rawERLModel.setPayload(rawERLDto.getPayload());
        rawERLModel.setCreatedOn(getCurrentTimeStamp(tz));
        rawERLModel.setUpdatedOn(getCurrentTimeStamp(tz));
        rawERLModel.setCreatedBy(CREATED_BY);
        rawERLModel.setUpdatedBy(CREATED_BY);
        return rawERLModel;
    }

    private RawERLDto convert(RawERLModel rawERLModel) {

        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setId(rawERLModel.getId());
        rawERLDto.setType(rawERLModel.getType());
        rawERLDto.setPayload(rawERLModel.getPayload());
        return rawERLDto;
    }
}
