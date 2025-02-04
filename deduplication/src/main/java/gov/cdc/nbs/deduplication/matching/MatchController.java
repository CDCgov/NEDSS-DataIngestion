package gov.cdc.nbs.deduplication.matching;

import gov.cdc.nbs.deduplication.matching.model.MatchingConfigRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.matching.model.RelateRequest;

@RestController
@RequestMapping("/api/deduplication")
public class MatchController {

  private final MatchService matchService;
  private static final Logger log = LoggerFactory.getLogger(MatchController.class);


  public MatchController(final MatchService matchService) {
    this.matchService = matchService;
  }

  @PostMapping("/match")
  public MatchResponse checkForPatientMatch(@RequestBody PersonMatchRequest request) {
    return matchService.match(request);
  }

  @PostMapping("/relate")
  public void relateNbsPatient(@RequestBody RelateRequest request) {
    // NBS created a new person, associate it to the record in the MPI
    matchService.relateNbsIdToMpiId(request);
  }

  // Expose the configureMatching method via a POST endpoint
  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/configure-matching")
  public void configureMatching(@RequestBody MatchingConfigRequest request) {
    try {
      log.info("Received configure matching request: {}", request);
      matchService.configureMatching(request);
    } catch (Exception e) {
      log.error("Error while processing the configure matching request: ", e);
    }
  }

  // Fetch Matching Configuration (UI retrieves this)
  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/matching-configuration")
  public MatchingConfigRequest getMatchingConfiguration() {
    return matchService.getMatchingConfiguration();
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/update-algorithm")
  public void updateAlgorithm(@RequestBody MatchingConfigRequest request) {
    matchService.updateAlgorithm(request);  // Pass the MatchingConfigRequest to the service
  }
}
