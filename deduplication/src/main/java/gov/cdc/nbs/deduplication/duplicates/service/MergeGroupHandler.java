package gov.cdc.nbs.deduplication.duplicates.service;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidateData;
import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.duplicates.model.PatientNameAndTimeDTO;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
public class MergeGroupHandler {

  private final NamedParameterJdbcTemplate deduplicationTemplate;
  private final PatientRecordService patientRecordService;

  public MergeGroupHandler(
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
        }).toList();
  }


  public List<PersonMergeData> getPotentialMatchesDetails(long personId) {
    List<String> possibleMatchesMpiIds = getPossibleMatchesOfPatient(personId);
    List<String> npsPersonIds = getPersonIdsByMpiIds(possibleMatchesMpiIds);
    return patientRecordService.fetchPersonsMergeData(npsPersonIds);
  }

  private List<String> getPossibleMatchesOfPatient(long personId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("personUid", personId);
    return deduplicationTemplate.query(
        QueryConstants.POSSIBLE_MATCH_IDS_BY_PATIENT_ID,
        parameters, (ResultSet rs, int rowNum) -> rs.getString(1));
  }

  private List<String> getPersonIdsByMpiIds(List<String> personIds) {
    return deduplicationTemplate.query(
        QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS,
        new MapSqlParameterSource("mpiPersonIds", personIds),
        (rs, rowNum) -> rs.getString("person_uid")
    );
  }

  private List<MatchCandidateData> getMatchCandidateData(int offset, int limit) {
    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("limit", limit)
        .addValue("offset", offset);
    return deduplicationTemplate.query(
        QueryConstants.POSSIBLE_MATCH_PATIENTS,
        parameters, this::mapRowToMatchCandidateData);
  }

  private MatchCandidateData mapRowToMatchCandidateData(ResultSet rs, int rowNum) throws SQLException {
    String personUid = rs.getString("person_uid");
    long numOfMatches = rs.getInt("num_of_matching");
    String dateIdentified = rs.getString("date_identified");
    return new MatchCandidateData(personUid, numOfMatches, dateIdentified);
  }

  private List<String> getMpiIdsByPersonIds(List<String> personIds) {
    return deduplicationTemplate.query(
        QueryConstants.PATIENT_IDS_BY_PERSON_UIDS,
        new MapSqlParameterSource("personIds", personIds),
        (rs, rowNum) -> rs.getString("mpi_person")
    );
  }

  public void updateMergeStatusForGroup(Long personOfTheGroup) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("person_id", personOfTheGroup);
    parameters.addValue("isMerge", false);
    deduplicationTemplate.update(QueryConstants.UPDATE_MERGE_STATUS_FOR_GROUP, parameters);
  }

  public void updateMergeStatusForPatients(String survivorPersonId, List<String> personIds) {
    markMergedRecordAsMerge(survivorPersonId, personIds);
    markNonActiveRecordAsNoMerge(survivorPersonId, personIds);
    markSingleRemainingRecordAsNoMergeIfExists(survivorPersonId);
  }

  private void markMergedRecordAsMerge(String survivorPersonId, List<String> personIds) {
    List<String> mpiPersonIds = getMpiIdsByPersonIds(personIds);
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("mpiIds", mpiPersonIds);
    parameters.addValue("personId", survivorPersonId);
    deduplicationTemplate.update(QueryConstants.UPDATE_MERGE_STATUS_FOR_PATIENTS, parameters);
  }

  private void markNonActiveRecordAsNoMerge(String survivorPersonId, List<String> personIds) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    List<String> mpiPersonIds = getMpiIdsByPersonIds(personIds);
    parameters.addValue("mpiIds", mpiPersonIds);
    parameters.addValue("personId", survivorPersonId);
    parameters.addValue("personIds", personIds);
    deduplicationTemplate.update(QueryConstants.UPDATE_MERGE_STATUS_FOR_NON_PATIENTS, parameters);
  }

  private void markSingleRemainingRecordAsNoMergeIfExists(String survivorPersonId) {
    deduplicationTemplate.update(QueryConstants.UPDATE_SINGLE_RECORD,
        new MapSqlParameterSource("personUid", survivorPersonId));
  }



}
