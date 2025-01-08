package gov.cdc.dataingestion.reportstatus.service;

import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.odse.repository.IEdxActivityLogRepository;
import gov.cdc.dataingestion.odse.repository.IEdxActivityParentLogRepository;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLogModelView;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
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

import java.sql.Timestamp;
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
    private IRawELRRepository iRawELRRepository;
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
        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setId(id);
        rawERLModel.setPayload("payload");
        rawERLModel.setCreatedOn(getCurrentTimeStamp("utc"));
        rawERLModel.setCreatedBy("admin");
        rawERLModel.setType("HL7");

        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("utc"));

        ReportStatusIdData reportStatusIdModel = new ReportStatusIdData();
        reportStatusIdModel.setRawMessageId(id);
        reportStatusIdModel.setCreatedOn(getCurrentTimeStamp("utc"));
        reportStatusIdModel.setNbsInterfaceUid(nbsId);

        NbsInterfaceModel nbsModel = new NbsInterfaceModel();
        nbsModel.setRecordStatusCd("Success");
        nbsModel.setPayload("payload");
        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawERLModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdModel));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.of(nbsModel));
        when(edxActivityParentLogRepository.getParentEdxActivity(Long.valueOf(nbsId))).thenReturn(new EdxActivityLog());
        var msgStatus = reportStatusServiceMock.getMessageStatus(id);
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
        var msgStatus = reportStatusServiceMock.getMessageStatus(id);
        assertNotNull(msgStatus);
        assertNotNull(msgStatus.getRawInfo());
        assertNotNull(msgStatus.getValidatedInfo());
        assertNotNull(msgStatus.getNbsInfo());
    }

    @Test
    void testGetMessageDetailStatus_RawExist_ValidateNotExist_DltExist() {
        String id = "test";
        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setId(id);
        rawERLModel.setPayload("payload");
        rawERLModel.setCreatedOn(getCurrentTimeStamp("utc"));
        rawERLModel.setCreatedBy("admin");
        rawERLModel.setType("HL7");
        ElrDeadLetterModel dltModel = new ElrDeadLetterModel();
        dltModel.setErrorMessageId(id);
        dltModel.setDltStatus("ERROR");
        dltModel.setCreatedOn(getCurrentTimeStamp("utc"));
        dltModel.setErrorMessageSource("origin");
        dltModel.setErrorStackTraceShort("short");

        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawERLModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.
                empty());

        when(iElrDeadLetterRepository.findById(id)).thenReturn(Optional.of(dltModel));

        var msgStatus = reportStatusServiceMock.getMessageStatus(id);
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
        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setId(id);
        rawERLModel.setPayload("payload");
        rawERLModel.setCreatedOn(getCurrentTimeStamp("utc"));
        rawERLModel.setCreatedBy("admin");
        rawERLModel.setType("HL7");

        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawERLModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.
                empty());

        when(iElrDeadLetterRepository.findById(id)).thenReturn(Optional.empty());

        var msgStatus = reportStatusServiceMock.getMessageStatus(id);
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
        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setId(id);
        rawERLModel.setPayload("payload");
        rawERLModel.setCreatedOn(getCurrentTimeStamp("utc"));
        rawERLModel.setCreatedBy("admin");
        rawERLModel.setType("HL7");

        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("utc"));

        ReportStatusIdData reportStatusIdModel = new ReportStatusIdData();
        reportStatusIdModel.setRawMessageId(id);
        reportStatusIdModel.setCreatedOn(getCurrentTimeStamp("utc"));
        reportStatusIdModel.setNbsInterfaceUid(nbsId);

        NbsInterfaceModel nbsModel = new NbsInterfaceModel();
        nbsModel.setRecordStatusCd("Success");
        nbsModel.setPayload("payload");
        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawERLModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdModel));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.empty());

        var msgStatus = reportStatusServiceMock.getMessageStatus(id);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals("IN PROGRESS", msgStatus.getNbsInfo().getNbsInterfacePipeLineStatus());
    }

    @Test
    void testGetMessageDetailStatus_RawExist_ValidateExist_ReportNotExist_DltExist() {
        String id = "test";
        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setId(id);
        rawERLModel.setPayload("payload");
        rawERLModel.setCreatedOn(getCurrentTimeStamp("utc"));
        rawERLModel.setCreatedBy("admin");
        rawERLModel.setType("HL7");
        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("utc"));

        ElrDeadLetterModel dltModel = new ElrDeadLetterModel();
        dltModel.setErrorMessageId("validate-id");
        dltModel.setDltStatus("ERROR");
        dltModel.setCreatedOn(getCurrentTimeStamp("utc"));
        dltModel.setErrorMessageSource("origin");
        dltModel.setErrorStackTraceShort("short");

        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawERLModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.
                empty());

        when(iElrDeadLetterRepository.findById("validate-id")).thenReturn(Optional.of(dltModel));
        var msgStatus = reportStatusServiceMock.getMessageStatus(id);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals("FAILED", msgStatus.getNbsInfo().getNbsInterfacePipeLineStatus());
        assertNotNull( msgStatus.getValidatedInfo().getValidatedCreatedOn());


    }

    @Test
    void testGetMessageDetailStatus_RawExist_ValidateExist_ReportNotExist_DltNotExist() {
        String id = "test";
        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setId(id);
        rawERLModel.setPayload("payload");
        rawERLModel.setCreatedOn(getCurrentTimeStamp("utc"));
        rawERLModel.setCreatedBy("admin");
        rawERLModel.setType("HL7");
        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("utc"));



        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawERLModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.
                empty());

        when(iElrDeadLetterRepository.findById("validate-id")).thenReturn(Optional.
                empty());
        var msgStatus = reportStatusServiceMock.getMessageStatus(id);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals("IN PROGRESS", msgStatus.getNbsInfo().getNbsInterfacePipeLineStatus());

    }


    @Test
    void testGetStatusForReportSuccessForValidData() {
        String id = "test_uuid_from_user";
        reportStatusIdData.setNbsInterfaceUid(1234);
        nbsInterfaceModel.setRecordStatusCd("Success");

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdData));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.of(nbsInterfaceModel));

        String status = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Success", status);
    }

    @Test
    void testGetStatusForReportEmptyReportIdData() {
        String id = "test_uuid_from_user_does_not_exist";

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.empty());

        String status = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Provided UUID is not present in the database. Either provided an invalid UUID or the injected message failed validation.", status);
    }

    @Test
    void testGetStatusForReportEmptyNbsInterfaceData() {
        String id = "test_uuid_from_user";

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdData));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.empty());

        String status = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Couldn't find status for the requested UUID.", status);
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

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdData));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.of(nbsInterfaceModel));

        String status = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Success", status);

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
        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setId(id);
        rawERLModel.setPayload("payload");
        rawERLModel.setCreatedOn(getCurrentTimeStamp("utc"));
        rawERLModel.setCreatedBy("admin");
        rawERLModel.setType("HL7");

        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setId("validate-id");
        validatedELRModel.setRawMessage("payload");
        validatedELRModel.setCreatedOn(getCurrentTimeStamp("utc"));

        ReportStatusIdData reportStatusIdModel = new ReportStatusIdData();
        reportStatusIdModel.setRawMessageId(id);
        reportStatusIdModel.setCreatedOn(getCurrentTimeStamp("utc"));
        reportStatusIdModel.setNbsInterfaceUid(nbsId);

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

        when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawERLModel));
        when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdModel));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.of(nbsModel));
        when (iEdxActivityLogRepository.getEdxActivityLogDetailsBySourceId(Long.valueOf(nbsId))).thenReturn(edxActivityLogList);
        when(edxActivityParentLogRepository.getParentEdxActivity(Long.valueOf(nbsId))).thenReturn(new EdxActivityLog());

        var msgStatus = reportStatusServiceMock.getMessageStatus(id);
        assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
        assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
        assertEquals(nbsId, msgStatus.getNbsInfo().getNbsInterfaceId());
        assertNotNull( msgStatus.getNbsInfo().getNbsCreatedOn());
        assertNull( msgStatus.getNbsInfo().getDltInfo());
        assertEquals("Test log type",msgStatus.getEdxLogStatus().getEdxActivityDetailLogList().get(0).getLogType());
    }

}