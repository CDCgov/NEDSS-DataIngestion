package gov.cdc.dataingestion.rawmessage.service;

import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static gov.cdc.dataingestion.constant.MessageType.HL7_ELR;
import static gov.cdc.dataingestion.constant.MessageType.XML_ELR;
import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class RawELRService {

    private static final String CREATED_BY = "admin";
    @Value("${kafka.raw.topic}")
    String topicName;

    @Value("${kafka.raw.xml-topic}")
    String rawXmlTopicName;

    private final IRawELRRepository rawELRRepository;
    private final KafkaProducerService kafkaProducerService;


    public String submission(RawERLDto rawERLDto, String version) {
        RawERLModel created = rawELRRepository.save(convert(rawERLDto));
        if(rawERLDto.getType().equalsIgnoreCase(HL7_ELR)) {
            kafkaProducerService.sendMessageFromController(
                    created.getId(),
                    topicName,
                    rawERLDto.getType(),
                    0,
                    rawERLDto.getValidationActive(),
                    version);
        }
        if(rawERLDto.getType().equalsIgnoreCase(XML_ELR)) {
            kafkaProducerService.sendElrXmlMessageFromController(
                    created.getId(),
                    rawXmlTopicName,
                    rawERLDto.getType(),
                    0,
                    rawERLDto.getPayload(),
                    version);
        }
        return created.getId();
    }

    public RawERLDto getById(String id) {
        RawERLModel rawERLModel = rawELRRepository.getById(id);
        return convert(rawERLModel);
    }

    private RawERLModel convert(RawERLDto rawERLDto) {

        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setId(UUID.randomUUID().toString());
        rawERLModel.setType(rawERLDto.getType());
        rawERLModel.setPayload(rawERLDto.getPayload());
        rawERLModel.setCreatedOn(getCurrentTimeStamp());
        rawERLModel.setUpdatedOn(getCurrentTimeStamp());
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
