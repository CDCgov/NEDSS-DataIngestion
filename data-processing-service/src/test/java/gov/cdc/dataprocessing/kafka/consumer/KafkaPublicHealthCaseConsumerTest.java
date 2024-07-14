package gov.cdc.dataprocessing.kafka.consumer;


import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaPublicHealthCaseConsumerTest {

    @Mock
    private KafkaManagerProducer kafkaManagerProducerMock;

    @Mock
    private IManagerService managerServiceMock;

    @Mock
    private IAuthUserService authUserServiceMock;

    @InjectMocks
    private KafkaPublicHealthCaseConsumer kafkaPublicHealthCaseConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleMessageForPublicHealthCase_Success() throws Exception {
        AuthUserProfileInfo authUserMock = new AuthUserProfileInfo();
        when(authUserServiceMock.getAuthUserInfo("superuser")).thenReturn(authUserMock);

        String message = "{\"someField\":\"someValue\"}";
        String topic = "testTopic";

        kafkaPublicHealthCaseConsumer.handleMessageForPublicHealthCase(message, topic);

        verify(authUserServiceMock, times(1)).getAuthUserInfo("superuser");
        verify(managerServiceMock, times(1)).initiatingInvestigationAndPublicHealthCase(any(PublicHealthCaseFlowContainer.class));
    }

    @Test
    void testHandleMessageForPublicHealthCase_Exception() throws Exception {
        when(authUserServiceMock.getAuthUserInfo("superuser")).thenThrow(new RuntimeException("Test Exception"));

        String message = "{\"someField\":\"someValue\"}";
        String topic = "testTopic";

        kafkaPublicHealthCaseConsumer.handleMessageForPublicHealthCase(message, topic);

        verify(authUserServiceMock, times(1)).getAuthUserInfo("superuser");
        verify(managerServiceMock, never()).initiatingInvestigationAndPublicHealthCase(any(PublicHealthCaseFlowContainer.class));
    }
}