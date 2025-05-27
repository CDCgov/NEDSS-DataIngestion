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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
    for (MatchCandidate candidate : candidates) {
      // Step 1: Insert into matches_requiring_review
      Long matchId = insertMatchGroup(candidate.personUid());

      // Step 2: Insert each potential match
      List<String> potentialNbsIds = getPersonIdsByMpiIds(candidate.possibleMatchList());
      for (String potentialNbsId : potentialNbsIds) {
        insertMatchCandidate(matchId, Long.valueOf(potentialNbsId));
      }
    }
  }

  private Long insertMatchGroup(String personId) {
    PatientNameAndTime patientNameAndTime = getPersonNameAndAddTime(personId);
    MapSqlParameterSource groupParams = new MapSqlParameterSource()
        .addValue("personUid", personId)
        .addValue("personName", patientNameAndTime.name())
        .addValue("personAddTime", patientNameAndTime.addTime())
        .addValue("identifiedDate", getCurrentDate());

    KeyHolder keyHolder = new GeneratedKeyHolder();

    namedParameterJdbcTemplate.update(
        QueryConstants.INSERT_MATCH_GROUP,
        groupParams,
        keyHolder
    );

    Number matchGroupId = keyHolder.getKey();
    return matchGroupId!=null?matchGroupId.longValue():null;
  }

  private List<String> getPersonIdsByMpiIds(List<String> mpiIds) {
    return namedParameterJdbcTemplate.query(
        QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS,
        new MapSqlParameterSource("mpiIds", mpiIds),
        (rs, rowNum) -> rs.getString("person_uid"));
  }


  private void insertMatchCandidate(Long matchId, Long personUid) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("matchId", matchId)
        .addValue("personUid", personUid);

    namedParameterJdbcTemplate.update(QueryConstants.INSERT_MATCH_CANDIDATE, params);
  }


  private void updateStatus(List<String> personIds) {
    if (!personIds.isEmpty()) {
      MapSqlParameterSource parameters = new MapSqlParameterSource();
      parameters.addValue("personIds", personIds);
      namedParameterJdbcTemplate.update(QueryConstants.UPDATE_PROCESSED_PERSONS, parameters);
    }
  }

  private String getCurrentDate() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  private PatientNameAndTime getPersonNameAndAddTime(String personId) {
    return patientRecordService.fetchPersonNameAndAddTime(personId);
  }

}
