package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@Component
@Order(9)
public class PersonMortalityMergeHandler implements SectionMergeHandler {


  static final String UPDATE_PERSON_AS_OF_DEATH = """
      UPDATE person
      SET as_of_date_morbidity = (
              SELECT as_of_date_morbidity
              FROM person
              WHERE person_uid = :sourceId
          ),
          last_chg_time = GETDATE(),
          last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_DECEASED_IND = """
      UPDATE person
      SET deceased_ind_cd = (
              SELECT deceased_ind_cd
              FROM person
              WHERE person_uid = :sourceId
          ),
          last_chg_time = GETDATE(),
          last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_DATE_OF_DEATH = """
      UPDATE person
      SET deceased_time = (
              SELECT deceased_time
              FROM person
              WHERE person_uid = :sourceId
          ),
          last_chg_time = GETDATE(),
          last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;
  static final String UPDATE_UN_SELECTED_DEATH_ADDRESS_INACTIVE = """
      UPDATE Entity_locator_participation
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE(),
          last_chg_reason_cd = 'merge'
      WHERE entity_uid = :survivingId
        AND use_cd = 'DTH'
        AND class_cd = 'PST'
      """;

  static final String COPY_DEATH_ADDRESS_FROM_SUPERSEDED_TO_SURVIVING = """
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
          'merge',
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
      WHERE entity_uid = :sourceId
        AND use_cd = 'DTH'
        AND class_cd = 'PST'
      """;

  static final String FIND_DEATH_ADDRESS_LOCATOR_UID = """
      SELECT locator_uid
      FROM Entity_locator_participation
      WHERE entity_uid = :sourceId
        AND use_cd = 'DTH'
        AND class_cd = 'PST'
      """;

  static final String FIND_UNSELECTED_DEATH_ADDRESS_FOR_AUDIT = """
      SELECT entity_uid, locator_uid, record_status_cd
      FROM Entity_locator_participation
      WHERE entity_uid = :survivingId
        AND class_cd = 'PST'
        AND use_cd = 'DTH';
      """;

  final NamedParameterJdbcTemplate nbsTemplate;

  public PersonMortalityMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  //Merge modifications have been applied to the person Mortality
  @Override
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit patientMergeAudit) {
    mergePersonMortality(request.survivingRecord(), request.mortalityFieldSource(), patientMergeAudit);
  }

  private void mergePersonMortality(String survivorId, PatientMergeRequest.MortalityFieldSource fieldSource,
      PatientMergeAudit audit) {
    updateAsOf(survivorId, fieldSource.asOfSource());
    updateDeceasedFlag(survivorId, fieldSource.deceasedSource());
    updateDateOfDeath(survivorId, fieldSource.dateOfDeathSource());
    updateDeathAddress(survivorId, fieldSource.deathAddressSource(), audit);
  }

  private void updateAsOf(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_AS_OF_DEATH);
    }
  }

  private void updateDeceasedFlag(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_DECEASED_IND);
    }
  }

  private void updateDateOfDeath(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_DATE_OF_DEATH);
    }
  }

  private void updateDeathAddress(String survivorId, String sourceId, PatientMergeAudit audit) {
    if (survivorId.equals(sourceId)) {
      return;
    }

    List<AuditUpdateAction> updateActions = performDeathAddressInactivation(survivorId);
    List<AuditInsertAction> insertActions = performDeathAddressCopy(survivorId, sourceId);

    audit.getRelatedTableAudits().add(
        new RelatedTableAudit("Entity_locator_participation", updateActions, insertActions)
    );
  }

  private List<AuditUpdateAction> performDeathAddressInactivation(String survivorId) {
    Map<String, Object> params = Collections.singletonMap("survivingId", survivorId);

    List<Map<String, Object>> rowsToUpdate = nbsTemplate.queryForList(
        FIND_UNSELECTED_DEATH_ADDRESS_FOR_AUDIT, params
    );

    List<AuditUpdateAction> auditUpdates = buildUpdateActions(rowsToUpdate);

    nbsTemplate.update(UPDATE_UN_SELECTED_DEATH_ADDRESS_INACTIVE, params);
    return auditUpdates;
  }

  private List<AuditUpdateAction> buildUpdateActions(List<Map<String, Object>> rows) {
    return rows.stream()
        .map(row -> new AuditUpdateAction(
            Map.of("entity_uid", row.get("entity_uid"), "locator_uid", row.get("locator_uid")),//NOSONAR
            Map.of("record_status_cd", row.get("record_status_cd"))
        ))
        .toList();
  }

  private List<AuditInsertAction> performDeathAddressCopy(String survivorId, String sourceId) {
    Map<String, Object> params = Map.of("survivingId", survivorId, "sourceId", sourceId);

    List<Map<String, Object>> insertedRows = nbsTemplate.queryForList(
        FIND_DEATH_ADDRESS_LOCATOR_UID, params
    );

    List<AuditInsertAction> insertActions = buildInsertActions(survivorId, insertedRows);

    nbsTemplate.update(COPY_DEATH_ADDRESS_FROM_SUPERSEDED_TO_SURVIVING, params);

    return insertActions;
  }

  private List<AuditInsertAction> buildInsertActions(String survivorId, List<Map<String, Object>> insertedRows) {
    return insertedRows.stream()
        .map(row -> new AuditInsertAction(Map.of(
            "entity_uid", survivorId,
            "locator_uid", row.get("locator_uid")
        )))
        .toList();
  }


  private void updatePersonField(String survivorId, String sourceId, String query) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("sourceId", sourceId);
    nbsTemplate.update(query, params);
  }



}
