package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;


import java.util.*;

@Component
@Order(7)
public class PersonRacesMergeHandler implements SectionMergeHandler {


  static final String UPDATE_ALL_SURVIVOR_RACES_INACTIVE = """
      UPDATE person_race
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE()
      WHERE person_uid = :survivorId
        AND record_status_cd = 'ACTIVE'
      """;
  static final String UPDATE_SELECTED_EXCLUDED_RACES_INACTIVE = """
      UPDATE person_race
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE()
      WHERE person_uid = :survivorId
        AND record_status_cd = 'ACTIVE'
        AND race_cd NOT IN (:survivingSelectedRaceCodes)
      """;

  static final String SELECT_ACTIVE_RACE_CATEGORIES = """
      SELECT DISTINCT race_category_cd
      FROM person_race
      WHERE person_uid = :survivorId
      """;

  static final String COPY_RACE_DETAIL_IF_NOT_EXISTS = """
      INSERT INTO person_race (
          person_uid, race_cd, race_category_cd,
          add_reason_cd, add_time, add_user_id,
          last_chg_reason_cd, last_chg_time, last_chg_user_id,
          race_desc_txt, record_status_cd, record_status_time,
          user_affiliation_txt, as_of_date
      )
      SELECT
          :survivorId, race_cd, race_category_cd,
          add_reason_cd, add_time, add_user_id,
          'merge', GETDATE(), last_chg_user_id,
          race_desc_txt, record_status_cd, record_status_time,
          user_affiliation_txt, as_of_date
      FROM Person_race pr
      WHERE pr.person_uid = :supersededUid
        AND pr.race_category_cd = :raceCategoryCd
        AND pr.race_cd = :selectedRaceCd
        AND pr.record_status_cd = 'ACTIVE'
        AND NOT EXISTS (
            SELECT 1
            FROM Person_race pr2
            WHERE pr2.person_uid = :survivorId
              AND pr2.race_cd = pr.race_cd
        )
      """;

  static final String COPY_RACE_FROM_SUPERSEDED_TO_SURVIVOR = """
      INSERT INTO person_race (
          person_uid, race_cd, race_category_cd,
          add_reason_cd, add_time, add_user_id,
          last_chg_reason_cd, last_chg_time, last_chg_user_id,
          race_desc_txt, record_status_cd, record_status_time,
          user_affiliation_txt, as_of_date
      )
      SELECT
          :survivorId, race_cd, race_category_cd,
          add_reason_cd, add_time, add_user_id,
          'merge', GETDATE(), last_chg_user_id,
          race_desc_txt, record_status_cd, record_status_time,
          user_affiliation_txt, as_of_date
      FROM Person_race pr
      WHERE pr.person_uid = :supersededUid
        AND pr.race_category_cd = :raceCategoryCd
        AND pr.race_cd = pr.race_category_cd
        AND pr.record_status_cd = 'ACTIVE'
      """;

  static final String COPY_RACE_DETAIL_FROM_SUPERSEDED_TO_SURVIVOR = """
      INSERT INTO person_race (
          person_uid, race_cd, race_category_cd,
          add_reason_cd, add_time, add_user_id,
          last_chg_reason_cd, last_chg_time, last_chg_user_id,
          race_desc_txt, record_status_cd, record_status_time,
          user_affiliation_txt, as_of_date
      )
      SELECT
          :survivorId, race_cd, race_category_cd,
          add_reason_cd, add_time, add_user_id,
          'merge', GETDATE(), last_chg_user_id,
          race_desc_txt, record_status_cd, record_status_time,
          user_affiliation_txt, as_of_date
      FROM Person_race pr
      WHERE pr.person_uid = :supersededUid
        AND pr.race_category_cd = :raceCategoryCd
        AND pr.race_cd = :selectedRaceCd
        AND pr.record_status_cd = 'ACTIVE'
      """;


  static final String SELECT_RACE_CATEGORY_FOR_RACE_CD = """
      SELECT DISTINCT race_category_cd
      FROM person_race
      WHERE race_cd = :raceCd
      """;

  private static final String FIND_PREEXISTING_PERSON_RACES_FOR_AUDIT = """
      SELECT person_uid, race_cd, race_category_cd, record_status_cd
      FROM person_race
      WHERE person_uid = :survivorId
        AND record_status_cd = 'ACTIVE'
      """;

  private static final String FIND_EXCLUDED_PERSON_RACES_FOR_AUDIT = """
      SELECT person_uid, race_cd, race_category_cd, record_status_cd
      FROM person_race
      WHERE person_uid = :survivorId
        AND record_status_cd = 'ACTIVE'
        AND race_cd NOT IN (:survivingSelectedRaceCodes)
      """;



