package gov.cdc.nbs.deduplication.merge.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.cdc.nbs.deduplication.merge.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;

@Component
@Order(7)
public class PersonEthnicityMergeHandler implements SectionMergeHandler {
  static final String SURVIVOR_ID = "survivorId";
  static final String SOURCE_ID = "sourceId";
  static final String PERSON_ID = "personId";
  static final String USER_ID = "userId";
  static final String PERSON_UID = "person_uid";
  static final String ETHNIC_GROUP_CD = "ethnic_group_cd";


  static final String UPDATE_PERSON_ETHNIC_GROUP = """
      UPDATE person
      SET
        ethnic_group_ind = (
          SELECT
            ethnic_group_ind
          FROM
            person
          WHERE
            person_uid = :sourceId
        ),
        ethnic_unk_reason_cd = (
          SELECT
            ethnic_unk_reason_cd
          FROM
            person
          WHERE
            person_uid = :sourceId
        )
      WHERE
        person_uid = :survivorId;
      """;

  static final String SELECT_SPANISH_ORIGIN_LIST = """
      SELECT
        ethnic_group_cd
      FROM
        Person_ethnic_group
        WHERE person_uid = :personId;
      """;

  static final String INSERT_SPANISH_ORIGIN = """
      INSERT INTO Person_ethnic_group (
        person_uid,
        ethnic_group_cd,
        add_time,
        add_user_id,
        last_chg_time,
        last_chg_user_id,
        record_status_cd,
        record_status_time
      )
      VALUES (
        :personId,
        :spanishOrigin,
        GETDATE(),
        :userId,
        GETDATE(),
        :userId,
        (SELECT record_status_cd FROM Person_ethnic_group WHERE person_uid = :sourceId AND ethnic_group_cd = :spanishOrigin),
        GETDATE()
      );
      """;

  static final String UPDATE_SPANISH_ORIGINS_TO_INACTIVE = """
      UPDATE Person_ethnic_group
      SET
        record_status_cd = 'INACTIVE',
        record_status_time = GETDATE(),
        last_chg_user_id = :userId,
        last_chg_time = GETDATE()
      WHERE
        person_uid = :personId
        AND ethnic_group_cd IN ( :spanishOrigins);
          """;

  private static final String SELECT_PERSON_ETHNIC_GROUP_FOR_SPANISH_ORIGIN_FOR_AUDIT = """
      SELECT person_uid, ethnic_group_cd, record_status_cd
      FROM Person_ethnic_group
      WHERE person_uid = :personId
        AND ethnic_group_cd IN (:spanishOrigins)
      """;

  final JdbcClient client;
  PatientMergeAudit audit;
  private final NamedParameterJdbcTemplate nbsTemplate;

  public PersonEthnicityMergeHandler(@Qualifier("nbsJdbcClient") JdbcClient client,
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.client = client;
    this.nbsTemplate = nbsTemplate;
  }

  // Merge modifications have been applied to the person ethnicity
  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit audit) {
    this.audit = audit;
    mergeEthnicity(request.survivingRecord(), request.ethnicity());
  }

  private void mergeEthnicity(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonEthnicGroup(survivorId, sourceId);
      // Get list of ethnicities for surviving and source
      List<String> survivorSpanishOrigins = selectSpanishOriginsForPerson(survivorId);
      List<String> sourceSpanishOrigins = selectSpanishOriginsForPerson(sourceId);

      // Determine what source entries are missing from survivor and insert them
      List<String> missingEntries = sourceSpanishOrigins
          .stream()
          .filter(s -> !survivorSpanishOrigins.contains(s))
          .toList();

      insertSpanishOrigins(survivorId, sourceId, missingEntries);

      // Set removed entries to INACTIVE
      List<String> removedSpanishOrigins = survivorSpanishOrigins
          .stream()
          .filter(s -> !sourceSpanishOrigins.contains(s))
          .toList();

      updateSpanishOriginsToInactive(survivorId, removedSpanishOrigins);

    }
  }

  private void updatePersonEthnicGroup(String survivorId, String sourceId) {
    client.sql(UPDATE_PERSON_ETHNIC_GROUP)
        .param(SURVIVOR_ID, survivorId)
        .param(SOURCE_ID, sourceId)
        .update();
  }

  private List<String> selectSpanishOriginsForPerson(String person) {
    return client.sql(SELECT_SPANISH_ORIGIN_LIST)
        .param(PERSON_ID, person)
        .query(String.class)
        .list();
  }

  private void insertSpanishOrigins(String personId, String sourceId, List<String> spanishOrigins) {
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = currentUser.getId();

    List<AuditInsertAction> insertActions = new ArrayList<>();

    for (String origin : spanishOrigins) {
      // Perform insert
      client.sql(INSERT_SPANISH_ORIGIN)
          .param("spanishOrigin", origin)
          .param(PERSON_ID, personId)
          .param(USER_ID, userId)
          .param(SOURCE_ID, sourceId)
          .update();

      // Record audit
      insertActions.add(new AuditInsertAction(
          Map.of(
              PERSON_UID, personId,
              ETHNIC_GROUP_CD, origin
          )
      ));
    }

    if (!insertActions.isEmpty()) {
      audit.getRelatedTableAudits()
          .add(new RelatedTableAudit("Person_ethnic_group", List.of(), insertActions));
    }
  }

  private void updateSpanishOriginsToInactive(String personId, List<String> spanishOrigins) {
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = currentUser.getId();

    if (!spanishOrigins.isEmpty()) {
      // Perform update
      client.sql(UPDATE_SPANISH_ORIGINS_TO_INACTIVE)
          .param(USER_ID, userId)
          .param(PERSON_ID, personId)
          .param("spanishOrigins", spanishOrigins)
          .update();

      List<Map<String, Object>> oldRows = fetchOldRows(personId, spanishOrigins);
      addUpdateAudit(oldRows);
    }
  }

  private void addUpdateAudit(List<Map<String, Object>> oldRows) {
    List<AuditUpdateAction> updateActions = oldRows.stream()
        .map(row -> new AuditUpdateAction(
            Map.of(
                PERSON_UID, row.get(PERSON_UID),
                ETHNIC_GROUP_CD, row.get(ETHNIC_GROUP_CD)
            ),
            Map.of("record_status_cd", row.get("record_status_cd"))
        ))
        .toList();

    audit.getRelatedTableAudits()
        .add(new RelatedTableAudit("Person_ethnic_group", updateActions, List.of()));
  }

  private List<Map<String, Object>> fetchOldRows(String personId, List<String> spanishOrigins) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue(PERSON_ID, personId);
    params.addValue("spanishOrigins", spanishOrigins);
    return nbsTemplate.queryForList(SELECT_PERSON_ETHNIC_GROUP_FOR_SPANISH_ORIGIN_FOR_AUDIT, params);
  }

}

