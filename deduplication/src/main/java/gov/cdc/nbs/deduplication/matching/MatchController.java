package gov.cdc.nbs.deduplication.matching;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.RelateRequest;

@RestController
@RequestMapping("/api/deduplication/match")
public class MatchController {

  private final MatchService matchService;

  public MatchController(final MatchService matchService) {
    this.matchService = matchService;
  }

  @PostMapping
  public MatchResponse checkForPatientMatch(@RequestBody String body) {
    return matchService.match(body);
  }

  @PostMapping("/relate")
  public void relateNbsPatient(@RequestBody RelateRequest request) {
    // NBS created a new person, associate it to the record in the MPI
    matchService.relateNbsIdToMpiId(request);
  }

}
