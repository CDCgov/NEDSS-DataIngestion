package gov.cdc.dataprocessing.kafka.consumer;


import com.google.gson.Gson;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.Message;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class KafkaEdxLogConsumerTest {

    @Mock
    private IManagerService managerServiceMock;

    @Mock
    private IEdxLogService edxLogServiceMock;

    @InjectMocks
    private KafkaEdxLogConsumer kafkaEdxLogConsumer;

    private Gson gson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gson = new Gson();
    }

    @Test
    void testHandleMessage_Success() throws EdxLogException {
        EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();
        String message = gson.toJson(edxActivityLogDto);

        Message<String> kafkaMessage = MessageBuilder.withPayload(message)
                .setHeader(KafkaHeaders.RECEIVED_TOPIC, "test-topic")
                .build();

        kafkaEdxLogConsumer.handleMessage(kafkaMessage.getPayload(), kafkaMessage.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class));

        verify(edxLogServiceMock, times(1)).saveEdxActivityLogs(any(EDXActivityLogDto.class));
    }

    @Test
    void testHandleMessage_Exception() throws EdxLogException {
        EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();
        String message = gson.toJson(edxActivityLogDto);

        Message<String> kafkaMessage = MessageBuilder.withPayload(message)
                .setHeader(KafkaHeaders.RECEIVED_TOPIC, "test-topic")
                .build();

        doThrow(new EdxLogException("Test Exception", new Object())).when(edxLogServiceMock).saveEdxActivityLogs(any(EDXActivityLogDto.class));

        assertThrows(EdxLogException.class, () -> {
            kafkaEdxLogConsumer.handleMessage(kafkaMessage.getPayload(), kafkaMessage.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class));
        });
    }
}