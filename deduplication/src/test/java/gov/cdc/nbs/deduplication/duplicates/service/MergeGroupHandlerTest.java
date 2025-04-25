package gov.cdc.nbs.deduplication.duplicates.service;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidateData;
import gov.cdc.nbs.deduplication.duplicates.model.PatientNameAndTimeDTO;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class MergeGroupHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;

  @Mock
  private PatientRecordService patientRecordService;

  @Mock
  private MergeGroupService mergeGroupService;

  @InjectMocks
  private MergeGroupHandler mergeGroupHandler;


  @Test
  void testGetPotentialMatches() throws SQLException {
    PatientNameAndTimeDTO patientNameAndTimeDTO1 = mockPatientNameAndTimeDTO("John Doe");
    PatientNameAndTimeDTO patientNameAndTimeDTO2 = mockPatientNameAndTimeDTO("Andrew James");

    // Mocking
    ResultSet rsMatchCandidates = mockResultSetForMatchCandidates();
    mockQueryForMatchCandidates(rsMatchCandidates);
    when(patientRecordService.fetchPatientNameAndAddTime("personUid1"))
        .thenReturn(patientNameAndTimeDTO1);
    when(patientRecordService.fetchPatientNameAndAddTime("personUid2"))
        .thenReturn(patientNameAndTimeDTO2);

    // Act
    List<MatchesRequireReviewResponse> result = mergeGroupHandler.getPotentialMatches(0, 10);

    verifyPotentialMatchesResponse(result);
  }

  @Test
  void testGetPotentialMatches_ReturnsEmptyListWhenNoMatchCandidates() {
    // Mocking
    when(deduplicationTemplate.query(
        eq(QueryConstants.POSSIBLE_MATCH_PATIENTS),
        any(MapSqlParameterSource.class),
        ArgumentMatchers.<RowMapper<MatchCandidateData>>any()
    )).thenReturn(Collections.emptyList());

    // Act
    List<MatchesRequireReviewResponse> result = mergeGroupHandler.getPotentialMatches(0, 10);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  private PatientNameAndTimeDTO mockPatientNameAndTimeDTO(String patientName) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
    LocalDateTime dateTime = LocalDateTime.parse("2020-10-01-12-30", formatter);
    return new PatientNameAndTimeDTO(dateTime, patientName);
  }


  private ResultSet mockResultSetForMatchCandidates() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("person_uid")).thenReturn("personUid1", "personUid2");
    when(rs.getInt("num_of_matching")).thenReturn(2, 4);
    when(rs.getString("date_identified")).thenReturn("2020-10-01", "2020-10-02");
    return rs;
  }

  private void mockQueryForMatchCandidates(ResultSet rs) {
    when(deduplicationTemplate.query(
        eq(QueryConstants.POSSIBLE_MATCH_PATIENTS),
        argThat((MapSqlParameterSource parameters) -> {
          Integer limit = (Integer) parameters.getValue("limit");
          Integer offset = (Integer) parameters.getValue("offset");
          return limit != null && offset != null;
        }),
        ArgumentMatchers.<RowMapper<MatchCandidateData>>any()))
        .thenAnswer(invocation -> {
          RowMapper<MatchCandidateData> rowMapper = invocation.getArgument(2);
          List<MatchCandidateData> result = new ArrayList<>();
          result.add(rowMapper.mapRow(rs, 1));
          result.add(rowMapper.mapRow(rs, 2));
          return result;
        });
  }

  private void verifyPotentialMatchesResponse(List<MatchesRequireReviewResponse> result) {
    assertNotNull(result);
    assertEquals(2, result.size());

    MatchesRequireReviewResponse response1 = result.getFirst();
    assertEquals("personUid1", response1.patientId());
    assertEquals("John Doe", response1.patientName());
    assertEquals("2020-10-01T12:30", response1.createdDate());
    assertEquals("2020-10-01", response1.identifiedDate());
    assertEquals(3, response1.numOfMatchingRecords());

    MatchesRequireReviewResponse response2 = result.get(1);
    assertEquals("personUid2", response2.patientId());
    assertEquals("Andrew James", response2.patientName());
    assertEquals("2020-10-01T12:30", response2.createdDate());
    assertEquals("2020-10-02", response2.identifiedDate());
    assertEquals(5, response2.numOfMatchingRecords());
  }


  @Test
  void testUpdateMergeStatus() {

    mergeGroupHandler.updateMergeStatusForGroup(100L);

    verify(deduplicationTemplate, times(1)).update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_GROUP),
        any(MapSqlParameterSource.class)
    );
  }

  @Test
  void testUpdateMergeStatusForPatients() {
    String survivorPersonId = "survivorId";
    List<String> personIds = List.of("person1", "person2");
    List<String> mpiPersonIds = List.of("mpi1", "mpi2");

    updateMergeStatusForPatientsMocking(mpiPersonIds);
    mergeGroupHandler.updateMergeStatusForPatients(survivorPersonId, personIds);
    updateMergeStatusForPatientsVerifying(survivorPersonId);
  }

  private void updateMergeStatusForPatientsMocking(List<String> mpiPersonIds) {
    // Mock the call of getMpiIdsByPersonIds method
    when(deduplicationTemplate.query(
        eq(QueryConstants.PATIENT_IDS_BY_PERSON_UIDS),
        argThat((MapSqlParameterSource params) ->
            params.getValue("personIds") != null),
        ArgumentMatchers.<RowMapper<String>>any()))
        .thenReturn(mpiPersonIds);

    // Mock the update operations
    when(deduplicationTemplate.update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_PATIENTS),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);

    // Mock the markNonActiveRecordAsNoMerge call
    when(deduplicationTemplate.update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_NON_PATIENTS),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);

    //Mock the markSingleRemainingRecordAsNoMergeIfExists call
    when(deduplicationTemplate.update(
        eq(QueryConstants.UPDATE_SINGLE_RECORD),
        any(MapSqlParameterSource.class)))
        .thenReturn(1);
  }

  @SuppressWarnings("unchecked")
  private void updateMergeStatusForPatientsVerifying(String survivorPersonId) {
    // Verify the markMergedRecordAsMerge call
    verify(deduplicationTemplate).update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_PATIENTS),
        argThat((MapSqlParameterSource params) -> {
          List<String> mpiIds = (List<String>) params.getValue("mpiIds");
          String personId = (String) params.getValue("personId");
          return mpiIds != null && mpiIds.size() == 2
              && personId != null && personId.equals(survivorPersonId);
        }));

    // Verify the markNonActiveRecordAsNoMerge call
    verify(deduplicationTemplate).update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_NON_PATIENTS),
        argThat((MapSqlParameterSource params) -> {
          List<String> mpiIds = (List<String>) params.getValue("mpiIds");
          String personId = (String) params.getValue("personId");
          List<String> personIdsParam = (List<String>) params.getValue("personIds");
          return mpiIds != null && mpiIds.size() == 2
              && personId != null && personId.equals(survivorPersonId)
              && personIdsParam != null && personIdsParam.size() == 2;
        }));

    // Verify the markSingleRemainingRecordAsNoMergeIfExists call
    verify(deduplicationTemplate).update(
        eq(QueryConstants.UPDATE_SINGLE_RECORD),
        argThat((MapSqlParameterSource params) -> {
          String personId = (String) params.getValue("personUid");
          return personId != null && personId.equals(survivorPersonId);
        }));
  }


  @Test
  void testGetPotentialMatchesDetails() {
    long personId = 123L;
    List<String> possibleMatchesMpiIds = Arrays.asList("mpi1", "mpi2");
    List<String> npsPersonIds = Arrays.asList("person1", "person2");
    List<PersonMergeData> mockPersonMergeData = createMockPersonMergeData();

    mockPossibleMatchesOfPatient(personId, possibleMatchesMpiIds);
    mockPersonIdsByMpiIds(possibleMatchesMpiIds, npsPersonIds);
    mockFetchPersonsMergeData(npsPersonIds, mockPersonMergeData);

    // Act
    List<PersonMergeData> result = mergeGroupHandler.getPotentialMatchesDetails(personId);

    // Assert
    verifyAndAssertResults(result);
  }

  @Test
  void testGetAllMatchesRequiringReview() {
    // Given
    MatchCandidateData candidate1 = new MatchCandidateData("personUid1", 2, "2020-10-01");
    MatchCandidateData candidate2 = new MatchCandidateData("personUid2", 4, "2020-10-02");

    List<MatchCandidateData> candidates = List.of(candidate1, candidate2);

    PatientNameAndTimeDTO dto1 = new PatientNameAndTimeDTO(
            LocalDateTime.of(2020, 10, 1, 12, 30), "John Doe");
    PatientNameAndTimeDTO dto2 = new PatientNameAndTimeDTO(
            LocalDateTime.of(2020, 10, 1, 12, 30), "Andrew James");

    when(mergeGroupService.fetchAllMatchesRequiringReview()).thenReturn(candidates);
    when(patientRecordService.fetchPatientNameAndAddTime("personUid1")).thenReturn(dto1);
    when(patientRecordService.fetchPatientNameAndAddTime("personUid2")).thenReturn(dto2);

    // When
    List<MatchesRequireReviewResponse> result = mergeGroupHandler.getAllMatchesRequiringReview();

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());

    MatchesRequireReviewResponse response1 = result.get(0);
    assertEquals("personUid1", response1.patientId());
    assertEquals("John Doe", response1.patientName());
    assertEquals("2020-10-01T12:30", response1.createdDate());
    assertEquals("2020-10-01", response1.identifiedDate());
    assertEquals(3, response1.numOfMatchingRecords());

    MatchesRequireReviewResponse response2 = result.get(1);
    assertEquals("personUid2", response2.patientId());
    assertEquals("Andrew James", response2.patientName());
    assertEquals("2020-10-01T12:30", response2.createdDate());
    assertEquals("2020-10-02", response2.identifiedDate());
    assertEquals(5, response2.numOfMatchingRecords());
  }


  private List<PersonMergeData> createMockPersonMergeData() {
    return List.of(
        new PersonMergeData(
            "2023-01-01",
            "test comment",
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        )
    );
  }

  private void mockPossibleMatchesOfPatient(long personId, List<String> possibleMatchesMpiIds) {
    when(deduplicationTemplate.query(
        eq(QueryConstants.POSSIBLE_MATCH_IDS_BY_PATIENT_ID),
        argThat((MapSqlParameterSource params) ->
            Objects.equals(params.getValue("personUid"), personId)),
        ArgumentMatchers.<RowMapper<String>>any()))
        .thenReturn(possibleMatchesMpiIds);
  }

  private void mockPersonIdsByMpiIds(List<String> possibleMatchesMpiIds, List<String> npsPersonIds) {
    when(deduplicationTemplate.query(
        eq(QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS),
        argThat((MapSqlParameterSource params) ->
            Objects.equals(params.getValue("mpiPersonIds"), possibleMatchesMpiIds)),
        ArgumentMatchers.<RowMapper<String>>any()))
        .thenReturn(npsPersonIds);
  }

  private void mockFetchPersonsMergeData(List<String> npsPersonIds, List<PersonMergeData> mockPersonMergeData) {
    when(patientRecordService.fetchPersonsMergeData(npsPersonIds)).thenReturn(mockPersonMergeData);
  }

  private void verifyAndAssertResults(List<PersonMergeData> result) {
    assertNotNull(result);
    assertEquals(1, result.size());
    PersonMergeData firstResult = result.getFirst();
    assertEquals("2023-01-01", firstResult.asOfDate());
    assertEquals("test comment", firstResult.comments());
  }


}
