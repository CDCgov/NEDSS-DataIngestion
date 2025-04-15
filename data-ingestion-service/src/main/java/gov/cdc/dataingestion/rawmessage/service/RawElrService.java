package gov.cdc.dataingestion.rawmessage.service;

import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.exception.KafkaProducerException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.rawmessage.dto.RawElrDto;
import gov.cdc.dataingestion.report.repository.IRawElrRepository;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
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
public class RawElrService {

    private static final String CREATED_BY = "admin";
    @Value("${kafka.raw.topic}")
    String topicName;

    @Value("${kafka.raw.xml-topic}")
    String rawXmlTopicName;

    @Value("${service.timezone}")
    private String tz = "UTC";

    private final IRawElrRepository rawElrRepository;
    private final KafkaProducerService kafkaProducerService;
    private final IElrDeadLetterRepository iElrDeadLetterRepository;

    public String submission(RawElrDto rawElrDto) throws KafkaProducerException {
        RawElrModel created = rawElrRepository.save(convert(rawElrDto));
        int dltOccurrence = 0;
        try {
            if(rawElrDto.getType().equalsIgnoreCase(HL7_ELR)) {
                kafkaProducerService.sendMessageFromController(
                        created.getId(),
                        topicName,
                        rawElrDto.getType(),
                        dltOccurrence,
                        rawElrDto.getValidationActive(),
                        rawElrDto.getVersion(),rawElrDto.getCustomMapper());
            }
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
            String errorStatus = "Sending event to elr_raw kafka topic failed";
            iElrDeadLetterRepository.addErrorStatusForRawId(created.getId(), topicName, created.getType(), created.getPayload(), errorStatus, dltOccurrence + 1);
            throw new KafkaProducerException("Failed publishing message to kafka topic: " + topicName + " with UUID: " + created.getId());
        }
        return created.getId();
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
                        rawElrDto.getVersion(),
                        rawElrDto.getCustomMapper());
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

    private RawElrDto convert(RawElrModel rawElrModel) {
        RawElrDto rawElrDto = new RawElrDto();
        rawElrDto.setId(rawElrModel.getId());
        rawElrDto.setType(rawElrModel.getType());
        rawElrDto.setPayload(rawElrModel.getPayload());
        return rawElrDto;
    }
}
