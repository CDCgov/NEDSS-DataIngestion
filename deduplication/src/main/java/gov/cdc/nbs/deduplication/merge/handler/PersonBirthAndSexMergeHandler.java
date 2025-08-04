package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import gov.cdc.nbs.deduplication.merge.id.LocalUidGenerator;
import gov.cdc.nbs.deduplication.merge.id.LocalUidGenerator.EntityType;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest.SexAndBirthFieldSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(8)
public class PersonBirthAndSexMergeHandler implements SectionMergeHandler {
  private static final String SURVIVOR_ID = "survivorId";
  private static final String SOURCE_ID = "sourceId";

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

  // Returns 0 if the survivorId already has a birth locator entry
  // Returns 1 if the survivorId does not have a birth locator and the sourceId
  // does
  static final String SHOULD_CREATE_POSTAL_LOCATOR = """
      SELECT
        CASE
          WHEN (
            SELECT
              count(locator_uid)
            FROM
              Entity_locator_participation elp
            WHERE
              elp.entity_uid = :survivorId
              AND elp.record_status_cd = 'ACTIVE'
              AND elp.use_cd = 'BIR'
          ) = 0
          AND (
            SELECT
              count(locator_uid)
            FROM
              Entity_locator_participation elp
            WHERE
              elp.entity_uid = :sourceId
              AND elp.record_status_cd = 'ACTIVE'
              AND elp.use_cd = 'BIR'
          ) = 1 THEN 1
          ELSE 0
        END AS isMissingElp;
      """;

  static final String INSERT_ENTITY_LOCATOR_PARTICIPATION = """
      INSERT INTO Entity_locator_participation (
        entity_uid,
        locator_uid,
        version_ctrl_nbr,
        cd,
        class_cd,
        last_chg_time,
        last_chg_user_id,
        record_status_cd,
        record_status_time,
        status_cd,
        status_time,
        use_cd,
        as_of_date)
      VALUES
      (
        :survivorId,
        :locatorId,
        1,
        'F',
        'PST',
        GETDATE(),
        :userId,
        'ACTIVE',
        GETDATE(),
        'A',
        GETDATE(),
        'BIR',
        GETDATE()
      );
          """;

  static final String INSERT_POSTAL_LOCATOR = """
      INSERT INTO Postal_locator (
        postal_locator_uid,
        add_time,
        last_chg_time,
        last_chg_user_id,
        record_status_cd,
        record_status_time
        )
      VALUES (
        :locatorId,
        GETDATE(),
        GETDATE(),
        :userId,
        'ACTIVE',
        GETDATE()
      );
      """;

  static final String UPDATE_BIRTH_CITY = """
      UPDATE Postal_locator
      SET
        city_desc_txt = (
          SELECT
            city_desc_txt
          FROM
            Entity_locator_participation elp
            JOIN Postal_locator pst on elp.locator_uid = pst.postal_locator_uid
          WHERE
            elp.entity_uid = :sourceId
            AND elp.use_cd = 'BIR'
        )
      WHERE postal_locator_uid = (SELECT
        locator_uid
      FROM
        Entity_locator_participation elp
      WHERE
        elp.use_cd = 'BIR'
        AND Entity_uid = :survivorId)
          """;

  static final String UPDATE_BIRTH_STATE_AND_COUNTY = """
      UPDATE Postal_locator
      SET
        state_cd = (
          SELECT
            state_cd
          FROM
            Entity_locator_participation elp
            JOIN Postal_locator pst on elp.locator_uid = pst.postal_locator_uid
          WHERE
            elp.entity_uid = :sourceId
            AND elp.use_cd = 'BIR'
        ),
        cnty_cd = (
          SELECT
            cnty_cd
          FROM
            Entity_locator_participation elp
            JOIN Postal_locator pst on elp.locator_uid = pst.postal_locator_uid
          WHERE
            elp.entity_uid = :sourceId
            AND elp.use_cd = 'BIR'
        )
      WHERE postal_locator_uid = (SELECT
        locator_uid
      FROM
        Entity_locator_participation elp
      WHERE
        elp.use_cd = 'BIR'
        AND Entity_uid = :survivorId)
          """;

  static final String UPDATE_BIRTH_COUNTRY = """
      UPDATE Postal_locator
      SET
        cntry_cd = (
          SELECT
            cntry_cd
          FROM
            Entity_locator_participation elp
            JOIN Postal_locator pst on elp.locator_uid = pst.postal_locator_uid
          WHERE
            elp.entity_uid = :sourceId
            AND elp.use_cd = 'BIR'
        )
      WHERE postal_locator_uid = (SELECT
        locator_uid
      FROM
        Entity_locator_participation elp
      WHERE
        elp.use_cd = 'BIR'
        AND Entity_uid = :survivorId)
          """;

