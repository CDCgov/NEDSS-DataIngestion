package gov.cdc.dataprocessing.controller;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class ControllerTest {
    @Mock
    private IManagerService managerServiceMock;

    @InjectMocks
    private Controller controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testGetDataPipelineStatusHealth() {
        ResponseEntity<String> response = controller.getDataPipelineStatusHealth();

        assertNotNull(response);
        assertEquals("Data Processing Service Status OK", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }
}
