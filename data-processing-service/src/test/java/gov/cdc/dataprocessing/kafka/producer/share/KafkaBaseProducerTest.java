package gov.cdc.dataprocessing.kafka.producer.share;

import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.kafka.producer.share.KafkaBaseProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class KafkaManagerProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplateMock;

    @InjectMocks
    private KafkaManagerProducer kafkaManagerProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendDataPhc() {
        String msg = "test message for PHC";
        kafkaManagerProducer.sendDataPhc(msg);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals("elr_processing_public_health_case", record.topic());
        assertEquals(msg, record.value());
    }

    @Test
    void testSendDataLabHandling() {
        String msg = "test message for Lab Handling";
        kafkaManagerProducer.sendDataLabHandling(msg);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals("elr_processing_handle_lab", record.topic());
        assertEquals(msg, record.value());
    }

    @Test
    void testSendDataActionTracker() {
        String msg = "test message for Action Tracker";
        kafkaManagerProducer.sendDataActionTracker(msg);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals("elr_action_tracker", record.topic());
        assertEquals(msg, record.value());
    }

    @Test
    void testSendDataEdxActivityLog() {
        String msg = "test message for EDX Activity Log";
        kafkaManagerProducer.sendDataEdxActivityLog(msg);

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplateMock, times(1)).send(recordCaptor.capture());

        ProducerRecord<String, String> record = recordCaptor.getValue();
        assertEquals("elr_edx_log", record.topic());
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