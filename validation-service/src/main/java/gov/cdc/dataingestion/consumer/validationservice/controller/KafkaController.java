package gov.cdc.dataingestion.consumer.validationservice.controller;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.consumer.validationservice.model.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.consumer.validationservice.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@RestController
@RequestMapping("/kafka/")
public class KafkaController {

    @Value("${kafka.topic}")
    String topicName;

    @Autowired
    KafkaProducerService kafkaProducerService;
    @RequestMapping(value = "/simple-producer-hl7", method = RequestMethod.POST)
    public ResponseEntity<?> parsingHLv7WithHapi(@RequestBody String payload) {
        kafkaProducerService.sendMessageFromController(payload, topicName, KafkaHeaderValue.MessageType_HL7v2);
        return ResponseEntity.ok(payload);
    }
}
