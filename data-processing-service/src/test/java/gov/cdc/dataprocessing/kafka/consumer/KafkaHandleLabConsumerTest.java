package gov.cdc.dataprocessing.kafka.consumer;


import com.google.gson.Gson;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.annotation.KafkaListener;

import static org.mockito.Mockito.*;

class KafkaHandleLabConsumerTest {

    @Mock
    private KafkaManagerProducer kafkaManagerProducerMock;

    @Mock
    private IManagerService managerServiceMock;

    @Mock
    private IAuthUserService authUserServiceMock;

    @InjectMocks
    private KafkaHandleLabConsumer kafkaHandleLabConsumer;

    private Gson gson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gson = new Gson();
    }

    @Test
    void testHandleMessage_Success() throws Exception {
        AuthUserProfileInfo authUserMock = new AuthUserProfileInfo();
        when(authUserServiceMock.getAuthUserInfo("superuser")).thenReturn(authUserMock);

        PublicHealthCaseFlowContainer publicHealthCaseFlowContainerMock = new PublicHealthCaseFlowContainer();
        String message = gson.toJson(publicHealthCaseFlowContainerMock);

        kafkaHandleLabConsumer.handleMessage(message);

        verify(authUserServiceMock, times(1)).getAuthUserInfo("superuser");
        verify(managerServiceMock, times(1)).initiatingLabProcessing(any(PublicHealthCaseFlowContainer.class));
    }

    @Test
    void testHandleMessage_Exception() throws Exception {
        when(authUserServiceMock.getAuthUserInfo("superuser")).thenThrow(new RuntimeException("Test Exception"));

        PublicHealthCaseFlowContainer publicHealthCaseFlowContainerMock = new PublicHealthCaseFlowContainer();
        String message = gson.toJson(publicHealthCaseFlowContainerMock);

        kafkaHandleLabConsumer.handleMessage(message);

        verify(authUserServiceMock, times(1)).getAuthUserInfo("superuser");
        verify(managerServiceMock, never()).initiatingLabProcessing(any(PublicHealthCaseFlowContainer.class));
    }
}