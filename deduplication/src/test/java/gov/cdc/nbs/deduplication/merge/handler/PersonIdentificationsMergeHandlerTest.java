package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import gov.cdc.nbs.deduplication.merge.model.RelatedTableAudit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    PatientMergeAudit audit = new PatientMergeAudit(new ArrayList<>());

    mockMaxSequenceQueryToReturn(2);
    mockAuditForInactiveIdentifications();
    mockAuditForSupersededIdentifications();

    handler.handleMerge(MATCH_ID, request, audit);

    verifyInactiveSurvivingIdentifications();
    verifySupersededIdentificationsMoves();
    verifyAuditData(audit);
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
  private void mockAuditForInactiveIdentifications() {
    when(nbsTemplate.queryForList(
        eq(PersonIdentificationsMergeHandler.FIND_UNSELECTED_IDENTIFICATIONS_FOR_AUDIT),
        any(Map.class)
    )).thenReturn(List.of(
        Map.of("entity_uid", SURVIVING_PERSON_UID, "entity_id_seq", 2, "record_status_cd", "ACTIVE")
    ));
  }

  @SuppressWarnings("unchecked")
  private void mockAuditForSupersededIdentifications() {
    when(nbsTemplate.queryForList(
        eq(PersonIdentificationsMergeHandler.FIND_SUPERSEDED_IDENTIFICATIONS_FOR_AUDIT),
        any(Map.class)
    )).thenReturn(List.of(
        Map.of("entity_uid", SUPERSEDED_PERSON_UID_1, "entity_id_seq", 1, "record_status_cd", "ACTIVE")
    ));
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

  private void verifyAuditData(PatientMergeAudit audit) {
    assertEquals(1, audit.getRelatedTableAudits().size());

    RelatedTableAudit tableAudit = audit.getRelatedTableAudits().getFirst();
    assertEquals("Entity_id", tableAudit.tableName());

    assertEquals(1, tableAudit.updates().size());
    assertEquals(2, tableAudit.inserts().size());
  }

  private PatientMergeRequest getPatientMergeRequest(List<PatientMergeRequest.IdentificationId> identifications) {
    return new PatientMergeRequest(SURVIVING_PERSON_UID, null, null, null,
        null, identifications, null, null, null, null, null);
  }
}
