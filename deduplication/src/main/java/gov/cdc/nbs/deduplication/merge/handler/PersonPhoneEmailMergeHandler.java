package gov.cdc.nbs.deduplication.merge.handler;

import java.util.List;
import java.util.Map;

import gov.cdc.nbs.deduplication.merge.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(5)
public class PersonPhoneEmailMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;

  static final String UPDATE_UN_SELECTED_PHONE_EMAIL_INACTIVE = """
      UPDATE Entity_locator_participation
      SET record_status_cd = 'INACTIVE',
         last_chg_time = GETDATE()
      WHERE entity_uid = :survivingId
        AND locator_uid NOT IN (:selectedLocators)
        AND class_cd = 'TELE';
      """;

  static final String INSERT_NEW_PHONE_EMAIL_LOCATORS = """
      INSERT INTO Entity_locator_participation (
          entity_uid,
          locator_uid,
          version_ctrl_nbr,
          add_reason_cd,
          add_time,
          add_user_id,
          cd,
          cd_desc_txt,
          class_cd,
          duration_amt,
          duration_unit_cd,
          from_time,
          last_chg_reason_cd,
          last_chg_time,
          last_chg_user_id,
          locator_desc_txt,
          record_status_cd,
          record_status_time,
          status_cd,
          status_time,
          to_time,
          use_cd,
          user_affiliation_txt,
          valid_time_txt,
          as_of_date
      )
      SELECT
          :survivingId,
          locator_uid,
          version_ctrl_nbr,
          add_reason_cd,
          add_time,
          add_user_id,
          cd,
          cd_desc_txt,
          class_cd,
          duration_amt,
          duration_unit_cd,
          from_time,
          last_chg_reason_cd,
          GETDATE(),
          last_chg_user_id,
          locator_desc_txt,
          record_status_cd,
          record_status_time,
          status_cd,
          status_time,
          to_time,
          use_cd,
          user_affiliation_txt,
          valid_time_txt,
          as_of_date
      FROM Entity_locator_participation
      WHERE locator_uid IN (:selectedLocators)
        AND entity_uid != :survivingId
        AND class_cd = 'TELE';
      """;

  static final String FIND_UNSELECTED_PHONE_EMAILS_FOR_AUDIT = """
      SELECT entity_uid, locator_uid, record_status_cd
      FROM Entity_locator_participation
      WHERE entity_uid = :survivingId
        AND locator_uid NOT IN (:selectedLocators)
        AND class_cd = 'TELE'
      """;

  static final String FIND_SELECTED_PHONE_EMAIL_LOCATORS_FOR_INSERT = """
      SELECT locator_uid
      FROM Entity_locator_participation
      WHERE locator_uid IN (:selectedLocators)
        AND entity_uid != :survivingId
        AND class_cd = 'TELE'
      """;

  public PersonPhoneEmailMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit patientMergeAudit) {
    mergePersonPhoneEmail(request, patientMergeAudit);
  }

  private void mergePersonPhoneEmail(PatientMergeRequest request, PatientMergeAudit audit) {
    String survivingId = request.survivingRecord();
    List<String> selectedLocators = extractSelectedLocatorIds(request);

    if (!selectedLocators.isEmpty()) {
      List<AuditUpdateAction> updateActions = performUnselectedPhoneEmailInactivation(survivingId, selectedLocators);
      List<AuditInsertAction> insertActions = performSelectedPhoneEmailCopy(survivingId, selectedLocators);

      audit.getRelatedTableAudits()
          .add(new RelatedTableAudit("Entity_locator_participation", updateActions, insertActions));
    }
  }

  private List<String> extractSelectedLocatorIds(PatientMergeRequest request) {
    return request.phoneEmails().stream()
        .map(PatientMergeRequest.PhoneEmailId::locatorId)
        .toList();
  }

  private List<AuditUpdateAction> performUnselectedPhoneEmailInactivation(String survivingId,
      List<String> selectedLocators) {
    Map<String, Object> params = Map.of(
        "survivingId", survivingId, //NOSONAR
        "selectedLocators", selectedLocators  //NOSONAR
    );

    List<Map<String, Object>> rowsToUpdate = fetchUnselectedPhoneEmailsForAudit(survivingId, selectedLocators);
    List<AuditUpdateAction> auditUpdates = buildUpdateActions(rowsToUpdate);

    nbsTemplate.update(UPDATE_UN_SELECTED_PHONE_EMAIL_INACTIVE, params);
    return auditUpdates;
  }

  private List<Map<String, Object>> fetchUnselectedPhoneEmailsForAudit(String survivingId,
      List<String> selectedLocators) {
    return nbsTemplate.queryForList(
        FIND_UNSELECTED_PHONE_EMAILS_FOR_AUDIT,
        Map.of("survivingId", survivingId, "selectedLocators", selectedLocators)
    );
  }

  private List<AuditUpdateAction> buildUpdateActions(List<Map<String, Object>> rows) {
    return rows.stream()
        .map(row -> new AuditUpdateAction(
            Map.of("entity_uid", row.get("entity_uid"), "locator_uid", row.get("locator_uid")),//NOSONAR
            Map.of("record_status_cd", row.get("record_status_cd"))
        ))
        .toList();
  }

  private List<AuditInsertAction> performSelectedPhoneEmailCopy(String survivingId, List<String> selectedLocators) {
    Map<String, Object> params = Map.of(
        "survivingId", survivingId,
        "selectedLocators", selectedLocators
    );

    List<Map<String, Object>> insertedRows = nbsTemplate.queryForList(
        FIND_SELECTED_PHONE_EMAIL_LOCATORS_FOR_INSERT, params
    );

    List<AuditInsertAction> insertActions = buildInsertActions(survivingId, insertedRows);

    nbsTemplate.update(INSERT_NEW_PHONE_EMAIL_LOCATORS, params);
    return insertActions;
  }

  private List<AuditInsertAction> buildInsertActions(String survivingId, List<Map<String, Object>> insertedRows) {
    return insertedRows.stream()
        .map(row -> new AuditInsertAction(Map.of(
            "entity_uid", survivingId,
            "locator_uid", row.get("locator_uid")
        )))
        .toList();
  }
}