  static final String SELECT_BIRTH_CITY_FOR_AUDIT_BEFORE_UPDATE = """
          SELECT postal_locator_uid, city_desc_txt
          FROM Postal_locator
          WHERE postal_locator_uid = (
              SELECT locator_uid
              FROM Entity_locator_participation
              WHERE entity_uid = :survivorId AND use_cd = 'BIR'
          )
      """;

  static final String SELECT_BIRTH_STATE_AND_COUNTY_FOR_AUDIT_BEFORE_UPDATE = """
          SELECT postal_locator_uid, state_cd, cnty_cd
          FROM Postal_locator
          WHERE postal_locator_uid = (
              SELECT locator_uid
              FROM Entity_locator_participation
              WHERE entity_uid = :survivorId AND use_cd = 'BIR'
          )
      """;

  static final String SELECT_BIRTH_COUNTRY_FOR_AUDIT_BEFORE_UPDATE = """
          SELECT postal_locator_uid, cntry_cd
          FROM Postal_locator
          WHERE postal_locator_uid = (
              SELECT locator_uid
              FROM Entity_locator_participation
              WHERE entity_uid = :survivorId AND use_cd = 'BIR'
          )
      """;

  private final NamedParameterJdbcTemplate nbsTemplate;
  private final LocalUidGenerator idGenerator;

  private PatientMergeAudit audit;

  public PersonBirthAndSexMergeHandler(
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate,
      final LocalUidGenerator idGenerator) {
    this.nbsTemplate = nbsTemplate;
    this.idGenerator = idGenerator;
  }

