package gov.cdc.nbs.deduplication.merge;

import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.merge.model.PatientFileMergeHistory;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MergeHistoryHandler {

  static final String QUERY = """
      SELECT
          pm.superced_person_uid AS supersededPersonId,
          u.user_id AS mergedBy,
          pm.merge_time AS mergeTime
      FROM
          person_merge pm
      LEFT JOIN
          auth_user u ON pm.merge_user_id = u.auth_user_uid
      WHERE
          pm.surviving_person_uid = :patientId
      ORDER BY
          pm.merge_time DESC
      """;

  private final NamedParameterJdbcTemplate nbsNamedTemplate;
  private final PatientRecordService patientRecordService;


  public MergeHistoryHandler(
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsNamedTemplate,
      final PatientRecordService patientRecordService) {
    this.nbsNamedTemplate = nbsNamedTemplate;
    this.patientRecordService = patientRecordService;
  }


  public List<PatientFileMergeHistory> getPatientMergeHistoryList(long patientId) {
    List<PatientFileMergeHistory> result = new ArrayList<>();
    MapSqlParameterSource param = new MapSqlParameterSource("patientId", patientId);
    List<Map<String, Object>> mergeHistoryData = nbsNamedTemplate.queryForList(QUERY, param);
    for (Map<String, Object> row : mergeHistoryData) {
      result.add(buildPatientFileMergeHistory(row));
    }
    return result;
  }

  private PatientFileMergeHistory buildPatientFileMergeHistory(Map<String, Object> row) {
    String supersededPersonId = String.valueOf(row.get("supersededPersonId"));
    String mergedByUser = String.valueOf(row.get("mergedBy"));
    String mergeTimestamp = ((Timestamp) row.get("mergeTime")) .toLocalDateTime().toString();
    PatientNameAndTime localIdAndName = patientRecordService.fetchPersonNameAndAddTime(supersededPersonId);
    return new PatientFileMergeHistory(
        localIdAndName.personLocalId(),
        localIdAndName.name(),
        mergeTimestamp,
        mergedByUser
    );
  }

}
