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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonPhoneEmailMergeHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsTemplate;

  private PersonPhoneEmailMergeHandler handler;

  static final String SURVIVING_PERSON_UID = "survivor123";
  static final String PHONE_EMAIL_LOCATOR_1 = "locator1";
  static final String PHONE_EMAIL_LOCATOR_2 = "locator2";
  static final String MATCH_ID = "match-001";

  @BeforeEach
  void setUp() {
    handler = new PersonPhoneEmailMergeHandler(nbsTemplate);
  }

  @Test
  void handleMerge_shouldPerformAllPersonPhoneEmailRelatedDatabaseOperations() {
    // Prepare request and audit
    PatientMergeRequest request = getPatientMergeRequest();
    PatientMergeAudit audit = new PatientMergeAudit(new ArrayList<>());

    // Mock DB queries
    mockAuditForUnselectedPhoneEmails();
    mockSelectedLocatorsForInsert();

    // Call the method under test
    handler.handleMerge(MATCH_ID, request, audit);

    // Verify DB operations
    verifyInactiveUnselectedPhoneEmails();
    verifySelectedPhoneEmailInsertions();

    // Verify audit data
    verifyAuditData(audit);
  }

  private PatientMergeRequest getPatientMergeRequest() {
    List<PatientMergeRequest.PhoneEmailId> phoneEmailIds = List.of(
        new PatientMergeRequest.PhoneEmailId(PHONE_EMAIL_LOCATOR_1),
        new PatientMergeRequest.PhoneEmailId(PHONE_EMAIL_LOCATOR_2)
    );

    return new PatientMergeRequest(
        PersonPhoneEmailMergeHandlerTest.SURVIVING_PERSON_UID, null, null, null,
        phoneEmailIds, null, null, null, null, null, null
    );
  }

  @SuppressWarnings("unchecked")
  private void mockAuditForUnselectedPhoneEmails() {
    when(nbsTemplate.queryForList(
        eq(PersonPhoneEmailMergeHandler.FIND_UNSELECTED_PHONE_EMAILS_FOR_AUDIT),
        any(Map.class)
    )).thenReturn(List.of(
        Map.of(
            "entity_uid", SURVIVING_PERSON_UID,
            "locator_uid", "locator789",
            "record_status_cd", "ACTIVE"
        )
    ));
  }

  @SuppressWarnings("unchecked")
  private void mockSelectedLocatorsForInsert() {
    when(nbsTemplate.queryForList(
        eq(PersonPhoneEmailMergeHandler.FIND_SELECTED_PHONE_EMAIL_LOCATORS_FOR_INSERT),
        any(Map.class)
    )).thenReturn(List.of(
        Map.of("locator_uid", PHONE_EMAIL_LOCATOR_1),
        Map.of("locator_uid", PHONE_EMAIL_LOCATOR_2)
    ));
  }

  @SuppressWarnings("unchecked")
  private void verifyInactiveUnselectedPhoneEmails() {
    ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(nbsTemplate).update(
        eq(PersonPhoneEmailMergeHandler.UPDATE_UN_SELECTED_PHONE_EMAIL_INACTIVE),
        paramsCaptor.capture()
    );

    Map<String, Object> params = paramsCaptor.getValue();
    assertEquals(SURVIVING_PERSON_UID, params.get("survivingId"));
    assertTrue(((List<String>) params.get("selectedLocators"))
        .containsAll(List.of(PHONE_EMAIL_LOCATOR_1, PHONE_EMAIL_LOCATOR_2)));
  }

  @SuppressWarnings("unchecked")
  private void verifySelectedPhoneEmailInsertions() {
    ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(nbsTemplate).update(
        eq(PersonPhoneEmailMergeHandler.INSERT_NEW_PHONE_EMAIL_LOCATORS),
        paramsCaptor.capture()
    );

    Map<String, Object> params = paramsCaptor.getValue();
    assertEquals(SURVIVING_PERSON_UID, params.get("survivingId"));
    assertTrue(((List<String>) params.get("selectedLocators"))
        .containsAll(List.of(PHONE_EMAIL_LOCATOR_1, PHONE_EMAIL_LOCATOR_2)));
  }

  private void verifyAuditData(PatientMergeAudit audit) {
    assertEquals(1, audit.getRelatedTableAudits().size());

    RelatedTableAudit tableAudit = audit.getRelatedTableAudits().getFirst();
    assertEquals("Entity_locator_participation", tableAudit.tableName());

    assertEquals(1, tableAudit.updates().size());
    assertEquals(2, tableAudit.inserts().size());
  }
}
