package gov.cdc.nbs.deduplication.merge;

import java.sql.ResultSet;
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
        (rs, rowNum) -> rs.getString("person_uid"));
  }

  private List<String> getMpiIdsByPersonIds(List<String> personIds) {
    return deduplicationTemplate.query(
        QueryConstants.PATIENT_IDS_BY_PERSON_UIDS,
        new MapSqlParameterSource("personIds", personIds),
        (rs, rowNum) -> rs.getString("mpi_person"));
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
