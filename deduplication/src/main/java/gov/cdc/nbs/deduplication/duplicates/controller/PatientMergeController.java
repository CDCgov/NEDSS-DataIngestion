package gov.cdc.nbs.deduplication.duplicates.controller;

import gov.cdc.nbs.deduplication.duplicates.model.GroupNoMergeRequest;
import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergePatientRequest;
import gov.cdc.nbs.deduplication.duplicates.service.MergeGroupHandler;
import gov.cdc.nbs.deduplication.duplicates.service.MergePatientHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/deduplication")
public class PatientMergeController {

  private final MergeGroupHandler mergeGroupHandler;

  private final MergePatientHandler mergePatientsHandler;

  public PatientMergeController(MergeGroupHandler possibleMatchHandler, MergePatientHandler mergePatientsHandler) {
    this.mergeGroupHandler = possibleMatchHandler;
    this.mergePatientsHandler = mergePatientsHandler;
  }

  @GetMapping("/matches/requiring-review")
  public ResponseEntity<List<MatchesRequireReviewResponse>> getPotentialMatches(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {
    List<MatchesRequireReviewResponse> matches = mergeGroupHandler.getPotentialMatches(page, size);
    return ResponseEntity.ok(matches);
  }

  @PostMapping("/group-no-merge")
  public ResponseEntity<String> updateGroupNoMerge(@RequestBody GroupNoMergeRequest request) {
    try {
      mergeGroupHandler.updateMergeStatusForGroup(request.personOfTheGroup());
      return ResponseEntity.ok("Merge status updated successfully.");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error updating merge status: " + e.getMessage());
    }
  }

  @PostMapping("/merge-patient")
  public ResponseEntity<Void> mergeRecords(@RequestBody MergePatientRequest mergeRequest) {
    if (mergeRequest.getSurvivorPersonId() == null || mergeRequest.getSupersededPersonIds() == null || mergeRequest.getSupersededPersonIds()
        .isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    try {
      mergePatientsHandler.performMerge(mergeRequest.getSurvivorPersonId(), mergeRequest.getSupersededPersonIds());
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
