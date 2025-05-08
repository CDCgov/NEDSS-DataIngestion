package gov.cdc.nbs.deduplication.batch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import gov.cdc.nbs.deduplication.batch.mapper.PersonMergeDataMapper;
import gov.cdc.nbs.deduplication.batch.model.PatientNameAndTimeDTO;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.seed.mapper.MpiPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PatientRecordServiceTest {

  @Mock
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @InjectMocks
  private PatientRecordService patientRecordService;

  @Test
  void fetchMostRecentPatientReturnsMpiPerson() {
    String personParentUid = "123";
    MpiPerson expectedPerson = new MpiPerson(null, personParentUid, null, null,
        null, null, null, null, null);
    List<MpiPerson> mpiPersons = Collections.singletonList(expectedPerson);

    when(namedParameterJdbcTemplate.query(any(String.class), any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class)))
        .thenReturn(mpiPersons);

    MpiPerson actualPerson = patientRecordService.fetchMostRecentPatient(personParentUid);

    assertThat(actualPerson).isEqualTo(expectedPerson);
  }

  @Test
  void fetchMostRecentPatientReturnsNullWhenNoRecordsFound() {
    String personParentUid = "123";
    List<MpiPerson> mpiPersons = Collections.emptyList();

    when(namedParameterJdbcTemplate.query(any(String.class), any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class)))
        .thenReturn(mpiPersons);

    MpiPerson actualPerson = patientRecordService.fetchMostRecentPatient(personParentUid);
    assertThat(actualPerson).isNull();
  }

  @Test
  void fetchPersonRecord_ReturnsMpiPerson() {
    String personUid = "123";
    MpiPerson expectedPerson = new MpiPerson(personUid, null, null, null,
        null, null, null, null, null);

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.PERSON_RECORD_BY_PERSON_ID),
        any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class))).thenReturn(expectedPerson);

    MpiPerson actualPerson = patientRecordService.fetchPersonRecord(personUid);

    assertThat(actualPerson).isEqualTo(expectedPerson);
  }

  @Test
  void fetchPersonRecords_ReturnsListOfMpiPersons() {
    List<String> personUids = Arrays.asList("123", "456");
    MpiPerson person1 = new MpiPerson("123", null, null, null, null, null, null, null, null);
    MpiPerson person2 = new MpiPerson("456", null, null, null, null, null, null, null, null);
    List<MpiPerson> expectedPersons = Arrays.asList(person1, person2);

    when(namedParameterJdbcTemplate.query(
        eq(QueryConstants.PERSON_RECORDS_BY_PERSON_IDS),
        any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class))).thenReturn(expectedPersons);

    List<MpiPerson> actualPersons = patientRecordService.fetchPersonRecords(personUids);

    assertThat(actualPersons).isEqualTo(expectedPersons);
  }

  @Test
  @SuppressWarnings("unchecked")
  void fetchPatientNameAndAddTime_ReturnsPatientNameAndTimeDTO() {
    String personUid = "123";
    LocalDateTime expectedAddTime = LocalDateTime.of(2023, 10, 1, 14, 30);
    String expectedFullName = "John Doe";

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.FETCH_PATIENT_NAME_AND_ADD_TIME_QUERY),
        any(MapSqlParameterSource.class),
        any(RowMapper.class))).thenAnswer(invocation -> {
          RowMapper<PatientNameAndTimeDTO> rowMapper = invocation.getArgument(2);
          ResultSet resultSet = mockResultSetForPatientNameAndAddTime(expectedAddTime, expectedFullName);
          return rowMapper.mapRow(resultSet, 1);
        });

    // Act
    PatientNameAndTimeDTO result = patientRecordService.fetchPatientNameAndAddTime(personUid);

    // Assert
    assertThat(result).isNotNull();
    assertThat(expectedAddTime).isEqualTo(result.addTime());
    assertThat(expectedFullName).isEqualTo(result.fullName());
  }

  @Test
  void fetchPersonsMergeData_ReturnsListOfPersonMergeData() {
    List<String> personUids = Arrays.asList("123", "456");
    PersonMergeData person1 = getPersonMergeData();
    PersonMergeData person2 = getPersonMergeData();
    List<PersonMergeData> expectedPersons = Arrays.asList(person1, person2);

    when(namedParameterJdbcTemplate.query(
        eq(QueryConstants.PERSONS_MERGE_DATA_BY_PERSON_IDS),
        any(MapSqlParameterSource.class),
        any(PersonMergeDataMapper.class))).thenReturn(expectedPersons);

    List<PersonMergeData> actualPersons = patientRecordService.fetchPersonsMergeData(personUids);

    assertThat(actualPersons).isEqualTo(expectedPersons);
  }

  private ResultSet mockResultSetForPatientNameAndAddTime(LocalDateTime addTime, String fullName) throws SQLException {
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getTimestamp("add_time")).thenReturn(Timestamp.valueOf(addTime));
    when(resultSet.getString("full_name")).thenReturn(fullName);
    return resultSet;
  }

  private PersonMergeData getPersonMergeData() {
    return new PersonMergeData(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }

}
