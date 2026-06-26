package gov.cdc.dataingestion.reportstatus.service;

import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import gov.cdc.dataingestion.reportstatus.exception.ElrNotFoundException;
import gov.cdc.dataingestion.reportstatus.model.ElrStatus;
import gov.cdc.dataingestion.reportstatus.model.ElrStatus.Detail;
import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.model.ReportStatusIdData;
import gov.cdc.dataingestion.reportstatus.repository.IReportStatusRepository;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * 1118 - require constructor complaint 125 - comment complaint 6126 - String block complaint 1135 -
 * todos complaint
 */
@SuppressWarnings({"java:S1118", "java:S125", "java:S6126", "java:S1135"})
class ReportStatusServiceTest {
  @Mock private IReportStatusRepository iReportStatusRepositoryMock;
  @Mock private IEdxActivityParentLogRepository edxActivityParentLogRepository;
  @Mock private NbsInterfaceRepository nbsInterfaceRepositoryMock;
  @Mock private IRawElrRepository iRawELRRepository;
  @Mock private IValidatedELRRepository iValidatedELRRepository;
  @Mock private IElrDeadLetterRepository iElrDeadLetterRepository;
  @Mock private IEdxActivityLogRepository iEdxActivityLogRepository;
  @InjectMocks private ReportStatusService reportStatusServiceMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
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
    when(edxActivityParentLogRepository.getParentEdxActivity(Long.valueOf(nbsId)))
        .thenReturn(new EdxActivityLog());
    List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
    MessageStatus msgStatus = msgStatusList.get(0);
    assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
    assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
    assertEquals(nbsId, msgStatus.getNbsInfo().getNbsInterfaceId());
    assertEquals("Success", msgStatus.getNbsInfo().getNbsInterfaceStatus());
    assertNotNull(msgStatus.getNbsInfo().getNbsCreatedOn());
    assertNull(msgStatus.getNbsInfo().getDltInfo());
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
    when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.empty());

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
    when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.empty());

    when(iElrDeadLetterRepository.findById(id)).thenReturn(Optional.empty());

    // var msgStatus = reportStatusServiceMock.getMessageStatus(id);
    List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
    MessageStatus msgStatus = msgStatusList.get(0);

    assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
    assertEquals("IN PROGRESS", msgStatus.getValidatedInfo().getValidatedPipeLineStatus());
    assertEquals("admin", msgStatus.getRawInfo().getRawCreatedBy());
    assertEquals("COMPLETED", msgStatus.getRawInfo().getRawPipeLineStatus());
    assertNotNull(msgStatus.getRawInfo().getRawCreatedOn());
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
    assertNotNull(msgStatus.getValidatedInfo().getValidatedCreatedOn());
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

    when(iElrDeadLetterRepository.findById("validate-id")).thenReturn(Optional.empty());
    List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
    MessageStatus msgStatus = msgStatusList.get(0);
    assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
    assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
    assertEquals("IN PROGRESS", msgStatus.getNbsInfo().getNbsInterfacePipeLineStatus());
  }

  @Test
  void test_no_records_found() {
    // given an id that returns no records
    UUID id = UUID.randomUUID();
    when(iReportStatusRepositoryMock.findByRawMessageId(id.toString())).thenReturn(List.of());

    // when get report status is called
    ElrNotFoundException ex =
        assertThrows(
            ElrNotFoundException.class, () -> reportStatusServiceMock.getStatusForReport(id));

    // then an ElrNotFoundException is thrown
    assertThat(ex.getMessage())
        .isEqualTo(
            "Provided UUID is not present in the database. The provided UUID is either invalid or the message failed validation.");
  }

  @Test
  void test_no_nbs_interface_entry_exists() {
    // given an id that returns an entry in the elr_record_status_id table but no entries in the
    // nbs_interface table exist
    UUID id = UUID.randomUUID();
    // elr_record_status_id table setup
    List<ReportStatusIdData> recordStatusIdTableEntries = new ArrayList<>();
    ReportStatusIdData recordStatusIdEntry = new ReportStatusIdData();
    recordStatusIdEntry.setRawMessageId(id.toString());
    recordStatusIdEntry.setNbsInterfaceUid(123);
    recordStatusIdTableEntries.add(recordStatusIdEntry);
    when(iReportStatusRepositoryMock.findByRawMessageId(id.toString()))
        .thenReturn(recordStatusIdTableEntries);

    // empty nbs_interface table
    when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(123)).thenReturn(Optional.empty());

    // when get report status is called
    ElrStatus status = reportStatusServiceMock.getStatusForReport(id);

    // then the overall status reflects the missing entry
    assertThat(status.status()).isEqualTo("Failed to find an entry in the nbs_interface table");
    assertThat(status.id()).isEqualTo(id);
    assertThat(status.details()).hasSize(1);
    // and the detailed status reflects the missing entry
    Detail detail = status.details().get(0);
    assertThat(detail.messageId()).isEqualTo(123);
    assertThat(detail.status()).isEqualTo("Failed to find an entry in the nbs_interface table");
  }

  private static Stream<Arguments> provideStatus() {
    // A CSV of status' present in the nbs_interface table and the expected derived status
    return Stream.of(
        Arguments.of("ODD_VALUE,RTI_SUCCESS,RTI_QUEUED", "RTI_QUEUED"),
        Arguments.of("RTI_SUCCESS,RTI_QUEUED", "RTI_QUEUED"),
        Arguments.of("RTI_QUEUED,QUEUED", "QUEUED"),
        Arguments.of("QUEUED,RTI_PENDING", "RTI_PENDING"),
        Arguments.of("RTI_PENDING,RTI_FAILURE_STEP_1", "RTI_FAILURE_STEP_1"),
        Arguments.of("RTI_FAILURE_STEP_1,RTI_FAILURE_STEP_2", "RTI_FAILURE_STEP_2"),
        Arguments.of("RTI_FAILURE_STEP_2,RTI_FAILURE_STEP_3", "RTI_FAILURE_STEP_3"),
        Arguments.of("ODD_VALUE", "ODD_VALUE"));
  }

  @ParameterizedTest
  @MethodSource("provideStatus")
  void test_overall_derived_status(String statusCsv, String expectedStatus) {
    // given an id that returns multiple entries in the elr_record_status_id with valid
    // nbs_interface entries with the specified status
    UUID id = UUID.randomUUID();
    List<ReportStatusIdData> recordStatusIdTableEntries = new ArrayList<>();
    int interfaceId = 1;
    for (String status : statusCsv.split(",")) {
      // elr_record_status_entry
      ReportStatusIdData recordStatusIdEntry = new ReportStatusIdData();
      recordStatusIdEntry.setRawMessageId(id.toString());
      recordStatusIdEntry.setNbsInterfaceUid(interfaceId);
      recordStatusIdTableEntries.add(recordStatusIdEntry);
      recordStatusIdTableEntries.add(recordStatusIdEntry);

      // associated nbs_interface entry
      NbsInterfaceModel nbsModel = new NbsInterfaceModel();
      nbsModel.setNbsInterfaceUid(interfaceId);
      nbsModel.setRecordStatusCd(status);
      when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(interfaceId))
          .thenReturn(Optional.of(nbsModel));

      interfaceId++;
    }
    when(iReportStatusRepositoryMock.findByRawMessageId(id.toString()))
        .thenReturn(recordStatusIdTableEntries);

    // when get report status is called
    ElrStatus status = reportStatusServiceMock.getStatusForReport(id);

    // then the correct overall status is derived
    assertThat(status.status()).isEqualTo(expectedStatus);
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

    List<EdxActivityDetailLog> edxActivityLogList = new ArrayList<>();
    EdxActivityDetailLog edxActivityLogModelProjection = mock(EdxActivityDetailLog.class);
    when(edxActivityLogModelProjection.getLogComment()).thenReturn("Test activity log");
    when(edxActivityLogModelProjection.getLogType()).thenReturn("Test log type");
    when(edxActivityLogModelProjection.getRecordId()).thenReturn("Test Record Id");
    when(edxActivityLogModelProjection.getRecordType()).thenReturn("Test Record Type");
    // when(edxActivityLogModelProjection.getRecordStatusTime()).thenReturn(new
    // Timestamp(System.currentTimeMillis()));

    edxActivityLogList.add(edxActivityLogModelProjection);

    NbsInterfaceModel nbsModel = new NbsInterfaceModel();
    nbsModel.setRecordStatusCd("Failure");
    nbsModel.setPayload("payload");

    when(iRawELRRepository.findById(id)).thenReturn(Optional.of(rawElrModel));
    when(iValidatedELRRepository.findByRawId(id)).thenReturn(Optional.of(validatedELRModel));
    when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(rptStatusIdDataList);
    when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.of(nbsModel));
    when(iEdxActivityLogRepository.getEdxActivityLogDetailsBySourceId(Long.valueOf(nbsId)))
        .thenReturn(edxActivityLogList);
    when(edxActivityParentLogRepository.getParentEdxActivity(Long.valueOf(nbsId)))
        .thenReturn(new EdxActivityLog());

    List<MessageStatus> msgStatusList = reportStatusServiceMock.getMessageStatus(id);
    MessageStatus msgStatus = msgStatusList.get(0);
    assertEquals(id, msgStatus.getRawInfo().getRawMessageId());
    assertEquals("validate-id", msgStatus.getValidatedInfo().getValidatedMessageId());
    assertEquals(nbsId, msgStatus.getNbsInfo().getNbsInterfaceId());
    assertNotNull(msgStatus.getNbsInfo().getNbsCreatedOn());
    assertNull(msgStatus.getNbsInfo().getDltInfo());
    assertEquals(
        "Test log type",
        msgStatus.getEdxLogStatus().getEdxActivityDetailLogList().get(0).getLogType());
  }
}