  // Merge modifications have been applied to the person birth and sex
  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit audit) {
    this.audit = audit;
    mergePersonBirthAndSex(request.survivingRecord(), request.sexAndBirth());
  }

  private void mergePersonBirthAndSex(String survivorId, PatientMergeRequest.SexAndBirthFieldSource fieldSource) {
    updateAsOf(survivorId, fieldSource.asOf());
    updateDateOfBirth(survivorId, fieldSource.dateOfBirth());
    updateAdditionalGender(survivorId, fieldSource.additionalGender());
    updateTransgender(survivorId, fieldSource.transgenderInfo());
    updateBirthGender(survivorId, fieldSource.birthGender());

    // Sex Unknown reason and current sex are tied together and use the same Id
    updateCurrentSexAndUnknownReason(survivorId, fieldSource.currentSex());

    // Multiple birth and birth order are tied together and use the same Id
    updateMultipleBirthAndBirthOrder(survivorId, fieldSource.multipleBirth());

    updateBirthAddress(survivorId, fieldSource);
  }

  private void updateBirthAddress(String survivorId, SexAndBirthFieldSource fieldSource) {
    updateBirthCity(survivorId, fieldSource.birthCity());
    updateBirthStateAndCounty(survivorId, fieldSource.birthState());
    updateBirthCountry(survivorId, fieldSource.birthCountry());
  }

  // Checks if the surviving patient is missing an Entity_locator_participation
  // and postal_locator entry
  // Will only create a new one if the survivingId does not have one and the
  // sourceId does
  private void ensurePostalLocator(String survivorId, String sourceId) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(SURVIVOR_ID, survivorId);
    params.addValue(SOURCE_ID, sourceId);
    Boolean shouldCreate = nbsTemplate.queryForObject(SHOULD_CREATE_POSTAL_LOCATOR, params, Boolean.class);
    if (Boolean.TRUE.equals(shouldCreate)) {
      createBirthLocator(survivorId);
    }
  }

  private void createBirthLocator(String survivorId) {
    long locatorId = idGenerator.getNextValidId(EntityType.NBS).id();
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Create Entity_locator_participation
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(SURVIVOR_ID, survivorId);
    params.addValue("locatorId", locatorId);
    params.addValue("userId", currentUser.getId());

    nbsTemplate.update(INSERT_ENTITY_LOCATOR_PARTICIPATION, params);

    addAuditInsertEntry("Entity_locator_participation", Map.of(
        "entity_uid", survivorId,
        "locator_uid", locatorId));

    // Create Postal_locator
    MapSqlParameterSource locatorParams = new MapSqlParameterSource();
    locatorParams.addValue("locatorId", locatorId);
    locatorParams.addValue("userId", currentUser.getId());

    nbsTemplate.update(INSERT_POSTAL_LOCATOR, locatorParams);

    addAuditInsertEntry("Postal_locator", Map.of(
        "postal_locator_uid", locatorId // NOSONAR
    ));

  }

  private void addAuditInsertEntry(String tableName, Map<String, Object> data) {
    AuditInsertAction insertAction = new AuditInsertAction(data);
    RelatedTableAudit relatedTableAudit = new RelatedTableAudit(tableName, List.of(),
        Collections.singletonList(insertAction));
    audit.getRelatedTableAudits().add(relatedTableAudit);
  }

  private void updateBirthCity(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      // make sure a postal_locator entry exists if needed
      ensurePostalLocator(survivorId, sourceId);

      // fetch current value for audit
      List<Map<String, Object>> oldRows = nbsTemplate.queryForList(SELECT_BIRTH_CITY_FOR_AUDIT_BEFORE_UPDATE,
          new MapSqlParameterSource(SURVIVOR_ID, survivorId));

      // update surviving record's 'BIR' postal_locator to selected value
      updatePersonField(survivorId, sourceId, UPDATE_BIRTH_CITY);

      // build audit actions
      List<AuditUpdateAction> updateActions = oldRows.stream()
          .map(row -> {
            Map<String, Object> values = new HashMap<>();
            values.put("city_desc_txt", row.get("city_desc_txt"));

            return new AuditUpdateAction(
                Map.of("postal_locator_uid", row.get("postal_locator_uid")),
                values);
          })
          .toList();

      addAuditUpdateEntry(updateActions);
    }
  }

  private void updateBirthStateAndCounty(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      // make sure a postal_locator entry exists if needed
      ensurePostalLocator(survivorId, sourceId);

      // fetch current value for audit
      List<Map<String, Object>> oldRows = nbsTemplate.queryForList(
          SELECT_BIRTH_STATE_AND_COUNTY_FOR_AUDIT_BEFORE_UPDATE, new MapSqlParameterSource(SURVIVOR_ID, survivorId));

      // update surviving record's 'BIR' postal_locator to selected value
      updatePersonField(survivorId, sourceId, UPDATE_BIRTH_STATE_AND_COUNTY);

      // build audit actions
      List<AuditUpdateAction> updateActions = oldRows.stream()
          .map(row -> {
            Map<String, Object> values = new HashMap<>();
            values.put("state_cd", row.get("state_cd"));
            values.put("cnty_cd", row.get("cnty_cd"));

            return new AuditUpdateAction(
                Map.of("postal_locator_uid", row.get("postal_locator_uid")),
                values);
          })
          .toList();

      addAuditUpdateEntry(updateActions);

    }
  }

  private void updateBirthCountry(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      // make sure a postal_locator entry exists if needed
      ensurePostalLocator(survivorId, sourceId);

      // fetch current value for audit
      List<Map<String, Object>> oldRows = nbsTemplate.queryForList(SELECT_BIRTH_COUNTRY_FOR_AUDIT_BEFORE_UPDATE,
          new MapSqlParameterSource(SURVIVOR_ID, survivorId));

      // update surviving record's 'BIR' postal_locator to selected value
      updatePersonField(survivorId, sourceId, UPDATE_BIRTH_COUNTRY);

      // build audit actions
      List<AuditUpdateAction> updateActions = oldRows.stream()
          .map(row -> {
            Map<String, Object> values = new HashMap<>();
            values.put("cntry_cd", row.get("cntry_cd"));

            return new AuditUpdateAction(
                Map.of("postal_locator_uid", row.get("postal_locator_uid")),
                values);
          })
          .toList();

      addAuditUpdateEntry(updateActions);
    }
  }

  private void addAuditUpdateEntry(List<AuditUpdateAction> updateActions) {
    audit.getRelatedTableAudits()
        .add(new RelatedTableAudit("Postal_locator", updateActions, List.of()));
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

  private void updateCurrentSexAndUnknownReason(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_CURRENT_SEX_CD);
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

  private void updateMultipleBirthAndBirthOrder(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_MULTIPLE_BIRTH_IND);
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_BIRTH_ORDER);
    }
  }

  private void updatePersonField(String survivorId, String sourceId, String sqlQuery) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(SURVIVOR_ID, survivorId);
    params.addValue(SOURCE_ID, sourceId);
    nbsTemplate.update(sqlQuery, params);
  }

}
