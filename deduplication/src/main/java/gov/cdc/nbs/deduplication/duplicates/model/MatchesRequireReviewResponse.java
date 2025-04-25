package gov.cdc.nbs.deduplication.duplicates.model;


public record MatchesRequireReviewResponse(
    String patientId,
    String patientName,
    String createdDate,
    String identifiedDate,
    long numOfMatchingRecords
) {
  public MatchesRequireReviewResponse(MatchCandidateData matchCandidateData,
      PatientNameAndTimeDTO patientNameAndTimeDTO) {
    this(
        matchCandidateData.personUid(),
        patientNameAndTimeDTO.fullName(),
        patientNameAndTimeDTO.addTime().toString(),
        matchCandidateData.dateIdentified(),
        matchCandidateData.numOfMatches()+1 // count of the matched patients + 1 for the patient itself
    );
  }
}
