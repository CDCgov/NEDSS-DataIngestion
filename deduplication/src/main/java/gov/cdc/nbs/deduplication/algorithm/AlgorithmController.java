package gov.cdc.nbs.deduplication.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;

@RestController
@RequestMapping("/api/deduplication/algorithm")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    private static final Logger log = LoggerFactory.getLogger(AlgorithmController.class);

    public AlgorithmController(final AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @PutMapping()
    public void save(@RequestBody MatchingConfiguration request) {
        try {
            log.info("Received configure matching request: {}", request);
            algorithmService.save(request);
        } catch (Exception e) {
            log.error("Error while processing the configure matching request: ", e);
        }
    }

    @GetMapping()
    public MatchingConfiguration getMatchingConfiguration() {
        return algorithmService.getMatchingConfiguration();
    }

}
