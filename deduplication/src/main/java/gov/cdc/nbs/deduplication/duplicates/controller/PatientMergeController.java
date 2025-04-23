package gov.cdc.nbs.deduplication.duplicates.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.duplicates.model.GroupNoMergeRequest;
import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergePatientRequest;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData;
import gov.cdc.nbs.deduplication.duplicates.service.MergeGroupHandler;
import gov.cdc.nbs.deduplication.duplicates.service.MergePatientHandler;

@RestController
@RequestMapping("/merge")
public class PatientMergeController {

  private final MergeGroupHandler mergeGroupHandler;

  private final MergePatientHandler mergePatientsHandler;

  public PatientMergeController(
      MergeGroupHandler possibleMatchHandler,
      MergePatientHandler mergePatientsHandler) {
    this.mergeGroupHandler = possibleMatchHandler;
    this.mergePatientsHandler = mergePatientsHandler;
  }

  @GetMapping
  public MatchesRequireReviewResponse getPotentialMatches(
      @RequestParam(defaultValue = "0", name = "page") int page,
      @RequestParam(defaultValue = "5", name = "size") int size) {
    return mergeGroupHandler.getPotentialMatches(page, size);
  }

  @GetMapping("/matches/details/{patientId}")
  public ResponseEntity<List<PersonMergeData>> getPotentialMatchesDetails(
      @PathVariable("patientId") Long patientId) {
    return ResponseEntity.ok(mergeGroupHandler.getPotentialMatchesDetails(patientId));
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
    if (mergeRequest.getSurvivorPersonId() == null || mergeRequest.getSupersededPersonIds() == null
        || mergeRequest.getSupersededPersonIds()
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
