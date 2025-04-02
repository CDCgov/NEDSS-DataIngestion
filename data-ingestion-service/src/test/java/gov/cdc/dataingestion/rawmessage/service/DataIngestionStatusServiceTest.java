package gov.cdc.dataingestion.rawmessage.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataIngestionStatusServiceTest {

    private final DataIngestionStatusService service = new DataIngestionStatusService();

    @Test
    void testGetHealthStatus() {
        ResponseEntity<String> response = service.getHealthStatus();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Data Ingestion Service Status OK", response.getBody());
    }
}
