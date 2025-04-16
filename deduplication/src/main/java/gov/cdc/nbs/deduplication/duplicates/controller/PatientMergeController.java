package gov.cdc.nbs.deduplication.duplicates.controller;

import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergeStatusRequest;
import gov.cdc.nbs.deduplication.duplicates.service.PatientMergeHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/deduplication")
public class PatientMergeController {

  private final PatientMergeHandler mergeGroupHandler;

  public PatientMergeController(PatientMergeHandler possibleMatchHandler) {
    this.mergeGroupHandler = possibleMatchHandler;
  }

  @GetMapping("/matches/requiring-review")
  public ResponseEntity<List<MatchesRequireReviewResponse>> getPotentialMatches(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {
    List<MatchesRequireReviewResponse> matches = mergeGroupHandler.getPotentialMatches(page, size);
    return ResponseEntity.ok(matches);
  }

  @PostMapping("/merge-status")
  public ResponseEntity<String> updateMergeStatus(@RequestBody MergeStatusRequest request) {
    try {
      mergeGroupHandler.updateMergeStatus(request);
      return ResponseEntity.ok("Merge status updated successfully.");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error updating merge status: " + e.getMessage());
    }
  }
}
