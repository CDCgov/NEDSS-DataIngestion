package gov.cdc.dataingestion.reportstatus.service;

import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.odse.repository.IEdxActivityLogRepository;
import gov.cdc.dataingestion.odse.repository.IEdxActivityParentLogRepository;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLog;
import gov.cdc.dataingestion.report.repository.IRawElrRepository;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.model.ReportStatusIdData;
import gov.cdc.dataingestion.reportstatus.repository.IReportStatusRepository;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class ReportStatusServiceTest {
    @Mock
    private IReportStatusRepository iReportStatusRepositoryMock;
    @Mock
    private IEdxActivityParentLogRepository edxActivityParentLogRepository;
    @Mock
    private NbsInterfaceRepository nbsInterfaceRepositoryMock;
    @Mock
    private IRawElrRepository iRawELRRepository;
    @Mock
    private IValidatedELRRepository iValidatedELRRepository;
    @Mock
    private IElrDeadLetterRepository iElrDeadLetterRepository;
    @Mock
    private IEdxActivityLogRepository iEdxActivityLogRepository;
    @InjectMocks
    private ReportStatusService reportStatusServiceMock;
    private ReportStatusIdData reportStatusIdData;
    private NbsInterfaceModel nbsInterfaceModel;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportStatusIdData = new ReportStatusIdData();
        nbsInterfaceModel = new NbsInterfaceModel();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(iReportStatusRepositoryMock);
        Mockito.reset(nbsInterfaceRepositoryMock);
        Mockito.reset(iRawELRRepository);
        Mockito.reset(iValidatedELRRepository);
        Mockito.reset(iElrDeadLetterRepository);
        Mockito.reset(iEdxActivityLogRepository);
    }

    @Test
    void testGetMessageDetailStatus_RawExist_ValidateExist_ReportExist_NbsExist() {
        String id = "test";
        Integer nbsId = 123456;
        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setId(id);
        rawElrModel.setPayload("payload");
        rawElrModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        rawElrModel.setCreatedBy("admin");
        rawElrModel.setType("HL7");

        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("UTC"));

        List<ReportStatusIdData> rptStatusIdDataList = new ArrayList<>();
        ReportStatusIdData reportStatusIdModel = new ReportStatusIdData();
        reportStatusIdModel.setRawMessageId(id);
        reportStatusIdModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        reportStatusIdModel.setNbsInterfaceUid(nbsId);
        rptStatusIdDataList.add(reportStatusIdModel);

        NbsInterfaceModel nbsModel = new NbsInterfaceModel();
        nbsModel.setRecordStatusCd("Success");
        nbsModel.setPayload("payload");
        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawElrModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(rptStatusIdDataList);
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.of(nbsModel));
        when(edxActivityParentLogRepository.getParentEdxActivity(Long.valueOf(nbsId))).thenReturn(new EdxActivityLog());
        List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
        MessageStatus msgStatus = msgStatusList.get(0);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals(nbsId, msgStatus.getNbsInfo().getNbsInterfaceId());
        assertEquals("Success", msgStatus.getNbsInfo().getNbsInterfaceStatus());
        assertNotNull( msgStatus.getNbsInfo().getNbsCreatedOn());
        assertNull( msgStatus.getNbsInfo().getDltInfo());



    }

    @Test
    void testGetMessageDetailStatus_RawNotExist() {
        String id = "test";
        when(iRawELRRepository.findById(id)).thenReturn(Optional.empty());
        List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
        assertEquals(0, msgStatusList.size());
    }

    @Test
    void testGetMessageDetailStatus_RawExist_ValidateNotExist_DltExist() {
        String id = "test";
        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setId(id);
        rawElrModel.setPayload("payload");
        rawElrModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        rawElrModel.setCreatedBy("admin");
        rawElrModel.setType("HL7");
        ElrDeadLetterModel dltModel = new ElrDeadLetterModel();
        dltModel.setErrorMessageId(id);
        dltModel.setDltStatus("ERROR");
        dltModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        dltModel.setErrorMessageSource("origin");
        dltModel.setErrorStackTraceShort("short");

        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawElrModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.
                empty());

        when(iElrDeadLetterRepository.findById(id)).thenReturn(Optional.of(dltModel));

        List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
        MessageStatus msgStatus = msgStatusList.get(0);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("FAILED", msgStatus.getValidatedInfo().getValidatedPipeLineStatus());
        assertEquals(id, msgStatus.getRawInfo().getDltInfo().getDltId());
        assertEquals("ERROR", msgStatus.getRawInfo().getDltInfo().getDltStatus());
        assertEquals("origin", msgStatus.getRawInfo().getDltInfo().getDltOrigin());
        assertEquals("short", msgStatus.getRawInfo().getDltInfo().getDltShortTrace());
        assertNotNull(msgStatus.getRawInfo().getDltInfo().getDltCreatedOn());

    }

    @Test
    void testGetMessageDetailStatus_RawExist_ValidateNotExist_DltNotExist() {
        String id = "test";
        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setId(id);
        rawElrModel.setPayload("payload");
        rawElrModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        rawElrModel.setCreatedBy("admin");
        rawElrModel.setType("HL7");

        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawElrModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.
                empty());

        when(iElrDeadLetterRepository.findById(id)).thenReturn(Optional.empty());

        //var msgStatus = reportStatusServiceMock.getMessageStatus(id);
        List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
        MessageStatus msgStatus = msgStatusList.get(0);

        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("IN PROGRESS", msgStatus.getValidatedInfo().getValidatedPipeLineStatus());
        assertEquals("admin", msgStatus.getRawInfo().getRawCreatedBy());
        assertEquals("COMPLETED", msgStatus.getRawInfo().getRawPipeLineStatus());
        assertNotNull( msgStatus.getRawInfo().getRawCreatedOn());
    }


    @Test
    void testGetMessageDetailStatus_RawExist_ValidateExist_ReportExist_NbsNotExist() {
        String id = "test";
        Integer nbsId = 123456;
        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setId(id);
        rawElrModel.setPayload("payload");
        rawElrModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        rawElrModel.setCreatedBy("admin");
        rawElrModel.setType("HL7");

        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("UTC"));

        ReportStatusIdData reportStatusIdModel = new ReportStatusIdData();
        reportStatusIdModel.setRawMessageId(id);
        reportStatusIdModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        reportStatusIdModel.setNbsInterfaceUid(nbsId);
        List<ReportStatusIdData> rptStatusIdDataList = new ArrayList<>();
        rptStatusIdDataList.add(reportStatusIdModel);

        NbsInterfaceModel nbsModel = new NbsInterfaceModel();
        nbsModel.setRecordStatusCd("Success");
        nbsModel.setPayload("payload");
        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawElrModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(rptStatusIdDataList);
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.empty());

        List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
        MessageStatus msgStatus = msgStatusList.get(0);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals("IN PROGRESS", msgStatus.getNbsInfo().getNbsInterfacePipeLineStatus());
    }

    @Test
    void testGetMessageDetailStatus_RawExist_ValidateExist_ReportNotExist_DltExist() {
        String id = "test";
        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setId(id);
        rawElrModel.setPayload("payload");
        rawElrModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        rawElrModel.setCreatedBy("admin");
        rawElrModel.setType("HL7");
        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("UTC"));

        ElrDeadLetterModel dltModel = new ElrDeadLetterModel();
        dltModel.setErrorMessageId("validate-id");
        dltModel.setDltStatus("ERROR");
        dltModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        dltModel.setErrorMessageSource("origin");
        dltModel.setErrorStackTraceShort("short");

        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawElrModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(List.of());

        when(iElrDeadLetterRepository.findById("validate-id")).thenReturn(Optional.of(dltModel));
        List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
        MessageStatus msgStatus = msgStatusList.get(0);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals("FAILED", msgStatus.getNbsInfo().getNbsInterfacePipeLineStatus());
        assertNotNull( msgStatus.getValidatedInfo().getValidatedCreatedOn());


    }

    @Test
    void testGetMessageDetailStatus_RawExist_ValidateExist_ReportNotExist_DltNotExist() {
        String id = "test";
        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setId(id);
        rawElrModel.setPayload("payload");
        rawElrModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        rawElrModel.setCreatedBy("admin");
        rawElrModel.setType("HL7");
        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("UTC"));



        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawElrModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(List.of());

        when(iElrDeadLetterRepository.findById("validate-id")).thenReturn(Optional.
                empty());
        List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
        MessageStatus msgStatus = msgStatusList.get(0);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals("IN PROGRESS", msgStatus.getNbsInfo().getNbsInterfacePipeLineStatus());

    }


    @Test
    void testGetStatusForReportSuccessForValidData() {
        String id = "test_uuid_from_user";
        reportStatusIdData.setNbsInterfaceUid(1234);
        nbsInterfaceModel.setRecordStatusCd("Success");

        List<ReportStatusIdData> rptStatusIdDataList = new ArrayList<>();
        rptStatusIdDataList.add(reportStatusIdData);

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(rptStatusIdDataList);
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.of(nbsInterfaceModel));

        List<String> statusList = reportStatusServiceMock.getStatusForReport(id);
        //Actual value - 'NBS Inerface Id:1234 Status:Success'
        assertTrue(statusList.get(0).endsWith("Success"));
    }

    @Test
    void testGetStatusForReportEmptyReportIdData() {
        String id = "test_uuid_from_user_does_not_exist";

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(List.of());

        List<String> statusList = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Provided UUID is not present in the database. Either provided an invalid UUID or the injected message failed validation.", statusList.get(0));
    }

    @Test
    void testGetStatusForReportEmptyNbsInterfaceData() {
        String id = "test_uuid_from_user";

        List<ReportStatusIdData> rptStatusIdDataList = new ArrayList<>();
        rptStatusIdDataList.add(reportStatusIdData);

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(rptStatusIdDataList);
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.empty());

        List<String> statusList = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Couldn't find status for the requested UUID.", statusList.get(0));
    }

    @Test
    void testDummyGetStatusForReportSuccessModelCoverage() {
        String id = "test_uuid_from_user";
        reportStatusIdData.setNbsInterfaceUid(1234);
        nbsInterfaceModel.setRecordStatusCd("Success");

        // These setters are added to increase the line coverage for model class
        reportStatusIdData.setId("test_uuid");
        reportStatusIdData.setRawMessageId(id);
        reportStatusIdData.setCreatedBy("junit_test");
        reportStatusIdData.setUpdatedBy("junit_test");
        List<ReportStatusIdData> rptStatusIdDataList = new ArrayList<>();
        rptStatusIdDataList.add(reportStatusIdData);

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(rptStatusIdDataList);
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.of(nbsInterfaceModel));

        List<String> statusList = reportStatusServiceMock.getStatusForReport(id);
        assertTrue(statusList.get(0).endsWith("Success"));

        // The following asserts are added to increase the line coverage for model class
        assertEquals("test_uuid", reportStatusIdData.getId());
        assertEquals(id, reportStatusIdData.getRawMessageId());
        assertEquals("junit_test", reportStatusIdData.getCreatedBy());
        assertEquals("junit_test", reportStatusIdData.getUpdatedBy());
    }
    @Test
    void testGetMessageDetailStatus_RawExist_ValidateExist_ReportExist_NbsExist_OdseExist() {
        String id = "123";
        Integer nbsId = 123456;
        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setId(id);
        rawElrModel.setPayload("payload");
        rawElrModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        rawElrModel.setCreatedBy("admin");
        rawElrModel.setType("HL7");

        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("UTC"));

        ReportStatusIdData reportStatusIdModel = new ReportStatusIdData();
        reportStatusIdModel.setRawMessageId(id);
        reportStatusIdModel.setCreatedOn(getCurrentTimeStamp("UTC"));
        reportStatusIdModel.setNbsInterfaceUid(nbsId);
        List<ReportStatusIdData> rptStatusIdDataList = new ArrayList<>();
        rptStatusIdDataList.add(reportStatusIdModel);

        List<EdxActivityDetailLog> edxActivityLogList=new ArrayList();
        EdxActivityDetailLog edxActivityLogModelProjection=mock(EdxActivityDetailLog.class);
        when(edxActivityLogModelProjection.getLogComment()).thenReturn("Test activity log");
        when(edxActivityLogModelProjection.getLogType()).thenReturn("Test log type");
        when(edxActivityLogModelProjection.getRecordId()).thenReturn("Test Record Id");
        when(edxActivityLogModelProjection.getRecordType()).thenReturn("Test Record Type");
       // when(edxActivityLogModelProjection.getRecordStatusTime()).thenReturn(new Timestamp(System.currentTimeMillis()));

        edxActivityLogList.add(edxActivityLogModelProjection);

        NbsInterfaceModel nbsModel = new NbsInterfaceModel();
        nbsModel.setRecordStatusCd("Failure");
        nbsModel.setPayload("payload");

        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawElrModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(rptStatusIdDataList);
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.of(nbsModel));
        when (iEdxActivityLogRepository.getEdxActivityLogDetailsBySourceId(Long.valueOf(nbsId))).thenReturn(edxActivityLogList);
        when(edxActivityParentLogRepository.getParentEdxActivity(Long.valueOf(nbsId))).thenReturn(new EdxActivityLog());

        List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
        MessageStatus msgStatus = msgStatusList.get(0);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals(nbsId, msgStatus.getNbsInfo().getNbsInterfaceId());
        assertNotNull( msgStatus.getNbsInfo().getNbsCreatedOn());
        assertNull( msgStatus.getNbsInfo().getDltInfo());
        assertEquals("Test log type",msgStatus.getEdxLogStatus().getEdxActivityDetailLogList().get(0).getLogType());
    }

}