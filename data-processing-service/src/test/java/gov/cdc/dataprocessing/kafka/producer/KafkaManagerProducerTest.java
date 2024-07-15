package gov.cdc.dataprocessing.kafka.producer;


import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KafkaManagerProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplateMock;

    @InjectMocks
    private KafkaManagerProducer kafkaManagerProducer;

    @Value("${kafka.topic.elr_health_case}")
    private String phcTopic = "elr_processing_public_health_case";

    @Value("${kafka.topic.elr_handle_lab}")
    private String labHandleTopic = "elr_processing_handle_lab";

    @Value("${kafka.topic.elr_action_tracker}")
    private String actionTrackerTopic = "elr_action_tracker";

    @Value("${kafka.topic.elr_edx_log}")
    private String edxLogTopic = "elr_edx_log";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaManagerProducer = new KafkaManagerProducer(kafkaTemplateMock);
    }

    @Test
    void testSendDataPhc() {
        String msg = "test message for PHC";
        kafkaManagerProducer.sendDataPhc(msg);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals(phcTopic, record.topic());
        assertEquals(msg, record.value());
    }

    @Test
    void testSendDataLabHandling() {
        String msg = "test message for Lab Handling";
        kafkaManagerProducer.sendDataLabHandling(msg);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals(labHandleTopic, record.topic());
        assertEquals(msg, record.value());
    }

    @Test
    void testSendDataActionTracker() {
        String msg = "test message for Action Tracker";
        kafkaManagerProducer.sendDataActionTracker(msg);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals(actionTrackerTopic, record.topic());
        assertEquals(msg, record.value());
    }

    @Test
    void testSendDataEdxActivityLog() {
        String msg = "test message for EDX Activity Log";
        kafkaManagerProducer.sendDataEdxActivityLog(msg);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals(edxLogTopic, record.topic());
        assertEquals(msg, record.value());
    }

    @Test
    void testSendData() {
        String topic = "testTopic";
        String msgContent = "test message";

        kafkaManagerProducer.sendData(topic, msgContent);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals(topic, record.topic());
        assertEquals(msgContent, record.value());
    }
}