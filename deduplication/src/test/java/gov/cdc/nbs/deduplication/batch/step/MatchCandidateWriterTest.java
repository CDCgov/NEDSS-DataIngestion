package gov.cdc.nbs.deduplication.batch.step;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;

import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.merge.model.PatientNameAndTime;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class MatchCandidateWriterTest {

  @Mock
  private JdbcClient jdbcClient;

  @Mock
  private PatientRecordService patientRecordService;

  @InjectMocks
  private MatchCandidateWriter writer;

  @Test
  void writesChunkWithNullPossibleMatches() {
    // Mock
    mockUpdateStatus(List.of("nbsId1"));

    // Act
    List<MatchCandidate> candidates = new ArrayList<>();
    candidates.add(new MatchCandidate("nbsId1", null)); // No possible matches
    var chunk = new Chunk<>(candidates);

    writer.write(chunk);

    // Verify
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.SELECT_PERSON_UID_BY_MPI_ID);
    verify(patientRecordService, times(0)).fetchPersonNameAndAddTime("1234");
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.INSERT_MATCH_REQUIRING_REVIEW);

    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.UPDATE_STATUS_TO_P);
  }

  @Test
  void writesChunkWithNoPossibleMatches() {
    // Mock
    mockUpdateStatus(List.of("nbsId1"));

    // Act
    List<MatchCandidate> candidates = new ArrayList<>();
    candidates.add(new MatchCandidate("nbsId1", List.of())); // No possible matches
    var chunk = new Chunk<>(candidates);

    writer.write(chunk);

    // Verify
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.SELECT_PERSON_UID_BY_MPI_ID);
    verify(patientRecordService, times(0)).fetchPersonNameAndAddTime("1234");
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.INSERT_MATCH_REQUIRING_REVIEW);

    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.UPDATE_STATUS_TO_P);
  }

  @Test
  void writesChunkWithPossibleMatches() {
    // Mock
    mockGetPersonIds("mpiId", "4321");

    LocalDateTime addTime = LocalDateTime.now();
    PatientNameAndTime patientNameAndTime = new PatientNameAndTime("localId", "Smith, John", addTime);
    when(patientRecordService.fetchPersonNameAndAddTime("1234")).thenReturn(patientNameAndTime);
    mockInsert("1234", patientNameAndTime, "4321");

    mockUpdateStatus(List.of("1234"));

    // Act
    List<MatchCandidate> candidates = new ArrayList<>();
    candidates.add(new MatchCandidate("1234", List.of("mpiId", "abcd")));
    var chunk = new Chunk<>(candidates);

    writer.write(chunk);

    // Verify
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.SELECT_PERSON_UID_BY_MPI_ID);
    verify(patientRecordService, times(1)).fetchPersonNameAndAddTime("1234");
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.INSERT_MATCH_REQUIRING_REVIEW);
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.UPDATE_STATUS_TO_P);
  }

  private void mockInsert(String incomingPersonId, PatientNameAndTime patientData, String matchedPersonId) {
    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(jdbcClient.sql(MatchCandidateWriter.INSERT_MATCH_REQUIRING_REVIEW)).thenReturn(spec);

    when(spec.param("personUid", incomingPersonId)).thenReturn(spec);
    when(spec.param("personLocalId", patientData.personLocalId())).thenReturn(spec);
    when(spec.param("personName", patientData.name())).thenReturn(spec);
    when(spec.param("personAddTime", patientData.addTime())).thenReturn(spec);
    when(spec.param("matchedPersonUid", matchedPersonId)).thenReturn(spec);
    when(spec.param(Mockito.eq("identifiedDate"), Mockito.any())).thenReturn(spec);
  }

  private void mockGetPersonIds(String mpiId, String nbsId) {
    StatementSpec spec = Mockito.mock(StatementSpec.class);

    when(jdbcClient.sql(MatchCandidateWriter.SELECT_PERSON_UID_BY_MPI_ID)).thenReturn(spec);
    when(spec.param("mpiId", mpiId)).thenReturn(spec);

    MappedQuerySpec<String> mqs = Mockito.mock(MappedQuerySpec.class);
    when(spec.query(String.class)).thenReturn(mqs);
    when(mqs.single()).thenReturn(nbsId);
  }

  private void mockUpdateStatus(List<String> personIds) {
    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(jdbcClient.sql(MatchCandidateWriter.UPDATE_STATUS_TO_P)).thenReturn(spec);
    when(spec.param("personIds", personIds)).thenReturn(spec);

  }

}
