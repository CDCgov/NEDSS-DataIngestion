package gov.cdc.nbs.deduplication.duplicates.service;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidateData;
import gov.cdc.nbs.deduplication.duplicates.model.MergeStatusRequest;
import gov.cdc.nbs.deduplication.duplicates.model.PatientNameAndTimeDTO;
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
class PatientMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;

  @Mock
  private PatientRecordService patientRecordService;

  @InjectMocks
  private PatientMergeHandler patientMergeHandler;

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
    List<MatchesRequireReviewResponse> result = patientMergeHandler.getPotentialMatches(0, 10);

    verifyPotentialMatchesResponse(result);
  }

  @Test
  void testGetPotentialMatches_ReturnsEmptyListWhenNoMatchCandidates()  {
    // Mocking
    when(deduplicationTemplate.query(
        eq(QueryConstants.POSSIBLE_MATCH_PATIENTS),
        any(MapSqlParameterSource.class),
        ArgumentMatchers.<RowMapper<MatchCandidateData>>any()
    )).thenReturn(Collections.emptyList());

    // Act
    List<MatchesRequireReviewResponse> result = patientMergeHandler.getPotentialMatches(0, 10);

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
    MergeStatusRequest request = new MergeStatusRequest(100L, false);

    patientMergeHandler.updateMergeStatus(request);

    verify(deduplicationTemplate, times(1)).update(
        eq(QueryConstants.UPDATE_MERGE_STATUS_FOR_GROUP),
        any(MapSqlParameterSource.class)
    );
  }
}
