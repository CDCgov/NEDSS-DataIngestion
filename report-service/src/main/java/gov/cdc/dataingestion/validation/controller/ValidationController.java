package gov.cdc.dataingestion.validation.controller;

import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.model.constant.KafkaHeaderValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class ValidationController {

    @Value("${kafka.raw.topic}")
    String topicName;

    @Autowired
    private IRawELRRepository rawELRRepository;
    KafkaProducerService kafkaProducerService;
    public ValidationController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }
    @RequestMapping(value = "/elr-simulator", method = RequestMethod.POST)
    public ResponseEntity<?> saveELRMessage(@RequestBody String payload) {
        RawERLModel model = new RawERLModel();
        model.setType(KafkaHeaderValue.MessageType_HL7v2);
        model.setPayload(payload);
        model.setCreatedBy("ELR-SIMULATOR");
        model.setUpdatedBy("ELR-SIMULATOR");
        rawELRRepository.save(model);
        kafkaProducerService.sendMessageFromController(model.getId(), topicName, model.getType());
        return ResponseEntity.ok("OK");
    }

}
