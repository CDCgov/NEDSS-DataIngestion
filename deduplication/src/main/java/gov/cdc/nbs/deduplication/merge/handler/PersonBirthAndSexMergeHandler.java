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
@Order(8)
public class PersonBirthAndSexMergeHandler implements SectionMergeHandler {

  static final String UPDATE_PERSON_AS_OF_SEX = """
      UPDATE person
      SET as_of_date_sex = (
          SELECT as_of_date_sex
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_BIRTH_TIME = """
      UPDATE person
      SET birth_time = (
          SELECT birth_time
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_ADDITIONAL_GENDER = """
      UPDATE person
      SET additional_gender_cd = (
          SELECT additional_gender_cd
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_BIRTH_ORDER = """
      UPDATE person
      SET birth_order_nbr = (
          SELECT birth_order_nbr
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_CURRENT_SEX_CD = """
      UPDATE person
      SET curr_sex_cd = (
          SELECT curr_sex_cd
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_SEX_UNKNOWN_CD = """
      UPDATE person
      SET sex_unk_reason_cd = (
          SELECT sex_unk_reason_cd
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_TRANSGENDER_CD = """
      UPDATE person
      SET preferred_gender_cd = (
          SELECT preferred_gender_cd
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_BIRTH_GENDER_CD = """
      UPDATE person
      SET birth_gender_cd = (
          SELECT birth_gender_cd
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_MULTIPLE_BIRTH_IND = """
      UPDATE person
      SET multiple_birth_ind = (
          SELECT multiple_birth_ind
          FROM person
          WHERE person_uid = :sourceId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
      WHERE person_uid = :survivorId
      """;

  static final String UPDATE_UN_SELECTED_BIRTH_ADDRESS_INACTIVE = """
      UPDATE Entity_locator_participation
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE(),
          last_chg_reason_cd = 'merge'
      WHERE entity_uid = :survivingId
        AND class_cd = 'PST'
        AND use_cd = 'BIR';
      """;

  public static final String COPY_BIRTH_ADDRESS_FROM_SUPERSEDED_TO_SURVIVING = """
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
        AND use_cd     = 'BIR'
        AND class_cd   = 'PST'
      """;

  private static final String FIND_BIRTH_ADDRESS_LOCATOR_UID = """
      SELECT locator_uid
      FROM Entity_locator_participation
      WHERE entity_uid = :sourceId
        AND use_cd = 'BIR'
        AND class_cd = 'PST'
      """;

  private static final String FIND_UNSELECTED_BIRTH_ADDRESS_FOR_AUDIT = """
      SELECT entity_uid, locator_uid, record_status_cd
      FROM Entity_locator_participation
      WHERE entity_uid = :survivingId
          AND class_cd = 'PST'
          AND use_cd = 'BIR';
      """;


  final NamedParameterJdbcTemplate nbsTemplate;

  public PersonBirthAndSexMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  //Merge modifications have been applied to the person birth and sex
  @Override
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit patientMergeAudit) {
    mergePersonBirthAndSex(request.survivingRecord(), request.sexAndBirthFieldSource(), patientMergeAudit);
  }

  private void mergePersonBirthAndSex(String survivorId, PatientMergeRequest.SexAndBirthFieldSource fieldSource
      , PatientMergeAudit patientMergeAudit) {
    updateAsOf(survivorId, fieldSource.asOfSource());
    updateDateOfBirth(survivorId, fieldSource.dateOfBirthSource());
    updateAdditionalGender(survivorId, fieldSource.additionalGenderSource());
    updateBirthOrder(survivorId, fieldSource.birthOrderSource());
    updateCurrentSex(survivorId, fieldSource.currentSexSource());
    updateSexUnknown(survivorId, fieldSource.sexUnknownSource());
    updateTransgender(survivorId, fieldSource.transgenderSource());
    updateBirthGender(survivorId, fieldSource.birthGenderSource());
    updateMultipleBirth(survivorId, fieldSource.multipleBirthSource());
    updateBirthAddress(survivorId, fieldSource.birthAddressSource(), patientMergeAudit);
  }

  private void updateAsOf(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_AS_OF_SEX);
    }
  }

  private void updateDateOfBirth(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_BIRTH_TIME);
    }
  }

  private void updateAdditionalGender(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_ADDITIONAL_GENDER);
    }
  }

  private void updateBirthOrder(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_BIRTH_ORDER);
    }
  }

  private void updateCurrentSex(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_CURRENT_SEX_CD);
    }
  }

  private void updateSexUnknown(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_SEX_UNKNOWN_CD);
    }
  }

  private void updateTransgender(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_TRANSGENDER_CD);
    }
  }

  private void updateBirthGender(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_BIRTH_GENDER_CD);
    }
  }

  private void updateMultipleBirth(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_MULTIPLE_BIRTH_IND);
    }
  }


  private void updateBirthAddress(String survivorId, String sourceId, PatientMergeAudit audit) {
    if (survivorId.equals(sourceId)) {
      return;
    }
    List<AuditUpdateAction> updateActions = performBirthAddressInactivation(survivorId);
    List<AuditInsertAction> insertActions = performBirthAddressCopy(survivorId, sourceId);
    audit.getRelatedTableAudits()
        .add(new RelatedTableAudit("Entity_locator_participation", updateActions, insertActions));
  }

  private List<AuditUpdateAction> performBirthAddressInactivation(String survivorId) {
    Map<String, Object> params = Collections.singletonMap("survivorId", survivorId);

    List<Map<String, Object>> rowsToUpdate = nbsTemplate.queryForList(
        FIND_UNSELECTED_BIRTH_ADDRESS_FOR_AUDIT, params
    );

    List<AuditUpdateAction> auditUpdates = buildUpdateActions(rowsToUpdate);

    nbsTemplate.update(UPDATE_UN_SELECTED_BIRTH_ADDRESS_INACTIVE, params);
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


  private List<AuditInsertAction> performBirthAddressCopy(String survivorId, String sourceId) {
    Map<String, Object> params = Map.of("survivingId", survivorId, "sourceId", sourceId);

    List<Map<String, Object>> insertedRows = nbsTemplate.queryForList(
        FIND_BIRTH_ADDRESS_LOCATOR_UID, params
    );

    List<AuditInsertAction> insertActions = buildInsertActions(survivorId, insertedRows);

    nbsTemplate.update(COPY_BIRTH_ADDRESS_FROM_SUPERSEDED_TO_SURVIVING, params);

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

  private void updatePersonField(String survivorId, String sourceId, String sqlQuery) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("sourceId", sourceId);
    nbsTemplate.update(sqlQuery, params);
  }

}
