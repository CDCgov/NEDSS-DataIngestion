package gov.cdc.dataprocessing.service.implementation.action;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LabReportProcessingTest {
    @Mock
    private IObservationService observationService;
    @InjectMocks
    private LabReportProcessing labReportProcessing;
    @Mock
    AuthUtil authUtil;

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
        Mockito.reset(observationService, authUtil);
    }

    @Test
    void markAsReviewedHandler_Success_Processed() throws DataProcessingException {
        long uid = 10L;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setAssociatedPublicHealthCaseUid(null);

        when(observationService.processObservation(10L))
                .thenReturn(true);

        var test = labReportProcessing.markAsReviewedHandler(uid, edxLabInformationDto);

        assertEquals("PROCESSED", test);
    }

    @Test
    void markAsReviewedHandler_Success_UnProcessed() throws DataProcessingException {
        long uid = 10L;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setAssociatedPublicHealthCaseUid(-1L);

        when(observationService.processObservation(10L))
                .thenReturn(false);

        var test = labReportProcessing.markAsReviewedHandler(uid, edxLabInformationDto);

        assertEquals("UNPROCESSED", test);
    }

    @Test
    void markAsReviewedHandler_Success_Assoc() throws DataProcessingException {
        long uid = 10L;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setAssociatedPublicHealthCaseUid(0L);

        var test = labReportProcessing.markAsReviewedHandler(uid, edxLabInformationDto);

        assertEquals("", test);
        verify(observationService, times(1)).setLabInvAssociation(eq(10L), any());

    }


    @Test
    void markAsReviewedHandler_Exception() throws DataProcessingException {
        long uid = 10L;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setAssociatedPublicHealthCaseUid(-1L);

        when(observationService.processObservation(10L))
                .thenThrow(new RuntimeException("TEST"));


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            labReportProcessing.markAsReviewedHandler(uid, edxLabInformationDto);
        });

        assertEquals(EdxELRConstant.ELR_MASTER_MSG_ID_12, thrown.getMessage());
    }
}
