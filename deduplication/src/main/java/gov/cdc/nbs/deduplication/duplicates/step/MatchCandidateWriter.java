package gov.cdc.nbs.deduplication.duplicates.step;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidate;
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

  @Autowired
  public MatchCandidateWriter(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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
      String identifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      for (String possibleMatchMpiId : candidate.possibleMatchList()) {
        if (isValidPossibleMatch(candidate.personUid(), possibleMatchMpiId)) {
          params = new MapSqlParameterSource()
              .addValue("personUid", candidate.personUid())
              .addValue("mpiPersonId", possibleMatchMpiId)
              .addValue("identifiedDate", identifiedDate);
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

  private String getPersonMpiIdByPersonId(String personId) {
    return namedParameterJdbcTemplate.queryForObject(QueryConstants.MPI_PERSON_ID_QUERY,
        new MapSqlParameterSource("personId", personId),
        String.class);
  }

  private String getPersonIdByPersonMpiId(String personMpiId) {
    return namedParameterJdbcTemplate.queryForObject(QueryConstants.PERSON_UID_BY_MPI_PATIENT_ID,
        new MapSqlParameterSource("mpiId", personMpiId),
        String.class);
  }

  private boolean isValidPossibleMatch(String personUid, String possibleMatchMpiId) {
    String possibleMatchNpsId = getPersonIdByPersonMpiId(possibleMatchMpiId);
    String personMpiId = getPersonMpiIdByPersonId(personUid);
    // Prevent self-matches (a person shouldn't match with themselves)
    if (personUid.equals(possibleMatchNpsId)){
      return false;
    }
    return !findExistingPossibleMatch(possibleMatchNpsId, personMpiId);// ensure there is no overlapping
  }

  private boolean findExistingPossibleMatch(String personId, String mpiPersonId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("personUid", personId);
    parameters.addValue("mpiPersonId", mpiPersonId);

    Integer count = namedParameterJdbcTemplate.queryForObject(
        QueryConstants.FIND_POSSIBLE_MATCH,
        parameters,
        Integer.class
    );
    return count != null && count > 0;
  }



}
