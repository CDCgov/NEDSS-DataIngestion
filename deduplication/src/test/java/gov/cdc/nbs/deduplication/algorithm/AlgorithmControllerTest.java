package gov.cdc.nbs.deduplication.algorithm;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration.BelongingnessRatio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AlgorithmControllerTest {

    @Mock
    private AlgorithmService algorithmService;

    @InjectMocks
    private AlgorithmController algorithmController;

    @Test
    void testConfigureMatching() {
        // Create a List of Pass objects (dummy example, replace with actual Pass
        // objects)
        List<Pass> passes = List.of(new Pass("TestPass", "Description", List.of(), List.of()));

        // Creating MatchingConfigRequest with the required parameters
        MatchingConfiguration request = new MatchingConfiguration(
                "Test Label", // String parameter (label)
                "Test Description", // String parameter (description)
                true, // boolean parameter (isDefault)
                passes, // List<Pass> parameter (passes)
                new BelongingnessRatio(0.0, 1.0));

        // Simulating the service method call
        doNothing().when(algorithmService).save(request);

        // Call the controller method
        algorithmController.save(request);

        // Verify that the service method was called once with the request
        verify(algorithmService, times(1)).save(request);
    }

    @Test
    void testGetMatchingConfiguration() {
        // Create a List of Pass objects (dummy example)
        List<Pass> passes = List.of(new Pass("TestPass", "Description", List.of(), List.of()));

        // Simulate the service method call
        MatchingConfiguration expectedConfig = new MatchingConfiguration(
                "Test Label",
                "Test Description",
                true,
                passes,
                new BelongingnessRatio(0.0, 1.0));

        when(algorithmService.getMatchingConfiguration()).thenReturn(expectedConfig);

        // Call the controller method
        MatchingConfiguration actualConfig = algorithmController.getMatchingConfiguration();

        // Verify the returned configuration matches
        assertEquals(expectedConfig, actualConfig);
    }

}
