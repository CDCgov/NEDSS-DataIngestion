package gov.cdc.nbs.deduplication.duplicates.step;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

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
    List<MapSqlParameterSource> batchParams = new ArrayList<>();
    for (MatchCandidate candidate : candidates) {
      for (String possibleMatchMpiId : candidate.possibleMatchList()) {
        batchParams.add(new MapSqlParameterSource()
            .addValue("personUid", candidate.personUid())
            .addValue("mpiPersonId", possibleMatchMpiId));
      }
    }
    if (!batchParams.isEmpty()) {
      namedParameterJdbcTemplate.batchUpdate(QueryConstants.MATCH_CANDIDATES_QUERY,
          batchParams.toArray(new MapSqlParameterSource[0]));
    }
  }

  private void updateStatus(List<String> personIds) {
    if (!personIds.isEmpty()) {
      MapSqlParameterSource parameters = new MapSqlParameterSource();
      parameters.addValue("personIds", personIds);
      namedParameterJdbcTemplate.update(QueryConstants.UPDATE_PROCESSED_PERSONS, parameters);
    }
  }

}
