package gov.cdc.nbs.deduplication.duplicates.service;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MergeStatusRequest;
import gov.cdc.nbs.deduplication.duplicates.model.PatientNameAndTimeDTO;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidateData;
import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
public class PatientMergeHandler {

  private final NamedParameterJdbcTemplate deduplicationTemplate;
  private final PatientRecordService patientRecordService;

  public PatientMergeHandler(
      @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate,
      PatientRecordService patientRecordService
  ) {
    this.deduplicationTemplate = deduplicationTemplate;
    this.patientRecordService = patientRecordService;
  }

  public List<MatchesRequireReviewResponse> getPotentialMatches(int page, int size) {
    int offset = page * size;
    List<MatchCandidateData> matchCandidates = getMatchCandidateData(offset, size);
    if (matchCandidates.isEmpty()) {
      return Collections.emptyList();
    }
    return matchCandidates.stream()
        .map(matchCandidateData -> {
          PatientNameAndTimeDTO patientNameAndTimeDTO =
              patientRecordService.fetchPatientNameAndAddTime(matchCandidateData.personUid());
          return new MatchesRequireReviewResponse(matchCandidateData, patientNameAndTimeDTO);
        })
        .toList();
  }


  private List<MatchCandidateData> getMatchCandidateData(int offset, int limit) {
    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("limit", limit)
        .addValue("offset", offset);
    return deduplicationTemplate.query(
        QueryConstants.POSSIBLE_MATCH_PATIENTS,
        parameters,
        this::mapRowToMatchCandidateData);
  }

  private MatchCandidateData mapRowToMatchCandidateData(ResultSet rs, int rowNum) throws SQLException {
    String personUid = rs.getString("person_uid");
    long numOfMatches = rs.getInt("num_of_matching");
    String dateIdentified = rs.getString("date_identified");
    return new MatchCandidateData(personUid, numOfMatches, dateIdentified);
  }


  public void updateMergeStatus(MergeStatusRequest request) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("personUid", request.personUid());
    parameters.addValue("isMerge", request.isMerge());
    deduplicationTemplate.update(QueryConstants.UPDATE_MERGE_STATUS_FOR_GROUP, parameters);
  }

}
