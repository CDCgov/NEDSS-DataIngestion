package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

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

  static final String INSERT_NEW_DEATH_LOCATOR = """
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

  final NamedParameterJdbcTemplate nbsTemplate;

  public PersonMortalityMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  // Merge modifications have been applied to the person Mortality
  public void handleMerge(String matchId, PatientMergeRequest request) {
    mergePersonMortality(request.survivingRecord(), request.mortality());
  }

  private void mergePersonMortality(String survivorId, PatientMergeRequest.MortalityFieldSource fieldSource) {
    updateAsOf(survivorId, fieldSource.asOf());
    updateDeceasedFlag(survivorId, fieldSource.deceased());
    updateDateOfDeath(survivorId, fieldSource.dateOfDeath());
    updateDeathAddress(survivorId, fieldSource.deathCity());
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

  private void updateDeathAddress(String survivorId, String sourceId) {
    // TODO - Mortality should allow individual selection of Death city, Death
    // country, Death state (Death county uses selected patient's death state)
    if (!survivorId.equals(sourceId)) {
      markUnselectedDeathAddressInactive(survivorId);
      copyDeathAddressFromSupersededToSurviving(survivorId, sourceId);
    }
  }

  private void markUnselectedDeathAddressInactive(String survivingId) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivingId", survivingId);
    nbsTemplate.update(UPDATE_UN_SELECTED_DEATH_ADDRESS_INACTIVE, params);
  }

  private void copyDeathAddressFromSupersededToSurviving(String survivingId, String sourceId) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivingId", survivingId);
    params.addValue("sourceId", sourceId);

    nbsTemplate.update(INSERT_NEW_DEATH_LOCATOR, params);
  }

  private void updatePersonField(String survivorId, String sourceId, String query) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("sourceId", sourceId);
    nbsTemplate.update(query, params);
  }

}
