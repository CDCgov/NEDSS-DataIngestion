package gov.cdc.dataprocessing.controller;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LocalUidControllerTest {

    @Mock
    private IOdseIdGeneratorService odseIdGeneratorService;

    @InjectMocks
    private LocalUidController localUidController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidClassName() throws DataProcessingException {
        LocalUidGenerator mockLocalUidGenerator = new LocalUidGenerator();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.OBSERVATION)).thenReturn(mockLocalUidGenerator);

        ResponseEntity<LocalUidGenerator> response = localUidController.test("OBSERVATION");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockLocalUidGenerator, response.getBody());
    }

    @Test
    void testInvalidClassName() {
        assertThrows(IllegalArgumentException.class, () -> {
            localUidController.test("INVALID_CLASS");
        });
    }

    @Test
    void testServiceThrowsException() throws DataProcessingException {
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.OBSERVATION)).thenThrow(new DataProcessingException("Error"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            localUidController.test("CLASS_A");
        });

        assertNotNull( exception.getMessage());
    }
}
