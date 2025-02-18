package gov.cdc.nbs.deduplication.algorithm;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deduplication")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    private static final Logger log = LoggerFactory.getLogger(AlgorithmController.class);

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
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

    @PostMapping("/import-configuration")
    public ResponseEntity<String> importConfiguration(@RequestBody MatchingConfigRequest configRequest) {
        algorithmService.saveMatchingConfiguration(configRequest);
        return ResponseEntity.ok("Configuration imported successfully.");
    }
}
