package gov.cdc.nbs.deduplication.matching;

import gov.cdc.nbs.deduplication.matching.model.MatchingConfigRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.matching.model.RelateRequest;

@RestController
@RequestMapping("/api/deduplication")
public class MatchController {

  private final MatchService matchService;

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
    matchService.configureMatching(request);  // calls the configureMatching method in MatchService
  }

  // Fetch Matching Configuration (UI retrieves this)
  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/matching-configuration")
  public MatchingConfigRequest getMatchingConfiguration() {
    return matchService.getMatchingConfiguration();
  }

  // Update /algorithm (Before calling /match)
  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/update-algorithm")
  public void updateAlgorithm() {
    matchService.updateAlgorithm();
  }
}
