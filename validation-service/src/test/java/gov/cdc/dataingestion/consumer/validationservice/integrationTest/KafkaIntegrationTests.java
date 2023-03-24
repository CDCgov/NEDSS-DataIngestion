package gov.cdc.dataingestion.consumer.validationservice.integrationTest;

import com.google.gson.Gson;
import gov.cdc.dataingestion.consumer.validationservice.model.constant.KafkaHeaderValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import gov.cdc.dataingestion.consumer.validationservice.model.enums.MessageType;
import gov.cdc.dataingestion.consumer.validationservice.service.KafkaConsumerService;
import gov.cdc.dataingestion.consumer.validationservice.service.KafkaProducerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext()
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaIntegrationTests {

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Value("${kafka.consumer.topic}")
    private String validatedTopic = "";

    @Value("${kafka.topic}")
    private String topicName;


    @BeforeEach
    public void setup() {
        kafkaConsumerService.resetLatch();
        kafkaConsumerService.resetMessageType();
        kafkaConsumerService.resetIsMessageValid();
        kafkaProducerService.resetLatch();
    }

    @Test
    public void handleMessage_consumedMessage_invalidTypeAndMessage() throws InterruptedException {
        String data = "test data";
        kafkaProducerService.sendMessageFromController(data, topicName, "test data");
        boolean messageConsumed = kafkaConsumerService.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(messageConsumed);
        Assertions.assertFalse(kafkaConsumerService.isMessageValid());
        Assertions.assertEquals(MessageType.None.name(), kafkaConsumerService.getMessageType().name());
    }

    @Test
    public void handleMessageDLT_consumedMessage_validType_invalidMessage() throws InterruptedException {
        String data = "test data";
        kafkaProducerService.sendMessageFromController(data, topicName,  KafkaHeaderValue.MessageType_HL7v2);
        boolean messageConsumed = kafkaConsumerService.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(messageConsumed);
        Assertions.assertFalse(kafkaConsumerService.isMessageValid());
        Assertions.assertEquals(MessageType.None.name(), kafkaConsumerService.getMessageType().name());
    }

    @Test
    public void handleMessage_consumedMessage_validHL7v2() throws InterruptedException {
        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||Test Value";
        kafkaProducerService.sendMessageFromController(data, topicName, KafkaHeaderValue.MessageType_HL7v2);
        boolean messageConsumed = kafkaConsumerService.getLatch().await(10, TimeUnit.SECONDS);
        boolean messageValidSend = kafkaProducerService.getLatch().await(10, TimeUnit.SECONDS);

        Assertions.assertTrue(messageConsumed);
        Assertions.assertTrue(kafkaConsumerService.isMessageValid());
        Assertions.assertEquals(MessageType.HL7v2.name(), kafkaConsumerService.getMessageType().name());
        Assertions.assertTrue(messageValidSend);
    }

    @Test
    public void handleMessage_consumedMessage_validCSV() throws InterruptedException {
        String data = "[[\"84568-4564\",\"John\",\"Quentin\",\"Cardinal\",\"M\",\"9\",\"1\",\"1\"],[\"84502-7664\",\"Bert\",\"Patrick\",\"Pudding\",\"M\",\"9\",\"1\",\"4\"],[\"87619-9902\",\"Lila\",\"Betty\",\"Johnson\",\"F\",\"9\",\"2\",\"2\"],[\"84568-4341\",\"Max\",\"Philip\",\"Headroom\",\"M\",\"9\",\"3\",\"1\"],[\"84568-3168\",\"Theresa\",\"\",\"Green\",\"F\",\"9\",\"2\",\"3\"]]";
        Gson gson = new Gson();
        List<List<String>> kafkaMsg = gson.fromJson(data, List.class);
        kafkaProducerService.sendMessageFromCSVController(kafkaMsg, topicName, KafkaHeaderValue.MessageType_CSV);
        boolean messageValidSend = kafkaProducerService.getLatch().await(10, TimeUnit.SECONDS);
        boolean messageConsumed = kafkaConsumerService.getLatch().await(10, TimeUnit.SECONDS);
        Assertions.assertTrue(messageConsumed);
        Assertions.assertTrue(kafkaConsumerService.isMessageValid());
        Assertions.assertEquals(MessageType.CSV.name(), kafkaConsumerService.getMessageType().name());
        Assertions.assertTrue(messageValidSend);
    }


}