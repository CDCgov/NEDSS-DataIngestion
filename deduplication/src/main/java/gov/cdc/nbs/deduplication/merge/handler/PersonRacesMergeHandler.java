package gov.cdc.nbs.deduplication.merge.handler;

import java.util.HashMap;
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
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest.RaceId;

@Component
@Order(7)
public class PersonRacesMergeHandler implements SectionMergeHandler {
  static final String USER_ID = "userId";
  static final String PERSON_ID = "personId";
  static final String SOURCE_ID = "sourceId";
  static final String RACE = "race";
  static final String DETAILED_RACE = "detailedRace";
  static final String PERSON_RACE = "person_race";
  static final String PERSON_UID = "person_uid";
  static final String RACE_CD = "race_cd";
  static final String RACE_CATEGORY_CD = "race_category_cd";

  static final String SET_RACE_ENTRIES_TO_INACTIVE = """
      UPDATE person_race
      SET
        record_status_cd = 'INACTIVE',
        record_status_time = GETDATE(),
        last_chg_user_id = :userId,
        last_chg_time = GETDATE()
      WHERE
        person_uid = :personId;
      """;

  static final String SELECT_RACE_ENTRIES = """
      SELECT
        race_category_cd as race,
        race_cd as detailedRace
      FROM
        person_race
      WHERE
        person_uid = :personId
        AND race_category_cd = :race;
      """;

  static final String SELECT_RACE_ENTRY_EXISTS = """
      SELECT
        count(*)
      FROM
        person_race
      WHERE
        person_uid = :personId
        AND race_category_cd = :race
        AND race_cd = :detailedRace;
      """;

  static final String UPDATE_EXISTING_RACE_ENTRY = """
      UPDATE person_race
      SET
        record_status_cd = 'ACTIVE'
      WHERE
        person_uid = :personId
        AND race_category_cd = :race
        AND race_cd = :detailedRace;
      """;

  static final String INSERT_NEW_RACE_ENTRY = """
          INSERT INTO person_race (
            person_uid,
            race_category_cd,
            race_cd,
            add_time,
            add_user_id,
            last_chg_time,
            last_chg_user_id,
            record_status_cd,
            record_status_time,
            as_of_date
          )
          VALUES (
            :personId,
            :race,
            :detailedRace,
            GETDATE(),
            :userId,
            GETDATE(),
            :userId,
            'ACTIVE',
              GETDATE(),
            (
              SELECT as_of_date
              FROM person_race
              WHERE
                person_uid = :sourceId
                AND race_category_cd = :race
                AND race_cd = :detailedRace
            )
      );
      """;

  private static final String SELECT_PERSON_RACE_FOR_AUDIT = """
      SELECT person_uid, race_category_cd, race_cd, record_status_cd
      FROM person_race
      WHERE person_uid = :personId
      """;

  private static final String SELECT_PERSON_RACE_BY_CATEGORY_AND_DETAILED_RACE_FOR_AUDIT = """
      SELECT person_uid, race_category_cd, race_cd, record_status_cd
      FROM person_race
      WHERE person_uid = :personId
        AND race_category_cd = :race
        AND race_cd = :detailedRace
      """;

  static final String INSERT_PERSON_RACE_HIST_FOR_ALL = """
    INSERT INTO person_race_hist (
        person_uid,
        race_cd,
        add_reason_cd,
        add_time,
        add_user_id,
        last_chg_reason_cd,
        last_chg_time,
        last_chg_user_id,
        race_category_cd,
        race_desc_txt,
        record_status_cd,
        record_status_time,
        user_affiliation_txt,
        as_of_date,
        version_ctrl_nbr
    )
    SELECT
        pr.*,
        ISNULL((
            SELECT MAX(h.version_ctrl_nbr)
            FROM person_race_hist h
            WHERE h.person_uid = pr.person_uid
              AND h.race_cd = pr.race_cd
              AND h.race_category_cd = pr.race_category_cd
        ), 0) + 1 AS version_ctrl_nbr
    FROM person_race pr
    WHERE pr.person_uid = :personId
    """;


  static final String INSERT_PERSON_RACE_HIST_FOR_SELECTED = """
    INSERT INTO person_race_hist (
        person_uid,
        race_cd,
        add_reason_cd,
        add_time,
        add_user_id,
        last_chg_reason_cd,
        last_chg_time,
        last_chg_user_id,
        race_category_cd,
        race_desc_txt,
        record_status_cd,
        record_status_time,
        user_affiliation_txt,
        as_of_date,
        version_ctrl_nbr
    )
    SELECT
        pr.*,
        ISNULL((
            SELECT MAX(h.version_ctrl_nbr)
            FROM person_race_hist h
            WHERE h.person_uid = pr.person_uid
              AND h.race_cd = pr.race_cd
              AND h.race_category_cd = pr.race_category_cd
        ), 0) + 1 AS version_ctrl_nbr
    FROM person_race pr
    WHERE pr.person_uid = :personId
      AND pr.race_category_cd = :race
      AND pr.race_cd = :detailedRace
    """;

  private final JdbcClient client;

  private final NamedParameterJdbcTemplate nbsTemplate;

  private PatientMergeAudit audit;

