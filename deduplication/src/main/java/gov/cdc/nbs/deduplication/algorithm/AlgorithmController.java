package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deduplication")
public class AlgorithmController {

    private final AlgorithmService algorithmService;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(AlgorithmController.class);

    public AlgorithmController(AlgorithmService algorithmService, ObjectMapper objectMapper) {
        this.algorithmService = algorithmService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/configure-matching")
    public void configureMatching(@RequestBody MatchingConfigRequest request) {
        try {
            log.info("Received configure matching request: {}", request);
            algorithmService.configureMatching(request);
        } catch (Exception e) {
            log.error("Error while processing the configure matching request: ", e);
        }
    }

    @GetMapping("/matching-configuration")
    public Map<String, List<Pass>> getMatchingConfiguration() {
        List<Pass> passes = algorithmService.getMatchingConfiguration();
        return Map.of("passes", passes);
    }

    @PostMapping("/update-algorithm")
    public void updateAlgorithm(@RequestBody MatchingConfigRequest request) {
        algorithmService.updateDibbsConfigurations(request);
    }

    @GetMapping("/export-configuration")
    public ResponseEntity<InputStreamResource> exportConfiguration() throws IOException {
        // Fetch the configuration from the service
        List<Pass> passes = algorithmService.getMatchingConfiguration();

        // Convert to JSON string
        String jsonConfig = objectMapper.writeValueAsString(passes);

        // Convert to bytes and create an InputStream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonConfig.getBytes(StandardCharsets.UTF_8));

        // Generate a timestamped filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "record_linker_config_" + timestamp + ".json";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(new InputStreamResource(byteArrayInputStream));

    @PostMapping("/import-configuration")
    public ResponseEntity<String> importConfiguration(@RequestBody MatchingConfigRequest configRequest) {
        algorithmService.saveMatchingConfiguration(configRequest);
        return ResponseEntity.ok("Configuration imported successfully.");
    }
}
