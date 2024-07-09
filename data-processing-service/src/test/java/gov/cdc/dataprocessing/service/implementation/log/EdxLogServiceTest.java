package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.EdxActivityDetailLogRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.EdxActivityLogRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.service.model.wds.WdsReport;
import gov.cdc.dataprocessing.service.model.wds.WdsValueCodedReport;
import gov.cdc.dataprocessing.service.model.wds.WdsValueNumericReport;
import gov.cdc.dataprocessing.service.model.wds.WdsValueTextReport;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EdxLogServiceTest {
    @Mock
    private EdxActivityLogRepository edxActivityLogRepository;
    @Mock
    private EdxActivityDetailLogRepository edxActivityDetailLogRepository;
    @Mock
    private EDXActivityLogDto edxActivityLogDto1;
    @Mock
    private EdxLabInformationDto edxLabInformationDto;
    @InjectMocks
    private EdxLogService edxLogService;
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
        Mockito.reset(edxActivityLogRepository,
                edxActivityDetailLogRepository, authUtil,
                edxActivityLogDto1, edxLabInformationDto);
    }

    @Test
    void saveEdxActivityDetailLog_Test() {
        EDXActivityDetailLogDto detailLogDto = new EDXActivityDetailLogDto();
        when(edxActivityDetailLogRepository.save(any())).thenReturn(new EdxActivityDetailLog());

        var res = edxLogService.saveEdxActivityDetailLog(detailLogDto);

        assertNotNull(res);

    }

    @Test
    void saveEdxActivityLogs_Test() {
        EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();
        edxActivityLogDto.setExceptionTxt("TEST");

        var edxActDeCol = new ArrayList<EDXActivityDetailLogDto>();
        var edxActDe = new EDXActivityDetailLogDto();
        edxActDeCol.add(edxActDe);
        edxActivityLogDto.setEDXActivityLogDTWithVocabDetails(edxActDeCol);
        var act = new EdxActivityLog();
        when(edxActivityLogRepository.findBySourceUid(any())).thenReturn(Optional.of(act));

        edxLogService.saveEdxActivityLogs(edxActivityLogDto);

        verify(edxActivityLogRepository, times(1)).findBySourceUid(any());

    }

    @Test
    void saveEdxActivityLogs_Test_2() {
        EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();
        edxActivityLogDto.setExceptionTxt("TEST");

        var edxActDeCol = new ArrayList<EDXActivityDetailLogDto>();
        var edxActDe = new EDXActivityDetailLogDto();
        edxActDeCol.add(edxActDe);
        edxActivityLogDto.setEDXActivityLogDTWithVocabDetails(edxActDeCol);
        var act = new EdxActivityLog();
        act.setId(10L);
        when(edxActivityLogRepository.findBySourceUid(any())).thenReturn(Optional.empty());
        when(edxActivityLogRepository.save(any())).thenReturn(act);


        edxLogService.saveEdxActivityLogs(edxActivityLogDto);

        verify(edxActivityLogRepository, times(1)).save(any());

    }

    @SuppressWarnings("java:S2699")
    @Test
    void updateActivityLogDT_Test() {
        NbsInterfaceModel nbsInterfaceModel = new NbsInterfaceModel();
        EdxLabInformationDto edxLabInformationDto1 = new EdxLabInformationDto();

        nbsInterfaceModel.setNbsInterfaceUid(10);

        var edxAct = new EDXActivityLogDto();
        edxLabInformationDto1.setEdxActivityLogDto(edxAct);
        edxLabInformationDto1.setRootObserbationUid(11L);
        edxLabInformationDto1.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_1);
        edxLabInformationDto1.setStatus(NbsInterfaceStatus.Failure);
        edxLabInformationDto1.setFillerNumber("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore");
        edxLabInformationDto1.setMessageControlID("CONTROL");
        edxLabInformationDto1.setEntityName("ENTITY");
        edxLabInformationDto1.setSendingFacilityName("SEND");
        edxLabInformationDto1.setLocalId("LOCAL");
        edxLabInformationDto1.setDsmAlgorithmName("DSM");
        edxLabInformationDto1.setAction("REVIEW");

        edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto1);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void updateActivityLogDT_Test_2() {
        NbsInterfaceModel nbsInterfaceModel = new NbsInterfaceModel();
        EdxLabInformationDto edxLabInformationDto1 = new EdxLabInformationDto();

        nbsInterfaceModel.setNbsInterfaceUid(10);

        var edxAct = new EDXActivityLogDto();
        edxLabInformationDto1.setEdxActivityLogDto(edxAct);
        edxLabInformationDto1.setRootObserbationUid(11L);
        edxLabInformationDto1.setErrorText(null);
        edxLabInformationDto1.setStatus(NbsInterfaceStatus.Failure);
        edxLabInformationDto1.setFillerNumber(null);
        edxLabInformationDto1.setMessageControlID("CONTROL");
        edxLabInformationDto1.setEntityName("ENTITY");
        edxLabInformationDto1.setSendingFacilityName("SEND");
        edxLabInformationDto1.setLocalId("LOCAL");
        edxLabInformationDto1.setDsmAlgorithmName("DSM");
        edxLabInformationDto1.setAction("REVIEW");

        edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto1);
    }


    @Test
    @SuppressWarnings("java:S2699")
    void addActivityDetailLogs_Test() {
        EdxLabInformationDto edxLabInformationDto1 = new EdxLabInformationDto();

        var edxAc = new EDXActivityLogDto();
        var edxAcDeCol = new ArrayList<EDXActivityDetailLogDto>();
        var edxAcDe = new EDXActivityDetailLogDto();
        edxAcDeCol.add(edxAcDe);
        edxAc.setEDXActivityLogDTWithVocabDetails(edxAcDeCol);
        edxLabInformationDto1.setEdxActivityLogDto(edxAc);
        edxLabInformationDto1.setLocalId("LOCAL");
    }

    @Test
    void testInvalidXML() {
        when(edxLabInformationDto.isInvalidXML()).thenReturn(true);
        when(edxLabInformationDto.getEdxActivityLogDto()).thenReturn(edxActivityLogDto1);
        when(edxActivityLogDto1.getEDXActivityLogDTWithVocabDetails()).thenReturn(new ArrayList<>());

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultipleOBR() {
        setCommonExpectations();
        when(edxLabInformationDto.isMultipleOBR()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testFillerNumberNotPresent() {
        when(edxLabInformationDto.getEdxActivityLogDto()).thenReturn(edxActivityLogDto1);
        when(edxActivityLogDto1.getEDXActivityLogDTWithVocabDetails()).thenReturn(new ArrayList<>());
        when(edxLabInformationDto.isFillerNumberPresent()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testOrderTestNameMissing() {
        setCommonExpectations();
        when(edxLabInformationDto.isOrderTestNameMissing()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testReflexOrderedTestCdMissing() {
        setCommonExpectations();
        when(edxLabInformationDto.isReflexOrderedTestCdMissing()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testReflexResultedTestCdMissing() {
        setCommonExpectations();
        when(edxLabInformationDto.isReflexResultedTestCdMissing()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testResultedTestNameMissing() {
        setCommonExpectations();
        when(edxLabInformationDto.isResultedTestNameMissing()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testReasonforStudyCdMissing() {
        setCommonExpectations();
        when(edxLabInformationDto.isReasonforStudyCdMissing()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testDrugNameMissing() {
        setCommonExpectations();
        when(edxLabInformationDto.isDrugNameMissing()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultipleSubject() {
        setCommonExpectations();
        when(edxLabInformationDto.isMultipleSubject()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNoSubject() {
        setCommonExpectations();
        when(edxLabInformationDto.isNoSubject()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testChildOBRWithoutParent() {
        setCommonExpectations();
        when(edxLabInformationDto.isChildOBRWithoutParent()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testOrderOBRWithParent() {
        setCommonExpectations();
        when(edxLabInformationDto.isOrderOBRWithParent()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testObsStatusNotTranslated() {
        setCommonExpectations();
        when(edxLabInformationDto.isObsStatusTranslated()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    private void setCommonExpectations2() {
        when(edxLabInformationDto.isObsStatusTranslated()).thenReturn(true);

        when(edxLabInformationDto.isCreateNotificationPermission()).thenReturn(true);
        when(edxLabInformationDto.isCreateInvestigationPermission()).thenReturn(true);
        when(edxLabInformationDto.isMarkAsReviewPermission()).thenReturn(true);
        when(edxLabInformationDto.isUpdateLabPermission()).thenReturn(true);
        when(edxLabInformationDto.isCreateLabPermission()).thenReturn(true);
        when(edxLabInformationDto.getFillerNumber()).thenReturn("TEST");
        when(edxLabInformationDto.getLocalId()).thenReturn("TEST");
        when(edxLabInformationDto.getUserName()).thenReturn("TEST");
        when(edxLabInformationDto.getPersonParentUid()).thenReturn(10L);
        when(edxLabInformationDto.getPatientUid()).thenReturn(10L);

    }


    @Test
    void testUniversalServiceIdMissing() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isUniversalServiceIdMissing()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testActivityToTimeMissing() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isActivityToTimeMissing()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testActivityTimeOutOfSequence() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isActivityTimeOutOfSequence()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testFinalPostCorrected() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isFinalPostCorrected()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testPreliminaryPostFinal() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isPreliminaryPostFinal()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testPreliminaryPostCorrected() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isPreliminaryPostCorrected()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMissingOrderingProviderandFacility() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMissingOrderingProviderandFacility()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testUnexpectedResultType() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isUnexpectedResultType()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testChildSuscWithoutParentResult() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isChildSuscWithoutParentResult()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNoCreateLabPermission() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isCreateLabPermission()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNoUpdateLabPermission() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isUpdateLabPermission()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNoMarkAsReviewPermission() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMarkAsReviewPermission()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNoCreateInvestigationPermission() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isCreateInvestigationPermission()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNoCreateNotificationPermission_Test() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isCreateNotificationPermission()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testFieldTruncationError() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isFieldTruncationError()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testInvalidDateError() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isInvalidDateError()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testSystemException() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isSystemException()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultipleSubjectMatch() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMultipleSubjectMatch()).thenReturn(true);
        when(edxLabInformationDto.getEntityName()).thenReturn("BLAH");

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testPatientMatch() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isPatientMatch()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNextOfKin() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isNextOfKin()).thenReturn(true);
        var labresult = new LabResultProxyContainer();

        var perConCol = new ArrayList<PersonContainer>();
        var perConn = new PersonContainer();
        var perDt = new PersonDto();
        perDt.setPersonUid(10L);
        perDt.setPersonParentUid(10L);
        perConn.setThePersonDto(perDt);
        perConn.setRole(NEDSSConstant.NOK);
        perConCol.add(perConn);
        labresult.setThePersonContainerCollection(perConCol);
        when(edxLabInformationDto.getLabResultProxyContainer()).thenReturn(labresult);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testProvider() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isProvider()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testLabIsCreateSuccess() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isLabIsCreateSuccess()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testObservationMatch() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isObservationMatch()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testLabIsUpdateSuccess() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isLabIsUpdateSuccess()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMissingOrderingProvider() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMissingOrderingProviderandFacility()).thenReturn(false);
        when(edxLabInformationDto.isMissingOrderingProvider()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMissingOrderingFacility() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMissingOrderingProviderandFacility()).thenReturn(false);
        when(edxLabInformationDto.isMissingOrderingFacility()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultipleOrderingProvider() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMultipleOrderingProvider()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultipleCollector() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMultipleCollector()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultiplePrincipalInterpreter() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMultiplePrincipalInterpreter()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultipleOrderingFacility() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMultipleOrderingFacility()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultipleReceivingFacility() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMultipleReceivingFacility()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testMultipleSpecimen() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isMultipleSpecimen()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testEthnicityCodeNotTranslated() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isEthnicityCodeTranslated()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testObsMethodNotTranslated() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isObsMethodTranslated()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testRaceNotTranslated() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isRaceTranslated()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testSexNotTranslated() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isSexTranslated()).thenReturn(false);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testSsnInvalid() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isSsnInvalid()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNullClia() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.isNullClia()).thenReturn(true);

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testWdsReportsNotEmpty() {
        setCommonExpectations();
        setCommonExpectations2();
        when(edxLabInformationDto.getWdsReports()).thenReturn(new ArrayList<>() {
            {
                add(new WdsReport());  // Assuming WdsReport is a valid class
            }
        });

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testWdsReportsNotEmpty_Numeric() {
        setCommonExpectations();
        setCommonExpectations2();
        var wds = new WdsReport();
        var wdsNum = new WdsValueNumericReport();
        wdsNum.setWdsCode("CODE");
        wdsNum.setOperator(">");
        wdsNum.setInputCode1("1");
        wdsNum.setInputCode2("2");
        wds.setWdsValueNumericReportList(new ArrayList<>() {
            {
                add(wdsNum);  // Assuming WdsReport is a valid class
            }
        });
        when(edxLabInformationDto.getWdsReports()).thenReturn(new ArrayList<>() {
            {
                add(wds);  // Assuming WdsReport is a valid class
            }
        });

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testWdsReportsNotEmpty_Text() {
        setCommonExpectations();
        setCommonExpectations2();
        var wds = new WdsReport();
        var wdsNum = new WdsValueTextReport();
        wdsNum.setWdsCode("CODE");
        wdsNum.setInputCode("1");
        wds.setWdsValueTextReportList(new ArrayList<>() {
            {
                add(wdsNum);  // Assuming WdsReport is a valid class
            }
        });
        when(edxLabInformationDto.getWdsReports()).thenReturn(new ArrayList<>() {
            {
                add(wds);  // Assuming WdsReport is a valid class
            }
        });

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }


    @Test
    void testWdsReportsNotEmpty_Code() {
        setCommonExpectations();
        setCommonExpectations2();
        var wds = new WdsReport();
        var wdsNum = new WdsValueCodedReport();
        wdsNum.setWdsCode("CODE");
        wdsNum.setInputCode("1");
        wds.setWdsValueCodedReport(wdsNum);
        when(edxLabInformationDto.getWdsReports()).thenReturn(new ArrayList<>() {
            {
                add(wds);  // Assuming WdsReport is a valid class
            }
        });

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }


    @Test
    @SuppressWarnings("java:S2699")
    void testWdsReportsNotEmpty_Exp() {
        setCommonExpectations();
        setCommonExpectations2();

        when(edxLabInformationDto.getWdsReports()).thenThrow(new RuntimeException("TEST"));

        edxLogService.addActivityDetailLogs(edxLabInformationDto, "Detailed message");

    }


    private void setCommonExpectations() {
        when(edxLabInformationDto.isFillerNumberPresent()).thenReturn(true);
        when(edxLabInformationDto.getEdxActivityLogDto()).thenReturn(edxActivityLogDto1);
        when(edxActivityLogDto1.getEDXActivityLogDTWithVocabDetails()).thenReturn(new ArrayList<>());
    }

    //
    private void setCommonExpectations1() {
        when(edxLabInformationDto.getEdxActivityLogDto()).thenReturn(edxActivityLogDto1);
        when(edxLabInformationDto.isCreateNotificationPermission()).thenReturn(true);

        when(edxActivityLogDto1.getEDXActivityLogDTWithVocabDetails()).thenReturn(new ArrayList<>());
    }

    @Test
    void testNoCreateNotificationPermission() {
        setCommonExpectations1();
        when(edxLabInformationDto.isCreateNotificationPermission()).thenReturn(false);
        when(edxLabInformationDto.getUserName()).thenReturn("TEST");

        edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testInvestigationSuccessfullyCreated() {
        setCommonExpectations1();
        when(edxLabInformationDto.isInvestigationSuccessfullyCreated()).thenReturn(true);
        when(edxLabInformationDto.getPublicHealthCaseUid()).thenReturn(123L);

        edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testLabAssociatedToInv() {
        setCommonExpectations1();
        when(edxLabInformationDto.isLabAssociatedToInv()).thenReturn(true);
        when(edxLabInformationDto.getPublicHealthCaseUid()).thenReturn(123L);

        edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNotificationSuccessfullyCreated() {
        setCommonExpectations1();
        when(edxLabInformationDto.isNotificationSuccessfullyCreated()).thenReturn(true);
        when(edxLabInformationDto.getNotificationUid()).thenReturn(456L);

        edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testInvestigationMissingFields() {
        setCommonExpectations1();
        when(edxLabInformationDto.isInvestigationMissingFields()).thenReturn(true);

        edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNotificationMissingFields() {
        setCommonExpectations1();
        when(edxLabInformationDto.isNotificationMissingFields()).thenReturn(true);

        edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    @Test
    void testNoExceptionThrown() {
        setCommonExpectations1();

        edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "Detailed message");

        verify(edxActivityLogDto1, times(1)).setEDXActivityLogDTWithVocabDetails(anyList());
    }

    // Test for exception handling
    @Test
    void testExceptionHandling() {
        when(edxLabInformationDto.getEdxActivityLogDto()).thenThrow(new RuntimeException("Test Exception"));

        assertDoesNotThrow(() -> edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "Detailed message"));
    }
}