  public PersonRacesMergeHandler(@Qualifier("nbsJdbcClient") JdbcClient client,
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.client = client;
    this.nbsTemplate = nbsTemplate;
  }

  public record RaceEntry(String race, String detailedRace) {
  }

  // Merge modifications have been applied to the person races
  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit audit) {
    this.audit = audit;
    mergeRace(request.survivingRecord(), request.races());
  }

  private void mergeRace(String survivorId, List<RaceId> races) {
    // Set all to INACTIVE
    setAllEntriesToInactive(survivorId);

    for (RaceId raceId : races) {
      // For each race selected, get all entries
      List<RaceEntry> raceEntries = selectRaceEntries(raceId);

      // For each entry, check if it already exists on survivor
      for (RaceEntry raceEntry : raceEntries) {
        if (raceEntryExists(survivorId, raceEntry)) {
          updateExistingRaceEntry(survivorId, raceEntry);
        } else {
          inserNewRaceEntry(survivorId, raceId.personUid(), raceEntry);
        }
      }
    }
  }

  private void setAllEntriesToInactive(String survivorId) {
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Fetch current values for audit
    MapSqlParameterSource selectParams = new MapSqlParameterSource();
    selectParams.addValue(PERSON_ID, survivorId);

    List<Map<String, Object>> oldRows = nbsTemplate.queryForList(
        SELECT_PERSON_RACE_FOR_AUDIT, selectParams);

    client.sql(INSERT_PERSON_RACE_HIST_FOR_ALL)
        .param(PERSON_ID, survivorId)
        .update();

    // Perform update
    client.sql(SET_RACE_ENTRIES_TO_INACTIVE)
        .param(USER_ID, currentUser.getId())
        .param(PERSON_ID, survivorId)
        .update();

    // Add audit
    audit.getRelatedTableAudits()
        .add(new RelatedTableAudit(PERSON_RACE,
            buildAuditUpdateActions(oldRows), List.of()));
  }

  private List<RaceEntry> selectRaceEntries(RaceId raceId) {
    return client.sql(SELECT_RACE_ENTRIES)
        .param(PERSON_ID, raceId.personUid())
        .param(RACE, raceId.raceCode())
        .query(RaceEntry.class)
        .list();
  }

  private boolean raceEntryExists(String survivorId, RaceEntry raceEntry) {
    Boolean entryExists = client.sql(SELECT_RACE_ENTRY_EXISTS)
        .param(PERSON_ID, survivorId)
        .param(RACE, raceEntry.race())
        .param(DETAILED_RACE, raceEntry.detailedRace())
        .query(Boolean.class)
        .single();
    return Boolean.TRUE.equals(entryExists);
  }

  private void updateExistingRaceEntry(String survivorId, RaceEntry raceEntry) {
    // Fetch current value for audit
    MapSqlParameterSource selectParams = new MapSqlParameterSource();
    selectParams.addValue(PERSON_ID, survivorId);
    selectParams.addValue(RACE, raceEntry.race());
    selectParams.addValue(DETAILED_RACE, raceEntry.detailedRace());

    List<Map<String, Object>> oldRows = nbsTemplate.queryForList(
        SELECT_PERSON_RACE_BY_CATEGORY_AND_DETAILED_RACE_FOR_AUDIT, selectParams);


    client.sql(INSERT_PERSON_RACE_HIST_FOR_SELECTED)
        .param(PERSON_ID, survivorId)
        .param(RACE, raceEntry.race())
        .param(DETAILED_RACE, raceEntry.detailedRace())
        .update();


    // Perform update
    client.sql(UPDATE_EXISTING_RACE_ENTRY)
        .param(PERSON_ID, survivorId)
        .param(RACE, raceEntry.race())
        .param(DETAILED_RACE, raceEntry.detailedRace())
        .update();

    // Add audit
    audit.getRelatedTableAudits()
        .add(new RelatedTableAudit(PERSON_RACE,
            buildAuditUpdateActions(oldRows), List.of()));
  }

  private List<AuditUpdateAction> buildAuditUpdateActions(List<Map<String, Object>> rows) {
    return rows.stream()
        .map(row -> {
          Map<String, Object> values = new HashMap<>();
          values.put("record_status_cd", row.get("record_status_cd"));

          return new AuditUpdateAction(
              Map.of(
                  PERSON_UID, row.get(PERSON_UID),
                  RACE_CATEGORY_CD, row.get(RACE_CATEGORY_CD),
                  RACE_CD, row.get(RACE_CD)),
              values);
        })
        .toList();
  }

  private void inserNewRaceEntry(String survivorId, String sourceId, RaceEntry raceEntry) {
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Perform insert
    client.sql(INSERT_NEW_RACE_ENTRY)
        .param(PERSON_ID, survivorId)
        .param(RACE, raceEntry.race())
        .param(DETAILED_RACE, raceEntry.detailedRace())
        .param(USER_ID, currentUser.getId())
        .param(SOURCE_ID, sourceId)
        .update();

    // Add audit
    audit.getRelatedTableAudits()
        .add(new RelatedTableAudit(
            PERSON_RACE,
            List.of(),
            List.of(new AuditInsertAction(
                Map.of(
                    PERSON_UID, survivorId,
                    RACE_CATEGORY_CD, raceEntry.race(),
                    RACE_CD, raceEntry.detailedRace())))));
  }

}
