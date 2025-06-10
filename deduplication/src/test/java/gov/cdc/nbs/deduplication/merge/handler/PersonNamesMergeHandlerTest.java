package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonNamesMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private PersonNamesMergeHandler handler;

  private static final String SURVIVING_PERSON_UID = "100";
  private static final String SUPERSEDED_PERSON_UID_1 = "200";
  private static final String SUPERSEDED_PERSON_UID_2 = "300";
  private static final String MATCH_ID = "123";

  @BeforeEach
  void setUp() {
    handler = new PersonNamesMergeHandler(nbsTemplate);
  }

  @Test
  void handleMerge_shouldPerformAllPersonNameRelatedDatabaseOperations() {
    // Surviving person has seq 1 (selected) and seq 2 (not selected)
    // Superseded persons each have seq 1 to move
    List<PatientMergeRequest.NameId> names = Arrays.asList(
        new PatientMergeRequest.NameId(SURVIVING_PERSON_UID, "1"),
        new PatientMergeRequest.NameId(SUPERSEDED_PERSON_UID_1, "1"),
        new PatientMergeRequest.NameId(SUPERSEDED_PERSON_UID_2, "1")
    );

    PatientMergeRequest request = getPatientMergeRequest(names);

    mockMaxSequenceQueryToReturn(2);
    handler.handleMerge(MATCH_ID, request);
    verifyInactiveSurvivingNames();
    verifySupersededNameMoves();
  }

  @SuppressWarnings("unchecked")
  private void mockMaxSequenceQueryToReturn(int maxSequence) {
    when(nbsTemplate.queryForObject(
        eq(PersonNamesMergeHandler.FIND_MAX_SEQUENCE_PERSON_NAME),
        any(Map.class),
        eq(Integer.class)
    )).thenReturn(maxSequence);
  }

  @SuppressWarnings("unchecked")
  private void verifyInactiveSurvivingNames() {
    ArgumentCaptor<Map<String, Object>> inactiveParamsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(nbsTemplate).update(
        eq(PersonNamesMergeHandler.UPDATE_SELECTED_EXCLUDED_NAMES_INACTIVE),
        inactiveParamsCaptor.capture()
    );
  }

  @SuppressWarnings("unchecked")
  private void verifySupersededNameMoves() {
    ArgumentCaptor<Map<String, Object>> moveParamsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(nbsTemplate, times(2)).update(
        eq(PersonNamesMergeHandler.UPDATE_SUPERSEDED_NAME_TO_SURVIVING),
        moveParamsCaptor.capture()
    );
  }

  private PatientMergeRequest getPatientMergeRequest(List<PatientMergeRequest.NameId> names) {
    return new PatientMergeRequest(SURVIVING_PERSON_UID, null, names, null, null, null, null);
  }
}
