package gov.cdc.dataprocessing.kafka.consumer;


import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import static org.mockito.Mockito.*;

class KafkaManagerConsumerTest {

    @Mock
    private KafkaManagerProducer kafkaManagerProducerMock;

    @Mock
    private IManagerService managerServiceMock;

    @Mock
    private IAuthUserService authUserServiceMock;

    @InjectMocks
    private KafkaManagerConsumer kafkaManagerConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleMessage_Success() throws DataProcessingException, DataProcessingConsumerException {
        AuthUserProfileInfo authUserMock = new AuthUserProfileInfo();
        when(authUserServiceMock.getAuthUserInfo("superuser")).thenReturn(authUserMock);

        String message = "testMessage";
        String topic = "testTopic";
        String dataType = "testDataType";

        kafkaManagerConsumer.handleMessage(message, topic, dataType);

        verify(authUserServiceMock, times(1)).getAuthUserInfo("superuser");
        verify(managerServiceMock, times(1)).processDistribution(dataType, message);
    }

    @Test
    void testHandleMessage_DataProcessingConsumerException() throws DataProcessingException {

        when(authUserServiceMock.getAuthUserInfo("superuser")).thenThrow(new RuntimeException("Test Exception"));

        String message = "testMessage";
        String topic = "testTopic";
        String dataType = "testDataType";

        kafkaManagerConsumer.handleMessage(message, topic, dataType);

    }

}