package gov.cdc.nbs.deduplication.duplicates.controller;

import gov.cdc.nbs.deduplication.duplicates.model.MergeGroupResponse;
import gov.cdc.nbs.deduplication.duplicates.service.MergeGroupHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
