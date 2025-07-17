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

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Order(9)
public class PersonMortalityMergeHandler implements SectionMergeHandler {
  private static final String SURVIVOR_ID = "survivorId";
  private static final String SOURCE_ID = "sourceId";
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
              AND elp.use_cd = 'DTH'
          ) = 0
          AND (
            SELECT
              count(locator_uid)
            FROM
              Entity_locator_participation elp
            WHERE
              elp.entity_uid = :sourceId
              AND elp.record_status_cd = 'ACTIVE'
              AND elp.use_cd = 'DTH'
          ) = 1 THEN 1
          ELSE 0
        END AS isMissingElp;
      """;

  static final String INSERT_ENTITY_LOCATOR_PARTICIPATION = """
      INSERT INTO Entity_locator_participation (
        entity_uid,
        locator_uid,
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
        'U',
        'PST',
        GETDATE(),
        :userId,
        'ACTIVE',
        GETDATE(),
        'A',
        GETDATE(),
        'DTH',
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

  static final String UPDATE_DEATH_CITY = """
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
            AND elp.use_cd = 'DTH'
        )
      WHERE postal_locator_uid = (SELECT
        locator_uid
      FROM
        Entity_locator_participation elp
      WHERE
        elp.use_cd = 'DTH'
        AND Entity_uid = :survivorId)
          """;

  static final String UPDATE_DEATH_STATE_AND_COUNTY = """
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
            AND elp.use_cd = 'DTH'
        ),
        cnty_cd = (
          SELECT
            cnty_cd
          FROM
            Entity_locator_participation elp
            JOIN Postal_locator pst on elp.locator_uid = pst.postal_locator_uid
          WHERE
            elp.entity_uid = :sourceId
            AND elp.use_cd = 'DTH'
        )
      WHERE postal_locator_uid = (SELECT
        locator_uid
      FROM
        Entity_locator_participation elp
      WHERE
        elp.use_cd = 'DTH'
        AND Entity_uid = :survivorId)
          """;

  static final String UPDATE_DEATH_COUNTRY = """
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
            AND elp.use_cd = 'DTH'
        )
      WHERE postal_locator_uid = (SELECT
        locator_uid
      FROM
        Entity_locator_participation elp
      WHERE
        elp.use_cd = 'DTH'
        AND Entity_uid = :survivorId)
          """;

  static final String SELECT_DEATH_CITY_FOR_AUDIT_BEFORE_UPDATE = """
          SELECT postal_locator_uid, city_desc_txt
          FROM Postal_locator
          WHERE postal_locator_uid = (
              SELECT locator_uid
              FROM Entity_locator_participation
              WHERE entity_uid = :survivorId AND use_cd = 'DTH'
          )
      """;

  static final String SELECT_DEATH_STATE_AND_COUNTY_FOR_AUDIT_BEFORE_UPDATE = """
          SELECT postal_locator_uid, state_cd, cnty_cd
          FROM Postal_locator
          WHERE postal_locator_uid = (
              SELECT locator_uid
              FROM Entity_locator_participation
              WHERE entity_uid = :survivorId AND use_cd = 'DTH'
          )
      """;

  static final String SELECT_DEATH_COUNTRY_FOR_AUDIT_BEFORE_UPDATE = """
          SELECT postal_locator_uid, cntry_cd
          FROM Postal_locator
          WHERE postal_locator_uid = (
              SELECT locator_uid
              FROM Entity_locator_participation
              WHERE entity_uid = :survivorId AND use_cd = 'DTH'
          )
      """;
  private final NamedParameterJdbcTemplate nbsTemplate;
  private final LocalUidGenerator idGenerator;

  private PatientMergeAudit audit;

  public PersonMortalityMergeHandler(
      final @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate,
      final LocalUidGenerator idGenerator) {
    this.nbsTemplate = nbsTemplate;
    this.idGenerator = idGenerator;
  }

  // Merge modifications have been applied to the person Mortality
  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit audit) {
    this.audit = audit;
    mergePersonMortality(request.survivingRecord(), request.mortality());
  }

  private void mergePersonMortality(String survivorId, PatientMergeRequest.MortalityFieldSource fieldSource) {
    updateAsOf(survivorId, fieldSource.asOf());
    updateDeceasedFlag(survivorId, fieldSource.deceased());
    updateDateOfDeath(survivorId, fieldSource.dateOfDeath());
    updateDeathAddress(survivorId, fieldSource);
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

  private void updateDeathAddress(String survivorId, PatientMergeRequest.MortalityFieldSource fieldSource) {
    updateDeathCity(survivorId, fieldSource.deathCity());
    updateDeathStateAndCounty(survivorId, fieldSource.deathState());
    updateDeathCountry(survivorId, fieldSource.deathCountry());
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
      createDeathLocator(survivorId);
    }
  }

  private void createDeathLocator(String survivorId) {
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
        "locator_uid", locatorId
    ));

    // Create Postal_locator
    MapSqlParameterSource locatorParams = new MapSqlParameterSource();
    params.addValue("locatorId", locatorId);
    params.addValue("userId", currentUser.getId());

    nbsTemplate.update(INSERT_POSTAL_LOCATOR, locatorParams);

    addAuditInsertEntry("Postal_locator", Map.of(
        "postal_locator_uid", locatorId //NOSONAR
    ));
  }

  private void addAuditInsertEntry(String tableName, Map<String, Object> data) {
    AuditInsertAction insertAction = new AuditInsertAction(data);
    RelatedTableAudit relatedTableAudit =
        new RelatedTableAudit(tableName, List.of(), Collections.singletonList(insertAction));
    audit.getRelatedTableAudits().add(relatedTableAudit);
  }

  private void updateDeathCity(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      // make sure a postal_locator entry exists if needed
      ensurePostalLocator(survivorId, sourceId);

      // fetch current value for audit
      List<Map<String, Object>> oldRows = nbsTemplate.queryForList(SELECT_DEATH_CITY_FOR_AUDIT_BEFORE_UPDATE,
          new MapSqlParameterSource(SURVIVOR_ID, survivorId));

      // update surviving record's 'DTH' postal_locator to selected value
      updatePersonField(survivorId, sourceId, UPDATE_DEATH_CITY);

      // audit update
      List<AuditUpdateAction> updateActions = oldRows.stream()
          .map(row -> new AuditUpdateAction(
              Map.of("postal_locator_uid", row.get("postal_locator_uid")),
              Map.of("city_desc_txt", row.get("city_desc_txt"))
          ))
          .toList();

      addAuditUpdateEntry(updateActions);
    }
  }

  private void updateDeathStateAndCounty(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      // make sure a postal_locator entry exists if needed
      ensurePostalLocator(survivorId, sourceId);

      // fetch current value for audit
      List<Map<String, Object>> oldRows =
          nbsTemplate.queryForList(SELECT_DEATH_STATE_AND_COUNTY_FOR_AUDIT_BEFORE_UPDATE,
              new MapSqlParameterSource(SURVIVOR_ID, survivorId));

      // update surviving record's 'DTH' postal_locator to selected value
      updatePersonField(survivorId, sourceId, UPDATE_DEATH_STATE_AND_COUNTY);

      List<AuditUpdateAction> updateActions = oldRows.stream()
          .map(row -> new AuditUpdateAction(
              Map.of("postal_locator_uid", row.get("postal_locator_uid")),
              Map.of(
                  "state_cd", row.get("state_cd"),
                  "cnty_cd", row.get("cnty_cd")
              )
          ))
          .toList();

      addAuditUpdateEntry(updateActions);
    }
  }

  private void updateDeathCountry(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      // make sure a postal_locator entry exists if needed
      ensurePostalLocator(survivorId, sourceId);

      // fetch current value for audit
      List<Map<String, Object>> oldRows = nbsTemplate.queryForList(SELECT_DEATH_COUNTRY_FOR_AUDIT_BEFORE_UPDATE,
          new MapSqlParameterSource(SURVIVOR_ID, survivorId));

      // update surviving record's 'DTH' postal_locator to selected value
      updatePersonField(survivorId, sourceId, UPDATE_DEATH_COUNTRY);

      List<AuditUpdateAction> updateActions = oldRows.stream()
          .map(row -> new AuditUpdateAction(
              Map.of("postal_locator_uid", row.get("postal_locator_uid")),
              Map.of("cntry_cd", row.get("cntry_cd"))
          ))
          .toList();

      addAuditUpdateEntry(updateActions);

    }
  }

  private void addAuditUpdateEntry(List<AuditUpdateAction> updateActions) {
    audit.getRelatedTableAudits()
        .add(new RelatedTableAudit("Postal_locator", updateActions, List.of()));
  }

  private void updatePersonField(String survivorId, String sourceId, String query) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(SURVIVOR_ID, survivorId);
    params.addValue(SOURCE_ID, sourceId);
    nbsTemplate.update(query, params);
  }

}
