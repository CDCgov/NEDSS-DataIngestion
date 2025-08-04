package gov.cdc.nbs.deduplication.batch.service;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import gov.cdc.nbs.deduplication.auth.authentication.PermissionResolver;
import gov.cdc.nbs.deduplication.batch.mapper.PersonMergeDataMapper;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;
import gov.cdc.nbs.deduplication.seed.mapper.MpiPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

@Service
public class PatientRecordService {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final PermissionResolver permissionResolver;
  private final MpiPersonMapper mpiPersonMapper = new MpiPersonMapper();

  public PatientRecordService(
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate,
      final PermissionResolver permissionResolver) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.permissionResolver = permissionResolver;
  }

  public MpiPerson fetchMostRecentPatient(String personParentUid) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("personParentUid", personParentUid);

    List<MpiPerson> mpiPersons = namedParameterJdbcTemplate.query(
        PatientQueries.PERSON_RECORD_BY_PARENT_ID,
        params,
        mpiPersonMapper);
    return (mpiPersons.isEmpty()) ? null : mpiPersons.getFirst();// most recent based on last_chg_time
  }

  public MpiPerson fetchPersonRecord(String personUid) {
    List<MpiPerson> results = namedParameterJdbcTemplate.query(
        PatientQueries.PERSON_RECORD_BY_PERSON_ID,
        new MapSqlParameterSource("personUid", personUid),
        mpiPersonMapper);
    if (results.isEmpty()) {
      return null;
    } else {
      return results.get(0);
    }
  }

  public List<MpiPerson> fetchPersonRecords(List<String> personUids) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("ids", personUids);
    return namedParameterJdbcTemplate.query(
        PatientQueries.PERSON_RECORDS_BY_PERSON_IDS,
        params,
        mpiPersonMapper);
  }

  public List<PersonMergeData> fetchPersonsMergeData(List<String> personUids) {
    List<Long> programJurisdictionOids = permissionResolver.resolve("view", "investigation");
    boolean hasHivAccess = !permissionResolver.resolve("HIVQuestions", "Global").isEmpty();

    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("ids", personUids)
        .addValue("programJurisdictionOids", programJurisdictionOids);

    return namedParameterJdbcTemplate.query(
        PatientQueries.PERSONS_MERGE_DATA_BY_PERSON_IDS,
        params,
        new PersonMergeDataMapper(hasHivAccess));
  }

  public Map<String, LocalDateTime> fetchPersonAddTimeMap(List<String> personUids) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("ids", personUids);

    List<Map<String, Object>> result = namedParameterJdbcTemplate.queryForList(
        PatientQueries.FETCH_PATIENT_ADD_TIME_QUERY, params);

    Map<String, LocalDateTime> addTimeMap = new HashMap<>();
    for (Map<String, Object> row : result) {
      Long personId = (Long) row.get("person_uid");
      Timestamp addTime = (Timestamp) row.get("add_time");
      addTimeMap.put(personId.toString(), addTime.toLocalDateTime());
    }
    return addTimeMap;
  }

  public PatientNameAndTime fetchPersonNameAndAddTime(String id) {
    return namedParameterJdbcTemplate.query(
        PatientQueries.FIND_NBS_ADD_TIME_AND_NAME_QUERY,
        new MapSqlParameterSource()
            .addValue("id", id),
        (ResultSet rs, int rowNum) -> new PatientNameAndTime(
            rs.getString("personLocalId"),
            rs.getString("name"),
            rs.getTimestamp("add_time").toLocalDateTime()))
        .getFirst();
  }

}
