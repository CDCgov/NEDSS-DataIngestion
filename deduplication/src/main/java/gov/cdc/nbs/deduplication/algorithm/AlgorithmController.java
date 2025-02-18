package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.springframework.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public ResponseEntity<Resource> exportConfiguration() throws IOException {
        List<Pass> passes = algorithmService.getMatchingConfiguration();

        // Generate a timestamped filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "record_linker_config_" + timestamp + ".json";
        String tempDir = System.getProperty("java.io.tmpdir");
        Path filePath = Paths.get(tempDir, fileName);

        // Convert the list of passes to JSON and save as a file
        objectMapper.writeValue(filePath.toFile(), passes);

        log.info("File exported to : {}", filePath);

        // serving the file for download
        Resource file = new FileSystemResource(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(file);
    }

    @PostMapping("/import-configuration")
    public ResponseEntity<String> importConfiguration(@RequestParam("file") MultipartFile file) {
        try {
            // Parse the uploaded JSON file
            MatchingConfigRequest configRequest = objectMapper.readValue(file.getInputStream(), MatchingConfigRequest.class);

            // Pass the configuration to the service layer to save it
            algorithmService.saveMatchingConfiguration(configRequest);

            return ResponseEntity.ok("Configuration imported successfully.");
        } catch (IOException e) {
            // Handle errors while reading or parsing the file
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error importing configuration: " + e.getMessage());
        }
    }

}


