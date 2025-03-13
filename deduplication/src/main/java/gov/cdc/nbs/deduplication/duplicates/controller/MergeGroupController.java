package gov.cdc.nbs.deduplication.duplicates.controller;

import gov.cdc.nbs.deduplication.duplicates.model.MergeGroupResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MergeStatusRequest;
import gov.cdc.nbs.deduplication.duplicates.service.MergeGroupHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/deduplication")
public class MergeGroupController {

  private final MergeGroupHandler mergeGroupHandler;

  public MergeGroupController(MergeGroupHandler possibleMatchHandler) {
    this.mergeGroupHandler = possibleMatchHandler;
  }

  @GetMapping("/merge-groups")
  public List<MergeGroupResponse> getPossibleMatchGroups(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {
    return mergeGroupHandler.getMergeGroups(page, size);
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
