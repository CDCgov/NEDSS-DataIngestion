package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.implementation.auth_user.AuthUserService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.utilities.component.public_health_case.CdaPhcProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class CdaPhcProcessorTest {
    @InjectMocks
    private CdaPhcProcessor cdaPhcProcessor;
    @Mock
    AuthUtil authUtil;
    @Mock
    private PublicHealthCaseDto phcDT;

    @Mock
    private NbsCaseAnswerDto nbsCaseAnswerDT;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtil);
    }
    @Test
    void testSetStandardNBSCaseAnswerVals_Success() throws DataProcessingException {
        // Arrange
        when(phcDT.getPublicHealthCaseUid()).thenReturn(1L);
        when(phcDT.getAddTime()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(phcDT.getLastChgTime()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(phcDT.getAddUserId()).thenReturn(1L);
        when(phcDT.getLastChgUserId()).thenReturn(1L);
        when(phcDT.getRecordStatusTime()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(nbsCaseAnswerDT.getSeqNbr()).thenReturn(-1);

        // Act
        CdaPhcProcessor.setStandardNBSCaseAnswerVals(phcDT, nbsCaseAnswerDT);

        // Assert
        verify(nbsCaseAnswerDT).setActUid(1L);
        verify(nbsCaseAnswerDT).setAddTime(any(Timestamp.class));
        verify(nbsCaseAnswerDT).setLastChgTime(any(Timestamp.class));
        verify(nbsCaseAnswerDT).setAddUserId(1L);
        verify(nbsCaseAnswerDT).setLastChgUserId(1L);
        verify(nbsCaseAnswerDT).setRecordStatusCd(NEDSSConstant.OPEN_INVESTIGATION);
        verify(nbsCaseAnswerDT).setSeqNbr(0);
        verify(nbsCaseAnswerDT).setRecordStatusTime(any(Timestamp.class));
        verify(nbsCaseAnswerDT).setItNew(true);
    }

    @Test
    void testSetStandardNBSCaseAnswerVals_Exception() {
        // Arrange
        when(phcDT.getPublicHealthCaseUid()).thenThrow(new RuntimeException("Test exception"));

        // Act & Assert
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            CdaPhcProcessor.setStandardNBSCaseAnswerVals(phcDT, nbsCaseAnswerDT);
        });

        assertNotNull(thrown);
    }
}
