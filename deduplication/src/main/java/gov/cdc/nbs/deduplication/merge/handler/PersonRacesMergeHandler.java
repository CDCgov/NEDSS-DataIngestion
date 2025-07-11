package gov.cdc.nbs.deduplication.merge.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest.RaceId;

@Component
@Order(7)
public class PersonRacesMergeHandler implements SectionMergeHandler {
  static final String USER_ID = "userId";
  static final String PERSON_ID = "personId";
  static final String SOURCE_ID = "sourceId";
  static final String RACE = "race";
  static final String DETAILED_RACE = "detailedRace";

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

  private final JdbcClient client;

  public PersonRacesMergeHandler(@Qualifier("nbsJdbcClient") JdbcClient client) {
    this.client = client;
  }

  public record RaceEntry(String race, String detailedRace) {
  }

  // Merge modifications have been applied to the person races
  @Override
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public void handleMerge(String matchId, PatientMergeRequest request) {
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

    client.sql(SET_RACE_ENTRIES_TO_INACTIVE)
        .param(USER_ID, currentUser.getId())
        .param(PERSON_ID, survivorId)
        .update();
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
    client.sql(UPDATE_EXISTING_RACE_ENTRY)
        .param(PERSON_ID, survivorId)
        .param(RACE, raceEntry.race())
        .param(DETAILED_RACE, raceEntry.detailedRace())
        .update();
  }

  private void inserNewRaceEntry(String survivorId, String sourceId, RaceEntry raceEntry) {
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    client.sql(INSERT_NEW_RACE_ENTRY)
        .param(PERSON_ID, survivorId)
        .param(RACE, raceEntry.race())
        .param(DETAILED_RACE, raceEntry.detailedRace())
        .param(USER_ID, currentUser.getId())
        .param(SOURCE_ID, sourceId)
        .update();
  }

}