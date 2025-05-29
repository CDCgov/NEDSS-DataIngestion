package gov.cdc.nbs.deduplication.batch.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import gov.cdc.nbs.deduplication.batch.model.LinkResult;
import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.model.MatchResponse;
import gov.cdc.nbs.deduplication.batch.service.DuplicateCheckService;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DuplicatesProcessorTest {

  @Mock
  private PatientRecordService patientRecordService;

  @Mock
  private DuplicateCheckService recordLinkerService;

  @InjectMocks
  private DuplicatesProcessor duplicatesProcessor;

  @Test
  void processReturnsMatchCandidateForPossibleMatch() {
    String personUid = "1234";
    MpiPerson patientRecord = new MpiPerson(null, null, null, null,
        null, null, null, null, null);
    MatchResponse response = mock(MatchResponse.class);
    LinkResult linkResult = mock(LinkResult.class);
    List<LinkResult> linkResults = Collections.singletonList(linkResult);

    when(patientRecordService.fetchMostRecentPatient(personUid)).thenReturn(patientRecord);
    when(recordLinkerService.findDuplicateRecords(any(MpiPerson.class))).thenReturn(response);
    when(response.prediction()).thenReturn(MatchResponse.Prediction.POSSIBLE_MATCH);
    when(response.results()).thenReturn(linkResults);
    when(linkResult.personReferenceId()).thenReturn(UUID.randomUUID());

    MatchCandidate result = duplicatesProcessor.process(personUid);

    assertThat(result).isNotNull();
    assertThat(result.personUid()).isEqualTo(personUid);
    assertThat(result.personUid()).isNotNull();
    assertThat(result.possibleMatchList()).hasSize(1);
  }

  @Test
  void processReturnsMatchCandidateWithNullForNoMatch() {
    String personUid = "1234";
    MpiPerson patientRecord = new MpiPerson(null, null, null, null,
        null, null, null, null, null);
    MatchResponse response = mock(MatchResponse.class);

    when(patientRecordService.fetchMostRecentPatient(personUid)).thenReturn(patientRecord);
    when(recordLinkerService.findDuplicateRecords(patientRecord)).thenReturn(response);
    when(response.prediction()).thenReturn(MatchResponse.Prediction.NO_MATCH);

    MatchCandidate result = duplicatesProcessor.process(personUid);

    assertThat(result).isNotNull();
    assertThat(result.personUid()).isEqualTo(personUid);
    assertThat(result.possibleMatchList()).isNull();
  }

  @Test
  void processReturnsMatchCandidateForMatch() {
    String personUid = "1234";
    MpiPerson patientRecord = new MpiPerson(null, null, null, null,
        null, null, null, null, null);
    MatchResponse response = mock(MatchResponse.class);
    LinkResult linkResult = mock(LinkResult.class);
    List<LinkResult> linkResults = Collections.singletonList(linkResult);

    when(patientRecordService.fetchMostRecentPatient(personUid)).thenReturn(patientRecord);
    when(recordLinkerService.findDuplicateRecords(any(MpiPerson.class))).thenReturn(response);
    when(response.prediction()).thenReturn(MatchResponse.Prediction.MATCH);
    when(response.results()).thenReturn(linkResults);
    when(linkResult.personReferenceId()).thenReturn(UUID.randomUUID());

    MatchCandidate result = duplicatesProcessor.process(personUid);

    assertThat(result).isNotNull();
    assertThat(result.personUid()).isEqualTo(personUid);
    assertThat(result.personUid()).isNotNull();
    assertThat(result.possibleMatchList()).hasSize(1);
  }

}
