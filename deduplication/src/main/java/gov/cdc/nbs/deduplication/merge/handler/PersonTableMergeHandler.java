package gov.cdc.nbs.deduplication.merge.handler;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

@Component
@Order(1)
public class PersonTableMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;
  private final NamedParameterJdbcTemplate deduplicationTemplate;

  public PersonTableMergeHandler(
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate,
      @Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate deduplicationTemplate) {
    this.nbsTemplate = nbsTemplate;
    this.deduplicationTemplate = deduplicationTemplate;
  }

  // Modifications have been performed on the person table entries.
  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request) {
    String survivorId = request.survivingRecord();
    List<String> supersededUids = getSupersededRecords(matchId, survivorId);
    List<String> involvedPatients = new ArrayList<>();
    involvedPatients.add(survivorId);
    involvedPatients.addAll(supersededUids);

    createHistoryEntries(involvedPatients);
    linkSupersededChildIdsToSurvivingMpr(survivorId, supersededUids);
    markSupersededRecords(supersededUids);
    updateLastChangeTime(involvedPatients);
    saveSupersededPersonMergeDetails(survivorId, supersededUids);
  }

  List<String> getSupersededRecords(String matchId, String survivorId) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("matchId", matchId);
    params.addValue("survivorId", survivorId);
    return deduplicationTemplate.queryForList(
        QueryConstants.FETCH_SUPERSEDED_CANDIDATES,
        params,
        String.class);
  }

  private void createHistoryEntries(List<String> involvedPatients) {
    savePersonCopyToPersonHist(involvedPatients);
    increasePersonVersionNbr(involvedPatients);
  }

  private void savePersonCopyToPersonHist(List<String> involvedPatients) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("involvedPatients", involvedPatients);// NOSONAR
    nbsTemplate.update(
        QueryConstants.COPY_PERSON_TO_HISTORY,
        params);
  }

  private void increasePersonVersionNbr(List<String> involvedPatients) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("involvedPatients", involvedPatients);
    nbsTemplate.update(
        QueryConstants.INCREMENT_PERSON_VERSION_NUMBER,
        params);
  }

  private void linkSupersededChildIdsToSurvivingMpr(String survivorUid, List<String> supersededPersonIds) {
    List<String> childIds = getChildIdsOfTheSupersededPerson(supersededPersonIds);
    if (!childIds.isEmpty()) {
      updateParentIdForChildIds(survivorUid, childIds);
    }
  }

  private List<String> getChildIdsOfTheSupersededPerson(List<String> supersededPersonIds) {
    return nbsTemplate.queryForList(
        QueryConstants.CHILD_IDS_BY_PARENT_PERSON_IDS,
        new MapSqlParameterSource("parentPersonIds", supersededPersonIds),
        String.class);
  }

  private void updateParentIdForChildIds(String survivorId, List<String> supersededChildIds) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("supersededChildIds", supersededChildIds);
    nbsTemplate.update(
        QueryConstants.LINK_SUPERSEDED_CHILD_IDS_TO_SURVIVOR,
        params);
  }

  private void markSupersededRecords(List<String> supersededPersonIds) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("supersededPersonIds", supersededPersonIds);
    nbsTemplate.update(QueryConstants.MARK_SUPERSEDED_RECORDS, parameters);
  }

  private void updateLastChangeTime(List<String> involvedPatients) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("involvedPatients", involvedPatients);
    parameters.addValue("lastChgTime", getCurrentUtcTimestamp());
    nbsTemplate.update(QueryConstants.UPDATE_LAST_CHANGE_TIME_FOR_PATIENTS, parameters);
  }

  private void saveSupersededPersonMergeDetails(String survivorPersonId, List<String> supersededPersonIds) {

    MapSqlParameterSource[] batchParameters = supersededPersonIds.stream()
        .map(supersededPersonId -> {
          MapSqlParameterSource parameters = new MapSqlParameterSource();
          parameters.addValue("survivorPersonId", survivorPersonId);
          parameters.addValue("supersededPersonId", supersededPersonId);
          parameters.addValue("mergeTime", Timestamp.from(Instant.now()));
          return parameters;
        })
        .toArray(MapSqlParameterSource[]::new);

    nbsTemplate.batchUpdate(QueryConstants.INSERT_PERSON_MERGE_RECORD, batchParameters);
  }

  public static Timestamp getCurrentUtcTimestamp() {
    return Timestamp.from(Instant.now());
  }

}
