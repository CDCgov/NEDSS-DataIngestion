package gov.cdc.nbs.deduplication.batch.step;

import static org.mockito.Mockito.*;

import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.constants.QueryConstants;

import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MatchCandidateWriterTest {

  @Mock
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Mock
  private PatientRecordService patientRecordService;

  @InjectMocks
  private MatchCandidateWriter writer;

  @Test
  void writesChunkWithNoPossibleMatches() {
    List<MatchCandidate> candidates = new ArrayList<>();
    candidates.add(new MatchCandidate("nbsId1", null)); // No possible matches
    var chunk = new Chunk<>(candidates);

    writer.write(chunk);

    verifySavePossibleMatch(never());
    verifyUpdateProcessedPerson();
  }


  @Test
  @SuppressWarnings("unchecked")
  void writesChunkWithValidPossibleMatches() {
    List<MatchCandidate> candidates = List.of(
        new MatchCandidate("111", List.of("mpiId1")),
        new MatchCandidate("222", List.of("mpiId2", "mpiId3"))
    );
    var chunk = new Chunk<>(candidates);

    when(namedParameterJdbcTemplate.query(
        eq(QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS),
        any(MapSqlParameterSource.class),
        any(RowMapper.class)))
        .thenAnswer(invocation -> {
          List<String> mpiIds = (List<String>) ((MapSqlParameterSource) invocation.getArgument(1))
              .getValue("mpiIds");
          assert mpiIds != null;
          return mpiIds.stream()
              .map(id -> "123")
              .toList();
        });

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.FIND_POSSIBLE_MATCH),
        any(MapSqlParameterSource.class),
        eq(Integer.class)))
        .thenReturn(0);

    mockFetchPersonNameAndAddTime();

    writer.write(chunk);

    verifySavePossibleMatch(times(3));
    verifyUpdateProcessedPerson();
  }


  @Test
  @SuppressWarnings("unchecked")
  void skipsInvalidPossibleMatches() {
    List<MatchCandidate> candidates = List.of(
        new MatchCandidate("123", List.of("mpiId1", "mpiId2")));
    var chunk = new Chunk<>(candidates);

    when(namedParameterJdbcTemplate.query(
        eq(QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS),
        any(MapSqlParameterSource.class),
        any(RowMapper.class)))
        .thenAnswer(invocation -> {
          @SuppressWarnings("unchecked")
          List<String> mpiIds = (List<String>) ((MapSqlParameterSource) invocation.getArgument(1))
              .getValue("mpiIds");
          assert mpiIds != null;
          return mpiIds.stream()
              .map(id -> id.equals("mpiId1") ? "123" : "333")
              .toList();
        });

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.FIND_POSSIBLE_MATCH),
        any(MapSqlParameterSource.class),
        eq(Integer.class)))
        .thenReturn(0);

    mockFetchPersonNameAndAddTime();

    writer.write(chunk);

    verifySavePossibleMatch(times(1));
    verifyUpdateProcessedPerson();
  }


  @Test
  @SuppressWarnings("unchecked")
  void skipsExistingPossibleMatches() {
    List<MatchCandidate> candidates = List.of(
        new MatchCandidate("nbsId1", List.of("mpiId1")));
    var chunk = new Chunk<>(candidates);

    when(namedParameterJdbcTemplate.query(
        eq(QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS),
        any(MapSqlParameterSource.class),
        any(RowMapper.class)))
        .thenReturn(List.of("222"));

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.FIND_POSSIBLE_MATCH),
        any(MapSqlParameterSource.class),
        eq(Integer.class)))
        .thenReturn(1);

    mockFetchPersonNameAndAddTime();

    writer.write(chunk);

    verifySavePossibleMatch(never());
    verifyUpdateProcessedPerson();
  }

  private void mockFetchPersonNameAndAddTime() {
    when(patientRecordService.fetchPersonNameAndAddTime(anyString()))
        .thenReturn(new PatientNameAndTime("John Doe", LocalDateTime.now()));
  }

  private void verifyUpdateProcessedPerson() {
    verify(namedParameterJdbcTemplate, times(1)).update(
        eq(QueryConstants.UPDATE_PROCESSED_PERSONS),
        any(MapSqlParameterSource.class));
  }

  private void verifySavePossibleMatch(VerificationMode callingTimes) {
    verify(namedParameterJdbcTemplate, callingTimes).update(
        eq(QueryConstants.MATCH_CANDIDATES_QUERY),
        any(MapSqlParameterSource.class));
  }
}
