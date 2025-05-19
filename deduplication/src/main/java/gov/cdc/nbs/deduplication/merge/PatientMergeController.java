package gov.cdc.nbs.deduplication.merge;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import gov.cdc.nbs.deduplication.batch.model.GroupNoMergeRequest;
import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.batch.model.MergePatientRequest;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse.MatchRequiringReview;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/merge")
public class PatientMergeController {
  static final String DEFAULT_SORT = "patient-id,desc";

  private final MergeGroupHandler mergeGroupHandler;
  private final MergePatientHandler mergePatientsHandler;
  private final PdfBuilder pdfBuilder;
  private final MatchesRequiringReviewResolver matchesRequiringReviewResolver;

  public PatientMergeController(
          final MergeGroupHandler possibleMatchHandler,
          final MergePatientHandler mergePatientsHandler,
          final PdfBuilder pdfBuilder,
          final MatchesRequiringReviewResolver matchesRequiringReviewResolver) {
    this.mergeGroupHandler = possibleMatchHandler;
    this.mergePatientsHandler = mergePatientsHandler;
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

  @GetMapping("/{patientId}")
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
    if (mergeRequest.getSurvivorPersonId() == null
            || mergeRequest.getSupersededPersonIds() == null
            || mergeRequest.getSupersededPersonIds().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    try {
      mergePatientsHandler.performMerge(mergeRequest.getSurvivorPersonId(), mergeRequest.getSupersededPersonIds());
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
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
                match.patientId(),
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
    String sort = sortParamRaw.replaceAll("^\"(.*)\"$", "$1");
    String timestampForFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
    String timestampForFooter = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"));

    List<MatchRequiringReview> matches = matchesRequiringReviewResolver.resolveAll(sort);
    pdfBuilder.build(response, matches, timestampForFilename, timestampForFooter);
  }
}
