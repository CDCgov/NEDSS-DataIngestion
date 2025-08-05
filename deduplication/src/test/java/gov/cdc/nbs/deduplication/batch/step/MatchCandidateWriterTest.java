package gov.cdc.nbs.deduplication.batch.step;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.jdbc.support.KeyHolder;

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
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.FIND_MATCH_GROUP_CONTAINING_TARGET);
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
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.FIND_MATCH_GROUP_CONTAINING_TARGET);
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.SELECT_PERSON_UID_BY_MPI_ID);
    verify(patientRecordService, times(0)).fetchPersonNameAndAddTime("1234");
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.INSERT_MATCH_REQUIRING_REVIEW);

    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.UPDATE_STATUS_TO_P);
  }

  @Test
  void writesChunkWithPossibleMatchesExistingGroup() {
    // Mock
    mockGetPersonIds("mpiId", "4321");
    mockExistingMergeGroup(1l, "4321");
    mockEnsureGroupContainsPerson(true, 1l);

    LocalDateTime addTime = LocalDateTime.now();
    PatientNameAndTime patientNameAndTime = new PatientNameAndTime("localId", "Smith, John", addTime);
    when(patientRecordService.fetchPersonNameAndAddTime("1234")).thenReturn(patientNameAndTime);
    mockInsert("1234", patientNameAndTime, "4321", 1l);

    mockUpdateStatus(List.of("1234"));

    // Act
    List<MatchCandidate> candidates = new ArrayList<>();
    candidates.add(new MatchCandidate("1234", List.of("mpiId", "abcd")));
    var chunk = new Chunk<>(candidates);

    writer.write(chunk);

    // Verify
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.SELECT_PERSON_UID_BY_MPI_ID);
    verify(patientRecordService, times(1)).fetchPersonNameAndAddTime("1234");

    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.FIND_MATCH_GROUP_CONTAINING_TARGET);
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.DOES_GROUP_INCLUDES_PERSON_UID);
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.INSERT_MATCH_GROUP_ENTRY);
    verify(jdbcClient, times(0)).sql(MatchCandidateWriter.INSERT_MATCH_GROUP);
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.INSERT_MATCH_REQUIRING_REVIEW);
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.UPDATE_STATUS_TO_P);
  }

  @Test
  void writesChunkWithPossibleMatchesNewGroupNewPerson() {
    // Mock
    mockGetPersonIds("mpiId", "4321");
    mockExistingMergeGroup(null, "4321");
    mockCreateMergeGroup(1l);
    mockEnsureGroupContainsPerson(false, 1l);

    LocalDateTime addTime = LocalDateTime.now();
    PatientNameAndTime patientNameAndTime = new PatientNameAndTime("localId", "Smith, John", addTime);
    when(patientRecordService.fetchPersonNameAndAddTime("1234")).thenReturn(patientNameAndTime);
    mockInsert("1234", patientNameAndTime, "4321", 1l);

    mockUpdateStatus(List.of("1234"));

    // Act
    List<MatchCandidate> candidates = new ArrayList<>();
    candidates.add(new MatchCandidate("1234", List.of("mpiId", "abcd")));
    var chunk = new Chunk<>(candidates);

    writer.write(chunk);

    // Verify
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.SELECT_PERSON_UID_BY_MPI_ID);
    verify(patientRecordService, times(1)).fetchPersonNameAndAddTime("1234");

    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.FIND_MATCH_GROUP_CONTAINING_TARGET);
    verify(jdbcClient, times(2)).sql(MatchCandidateWriter.DOES_GROUP_INCLUDES_PERSON_UID);
    verify(jdbcClient, times(2)).sql(MatchCandidateWriter.INSERT_MATCH_GROUP_ENTRY);
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.INSERT_MATCH_GROUP);
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.INSERT_MATCH_REQUIRING_REVIEW);
    verify(jdbcClient, times(1)).sql(MatchCandidateWriter.UPDATE_STATUS_TO_P);
  }

  private void mockCreateMergeGroup(Long groupId) {
    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(jdbcClient.sql(MatchCandidateWriter.INSERT_MATCH_GROUP)).thenReturn(spec);

    when(spec.update(Mockito.any(KeyHolder.class))).thenAnswer(new Answer<Integer>() {
      public Integer answer(InvocationOnMock invocation) {
        KeyHolder kh = invocation.getArgument(0);
        kh.getKeyList().add(Collections.singletonMap("GENERATED_KEY", groupId));
        return 1;
      }
    });
  }

  private void mockEnsureGroupContainsPerson(boolean includesPerson, long mergeGroup) {
    // Mock check if person already exists
    StatementSpec spec = Mockito.mock(StatementSpec.class);

    when(jdbcClient.sql(MatchCandidateWriter.DOES_GROUP_INCLUDES_PERSON_UID)).thenReturn(spec);
    when(spec.param("mergeGroup", mergeGroup)).thenReturn(spec);
    when(spec.param(eq("personUid"), anyString())).thenReturn(spec);

    MappedQuerySpec<Boolean> mqs = Mockito.mock(MappedQuerySpec.class);
    when(spec.query(Boolean.class)).thenReturn(mqs);
    when(mqs.single()).thenReturn(includesPerson);

    if (!includesPerson) {
      // Mock insert
      StatementSpec insertSpec = Mockito.mock(StatementSpec.class);
      when(jdbcClient.sql(MatchCandidateWriter.INSERT_MATCH_GROUP_ENTRY)).thenReturn(insertSpec);
      when(insertSpec.param("mergeGroup", mergeGroup)).thenReturn(insertSpec);
      when(insertSpec.param(eq("personUid"), anyString())).thenReturn(insertSpec);
    }

  }

  private void mockExistingMergeGroup(Long mergeGroup, String personUid) {
    StatementSpec spec = Mockito.mock(StatementSpec.class);

    when(jdbcClient.sql(MatchCandidateWriter.FIND_MATCH_GROUP_CONTAINING_TARGET)).thenReturn(spec);
    when(spec.param("personUid", personUid)).thenReturn(spec);

    MappedQuerySpec<Long> mqs = Mockito.mock(MappedQuerySpec.class);
    when(spec.query(Long.class)).thenReturn(mqs);
    if (mergeGroup != null) {
      when(mqs.optional()).thenReturn(Optional.of(mergeGroup));
    } else {
      when(mqs.optional()).thenReturn(Optional.empty());
    }
  }

  private void mockInsert(
      String incomingPersonId,
      PatientNameAndTime patientData,
      String matchedPersonId,
      long mergeGroup) {
    StatementSpec spec = Mockito.mock(StatementSpec.class);
    when(jdbcClient.sql(MatchCandidateWriter.INSERT_MATCH_REQUIRING_REVIEW)).thenReturn(spec);

    when(spec.param("personUid", incomingPersonId)).thenReturn(spec);
    when(spec.param("personLocalId", patientData.personLocalId())).thenReturn(spec);
    when(spec.param("personName", patientData.name())).thenReturn(spec);
    when(spec.param("personAddTime", patientData.addTime())).thenReturn(spec);
    when(spec.param("matchedPersonUid", matchedPersonId)).thenReturn(spec);
    when(spec.param("mergeGroup", mergeGroup)).thenReturn(spec);
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
