package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonIdentificationsMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private PersonIdentificationsMergeHandler handler;

  private static final String SURVIVING_PERSON_UID = "100";
  private static final String SUPERSEDED_PERSON_UID_1 = "200";
  private static final String SUPERSEDED_PERSON_UID_2 = "300";
  private static final String MATCH_ID = "123";

  @BeforeEach
  void setUp() {
    handler = new PersonIdentificationsMergeHandler(nbsTemplate);
  }

  @Test
  void handleMerge_shouldPerformAllPersonIdentificationRelatedDatabaseOperations() {
    // Surviving person has seq 1 (selected) and seq 2 (not selected)
    // Superseded persons each have seq 1 to move
    List<PatientMergeRequest.IdentificationId> identifications = Arrays.asList(
        new PatientMergeRequest.IdentificationId(SURVIVING_PERSON_UID, "1"),
        new PatientMergeRequest.IdentificationId(SUPERSEDED_PERSON_UID_1, "1"),
        new PatientMergeRequest.IdentificationId(SUPERSEDED_PERSON_UID_2, "1")
    );

    PatientMergeRequest request = getPatientMergeRequest(identifications);

    mockMaxSequenceQueryToReturn(2);
    handler.handleMerge(MATCH_ID, request);
    verifyInactiveSurvivingIdentifications();
    verifySupersededIdentificationsMoves();
  }

  @SuppressWarnings("unchecked")
  private void mockMaxSequenceQueryToReturn(int maxSequence) {
    when(nbsTemplate.queryForObject(
        eq(PersonIdentificationsMergeHandler.FIND_MAX_SEQUENCE_PERSON_IDENTIFICATION),
        any(Map.class),
        eq(Integer.class)
    )).thenReturn(maxSequence);
  }

  @SuppressWarnings("unchecked")
  private void verifyInactiveSurvivingIdentifications() {
    ArgumentCaptor<Map<String, Object>> inactiveParamsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(nbsTemplate).update(
        eq(PersonIdentificationsMergeHandler.UPDATE_SELECTED_EXCLUDED_IDENTIFICATION_INACTIVE),
        inactiveParamsCaptor.capture()
    );
  }

  @SuppressWarnings("unchecked")
  private void verifySupersededIdentificationsMoves() {
    ArgumentCaptor<Map<String, Object>> moveParamsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(nbsTemplate, times(2)).update(
        eq(PersonIdentificationsMergeHandler.COPY_ENTITY_ID_TO_SURVIVING),
        moveParamsCaptor.capture()
    );
  }

  private PatientMergeRequest getPatientMergeRequest(List<PatientMergeRequest.IdentificationId> identifications) {
    return new PatientMergeRequest(SURVIVING_PERSON_UID, null, null, null,
        null, identifications, null,null);
  }
}