  final NamedParameterJdbcTemplate nbsTemplate;

  public PersonRacesMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  //Merge modifications have been applied to the person races
  @Override
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit audit) {
    mergePersonRaces(request.survivingRecord(), request.races(), audit);
  }

  private void mergePersonRaces(String survivorId, List<PatientMergeRequest.RaceId> races, PatientMergeAudit audit) {
    List<String> survivingSelectedRaceCodes = new ArrayList<>();
    Map<String, List<String>> supersededRaceMap = new HashMap<>();

    categorizeRaces(survivorId, races, supersededRaceMap, survivingSelectedRaceCodes);
    List<AuditUpdateAction> updateActions =
        markUnselectedSurvivingRacesAsInactive(survivorId, survivingSelectedRaceCodes);

    List<String> survivingRaceCategories = getRaceCategoriesForSurvivor(survivorId);
    List<AuditInsertAction> insertActions =
        processSupersededRaces(survivorId, supersededRaceMap, survivingRaceCategories);

    audit.getRelatedTableAudits().add(
        new RelatedTableAudit("person_race", updateActions, insertActions)
    );
  }

  private void categorizeRaces(String survivorId, List<PatientMergeRequest.RaceId> races,
      Map<String, List<String>> supersededRaceMap, List<String> survivingSelectedRaceCodes) {

    for (PatientMergeRequest.RaceId race : races) {
      String personUid = race.personUid();
      String raceCode = race.raceCode();

      if (personUid.equals(survivorId)) {
        survivingSelectedRaceCodes.add(raceCode);
      } else {
        supersededRaceMap.computeIfAbsent(personUid, k -> new ArrayList<>()).add(raceCode);
      }
    }
  }

  private List<AuditUpdateAction> markUnselectedSurvivingRacesAsInactive(
      String survivorId, List<String> survivingSelectedRaceCodes) {

    String query = survivingSelectedRaceCodes.isEmpty()
        ? UPDATE_ALL_SURVIVOR_RACES_INACTIVE
        : UPDATE_SELECTED_EXCLUDED_RACES_INACTIVE;

    List<Map<String, Object>> rowsToUpdate = survivingSelectedRaceCodes.isEmpty()
        ? nbsTemplate.queryForList(
        FIND_PREEXISTING_PERSON_RACES_FOR_AUDIT,
        Map.of("survivorId", survivorId)//NOSONAR
    )
        : nbsTemplate.queryForList(
        FIND_EXCLUDED_PERSON_RACES_FOR_AUDIT,
        Map.of("survivorId", survivorId, "survivingSelectedRaceCodes", survivingSelectedRaceCodes)
    );

    List<AuditUpdateAction> updateActions = buildAuditUpdateActions(survivorId, rowsToUpdate);

    nbsTemplate.update(query, Map.of(
        "survivorId", survivorId,
        "survivingSelectedRaceCodes", survivingSelectedRaceCodes
    ));

    return updateActions;
  }

  private List<AuditUpdateAction> buildAuditUpdateActions(String survivorId, List<Map<String, Object>> rows) {
    return rows.stream()
        .map(row -> new AuditUpdateAction(
            Map.of(
                "person_uid", survivorId,//NOSONAR
                "race_cd", row.get("race_cd"),//NOSONAR
                "race_category_cd", row.get("race_category_cd")//NOSONAR
            ),
            Map.of("record_status_cd", row.get("record_status_cd"))
        ))
        .toList();
  }


  private List<String> getRaceCategoriesForSurvivor(String survivorId) {
    return nbsTemplate.queryForList(SELECT_ACTIVE_RACE_CATEGORIES,
        new MapSqlParameterSource("survivorId", survivorId), String.class);
  }

  private List<AuditInsertAction> processSupersededRaces(String survivorId,
      Map<String, List<String>> supersededRaceMap,
      List<String> survivingRaceCategories) {
    List<AuditInsertAction> auditInsertActionListAll = new ArrayList<>();

    for (Map.Entry<String, List<String>> entry : supersededRaceMap.entrySet()) {
      String supersededUid = entry.getKey();
      List<String> selectedRaceCodes = entry.getValue();
      List<AuditInsertAction> auditInsertActionList =
          processRaceCodesForSuperseded(survivorId, supersededUid, selectedRaceCodes, survivingRaceCategories);
      auditInsertActionListAll.addAll(auditInsertActionList);
    }
    return auditInsertActionListAll;
  }



  private List<AuditInsertAction> processRaceCodesForSuperseded(String survivorId, String supersededUid,
      List<String> raceCodes,
      List<String> survivingRaceCategories) {
    List<AuditInsertAction> auditInsertActionListAll = new ArrayList<>();
    Set<String> processedCategories = new HashSet<>();

    for (String raceCd : raceCodes) {
      String raceCategoryCd = getRaceCategoryCd(raceCd);
      if (processedCategories.contains(raceCategoryCd))
        continue;

      List<AuditInsertAction> auditInsertActionList =
          handleRaceCategory(survivorId, supersededUid, raceCategoryCd, survivingRaceCategories, raceCd);
      auditInsertActionListAll.addAll(auditInsertActionList);
      processedCategories.add(raceCategoryCd);
    }
    return auditInsertActionListAll;
  }

  private List<AuditInsertAction> handleRaceCategory(String survivorId, String supersededUid, String raceCategoryCd,
      List<String> survivingRaceCategories, String selectedRaceCd) {

    List<AuditInsertAction> auditInsertActionList = new ArrayList<>();
    AuditInsertAction auditInsertAction;
    if (survivingRaceCategories.contains(raceCategoryCd)) {
      auditInsertAction = addNewRaceDetailToSurviving(survivorId, supersededUid, raceCategoryCd, selectedRaceCd);
    } else {
      auditInsertActionList = copyRaceToSurviving(survivorId, supersededUid, raceCategoryCd);
      auditInsertAction = copyRaceDetailToSurviving(survivorId, supersededUid, raceCategoryCd, selectedRaceCd);
    }
    auditInsertActionList.add(auditInsertAction);
    return auditInsertActionList;
  }

  private AuditInsertAction addNewRaceDetailToSurviving(
      String survivorId, String supersededUid, String raceCategoryCd, String selectedRaceCd) {

    String query = """
        SELECT race_cd, race_category_cd
        FROM person_race
        WHERE person_uid = :supersededUid
          AND race_category_cd = :raceCategoryCd
          AND race_cd = :selectedRaceCd
        """;

    Map<String, Object> params = Map.of(
        "survivorId", survivorId,
        "supersededUid", supersededUid,//NOSONAR
        "raceCategoryCd", raceCategoryCd,//NOSONAR
        "selectedRaceCd", selectedRaceCd
    );

    Map<String, Object> rowToInsert = nbsTemplate.queryForMap(query, params);

    nbsTemplate.update(COPY_RACE_DETAIL_IF_NOT_EXISTS, params);

    return new AuditInsertAction(Map.of(
        "person_uid", survivorId,
        "race_cd", rowToInsert.get("race_cd"),
        "race_category_cd", rowToInsert.get("race_category_cd")
    ));
  }


  private List<AuditInsertAction> copyRaceToSurviving(
      String survivorId, String supersededUid, String raceCategoryCd) {

    String query = """
        SELECT race_cd, race_category_cd
        FROM person_race
        WHERE person_uid = :supersededUid
          AND race_category_cd = :raceCategoryCd
        """;

    Map<String, Object> params = Map.of(
        "survivorId", survivorId,
        "supersededUid", supersededUid,
        "raceCategoryCd", raceCategoryCd
    );

    List<Map<String, Object>> rowsToInsert = nbsTemplate.queryForList(query, params);

    nbsTemplate.update(COPY_RACE_FROM_SUPERSEDED_TO_SURVIVOR, params);

    return rowsToInsert.stream()
        .map(row -> new AuditInsertAction(Map.of(
            "person_uid", survivorId,
            "race_cd", row.get("race_cd"),
            "race_category_cd", row.get("race_category_cd")
        )))
        .toList();
  }



  private AuditInsertAction copyRaceDetailToSurviving(
      String survivorId, String supersededUid, String raceCategoryCd, String selectedRaceCd) {

    String query = """
        SELECT race_cd, race_category_cd
            FROM person_race
            WHERE person_uid = :supersededUid
              AND race_category_cd = :raceCategoryCd
              AND race_cd = :selectedRaceCd
        """;

    Map<String, Object> params = Map.of(
        "survivorId", survivorId,
        "supersededUid", supersededUid,
        "raceCategoryCd", raceCategoryCd,
        "selectedRaceCd", selectedRaceCd
    );

    Map<String, Object> rowToInsert = nbsTemplate.queryForMap(query, params);

    nbsTemplate.update(COPY_RACE_DETAIL_FROM_SUPERSEDED_TO_SURVIVOR, params);

    return new AuditInsertAction(Map.of(
        "person_uid", survivorId,
        "race_cd", rowToInsert.get("race_cd"),
        "race_category_cd", rowToInsert.get("race_category_cd")
    ));
  }



  private String getRaceCategoryCd(String raceCd) {
    return nbsTemplate.queryForObject(SELECT_RACE_CATEGORY_FOR_RACE_CD,
        new MapSqlParameterSource("raceCd", raceCd), String.class);
  }
}
