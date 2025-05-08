package gov.cdc.nbs.deduplication.batch.service;

import gov.cdc.nbs.deduplication.batch.mapper.PersonMergeDataMapper;
import gov.cdc.nbs.deduplication.batch.model.PatientNameAndTimeDTO;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.seed.mapper.MpiPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientRecordService {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final MpiPersonMapper mpiPersonMapper = new MpiPersonMapper();
  private final PersonMergeDataMapper personMergeDataMapper = new PersonMergeDataMapper();

  public PatientRecordService(
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public MpiPerson fetchMostRecentPatient(String personParentUid) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("personParentUid", personParentUid);

    List<MpiPerson> mpiPersons = namedParameterJdbcTemplate.query(
        QueryConstants.PERSON_RECORD_BY_PARENT_ID,
        params,
        mpiPersonMapper);
    return (mpiPersons.isEmpty()) ? null : mpiPersons.getFirst();// most recent based on last_chg_time
  }

  public MpiPerson fetchPersonRecord(String personUid) {
    return namedParameterJdbcTemplate.queryForObject(
        QueryConstants.PERSON_RECORD_BY_PERSON_ID,
        new MapSqlParameterSource("personUid", personUid),
        mpiPersonMapper);
  }

  public List<MpiPerson> fetchPersonRecords(List<String> personUids) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("ids", personUids);
    return namedParameterJdbcTemplate.query(
        QueryConstants.PERSON_RECORDS_BY_PERSON_IDS,
        params,
        mpiPersonMapper);
  }

  public PatientNameAndTimeDTO fetchPatientNameAndAddTime(String personUid) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("personUid", personUid);
    return namedParameterJdbcTemplate.queryForObject(
        QueryConstants.FETCH_PATIENT_NAME_AND_ADD_TIME_QUERY,
        params,
        (rs, rowNum) -> {
          LocalDateTime addTime = rs.getTimestamp("add_time").toLocalDateTime();
          String nestedName = rs.getString("full_name");
          return new PatientNameAndTimeDTO(addTime, nestedName);
        });
  }

  public List<PersonMergeData> fetchPersonsMergeData(List<String> personUids) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("ids", personUids);
    return namedParameterJdbcTemplate.query(
        QueryConstants.PERSONS_MERGE_DATA_BY_PERSON_IDS,
        params,
        personMergeDataMapper);
  }

}
