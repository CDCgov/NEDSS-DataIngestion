package gov.cdc.dataingestion.reportstatus.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * 1118 - require constructor complaint 125 - comment complaint 6126 - String block complaint 1135 -
 * todos complaint
 */
@SuppressWarnings({"java:S1118", "java:S125", "java:S6126", "java:S1135"})
@ActiveProfiles("test")
class ReportStatusControllerTest {
  @Mock private ReportStatusService reportStatusServiceMock;

  private ReportStatusController reportStatusController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    reportStatusController = new ReportStatusController(reportStatusServiceMock);
  }

  @Test
  void testGetMessageDetailStatus() {
    String rawId = "test";
    List<MessageStatus> messageStatusList = new ArrayList<>();

    MessageStatus status = new MessageStatus();
    status.getRawInfo().setRawMessageId(rawId);
    messageStatusList.add(status);
    when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(messageStatusList);

    ResponseEntity<List<MessageStatus>> jsonResponse =
        reportStatusController.getMessageStatus(rawId);
    verify(reportStatusServiceMock, times(1)).getMessageStatus(rawId);

    assertEquals(rawId, jsonResponse.getBody().get(0).getRawInfo().getRawMessageId());
  }

  @Test
  void testGetReportStatus() {
    UUID id = UUID.randomUUID();
    reportStatusController.getReportStatus(id);

    verify(reportStatusServiceMock, times(1)).getStatusForReport(id);
  }
}
