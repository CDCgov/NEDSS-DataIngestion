package gov.cdc.nbs.deduplication.duplicates.service;

import gov.cdc.nbs.deduplication.constants.QueryConstants;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
public class MergePatientHandler {

  private final NamedParameterJdbcTemplate nbsNamedTemplate;

  private final MergeGroupHandler mergeGroupHandler;

  public MergePatientHandler(
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsNamedTemplate,
      MergeGroupHandler mergeGroupHandler
  ) {
    this.nbsNamedTemplate = nbsNamedTemplate;
    this.mergeGroupHandler = mergeGroupHandler;
  }

  public void performMerge(String survivorPersonId, List<String> supersededPersonIds) {
    createMergeMetadata(survivorPersonId, supersededPersonIds);
    markSupersededRecords(survivorPersonId, supersededPersonIds);
    updateTheMergedPatients(survivorPersonId, supersededPersonIds);
  }

  private void markSupersededRecords(String survivorPersonId, List<String> supersededPersonIds) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("personId", survivorPersonId);
    parameters.addValue("supersededPersonIds", supersededPersonIds);
    nbsNamedTemplate.update(QueryConstants.MARK_SUPERSEDED_RECORDS, parameters);
  }

  private void createMergeMetadata(String survivorPersonId, List<String> supersededPersonIds) {

    MapSqlParameterSource[] batchParameters = supersededPersonIds.stream()
        .map(supersededPersonId -> {
          MapSqlParameterSource parameters = new MapSqlParameterSource();
          parameters.addValue("survivorPersonId", survivorPersonId);
          parameters.addValue("supersededPersonId", supersededPersonId);
          parameters.addValue("mergeTime", Timestamp.from(Instant.now()));
          return parameters;
        })
        .toArray(MapSqlParameterSource[]::new);

    nbsNamedTemplate.batchUpdate(QueryConstants.CREATE_MERGE_METADATA, batchParameters);
  }


  private void updateTheMergedPatients(String survivorPersonId, List<String> supersededPersonIds) {
    mergeGroupHandler.updateMergeStatusForPatients(survivorPersonId, supersededPersonIds);
  }



}
