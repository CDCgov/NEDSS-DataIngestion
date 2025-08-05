package gov.cdc.nbs.deduplication.batch.service;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import gov.cdc.nbs.deduplication.auth.authentication.PermissionResolver;
import gov.cdc.nbs.deduplication.batch.mapper.PersonMergeDataMapper;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.seed.mapper.MpiPersonMapper;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

@ExtendWith(MockitoExtension.class)
class PatientRecordServiceTest {

  @Mock
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Mock
  private PermissionResolver permissionResolver;

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

    when(namedParameterJdbcTemplate.query(
        eq(PatientQueries.PERSON_RECORD_BY_PERSON_ID),
        any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class))).thenReturn(List.of(expectedPerson));

    MpiPerson actualPerson = patientRecordService.fetchPersonRecord(personUid);

    assertThat(actualPerson).isEqualTo(expectedPerson);
  }

  @Test
  void fetchPersonRecord_ReturnsNull() {
    String personUid = "123";

    when(namedParameterJdbcTemplate.query(
        eq(PatientQueries.PERSON_RECORD_BY_PERSON_ID),
        any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class))).thenReturn(List.of());

    MpiPerson actualPerson = patientRecordService.fetchPersonRecord(personUid);

    assertThat(actualPerson).isNull();
  }

  @Test
  void fetchPersonRecords_ReturnsListOfMpiPersons() {
    List<String> personUids = Arrays.asList("123", "456");
    MpiPerson person1 = new MpiPerson("123", null, null, null, null, null, null, null, null);
    MpiPerson person2 = new MpiPerson("456", null, null, null, null, null, null, null, null);
    List<MpiPerson> expectedPersons = Arrays.asList(person1, person2);

    when(namedParameterJdbcTemplate.query(
        eq(PatientQueries.PERSON_RECORDS_BY_PERSON_IDS),
        any(MapSqlParameterSource.class),
        any(MpiPersonMapper.class))).thenReturn(expectedPersons);

    List<MpiPerson> actualPersons = patientRecordService.fetchPersonRecords(personUids);

    assertThat(actualPersons).isEqualTo(expectedPersons);
  }

  @Test
  void fetchPersonsMergeData_ReturnsListOfPersonMergeData() {
    List<String> personUids = Arrays.asList("123", "456");
    PersonMergeData person1 = getPersonMergeData();
    PersonMergeData person2 = getPersonMergeData();
    List<PersonMergeData> expectedPersons = Arrays.asList(person1, person2);

    when(namedParameterJdbcTemplate.query(
        eq(PatientQueries.PERSONS_MERGE_DATA_BY_PERSON_IDS),
        any(MapSqlParameterSource.class),
        any(PersonMergeDataMapper.class))).thenReturn(expectedPersons);

    List<PersonMergeData> actualPersons = patientRecordService.fetchPersonsMergeData(personUids);

    assertThat(actualPersons).isEqualTo(expectedPersons);
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
        null,
        null,
        null);
  }

  @Test
  void fetchPersonAddTimeMap_returnsCorrectMap() {
    List<String> personUids = List.of("123", "456");

    List<Map<String, Object>> mockResults = List.of(
        Map.of("person_uid", 123L, "add_time", Timestamp.valueOf(LocalDateTime.of(2023, 1, 1, 10, 0))),
        Map.of("person_uid", 456L, "add_time", Timestamp.valueOf(LocalDateTime.of(2024, 1, 1, 11, 0))));

    when(namedParameterJdbcTemplate.queryForList(any(String.class), any(MapSqlParameterSource.class)))
        .thenReturn(mockResults);

    Map<String, LocalDateTime> result = patientRecordService.fetchPersonAddTimeMap(personUids);

    assertThat(result).containsOnly(
        entry("123", LocalDateTime.of(2023, 1, 1, 10, 0)),
        entry("456", LocalDateTime.of(2024, 1, 1, 11, 0)));
  }

  @Test
  @SuppressWarnings("unchecked")
  void fetchPersonNameAndAddTime_returnsCorrectObject() throws Exception {
    String personId = "789";
    String personLocalId = "809";
    String expectedName = "John Doe";

    when(namedParameterJdbcTemplate.query(
        eq(PatientQueries.FIND_NBS_ADD_TIME_AND_NAME_QUERY),
        any(MapSqlParameterSource.class),
        any(RowMapper.class)))
        .thenAnswer(invocation -> {
          RowMapper<?> rowMapper = invocation.getArgument(2);
          ResultSet rs = mock(ResultSet.class);
          when(rs.getString("personLocalId")).thenReturn(personLocalId);
          when(rs.getString("name")).thenReturn(expectedName);
          when(rs.getTimestamp("add_time")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

          return List.of(Objects.requireNonNull(rowMapper.mapRow(rs, 1)));
        });

    PatientNameAndTime result = patientRecordService.fetchPersonNameAndAddTime(personId);

    assertThat(result.name()).isEqualTo(expectedName);
  }

}
