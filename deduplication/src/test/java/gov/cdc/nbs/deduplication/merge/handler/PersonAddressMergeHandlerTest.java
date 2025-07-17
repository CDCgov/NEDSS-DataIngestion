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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonAddressMergeHandlerTest {

  @Mock
  NamedParameterJdbcTemplate nbsTemplate;

  PersonAddressMergeHandler handler;

  static final String SURVIVING_PERSON_UID = "survivor123";
  static final String SUPERSEDED_LOCATOR_ID_1 = "locator123";
  static final String SUPERSEDED_LOCATOR_ID_2 = "locator456";
  static final String MATCH_ID = "match-001";

  @BeforeEach
  void setUp() {
    handler = new PersonAddressMergeHandler(nbsTemplate);
  }

  @Test
  void handleMerge_shouldPerformAllPersonAddressRelatedDatabaseOperations() {
    PatientMergeRequest request = getPatientMergeRequest();
    PatientMergeAudit audit = new PatientMergeAudit(new ArrayList<>());

    mockAuditForUnselectedAddresses();
    mockSelectedLocatorsForInsert();

    handler.handleMerge(MATCH_ID, request, audit);

    verifyInactiveUnselectedAddresses();
    verifySelectedAddressInsertions();

    verifyAuditData(audit);
  }

  private PatientMergeRequest getPatientMergeRequest() {
    List<PatientMergeRequest.AddressId> addressIds = Arrays.asList(
        new PatientMergeRequest.AddressId(SUPERSEDED_LOCATOR_ID_1),
        new PatientMergeRequest.AddressId(SUPERSEDED_LOCATOR_ID_2)
    );
    return new PatientMergeRequest(SURVIVING_PERSON_UID, null,
        null, addressIds, null, null, null, null, null, null, null);
  }

  @SuppressWarnings("unchecked")
  private void mockAuditForUnselectedAddresses() {
    when(nbsTemplate.queryForList(
        eq(PersonAddressMergeHandler.FIND_UNSELECTED_ADDRESSES_FOR_AUDIT),
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
        eq(PersonAddressMergeHandler.FIND_SELECTED_LOCATORS_FOR_INSERT),
        any(Map.class)
    )).thenReturn(List.of(
        Map.of("locator_uid", SUPERSEDED_LOCATOR_ID_1),
        Map.of("locator_uid", SUPERSEDED_LOCATOR_ID_2)
    ));
  }

  @SuppressWarnings("unchecked")
  private void verifyInactiveUnselectedAddresses() {
    ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(nbsTemplate).update(
        eq(PersonAddressMergeHandler.UPDATE_UN_SELECTED_ADDRESS_INACTIVE),
        paramsCaptor.capture()
    );

    Map<String, Object> params = paramsCaptor.getValue();
    assertEquals(SURVIVING_PERSON_UID, params.get("survivingId"));
  }

  @SuppressWarnings("unchecked")
  private void verifySelectedAddressInsertions() {
    ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
    verify(nbsTemplate).update(
        eq(PersonAddressMergeHandler.INSERT_NEW_LOCATORS),
        paramsCaptor.capture()
    );

    Map<String, Object> params = paramsCaptor.getValue();
    assertEquals(SURVIVING_PERSON_UID, params.get("survivingId"));
    assertTrue(((List<String>) params.get("selectedLocators")).contains(SUPERSEDED_LOCATOR_ID_1));
  }

  private void verifyAuditData(PatientMergeAudit audit) {
    assertEquals(1, audit.getRelatedTableAudits().size());

    RelatedTableAudit tableAudit = audit.getRelatedTableAudits().getFirst();
    assertEquals("Entity_locator_participation", tableAudit.tableName());

    assertEquals(1, tableAudit.updates().size());
    assertEquals(2, tableAudit.inserts().size());
  }
}
