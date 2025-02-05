package gov.cdc.nbs.deduplication.algorithm;

import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deduplication")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    private static final Logger log = LoggerFactory.getLogger(AlgorithmController.class);

    public AlgorithmController(final AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/configure-matching")
    public void configureMatching(@RequestBody MatchingConfigRequest request) {
        try {
            log.info("Received configure matching request: {}", request);
            algorithmService.configureMatching(request);
        } catch (Exception e) {
            log.error("Error while processing the configure matching request: ", e);
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/matching-configuration")
    public MatchingConfigRequest getMatchingConfiguration() {
        return algorithmService.getMatchingConfiguration();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/update-algorithm")
    public void updateAlgorithm(@RequestBody MatchingConfigRequest request) {
        algorithmService.updateDibbsConfigurations(request);
    }
}
