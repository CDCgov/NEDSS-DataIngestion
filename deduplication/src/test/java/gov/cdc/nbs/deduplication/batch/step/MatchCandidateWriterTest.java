package gov.cdc.nbs.deduplication.batch.step;

import static org.mockito.Mockito.*;

import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.constants.QueryConstants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MatchCandidateWriterTest {

  @Mock
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
  void writesChunkWithValidPossibleMatches() {
    List<MatchCandidate> candidates = List.of(
        new MatchCandidate("nbsId1", List.of("mpiId1")),
        new MatchCandidate("nbsId2", List.of("mpiId2", "mpiId3")));

    var chunk = new Chunk<>(candidates);

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.PERSON_UID_BY_MPI_PATIENT_ID),
        any(MapSqlParameterSource.class),
        eq(String.class)))
        .thenReturn("someNbsId");

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.MPI_PERSON_ID_QUERY),
        any(MapSqlParameterSource.class),
        eq(String.class)))
        .thenReturn("someMpiId");

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.FIND_POSSIBLE_MATCH),
        any(MapSqlParameterSource.class),
        eq(Integer.class)))
        .thenReturn(0);

    writer.write(chunk);

    verifySavePossibleMatch(times(3));
    verifyUpdateProcessedPerson();
  }

  @Test
  void skipsInvalidPossibleMatches() {
    List<MatchCandidate> candidates = List.of(new MatchCandidate("nbsId1", List.of("mpiId1", "mpiId2")));
    var chunk = new Chunk<>(candidates);

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.PERSON_UID_BY_MPI_PATIENT_ID),
        argThat((MapSqlParameterSource arg) -> "mpiId1".equals(arg.getValue("mpiId"))),
        eq(String.class)))
        .thenReturn("nbsId1"); // Same as original (not valid because it matches with itself)

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.PERSON_UID_BY_MPI_PATIENT_ID),
        argThat((MapSqlParameterSource arg) -> "mpiId2".equals(arg.getValue("mpiId"))),
        eq(String.class)))
        .thenReturn("someNbsId");

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.MPI_PERSON_ID_QUERY),
        any(MapSqlParameterSource.class),
        eq(String.class)))
        .thenReturn("someMpiId");

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.FIND_POSSIBLE_MATCH),
        argThat((MapSqlParameterSource arg) -> "someNbsId".equals(arg.getValue("personUid")) &&
            "someMpiId".equals(arg.getValue("mpiPersonId"))),
        eq(Integer.class)))
        .thenReturn(0); // No Existing match

    writer.write(chunk);

    verifySavePossibleMatch(times(1));
    verifyUpdateProcessedPerson();
  }

  @Test
  void skipsExistingPossibleMatches() {
    List<MatchCandidate> candidates = List.of(new MatchCandidate("nbsId1", List.of("mpiId1")));
    var chunk = new Chunk<>(candidates);

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.PERSON_UID_BY_MPI_PATIENT_ID),
        any(MapSqlParameterSource.class),
        eq(String.class)))
        .thenReturn("nbsId100");

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.MPI_PERSON_ID_QUERY),
        any(MapSqlParameterSource.class),
        eq(String.class)))
        .thenReturn("mpiId100");

    when(namedParameterJdbcTemplate.queryForObject(
        eq(QueryConstants.FIND_POSSIBLE_MATCH),
        argThat((MapSqlParameterSource arg) -> "nbsId100".equals(arg.getValue("personUid")) &&
            "mpiId100".equals(arg.getValue("mpiPersonId"))),
        eq(Integer.class)))
        .thenReturn(1); // Existing match

    writer.write(chunk);

    verifySavePossibleMatch(never());
    verifyUpdateProcessedPerson();
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
