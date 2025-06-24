package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;


@Component
@Order(7)
public class PersonEthnicityMergeHandler implements SectionMergeHandler {


  static final String UPDATE_PERSON_ETHNICITY_IND = """
      UPDATE person
      SET ethnic_group_ind = (
          SELECT ethnic_group_ind
          FROM person
          WHERE person_uid = :ethnicitySourcePersonId
      ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd='merge'
      WHERE person_uid = :survivorId
      """;

  static final String FETCH_SUPERSEDED_PERSON_ETHNICITY_IND = """
      SELECT ethnic_group_ind
      FROM person
      WHERE person_uid = :personUid
      """;

  static final String UPDATE_PRE_EXISTING_ENTRIES_TO_INACTIVE = """
      UPDATE person_ethnic_group
      SET
        record_status_cd = 'INACTIVE',
        last_chg_time = GETDATE(),
        last_chg_reason_cd='merge'
      WHERE person_uid = :survivorId
      """;

  static final String COPY_SUPERSEDED_ETHNIC_GROUPS_TO_SURVIVING = """
      INSERT INTO person_ethnic_group (
          person_uid,
          ethnic_group_cd,
          add_reason_cd,
          add_time,
          add_user_id,
          ethnic_group_desc_txt,
          last_chg_reason_cd,
          last_chg_time,
          last_chg_user_id,
          record_status_cd,
          record_status_time,
          user_affiliation_txt
      )
      SELECT
          :survivorId,
          ethnic_group_cd,
          add_reason_cd,
          GETDATE(),
          add_user_id,
          ethnic_group_desc_txt,
          'merge',
          GETDATE(),
          last_chg_user_id,
          record_status_cd,
          record_status_time,
          user_affiliation_txt
      FROM person_ethnic_group
      WHERE person_uid = :supersededId
        AND record_status_cd = 'ACTIVE'
      """;


  final NamedParameterJdbcTemplate nbsTemplate;

  public PersonEthnicityMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  //Merge modifications have been applied to the person ethnicity
  @Override
  public void handleMerge(String matchId, PatientMergeRequest request) {
    mergePersonEthnicity(request.survivingRecord(), request.ethnicitySource());
  }

  private void mergePersonEthnicity(String survivorId, String ethnicitySourcePersonId) {
    updatePersonEthnicityIndicator(survivorId, ethnicitySourcePersonId);
    updatePreexistingEntriesToInactive(survivorId);
    if (!isEthnicityIndicatorNull(ethnicitySourcePersonId)) {
      copySupersededEntriesToSurviving(survivorId, ethnicitySourcePersonId);
    }
  }


  private void updatePersonEthnicityIndicator(String survivorId, String ethnicitySourcePersonId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("survivorId", survivorId);
    parameters.addValue("ethnicitySourcePersonId", ethnicitySourcePersonId);
    nbsTemplate.update(UPDATE_PERSON_ETHNICITY_IND, parameters);
  }

  private void updatePreexistingEntriesToInactive(String survivorId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("survivorId", survivorId);
    nbsTemplate.update(UPDATE_PRE_EXISTING_ENTRIES_TO_INACTIVE, parameters);
  }


  private boolean isEthnicityIndicatorNull(String personUid) {
    String ethnicityInd = nbsTemplate.queryForObject(
        FETCH_SUPERSEDED_PERSON_ETHNICITY_IND,
        new MapSqlParameterSource("personUid", personUid),
        String.class
    );
    return ethnicityInd == null || ethnicityInd.isBlank();
  }

  private void copySupersededEntriesToSurviving(String survivorId, String supersededId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("survivorId", survivorId);
    parameters.addValue("supersededId", supersededId);

    nbsTemplate.update(COPY_SUPERSEDED_ETHNIC_GROUPS_TO_SURVIVING, parameters);
  }


}
