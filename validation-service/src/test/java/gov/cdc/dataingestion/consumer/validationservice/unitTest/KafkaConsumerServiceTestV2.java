//package gov.cdc.dataingestion.consumer.validationservice.service;
//
//import gov.cdc.dataingestion.consumer.validationservice.integration.CsvValidator;
//import gov.cdc.dataingestion.consumer.validationservice.integration.HL7v2Validator;
//import gov.cdc.dataingestion.consumer.validationservice.integration.interfaces.ICsvValidator;
//import gov.cdc.dataingestion.consumer.validationservice.integration.interfaces.IHL7v2Validator;
//
//import org.junit.Before;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.mockito.InOrder;
//import static org.mockito.Mockito.*;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.annotation.DirtiesContext;
//
//import java.util.concurrent.TimeUnit;
//
//@DirtiesContext
//@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
//public class KafkaConsumerServiceTestV2 {
//    private KafkaConsumerService target;
//    private IHL7v2Validator ihl7v2Validator;
//    private ICsvValidator iCsvValidator;
//    private KafkaProducerService kafkaProducerService;
//
//    @Value("${kafka.consumer.topic}")
//    private String validatedTopic = "";
//
//    @Value("${kafka.topic}")
//    private String topicName;
//
//    @Before
//    public void setupMock() {
//        ihl7v2Validator = mock(HL7v2Validator.class);
//        iCsvValidator = mock(CsvValidator.class);
//        kafkaProducerService = mock(KafkaProducerService.class);
//        target = new KafkaConsumerService(kafkaProducerService, ihl7v2Validator, iCsvValidator);
//    }
//
//    @Test
//    public void handleMessage_consumedMessage_invalidTypeAndMessage() throws InterruptedException {
//        String data = "test data";
//
//        kafkaProducerService.sendMessageFromController(data, topicName, "test data");
//
//        var test = "TEST";
////        var target = kafkaConsumerService;
////        boolean messageConsumed = target.getLatch().await(10, TimeUnit.SECONDS);
////        Assertions.assertTrue(messageConsumed);
////        Assertions.assertFalse(target.isMessageValid());
////        Assertions.assertEquals("None", target.getMessageType().name());
//    }
//}
