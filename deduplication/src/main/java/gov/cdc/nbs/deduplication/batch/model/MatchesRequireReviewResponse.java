package gov.cdc.nbs.deduplication.batch.model;

import java.util.ArrayList;
import java.util.List;

public record MatchesRequireReviewResponse(List<MatchRequiringReview> matches, int page, int total) {
  public MatchesRequireReviewResponse(int page, int total) {
    this(new ArrayList<>(), page, total);
  }

  public record MatchRequiringReview(
      String patientId,
      String patientLocalId,
      String patientName,
      String createdDate,
      String identifiedDate,
      long numOfMatchingRecords) {

  }
}
