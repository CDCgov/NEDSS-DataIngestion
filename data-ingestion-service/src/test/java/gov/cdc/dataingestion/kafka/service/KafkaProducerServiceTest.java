package gov.cdc.dataingestion.kafka.service;

import gov.cdc.dataingestion.constant.TopicPreparationType;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.exception.ConversionPrepareException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
@Testcontainers
class KafkaProducerServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    public void setUp() {
    }

    @AfterAll
    public static void tearDown() {
    }

    @Test
    void testSendMessageFromController() {
        String msg = "test message";
        String topic = "test-topic";
        String msgType = "test-type";
        Integer dltOccurrence = 1;
        kafkaProducerService.sendMessageFromController(msg, topic, msgType, dltOccurrence, false, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageAfterValidatingMessage() {
        String topic = "test-topic";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setMessageType("test");
        model.setId("test");
        model.setMessageVersion("1");
        kafkaProducerService.sendMessageAfterValidatingMessage(model, topic, 1, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessagePreparationTopicXML() throws ConversionPrepareException {
        var topicType = TopicPreparationType.XML;
        String topic = "test-topic";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setMessageType("test");
        model.setId("test");
        model.setMessageVersion("1");
        kafkaProducerService.sendMessagePreparationTopic(model, topic,
                topicType,
                1, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessagePreparationTopicFHIR() throws ConversionPrepareException {
        var topicType = TopicPreparationType.FHIR;
        String topic = "test-topic";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setMessageType("test");
        model.setId("test");
        model.setMessageVersion("1");
        kafkaProducerService.sendMessagePreparationTopic(model, topic,
                topicType,
                1, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageAfterConvertedToFhirMessage()  {
        String topic = "test-topic";
        HL7ToFHIRModel model = new HL7ToFHIRModel();
        model.setId("test");
        kafkaProducerService.sendMessageAfterConvertedToFhirMessage(model, topic,
                1 );
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void sendMessageDlt()  {
        String topic = "test-topic";
        kafkaProducerService.sendMessageDlt("test","test", topic, 1,
                "error", topic);
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }


    @Test
    void testSendMessageAfterConvertedToXml()  {
        String topic = "test-topic";
        String msg = "test";
        kafkaProducerService.sendMessageAfterConvertedToXml( msg, topic,
                1 );
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageAfterCheckingDuplicateHL7()  {
        String topic = "test-topic";
        String msg = "test";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setRawId("test");
        kafkaProducerService.sendMessageAfterCheckingDuplicateHL7(model, topic,
                1 );
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageFromDltController() {
        String topic = "test-topic";
        String msg = "test";
        String msgType = "HL7";
        Integer occurrence = 0;
        kafkaProducerService.sendMessageFromController(msg, topic,msgType, occurrence, false, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageFromCSVController() {
        String topic = "test-topic";
        List<String> msgNested = new ArrayList<>();
        msgNested.add("test");
        List<List<String>> msg = new ArrayList<>();
        msg.add(msgNested);
        String msgType = "HL7";
        kafkaProducerService.sendMessageFromCSVController(msg, topic,msgType);
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageFromDltController_Success() {
        String topic = "test-topic";
        String msg = "test";
        String msgType = "HL7";
        Integer occurrence = 0;
        kafkaProducerService.sendMessageFromDltController(msg, topic,msgType, occurrence);
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }
}
