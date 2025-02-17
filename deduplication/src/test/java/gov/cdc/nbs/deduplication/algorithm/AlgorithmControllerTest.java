package gov.cdc.nbs.deduplication.algorithm;

import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
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
        // Create a List of Pass objects (dummy example, replace with actual Pass objects)
        List<Pass> passes = List.of(new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of()));

        // Creating MatchingConfigRequest with the required parameters
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",      // String parameter (label)
                "Test Description", // String parameter (description)
                true,               // boolean parameter (isDefault)
                true,               // boolean parameter (includeMultipleMatches)
                passes              // List<Pass> parameter (passes)
        );

        // Simulating the service method call
        doNothing().when(algorithmService).configureMatching(request);

        // Call the controller method
        algorithmController.configureMatching(request);

        // Verify that the service method was called once with the request
        verify(algorithmService, times(1)).configureMatching(request);
    }

    @Test
    void testGetMatchingConfiguration() {
        // Create a List of Pass objects (dummy example)
        List<Pass> passes = List.of(new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of()));

        // Simulate the service method call
        MatchingConfigRequest expectedConfig = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                passes
        );

        when(algorithmService.getMatchingConfiguration()).thenReturn(expectedConfig);

        // Call the controller method
        MatchingConfigRequest actualConfig = algorithmController.getMatchingConfiguration();

        // Verify the returned configuration matches
        assertEquals(expectedConfig, actualConfig);
    }

    @Test
    void testUpdateAlgorithm() {
        // Create a List of Pass objects (dummy example)
        List<Pass> passes = List.of(new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of()));

        // Creating MatchingConfigRequest with the required parameters
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                passes
        );

        // Simulate the service method call
        doNothing().when(algorithmService).updateDibbsConfigurations(request);

        // Call the controller method
        algorithmController.updateAlgorithm(request);

        // Verify that the service method was called once with the request
        verify(algorithmService, times(1)).updateDibbsConfigurations(request);
    }
}
