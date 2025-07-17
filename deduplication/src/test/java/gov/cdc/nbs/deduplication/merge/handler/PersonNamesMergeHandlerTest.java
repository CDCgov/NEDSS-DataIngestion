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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    PatientMergeAudit audit = new PatientMergeAudit(new ArrayList<>());

    mockMaxSequenceQueryToReturn(2);
    mockAuditForInactiveNames();
    mockAuditForSupersededNames();

    handler.handleMerge(MATCH_ID, request, audit);
    verifyInactiveSurvivingNames();
    verifySupersededNameMoves();
    verifyAuditData(audit);
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
        eq(PersonNamesMergeHandler.COPY_PERSON_NAME_TO_SURVIVING),
        moveParamsCaptor.capture()
    );
  }


  @SuppressWarnings("unchecked")
  private void mockAuditForInactiveNames() {
    when(nbsTemplate.queryForList(
        eq(PersonNamesMergeHandler.FIND_EXCLUDED_PERSON_NAMES_FOR_INACTIVATION),
        any(Map.class)
    )).thenReturn(List.of(
        Map.of("person_uid", SURVIVING_PERSON_UID, "person_name_seq", 2, "record_status_cd", "ACTIVE")
    ));
  }

  @SuppressWarnings("unchecked")
  private void mockAuditForSupersededNames() {
    when(nbsTemplate.queryForList(
        eq(PersonNamesMergeHandler.FIND_SUPERSEDED_NAMES_FOR_AUDIT),
        any(Map.class)
    )).thenReturn(List.of(
        Map.of("person_uid", SUPERSEDED_PERSON_UID_1, "person_name_seq", 1, "record_status_cd", "ACTIVE")
    ));
  }

  private void verifyAuditData(PatientMergeAudit audit) {
    assertEquals(1, audit.getRelatedTableAudits().size());

    RelatedTableAudit tableAudit = audit.getRelatedTableAudits().getFirst();
    assertEquals("person_name", tableAudit.tableName());

    assertEquals(1, tableAudit.updates().size());
    assertEquals(2, tableAudit.inserts().size());
  }

  private PatientMergeRequest getPatientMergeRequest(List<PatientMergeRequest.NameId> names) {
    return new PatientMergeRequest(SURVIVING_PERSON_UID, null, names, null, null, null, null, null, null, null, null);
  }
}
