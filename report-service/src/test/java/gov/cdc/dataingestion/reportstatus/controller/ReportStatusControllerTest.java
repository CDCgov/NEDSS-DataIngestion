package gov.cdc.dataingestion.reportstatus.controller;

import gov.cdc.dataingestion.reportstatus.service.ReportStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    void testGetReportStatusSuccess() throws IOException {
        String id = "test_uuid_from_user";
        String status = "Success";

        when(reportStatusServiceMock.getStatusForReport(id)).thenReturn(status);

        String jsonResponse = reportStatusController.getReportStatus(id);

        verify(reportStatusServiceMock, times(1)).getStatusForReport(id);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responeMap = mapper.readValue(jsonResponse, Map.class);

        assertEquals(id, responeMap.get("id"));
        assertEquals(status, responeMap.get("status"));
    }

    @Test
    void testGetReportStatusNullIdProvided() throws IOException {
        String id = null;

        try {
            reportStatusController.getReportStatus(id);
        }
        catch (IllegalArgumentException e) {
            assertEquals("Invalid 'id' parameter provided.", e.getMessage());
        }

        verify(reportStatusServiceMock, never()).getStatusForReport(id);
    }

    @Test
    void testGetReportStatusBlankIdProvided() throws IOException {
        String id = " ";

        try {
            reportStatusController.getReportStatus(id);
        }
        catch (IllegalArgumentException e) {
            assertEquals("Invalid 'id' parameter provided.", e.getMessage());
        }

        verify(reportStatusServiceMock, never()).getStatusForReport(id);
    }

    @Test
    void testGetReportStatusBlankResponseFromDITable() throws IOException {
        String id = "test_uuid_from_user_not_in_database";
        String status = "Provided UUID is not present in the database.";

        when(reportStatusServiceMock.getStatusForReport(id)).thenReturn(status);

        String jsonResponse = reportStatusController.getReportStatus(id);

        verify(reportStatusServiceMock, times(1)).getStatusForReport(id);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responeMap = mapper.readValue(jsonResponse, Map.class);

        assertEquals(id, responeMap.get("id"));
        assertEquals(status, responeMap.get("error_message"));
    }

    @Test
    void testGetReportStatusBlankResponseFromNbsTable() throws IOException {
        String id = "test_uuid_from_user_not_in_database";
        String status = "Couldn't find status for the requested ID.";

        when(reportStatusServiceMock.getStatusForReport(id)).thenReturn(status);

        String jsonResponse = reportStatusController.getReportStatus(id);

        verify(reportStatusServiceMock, times(1)).getStatusForReport(id);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responeMap = mapper.readValue(jsonResponse, Map.class);

        assertEquals(id, responeMap.get("id"));
        assertEquals(status, responeMap.get("error_message"));
    }
}