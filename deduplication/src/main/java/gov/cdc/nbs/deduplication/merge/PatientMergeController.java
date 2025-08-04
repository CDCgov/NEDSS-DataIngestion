package gov.cdc.nbs.deduplication.merge;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse.MatchRequiringReview;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/merge")
@PreAuthorize("hasAuthority('MERGE-PATIENT')")
public class PatientMergeController {
  static final String DEFAULT_SORT = "identified,desc";

  private final MergeGroupService mergeGroupService;
  private final MergeService mergeService;
  private final PdfBuilder pdfBuilder;
  private final MatchesRequiringReviewResolver matchesRequiringReviewResolver;

  public PatientMergeController(
      final MergeGroupService mergeGroupService,
      final MergeService mergeService,
      final PdfBuilder pdfBuilder,
      final MatchesRequiringReviewResolver matchesRequiringReviewResolver) {
    this.mergeGroupService = mergeGroupService;
    this.mergeService = mergeService;
    this.pdfBuilder = pdfBuilder;
    this.matchesRequiringReviewResolver = matchesRequiringReviewResolver;
  }

  @GetMapping
  public MatchesRequireReviewResponse getPotentialMatches(
      @RequestParam(defaultValue = "0", name = "page") int page,
      @RequestParam(defaultValue = "5", name = "size") int size,
      @RequestParam(defaultValue = DEFAULT_SORT, name = "sort") String sort) {
    return matchesRequiringReviewResolver.resolve(page, size, sort);
  }

  @GetMapping("/{groupId}")
  public List<PersonMergeData> getPotentialMatchesDetails(@PathVariable("groupId") Long groupId) {
    return mergeGroupService.getMergeGroup(groupId);
  }

  @DeleteMapping("/{groupId}/{personUid}")
  public void markNoMerge(
      @PathVariable("groupId") Long groupId,
      @PathVariable("personUid") Long personUid) {
    mergeGroupService.markNoMerge(groupId, personUid);
  }

  @DeleteMapping("/{groupId}")
  public void markAllNoMerge(
      @PathVariable("groupId") Long groupId) {
    mergeGroupService.markAllNoMerge(groupId);
  }

  @PostMapping("/{groupId}")
  public void mergePatients(
      @RequestBody PatientMergeRequest mergeRequest,
      @PathVariable("groupId") Long groupId) throws JsonProcessingException {
    mergeService.performMerge(groupId, mergeRequest);
  }

  @GetMapping(value = "/export/csv", produces = "text/csv")
  public void exportMatchesAsCSV(
      @RequestParam(defaultValue = DEFAULT_SORT, name = "sort") String sort,
      HttpServletResponse response) throws IOException {

    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment; filename=matches_requiring_review.csv");

    List<MatchRequiringReview> matches = matchesRequiringReviewResolver.resolveAll(sort);

    try (PrintWriter writer = response.getWriter()) {
      writer.println("Patient ID,Person Name,Date Created,Date Identified,Number of Matching Records");
      for (MatchRequiringReview match : matches) {
        writer.printf(
            "\"%s\",\"%s\",\"%s\",\"%s\",%d%n",
            match.patientLocalId(),
            match.patientName(),
            pdfBuilder.formatDateTime(match.createdDate()),
            pdfBuilder.formatDateTime(match.identifiedDate()),
            match.numOfMatchingRecords());
      }
    }
  }

  @GetMapping(value = "/export/pdf", produces = "application/pdf")
  public void exportMatchesAsPDF(
      @RequestParam(defaultValue = DEFAULT_SORT, name = "sort") String sortParamRaw,
      HttpServletResponse response) throws IOException {
    String timestampForFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
    String timestampForFooter = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"));

    List<MatchRequiringReview> matches = matchesRequiringReviewResolver.resolveAll(sortParamRaw);
    pdfBuilder.build(response, matches, timestampForFilename, timestampForFooter);
  }
}
