package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

  final NamedParameterJdbcTemplate nbsTemplate;

  public PersonRacesMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  // Merge modifications have been applied to the person races
  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request) {
    mergePersonRaces(request.survivingRecord(), request.races());
  }

  private void mergePersonRaces(String survivorId, List<PatientMergeRequest.RaceId> races) {
    List<String> survivingSelectedRaceCodes = new ArrayList<>();
    Map<String, List<String>> supersededRaceMap = new HashMap<>();

    categorizeRaces(survivorId, races, supersededRaceMap, survivingSelectedRaceCodes);
    markUnselectedSurvivingRacesAsInactive(survivorId, survivingSelectedRaceCodes);

    List<String> survivingRaceCategories = getRaceCategoriesForSurvivor(survivorId);
    processSupersededRaces(survivorId, supersededRaceMap, survivingRaceCategories);
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

  private void markUnselectedSurvivingRacesAsInactive(String survivorId, List<String> survivingSelectedRaceCodes) {
    String query = survivingSelectedRaceCodes.isEmpty() ? UPDATE_ALL_SURVIVOR_RACES_INACTIVE
        : UPDATE_SELECTED_EXCLUDED_RACES_INACTIVE;

    Map<String, Object> params = new HashMap<>();
    params.put("survivorId", survivorId);// NOSONAR
    if (!survivingSelectedRaceCodes.isEmpty()) {
      params.put("survivingSelectedRaceCodes", survivingSelectedRaceCodes);
    }
    nbsTemplate.update(query, params);
  }

  private List<String> getRaceCategoriesForSurvivor(String survivorId) {
    return nbsTemplate.queryForList(SELECT_ACTIVE_RACE_CATEGORIES,
        new MapSqlParameterSource("survivorId", survivorId), String.class);
  }

  private void processSupersededRaces(String survivorId, Map<String, List<String>> supersededRaceMap,
      List<String> survivingRaceCategories) {

    for (Map.Entry<String, List<String>> entry : supersededRaceMap.entrySet()) {
      String supersededUid = entry.getKey();
      List<String> selectedRaceCodes = entry.getValue();
      processRaceCodesForSuperseded(survivorId, supersededUid, selectedRaceCodes, survivingRaceCategories);
    }
  }

  private void processRaceCodesForSuperseded(String survivorId, String supersededUid, List<String> raceCodes,
      List<String> survivingRaceCategories) {

    Set<String> processedCategories = new HashSet<>();

    for (String raceCd : raceCodes) {
      String raceCategoryCd = getRaceCategoryCd(raceCd);
      if (processedCategories.contains(raceCategoryCd))
        continue;

      handleRaceCategory(survivorId, supersededUid, raceCategoryCd, survivingRaceCategories, raceCd);
      processedCategories.add(raceCategoryCd);
    }
  }

  private void handleRaceCategory(String survivorId, String supersededUid, String raceCategoryCd,
      List<String> survivingRaceCategories, String selectedRaceCd) {

    if (survivingRaceCategories.contains(raceCategoryCd)) {
      addNewRaceDetailToSurviving(survivorId, supersededUid, raceCategoryCd, selectedRaceCd);
    } else {
      copyRaceToSurviving(survivorId, supersededUid, raceCategoryCd);
      copyRaceDetailToSurviving(survivorId, supersededUid, raceCategoryCd, selectedRaceCd);
    }
  }

  private void addNewRaceDetailToSurviving(String survivorId, String supersededUid, String raceCategoryCd,
      String selectedRaceCd) {

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("supersededUid", supersededUid);// NOSONAR
    params.addValue("raceCategoryCd", raceCategoryCd);// NOSONAR
    params.addValue("selectedRaceCd", selectedRaceCd);

    nbsTemplate.update(COPY_RACE_DETAIL_IF_NOT_EXISTS, params);
  }

  private void copyRaceToSurviving(String survivorId, String supersededUid, String raceCategoryCd) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("supersededUid", supersededUid);
    params.addValue("raceCategoryCd", raceCategoryCd);

    nbsTemplate.update(COPY_RACE_FROM_SUPERSEDED_TO_SURVIVOR, params);
  }

  private void copyRaceDetailToSurviving(String survivorId, String supersededUid, String raceCategoryCd,
      String selectedRaceCd) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("supersededUid", supersededUid);
    params.addValue("raceCategoryCd", raceCategoryCd);
    params.addValue("selectedRaceCd", selectedRaceCd);

    nbsTemplate.update(COPY_RACE_DETAIL_FROM_SUPERSEDED_TO_SURVIVOR, params);
  }

  private String getRaceCategoryCd(String raceCd) {
    return nbsTemplate.queryForObject(SELECT_RACE_CATEGORY_FOR_RACE_CD,
        new MapSqlParameterSource("raceCd", raceCd), String.class);
  }
}
