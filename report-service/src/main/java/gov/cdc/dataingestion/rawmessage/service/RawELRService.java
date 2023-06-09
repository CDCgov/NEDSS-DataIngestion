package gov.cdc.dataingestion.rawmessage.service;

import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RawELRService {

    private static final String CREATED_BY = "admin";
    @Value("${kafka.raw.topic}")
    String topicName;
    private final IRawELRRepository rawELRRepository;
    private final KafkaProducerService kafkaProducerService;


    public String submission(RawERLDto rawERLDto) {
        RawERLModel created = rawELRRepository.save(convert(rawERLDto));
        kafkaProducerService.sendMessageFromController(created.getId(), topicName, rawERLDto.getType(), 0);
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
        rawERLModel.setCreatedOn(Timestamp.from(ZonedDateTime.now().toInstant()));
        rawERLModel.setUpdatedOn(Timestamp.from(ZonedDateTime.now().toInstant()));
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
