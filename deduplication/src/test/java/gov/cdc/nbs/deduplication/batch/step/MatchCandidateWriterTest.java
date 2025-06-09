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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

    verifyInsertMatchGroup(never());
    verifyUpdateProcessedPerson();
  }

  @Test
  void writesChunkWithValidPossibleMatches() {
    String personUid = "123";
    String possibleMatchUid = "456";
    MatchCandidate candidate = new MatchCandidate(
        personUid,
        List.of(possibleMatchUid));

    mockFetchPersonNameAndAddTime();
    mockGetPersonIdsByMpiIds();
    mockInsertMatchGroup();

    writer.write(new Chunk<>(List.of(candidate)));

    verifyInsertMatchGroup();
    verifyGetPersonIdsByMpiIds();
    verifyUpdateProcessedPerson();
  }

  private void mockFetchPersonNameAndAddTime() {
    when(patientRecordService.fetchPersonNameAndAddTime(anyString()))
        .thenReturn(new PatientNameAndTime("123", "John Doe", LocalDateTime.now()));
  }

  @SuppressWarnings("unchecked")
  private void mockGetPersonIdsByMpiIds() {
    when(namedParameterJdbcTemplate.query(
        eq(MatchCandidateWriter.PERSON_UIDS_BY_MPI_PATIENT_IDS),
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
  }

  private void mockInsertMatchGroup() {
    Mockito.doAnswer(invocation -> {
      KeyHolder keyHolder = invocation.getArgument(2);
      keyHolder.getKeyList().add(Collections.singletonMap("GENERATED_KEY", 100L));
      return null;
    }).when(namedParameterJdbcTemplate)
        .update(eq(MatchCandidateWriter.INSERT_MATCH_GROUP), any(MapSqlParameterSource.class), any(KeyHolder.class));
  }

  private void verifyInsertMatchGroup() {
    verify(namedParameterJdbcTemplate).update(
        eq(MatchCandidateWriter.INSERT_MATCH_GROUP),
        any(MapSqlParameterSource.class),
        any(KeyHolder.class));
  }

  private void verifyGetPersonIdsByMpiIds() {
    verify(namedParameterJdbcTemplate).update(
        eq(MatchCandidateWriter.INSERT_MATCH_CANDIDATE),
        any(MapSqlParameterSource.class));
  }

  private void verifyInsertMatchGroup(VerificationMode callingTimes) {
    verify(namedParameterJdbcTemplate, callingTimes).update(
        eq(MatchCandidateWriter.INSERT_MATCH_GROUP),
        any(MapSqlParameterSource.class));
  }

  private void verifyUpdateProcessedPerson() {
    verify(namedParameterJdbcTemplate, times(1)).update(
        eq(QueryConstants.UPDATE_PROCESSED_PERSONS),
        any(MapSqlParameterSource.class));
  }
}
