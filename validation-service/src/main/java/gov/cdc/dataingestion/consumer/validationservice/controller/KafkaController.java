package gov.cdc.dataingestion.consumer.validationservice.controller;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.dataingestion.consumer.validationservice.integration.CsvValidator;
import gov.cdc.dataingestion.consumer.validationservice.model.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.consumer.validationservice.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@RestController
@RequestMapping("/kafka/")
public class KafkaController {

    @Value("${kafka.topic}")
    String topicName;

    KafkaProducerService kafkaProducerService;
    public KafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }
    @RequestMapping(value = "/simple-producer-hl7", method = RequestMethod.POST)
    public ResponseEntity<?> parsingHLv7WithHapi(@RequestBody String payload) {
        kafkaProducerService.sendMessageFromController(payload, topicName, KafkaHeaderValue.MessageType_HL7v2);
        return ResponseEntity.ok(payload);
    }

    @RequestMapping(value = "/simple-producer-csv", method = RequestMethod.POST)
    public ResponseEntity<?> parsingCSV(@RequestParam("file") MultipartFile file) throws Exception {

        if(!file.isEmpty()) {
            try {
                Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                CsvValidator csvValidator = new CsvValidator();
                var parsedCsv = csvValidator.ReadLineByLine(reader);
                kafkaProducerService.sendMessageFromCSVController(parsedCsv, topicName, KafkaHeaderValue.MessageType_CSV);

            } catch (Exception e) {
                return (ResponseEntity<?>) ResponseEntity.badRequest();
            }
        }
        return ResponseEntity.ok("ok");
    }
}
