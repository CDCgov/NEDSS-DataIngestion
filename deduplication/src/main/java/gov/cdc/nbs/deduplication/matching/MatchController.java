package gov.cdc.nbs.deduplication.matching;

import org.springframework.web.bind.annotation.*;

import gov.cdc.nbs.deduplication.matching.model.MatchResponse;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.matching.model.RelateRequest;

@RestController
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
    // NBS created a new person, associate it to the record in the MPI, update link in MPI
    matchService.relateNbsIdToMpiId(request);
  }

}
