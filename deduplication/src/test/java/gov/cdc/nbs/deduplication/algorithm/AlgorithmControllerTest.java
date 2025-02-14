package gov.cdc.nbs.deduplication.algorithm;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

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
        Map<String, Boolean> blockingCriteria = Map.of(
                "FIRST_NAME", true,
                "LAST_NAME", false
        );

        List<Pass> passes = List.of(new Pass(
                "TestPass",
                "Description",
                "0.1",
                "0.9",
                blockingCriteria,
                List.of()
        ));

        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                passes
        );

        doNothing().when(algorithmService).configureMatching(request);

        algorithmController.configureMatching(request);

        verify(algorithmService, times(1)).configureMatching(request);
    }

    @Test
    void testGetMatchingConfiguration() {
        Map<String, Boolean> blockingCriteria = Map.of(
                "FIRST_NAME", true,
                "LAST_NAME", false
        );

        List<Pass> passes = List.of(new Pass(
                "TestPass",
                "Description",
                "0.1",
                "0.9",
                blockingCriteria, // Updated to Map<String, Boolean>
                List.of()
        ));

        when(algorithmService.getMatchingConfiguration()).thenReturn(passes);

        Map<String, List<Pass>> actualResponse = algorithmController.getMatchingConfiguration();

        assertNotNull(actualResponse);
        assertTrue(actualResponse.containsKey("passes"));
        assertEquals(passes, actualResponse.get("passes"));
    }

    @Test
    void testUpdateAlgorithm() {
        Map<String, Boolean> blockingCriteria = Map.of(
                "FIRST_NAME", true,
                "LAST_NAME", false
        );

        List<Pass> passes = List.of(new Pass(
                "TestPass",
                "Description",
                "0.1",
                "0.9",
                blockingCriteria, // Updated to Map<String, Boolean>
                List.of()
        ));

        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                passes
        );

        doNothing().when(algorithmService).updateDibbsConfigurations(request);

        algorithmController.updateAlgorithm(request);

        verify(algorithmService, times(1)).updateDibbsConfigurations(request);
    }
}
