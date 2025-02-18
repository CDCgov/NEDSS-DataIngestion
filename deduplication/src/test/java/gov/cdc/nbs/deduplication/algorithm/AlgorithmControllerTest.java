package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Mock
    private ObjectMapper objectMapper;

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

    @Test
    void testExportConfiguration() throws IOException {
        // Prepare the mock data: only the passes section
        List<Pass> mockPasses = List.of(new Pass("pass1", "description", "0.1", "0.9", Map.of("FIRST_NAME", true, "LAST_NAME", false), List.of()));

        when(algorithmService.getMatchingConfiguration()).thenReturn(mockPasses);

        String uniqueFileName = "record_linker_config_test_" + System.currentTimeMillis() + ".json";
        Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), uniqueFileName);
        Files.createFile(tempFile); // Create the temporary file

        doAnswer(invocation -> {
            File file = invocation.getArgument(0);  // the first argument is the file
            List<Pass> passes = invocation.getArgument(1);  // the second argument is the passes

            assertTrue(file.getPath().contains("record_linker_config_"));  // Ensure the correct filename pattern
            assertEquals(mockPasses, passes);  // Ensure the right passes are passed

            String jsonContent = "[{\"name\":\"pass1\",\"description\":\"description\",\"lowerBound\":\"0.1\",\"upperBound\":\"0.9\",\"blockingCriteria\":{\"FIRST_NAME\":true,\"LAST_NAME\":false},\"matchingCriteria\":[]}]";
            Files.write(file.toPath(), jsonContent.getBytes()); // Write the passes to the file
            return null;
        }).when(objectMapper).writeValue(any(File.class), eq(mockPasses));

        ResponseEntity<Resource> response = algorithmController.exportConfiguration();

        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0).contains("attachment; filename="));

        // Clean up
        Files.deleteIfExists(tempFile);
    }



}

