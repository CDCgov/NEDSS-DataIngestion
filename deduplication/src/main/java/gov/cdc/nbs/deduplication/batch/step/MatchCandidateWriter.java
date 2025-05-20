package gov.cdc.nbs.deduplication.batch.step;

import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.constants.QueryConstants;

import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class MatchCandidateWriter implements ItemWriter<MatchCandidate> {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final PatientRecordService patientRecordService;

  @Autowired
  public MatchCandidateWriter(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
      final PatientRecordService patientRecordService) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.patientRecordService = patientRecordService;
  }

  @Override
  public void write(Chunk<? extends MatchCandidate> chunk) {
    List<String> personIds = new ArrayList<>();
    List<MatchCandidate> candidatesToInsert = new ArrayList<>();
    for (MatchCandidate candidate : chunk.getItems()) {
      personIds.add(candidate.personUid());
      if (candidate.possibleMatchList() != null) {
        candidatesToInsert.add(candidate);
      }
    }
    insertMatchCandidates(candidatesToInsert);
    updateStatus(personIds);
  }

  private void insertMatchCandidates(List<MatchCandidate> candidates) {
    MapSqlParameterSource params;
    for (MatchCandidate candidate : candidates) {
      PatientNameAndTime patientNameAndTime = getPersonNameAndAddTime(candidate.personUid());
      List<String> potentialNbsIds = getPersonIdsByMpiIds(candidate.possibleMatchList());
      for (String potentialNbsId : potentialNbsIds) {
        if (isValidPossibleMatch(candidate.personUid(), potentialNbsId)) {
          params = new MapSqlParameterSource()
              .addValue("personUid", candidate.personUid())
              .addValue("potentialPersonId", potentialNbsId)
              .addValue("identifiedDate", getCurrentDate())
              .addValue("personAddTime", patientNameAndTime.addTime())
              .addValue("personName", patientNameAndTime.name());
          namedParameterJdbcTemplate.update(QueryConstants.MATCH_CANDIDATES_QUERY, params);
        }
      }
    }
  }

  private void updateStatus(List<String> personIds) {
    if (!personIds.isEmpty()) {
      MapSqlParameterSource parameters = new MapSqlParameterSource();
      parameters.addValue("personIds", personIds);
      namedParameterJdbcTemplate.update(QueryConstants.UPDATE_PROCESSED_PERSONS, parameters);
    }
  }


  private boolean isValidPossibleMatch(String personUid, String potentialNbsId) {
    // Prevent self-matches (a person shouldn't match with themselves)
    if (personUid.equals(potentialNbsId)) {
      return false;
    }
    return !findExistingPossibleMatch(potentialNbsId, potentialNbsId);// ensure there is no overlapping
  }

  private boolean findExistingPossibleMatch(String personId, String mpiPersonId) {
    long personUid = Long.parseLong(personId);
    long potentialPersonUid = Long.parseLong(mpiPersonId);
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("personUid", personUid);
    parameters.addValue("potentialPersonId", potentialPersonUid);

    Integer count = namedParameterJdbcTemplate.queryForObject(
        QueryConstants.FIND_POSSIBLE_MATCH,
        parameters,
        Integer.class);
    return count != null && count > 0;
  }

  private List<String> getPersonIdsByMpiIds(List<String> mpiIds) {
    return namedParameterJdbcTemplate.query(
        QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS,
        new MapSqlParameterSource("mpiIds", mpiIds),
        (rs, rowNum) -> rs.getString("person_uid"));
  }

  private String getCurrentDate() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  private PatientNameAndTime getPersonNameAndAddTime(String personId) {
    return patientRecordService.fetchPersonNameAndAddTime(personId);
  }

}
