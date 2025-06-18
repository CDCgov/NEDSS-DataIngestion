package gov.cdc.dataingestion.kafka.service;

import gov.cdc.dataingestion.constant.TopicPreparationType;
import gov.cdc.dataingestion.exception.ConversionPrepareException;
import gov.cdc.dataingestion.exception.KafkaProducerException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Testcontainers
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class KafkaProducerServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
    }

    @AfterAll
    public static void tearDown() {
    }

    @Test
    void testSendMessageFromController() throws KafkaProducerException {
        String msg = "test message";
        String topic = "test-topic";
        String msgType = "test-type";
        Integer dltOccurrence = 1;
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessageFromController(msg, topic, msgType, dltOccurrence, false, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageAfterValidatingMessage() throws KafkaProducerException {
        String topic = "test-topic";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setMessageType("test");
        model.setId("test");
        model.setMessageVersion("1");
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessageAfterValidatingMessage(model, topic, 1, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessagePreparationTopicXML() throws ConversionPrepareException, KafkaProducerException {
        var topicType = TopicPreparationType.XML;
        String topic = "test-topic";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setMessageType("test");
        model.setId("test");
        model.setMessageVersion("1");
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessagePreparationTopic(model, topic,
                topicType,
                1, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessagePreparationTopicFHIR() throws ConversionPrepareException, KafkaProducerException {
        var topicType = TopicPreparationType.FHIR;
        String topic = "test-topic";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setMessageType("test");
        model.setId("test");
        model.setMessageVersion("1");
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessagePreparationTopic(model, topic,
                topicType,
                1, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendElrXmlMessageFromController() throws KafkaProducerException {
        String msg = "test message";
        String topic = "test-topic";
        String msgType = "test-type";
        Integer dltOccurrence = 1;
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendElrXmlMessageFromController(msg, topic, msgType, dltOccurrence, "payload", "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void sendMessageDlt() throws KafkaProducerException {
        String topic = "test-topic";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessageDlt("test","test", topic, 1,
                "error", topic);
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }


    @Test
    void testSendMessageAfterConvertedToXml() throws KafkaProducerException {
        String topic = "test-topic";
        String msg = "test";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessageAfterConvertedToXml( msg, topic,
                1 );
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageAfterCheckingDuplicateHL7() throws KafkaProducerException {
        String topic = "test-topic";
        String msg = "test";
        ValidatedELRModel model = new ValidatedELRModel();
        model.setRawId("test");
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessageAfterCheckingDuplicateHL7(model, topic,
                1 );
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageFromDltController() throws KafkaProducerException {
        String topic = "test-topic";
        String msg = "test";
        String msgType = "HL7";
        Integer occurrence = 0;
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessageFromController(msg, topic,msgType, occurrence, false, "false");
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageFromCSVController() throws KafkaProducerException {
        String topic = "test-topic";
        List<String> msgNested = new ArrayList<>();
        msgNested.add("test");
        List<List<String>> msg = new ArrayList<>();
        msg.add(msgNested);
        String msgType = "HL7";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessageFromCSVController(msg, topic,msgType);
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageFromDltController_Success() throws KafkaProducerException {
        String topic = "test-topic";
        String msg = "test";
        String msgType = "HL7";
        Integer occurrence = 0;
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(new ProducerRecord<>(topic, "test"), null));

        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        kafkaProducerService.sendMessageFromDltController(msg, topic,msgType, occurrence);
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    void testSendMessageException() throws Exception {
        ProducerRecord<String, String> prodRecord = new ProducerRecord<>("test-topic", "test-key", "test-value");

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new TimeoutException("Timeout"));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        Method sendMessageMethod = KafkaProducerService.class.getDeclaredMethod("sendMessage", ProducerRecord.class);
        sendMessageMethod.setAccessible(true);

        KafkaProducerException exception = assertThrows(KafkaProducerException.class, () -> {
            try {
                sendMessageMethod.invoke(kafkaProducerService, prodRecord);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });

        Assertions.assertEquals("Failed publishing message to kafka topic: test-topic with UUID: test-value", exception.getMessage());
    }
}
