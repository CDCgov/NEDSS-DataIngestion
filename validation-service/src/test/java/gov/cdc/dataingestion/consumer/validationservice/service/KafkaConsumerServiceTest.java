package gov.cdc.dataingestion.consumer.validationservice.service;

import gov.cdc.dataingestion.consumer.validationservice.model.constant.KafkaHeaderValue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaConsumerServiceTest {

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Value("${kafka.consumer.topic}")
    private String validatedTopic = "";

    @Value("${kafka.topic}")
    private String topicName;

    @Test
    public void handleMessage_consumedMessage_invalidTypeAndMessage() throws InterruptedException {
        String data = "test data";
        kafkaProducerService.sendMessageFromController(data, topicName, "test data");
        var target = kafkaConsumerService;
        boolean messageConsumed = target.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(messageConsumed);
        Assertions.assertFalse(target.isMessageValid());
        Assertions.assertEquals("None", target.getMessageType().name());
    }

}