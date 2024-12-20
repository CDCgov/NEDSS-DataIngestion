package gov.cdc.dataingestion.reportstatus.controller;

import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
@ActiveProfiles("test")
class ReportStatusControllerTest {
    @Mock
    private ReportStatusService reportStatusServiceMock;

    private ReportStatusController reportStatusController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportStatusController = new ReportStatusController(reportStatusServiceMock);
    }

    @Test
    void testGetMessageDetailStatus() {
        String rawId = "test";
        MessageStatus status = new MessageStatus();
        status.getRawInfo().setRawMessageId(rawId);
        when(reportStatusServiceMock.getMessageStatus(rawId)).thenReturn(
                status
        );

        ResponseEntity<List<MessageStatus>> jsonResponse = reportStatusController.getMessageStatus(rawId);
        verify(reportStatusServiceMock, times(1)).getMessageStatus(rawId);

        assertEquals(rawId, jsonResponse.getBody().get(0).getRawInfo().getRawMessageId());
    }

    @Test
    void testGetReportStatusSuccess() throws IOException {
        String id = "11111111-2222-3333-4444-555555555555";
        String status = "Success";

        when(reportStatusServiceMock.getStatusForReport(id)).thenReturn(status);

        ResponseEntity<String> jsonResponse = reportStatusController.getReportStatus(id);

        verify(reportStatusServiceMock, times(1)).getStatusForReport(id);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responeMap = mapper.readValue(jsonResponse.getBody(), Map.class);

        assertEquals(id, responeMap.get("id"));
        assertEquals(status, responeMap.get("status"));
    }

    @Test
    @SuppressWarnings("java:S5976")
    void testGetReportStatusNullIdProvided() throws IOException {
        String id = null;

        try {
            reportStatusController.getReportStatus(id);
        }
        catch (IllegalArgumentException e) {
            assertEquals("Invalid 'UUID' parameter provided.", e.getMessage());
        }

        verify(reportStatusServiceMock, never()).getStatusForReport(id);
    }

    @Test
    @SuppressWarnings("java:S5976")
    void testGetReportStatusBlankIdProvided() throws IOException {
        String id = "";

        try {
            reportStatusController.getReportStatus(id);
        }
        catch (IllegalArgumentException e) {
            assertEquals("Invalid 'UUID' parameter provided.", e.getMessage());
        }

        verify(reportStatusServiceMock, never()).getStatusForReport(id);
    }

    @Test
    @SuppressWarnings("java:S5976")
    void testGetReportStatusInvalidIdProvided() throws IOException {
        String id = "test_some_invalid_uuid";

        try {
            reportStatusController.getReportStatus(id);
        }
        catch (IllegalArgumentException e) {
            assertEquals("Invalid 'UUID' parameter provided.", e.getMessage());
        }

        verify(reportStatusServiceMock, never()).getStatusForReport(id);
    }

    @Test
    void testGetReportStatusBlankResponseFromDITable() throws IOException {
        String id = "11111111-3333-2222-4444-555555555555";
        String status = "Provided UUID is not present in the database. Either provided an invalid UUID or the injected message failed validation.";

        when(reportStatusServiceMock.getStatusForReport(id)).thenReturn(status);

        ResponseEntity<String> jsonResponse = reportStatusController.getReportStatus(id);

        verify(reportStatusServiceMock, times(1)).getStatusForReport(id);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responeMap = mapper.readValue(jsonResponse.getBody(), Map.class);

        System.out.println("responseMap is..." + responeMap);
        assertEquals(id, responeMap.get("id"));
        assertEquals(status, responeMap.get("error_message"));
    }

    @Test
    void testGetReportStatusBlankResponseFromNbsTable() throws IOException {
        String id = "11111111-4444-3333-2222-555555555555";
        String status = "Couldn't find status for the requested ID.";

        when(reportStatusServiceMock.getStatusForReport(id)).thenReturn(status);

        ResponseEntity<String> jsonResponse = reportStatusController.getReportStatus(id);

        verify(reportStatusServiceMock, times(1)).getStatusForReport(id);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responeMap = mapper.readValue(jsonResponse.getBody(), Map.class);

        assertEquals(id, responeMap.get("id"));
        assertEquals(status, responeMap.get("error_message"));
    }
}