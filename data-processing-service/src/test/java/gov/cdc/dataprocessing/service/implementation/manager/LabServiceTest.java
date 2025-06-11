
package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPageService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LabServiceTest {

    @Mock
    private IPageService pageService;
    @Mock
    private IPamService pamService;
    @Mock
    private ILabReportProcessing labReportProcessing;
    @Mock
    private IInvestigationNotificationService investigationNotificationService;

    @InjectMocks
    private LabService labService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandlePageContainer() throws DataProcessingException {
        PageActProxyContainer pageAct = new PageActProxyContainer();
        EdxLabInformationDto edxDto = new EdxLabInformationDto();
        edxDto.setRootObserbationUid(123L);
        when(pageService.setPageProxyWithAutoAssoc(NEDSSConstant.CASE, pageAct, 123L, NEDSSConstant.LABRESULT_CODE, null)).thenReturn(1L);
        Long result = labService.handlePageContainer(pageAct, edxDto);
        assertEquals(1L, result);
    }

    @Test
    void testHandlePamContainer() throws DataProcessingException {
        PamProxyContainer pamProxy = new PamProxyContainer();
        EdxLabInformationDto edxDto = new EdxLabInformationDto();
        edxDto.setRootObserbationUid(321L);
        when(pamService.setPamProxyWithAutoAssoc(pamProxy, 321L, NEDSSConstant.LABRESULT_CODE)).thenReturn(2L);
        Long result = labService.handlePamContainer(pamProxy, edxDto);
        assertEquals(2L, result);
    }

    @Test
    void testHandleMarkAsReviewedWithAssociatedPhcUid() throws DataProcessingException {
        ObservationDto obsDto = new ObservationDto();
        obsDto.setObservationUid(101L);
        EdxLabInformationDto edxDto = new EdxLabInformationDto();
        edxDto.setAssociatedPublicHealthCaseUid(202L);
        labService.handleMarkAsReviewed(obsDto, edxDto);
        assertEquals(202L, edxDto.getPublicHealthCaseUid());
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_21, edxDto.getErrorText());
        assertTrue(edxDto.isLabAssociatedToInv());
    }

    @Test
    void testHandleMarkAsReviewedWithoutAssociatedPhcUid() throws DataProcessingException {
        ObservationDto obsDto = new ObservationDto();
        obsDto.setObservationUid(103L);
        EdxLabInformationDto edxDto = new EdxLabInformationDto();
        labService.handleMarkAsReviewed(obsDto, edxDto);
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_11, edxDto.getErrorText());
    }

    @Test
    void testHandleNndNotificationSuccess() throws DataProcessingException {
        PublicHealthCaseContainer phc = new PublicHealthCaseContainer();
        EdxLabInformationDto edxDto = new EdxLabInformationDto();
        edxDto.setNndComment("test");
        edxDto.setEdxActivityLogDto(new EDXActivityLogDto());
        EDXActivityDetailLogDto logDto = new EDXActivityDetailLogDto();
        logDto.setLogType("Success");
        logDto.setComment("ok");

        when(investigationNotificationService.sendNotification(phc, "test")).thenReturn(logDto);

        labService.handleNndNotification(phc, edxDto);
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_6, edxDto.getErrorText());
    }

    @Test
    void testHandleNndNotificationFailureWithMissingFields() throws DataProcessingException {
        PublicHealthCaseContainer phc = new PublicHealthCaseContainer();
        EdxLabInformationDto edxDto = new EdxLabInformationDto();
        edxDto.setNndComment("fail");
        edxDto.setEdxActivityLogDto(new EDXActivityLogDto());
        EDXActivityDetailLogDto logDto = new EDXActivityDetailLogDto();
        logDto.setLogType("Failure");
        logDto.setComment("Missing noti required fields");

        when(investigationNotificationService.sendNotification(phc, "fail")).thenReturn(logDto);

        DataProcessingException ex = assertThrows(DataProcessingException.class, () -> labService.handleNndNotification(phc, edxDto));
        assertTrue(ex.getMessage().contains("MISSING NOTI REQUIRED"));
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_10, edxDto.getErrorText());
    }

    @Test
    void testHandleNndNotificationFailureGeneral() throws DataProcessingException {
        PublicHealthCaseContainer phc = new PublicHealthCaseContainer();
        EdxLabInformationDto edxDto = new EdxLabInformationDto();
        edxDto.setNndComment("error");
        edxDto.setEdxActivityLogDto(new EDXActivityLogDto());
        EDXActivityDetailLogDto logDto = new EDXActivityDetailLogDto();
        logDto.setLogType("Failure");
        logDto.setComment("Something else");

        when(investigationNotificationService.sendNotification(phc, "error")).thenReturn(logDto);

        DataProcessingException ex = assertThrows(DataProcessingException.class, () -> labService.handleNndNotification(phc, edxDto));
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_10, edxDto.getErrorText());
    }
}
