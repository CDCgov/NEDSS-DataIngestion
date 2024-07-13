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
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LocalUidControllerTest {

    @Mock
    private IOdseIdGeneratorService odseIdGeneratorServiceMock;

    @InjectMocks
    private LocalUidController localUidController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLocalUidAndUpdateSeed_Success() throws DataProcessingException {
        LocalUidGenerator expectedResult = new LocalUidGenerator();
        when(odseIdGeneratorServiceMock.getLocalIdAndUpdateSeed(any(LocalIdClass.class)))
                .thenReturn(expectedResult);

        ResponseEntity<LocalUidGenerator> response = localUidController.test("DEDUPLICATION_LOG");

        assertNotNull(response);
        assertEquals(ResponseEntity.ok(expectedResult), response);
        assertEquals(expectedResult, response.getBody());
    }

    @Test
    void testGetLocalUidAndUpdateSeed_Exception() throws DataProcessingException {
        when(odseIdGeneratorServiceMock.getLocalIdAndUpdateSeed(any(LocalIdClass.class)))
                .thenThrow(new DataProcessingException("Test Exception"));

        assertThrows(IllegalArgumentException.class, () -> {
            localUidController.test("YOUR_ENUM_VALUE");
        });
    }
}