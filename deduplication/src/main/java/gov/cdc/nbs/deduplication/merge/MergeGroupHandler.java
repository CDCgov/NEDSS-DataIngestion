package gov.cdc.nbs.deduplication.merge;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.constants.QueryConstants;

@Component
public class MergeGroupHandler {

  private final NamedParameterJdbcTemplate deduplicationTemplate;
  private final PatientRecordService patientRecordService;

  public MergeGroupHandler(
      @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate,
      PatientRecordService patientRecordService) {
    this.deduplicationTemplate = deduplicationTemplate;
    this.patientRecordService = patientRecordService;
  }

  public List<PersonMergeData> getPotentialMatchesDetails(long personId) {
    List<String> nbsPersonIds = getPossibleMatchesOfPatient(personId);
    return patientRecordService.fetchPersonsMergeData(nbsPersonIds);
  }

  private List<String> getPossibleMatchesOfPatient(long personId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("personUid", personId);//NOSONAR
    return deduplicationTemplate.query(
        QueryConstants.POSSIBLE_MATCH_IDS_BY_PATIENT_ID,
        parameters, (ResultSet rs, int rowNum) -> rs.getString(1));
  }

  public void unMergeAll(Long personUid) {//Keep Separate
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("person_id", personUid);
    deduplicationTemplate.update(QueryConstants.UN_MERGE_ALL_GROUP, parameters);
  }


  public void resolvePatientMergeStatuses(String survivorPersonId, List<String> personIds) {
    markMergedRecordAsMerge(survivorPersonId, personIds);
    markNonActiveRecordAsNoMerge(survivorPersonId, personIds);
    markSingleRemainingRecordAsNoMergeIfExists(survivorPersonId);
  }


  private void markMergedRecordAsMerge(String personId, List<String> potentialPersonIds) {
    Long personUid = Long.valueOf(personId);
    List<Long> mergedUids = potentialPersonIds.stream()
        .map(Long::valueOf)
        .toList();

    List<Long> uidsToMarkAsMerged = new ArrayList<>();
    uidsToMarkAsMerged.add(personUid);
    uidsToMarkAsMerged.addAll(mergedUids);

    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("personUid", personUid);
    parameters.addValue("potentialIds", uidsToMarkAsMerged);

    deduplicationTemplate.update(QueryConstants.MARK_PATIENTS_AS_MERGED, parameters);
  }


  private void markNonActiveRecordAsNoMerge(String personId, List<String> potentialPersonIds) {
    Long personUid = Long.valueOf(personId);
    List<Long> potentialUids = potentialPersonIds.stream()
        .map(Long::valueOf)
        .toList();

    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("personUid", personUid);
    parameters.addValue("potentialUids", potentialUids);

    deduplicationTemplate.update(
        QueryConstants.SET_IS_MERGE_TO_FALSE_FOR_EXCLUDED_PATIENTS,
        parameters
    );
  }

  private void markSingleRemainingRecordAsNoMergeIfExists(String survivorPersonId) {
    deduplicationTemplate.update(QueryConstants.UPDATE_SINGLE_RECORD,
        new MapSqlParameterSource("personUid", survivorPersonId));
  }

  public void unMergeSinglePerson(Long personUid, Long potentialMatchPersonUid) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("person_uid", personUid);
    parameters.addValue("potentialMatchPersonUid", potentialMatchPersonUid);
    deduplicationTemplate.update(QueryConstants.UN_MERGE_SINGLE_PERSON, parameters);
  }

}
