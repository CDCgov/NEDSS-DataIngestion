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

  public List<PersonMergeData> getPotentialMatchesDetails(long matchId) {
    List<String> nbsPersonIds = getPossibleMatchesOfPatient(matchId);
    return patientRecordService.fetchPersonsMergeData(nbsPersonIds);
  }

  private List<String> getPossibleMatchesOfPatient(long matchId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("matchId", matchId); // NOSONAR
    return deduplicationTemplate.query(
        QueryConstants.POSSIBLE_MATCH_IDS_BY_PATIENT_ID,
        parameters, (ResultSet rs, int rowNum) -> rs.getString(1));
  }

  public void unMergeAll(Long matchId) {// Keep Separate
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("matchId", matchId);
    deduplicationTemplate.update(QueryConstants.UN_MERGE_ALL_GROUP, parameters);
  }

  public void unMergeSinglePerson(Long matchId, Long potentialMatchPersonUid) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("matchId", matchId);
    parameters.addValue("potentialMatchPersonUid", potentialMatchPersonUid);
    deduplicationTemplate.update(QueryConstants.UN_MERGE_SINGLE_PERSON, parameters);
  }

}
