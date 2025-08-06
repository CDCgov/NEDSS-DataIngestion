package gov.cdc.nbs.deduplication.merge.handler;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

@Component
@Order(1)
public class PersonTableMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;
  private final NamedParameterJdbcTemplate deduplicationTemplate;

  public PersonTableMergeHandler(
      @Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate,
      @Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate deduplicationTemplate) {
    this.nbsTemplate = nbsTemplate;
    this.deduplicationTemplate = deduplicationTemplate;
  }

  public static final String FETCH_SUPERSEDED_CANDIDATES = """
      SELECT
        person_uid
      FROM
        merge_group_entries
      WHERE
        merge_group = :mergeGroup
        AND person_uid != :survivorId
        AND is_merge IS NULL;
      """;

  public static final String COPY_PERSON_TO_HISTORY = """
        INSERT INTO person_hist (
          person_uid,
          version_ctrl_nbr,
          add_reason_cd,
          add_time,
          add_user_id,
          administrative_gender_cd,
          age_calc,
          age_calc_time,
          age_calc_unit_cd,
          age_category_cd,
          age_reported,
          age_reported_time,
          age_reported_unit_cd,
          birth_gender_cd,
          birth_order_nbr,
          birth_time,
          birth_time_calc,
          cd,
          cd_desc_txt,
          curr_sex_cd,
          deceased_ind_cd,
          deceased_time,
          description,
          education_level_cd,
          education_level_desc_txt,
          ethnic_group_ind,
          last_chg_reason_cd,
          last_chg_time,
          last_chg_user_id,
          local_id,
          marital_status_cd,
          marital_status_desc_txt,
          mothers_maiden_nm,
          multiple_birth_ind,
          occupation_cd,
          preferred_gender_cd,
          prim_lang_cd,
          prim_lang_desc_txt,
          record_status_cd,
          record_status_time,
          status_cd,
          status_time,
          survived_ind_cd,
          user_affiliation_txt,
          first_nm,
          last_nm,
          middle_nm,
          nm_prefix,
          nm_suffix,
          preferred_nm,
          hm_street_addr1,
          hm_street_addr2,
          hm_city_cd,
          hm_city_desc_txt,
          hm_state_cd,
          hm_zip_cd,
          hm_cnty_cd,
          hm_cntry_cd,
          hm_phone_nbr,
          hm_phone_cntry_cd,
          hm_email_addr,
          cell_phone_nbr,
          wk_street_addr1,
          wk_street_addr2,
          wk_city_cd,
          wk_city_desc_txt,
          wk_state_cd,
          wk_zip_cd,
          wk_cnty_cd,
          wk_cntry_cd,
          wk_phone_nbr,
          wk_phone_cntry_cd,
          wk_email_addr,
          SSN,
          medicaid_num,
          dl_num,
          dl_state_cd,
          race_cd,
          race_seq_nbr,
          race_category_cd,
          ethnicity_group_cd,
          ethnicity_group_seq_nbr,
          adults_in_house_nbr,
          children_in_house_nbr,
          birth_city_cd,
          birth_city_desc_txt,
          birth_cntry_cd,
          birth_state_cd,
          race_desc_txt,
          ethnic_group_desc_txt,
          as_of_date_admin,
          as_of_date_ethnicity,
          as_of_date_general,
          as_of_date_morbidity,
          as_of_date_sex,
          electronic_ind,
          person_parent_uid,
          dedup_match_ind,
          group_nbr,
          group_time,
          edx_ind,
          speaks_english_cd,
          additional_gender_cd,
          ehars_id,
          ethnic_unk_reason_cd,
          sex_unk_reason_cd
      )
      SELECT
          person_uid,
          version_ctrl_nbr,
          add_reason_cd,
          add_time,
          add_user_id,
          administrative_gender_cd,
          age_calc,
          age_calc_time,
          age_calc_unit_cd,
          age_category_cd,
          age_reported,
          age_reported_time,
          age_reported_unit_cd,
          birth_gender_cd,
          birth_order_nbr,
          birth_time,
          birth_time_calc,
          cd,
          cd_desc_txt,
          curr_sex_cd,
          deceased_ind_cd,
          deceased_time,
          description,
          education_level_cd,
          education_level_desc_txt,
          ethnic_group_ind,
          last_chg_reason_cd,
          last_chg_time,
          last_chg_user_id,
          local_id,
          marital_status_cd,
          marital_status_desc_txt,
          mothers_maiden_nm,
          multiple_birth_ind,
          occupation_cd,
          preferred_gender_cd,
          prim_lang_cd,
          prim_lang_desc_txt,
          record_status_cd,
          record_status_time,
          status_cd,
          status_time,
          survived_ind_cd,
          user_affiliation_txt,
          first_nm,
          last_nm,
          middle_nm,
          nm_prefix,
          nm_suffix,
          preferred_nm,
          hm_street_addr1,
          hm_street_addr2,
          hm_city_cd,
          hm_city_desc_txt,
          hm_state_cd,
          hm_zip_cd,
          hm_cnty_cd,
          hm_cntry_cd,
          hm_phone_nbr,
          hm_phone_cntry_cd,
          hm_email_addr,
          cell_phone_nbr,
          wk_street_addr1,
          wk_street_addr2,
          wk_city_cd,
          wk_city_desc_txt,
          wk_state_cd,
          wk_zip_cd,
          wk_cnty_cd,
          wk_cntry_cd,
          wk_phone_nbr,
          wk_phone_cntry_cd,
          wk_email_addr,
          SSN,
          medicaid_num,
          dl_num,
          dl_state_cd,
          race_cd,
          race_seq_nbr,
          race_category_cd,
          ethnicity_group_cd,
          ethnic_group_seq_nbr,
          adults_in_house_nbr,
          children_in_house_nbr,
          birth_city_cd,
          birth_city_desc_txt,
          birth_cntry_cd,
          birth_state_cd,
          race_desc_txt,
          ethnic_group_desc_txt,
          as_of_date_admin,
          as_of_date_ethnicity,
          as_of_date_general,
          as_of_date_morbidity,
          as_of_date_sex,
          electronic_ind,
          person_parent_uid,
          dedup_match_ind,
          group_nbr,
          group_time,
          edx_ind,
          speaks_english_cd,
          additional_gender_cd,
          ehars_id,
          ethnic_unk_reason_cd,
          sex_unk_reason_cd
      FROM person
      WHERE person_uid IN (:involvedPatients)
      """;


  // Modifications have been performed on the person table entries.
  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String mergeGroup, PatientMergeRequest request, PatientMergeAudit patientMergeAudit) {
    String survivorId = request.survivingRecord();
    List<String> supersededUids = getSupersededRecords(mergeGroup, survivorId);
    List<String> involvedPatients = new ArrayList<>();
    involvedPatients.add(survivorId);
    involvedPatients.addAll(supersededUids);

    createHistoryEntries(involvedPatients);
    linkSupersededChildIdsToSurvivingMpr(survivorId, supersededUids);
    markSupersededRecords(supersededUids);
    updateLastChangeTime(involvedPatients);
    saveSupersededPersonMergeDetails(survivorId, supersededUids);

    patientMergeAudit.setSupersededIds(supersededUids);
    patientMergeAudit.setMergeTimestamp(getCurrentUtcTimestamp());
  }

  List<String> getSupersededRecords(String mergeGroup, String survivorId) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("mergeGroup", mergeGroup);
    params.addValue("survivorId", survivorId);
    return deduplicationTemplate.queryForList(
        FETCH_SUPERSEDED_CANDIDATES,
        params,
        String.class);
  }

  private void createHistoryEntries(List<String> involvedPatients) {
    savePersonCopyToPersonHist(involvedPatients);
    increasePersonVersionNbr(involvedPatients);
  }

  private void savePersonCopyToPersonHist(List<String> involvedPatients) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("involvedPatients", involvedPatients);// NOSONAR
    nbsTemplate.update(
        COPY_PERSON_TO_HISTORY,
        params);
  }

  private void increasePersonVersionNbr(List<String> involvedPatients) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("involvedPatients", involvedPatients);
    nbsTemplate.update(
        QueryConstants.INCREMENT_PERSON_VERSION_NUMBER,
        params);
  }

  private void linkSupersededChildIdsToSurvivingMpr(String survivorUid, List<String> supersededPersonIds) {
    List<String> childIds = getChildIdsOfTheSupersededPerson(supersededPersonIds);
    if (!childIds.isEmpty()) {
      updateParentIdForChildIds(survivorUid, childIds);
    }
  }

  private List<String> getChildIdsOfTheSupersededPerson(List<String> supersededPersonIds) {
    return nbsTemplate.queryForList(
        QueryConstants.CHILD_IDS_BY_PARENT_PERSON_IDS,
        new MapSqlParameterSource("parentPersonIds", supersededPersonIds),
        String.class);
  }

  private void updateParentIdForChildIds(String survivorId, List<String> supersededChildIds) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("supersededChildIds", supersededChildIds);
    nbsTemplate.update(
        QueryConstants.LINK_SUPERSEDED_CHILD_IDS_TO_SURVIVOR,
        params);
  }

  private void markSupersededRecords(List<String> supersededPersonIds) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("supersededPersonIds", supersededPersonIds);
    nbsTemplate.update(QueryConstants.MARK_SUPERSEDED_RECORDS, parameters);
  }

  private void updateLastChangeTime(List<String> involvedPatients) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("involvedPatients", involvedPatients);
    parameters.addValue("lastChgTime", getCurrentUtcTimestamp());
    nbsTemplate.update(QueryConstants.UPDATE_LAST_CHANGE_TIME_FOR_PATIENTS, parameters);
  }

  private void saveSupersededPersonMergeDetails(String survivorPersonId, List<String> supersededPersonIds) {
    NbsUserDetails currentUser = (NbsUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    MapSqlParameterSource[] batchParameters = supersededPersonIds.stream()
        .map(supersededPersonId -> {
          MapSqlParameterSource parameters = new MapSqlParameterSource();
          parameters.addValue("survivorPersonId", survivorPersonId);
          parameters.addValue("supersededPersonId", supersededPersonId);
          parameters.addValue("mergeTime", Timestamp.from(Instant.now()));
          parameters.addValue("mergeUserId", currentUser.getId());
          return parameters;
        })
        .toArray(MapSqlParameterSource[]::new);

    nbsTemplate.batchUpdate(QueryConstants.INSERT_PERSON_MERGE_RECORD, batchParameters);
  }

  public static Timestamp getCurrentUtcTimestamp() {
    return Timestamp.from(Instant.now());
  }

}
