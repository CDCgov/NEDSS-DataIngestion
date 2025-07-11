package gov.cdc.nbs.deduplication.merge.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

@Component
@Order(10)
public class PersonGeneralInfoMergeHandler implements SectionMergeHandler {

  static final String UPDATE_PERSON_GENERAL_AS_OF = """
      UPDATE person
         SET as_of_date_general = (
             SELECT as_of_date_general
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_MARITAL_STATUS_CD = """
      UPDATE person
         SET marital_status_cd = (
             SELECT marital_status_cd
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_MOTHERS_MAIDEN_NAME = """
      UPDATE person
         SET mothers_maiden_nm = (
             SELECT mothers_maiden_nm
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_NUMBER_OF_ADULTS = """
      UPDATE person
         SET adults_in_house_nbr = (
             SELECT adults_in_house_nbr
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_NUMBER_OF_CHILDREN = """
      UPDATE person
         SET children_in_house_nbr = (
             SELECT children_in_house_nbr
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_OCCUPATION_CD = """
      UPDATE person
         SET occupation_cd = (
             SELECT occupation_cd
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_EDUCATION_LEVEL_CD = """
      UPDATE person
         SET education_level_cd = (
             SELECT education_level_cd
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_PRIMARY_LANGUAGE_CD = """
      UPDATE person
         SET prim_lang_cd = (
             SELECT prim_lang_cd
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_SPEAKS_ENGLISH_CD = """
      UPDATE person
         SET speaks_english_cd = (
             SELECT speaks_english_cd
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  static final String UPDATE_PERSON_STATE_HIV_CASE_ID = """
      UPDATE person
         SET ehars_id = (
             SELECT ehars_id
               FROM person
              WHERE person_uid = :sourceId
         ),
      last_chg_time = GETDATE(),
      last_chg_reason_cd = 'merge'
       WHERE person_uid = :survivorId
      """;

  private final NamedParameterJdbcTemplate nbsTemplate;

  public PersonGeneralInfoMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request) {
    mergePersonGeneralInfo(request.survivingRecord(), request.generalInfo());
  }

  private void mergePersonGeneralInfo(String survivorId, PatientMergeRequest.GeneralInfoFieldSource fieldSource) {
    updateAsOf(survivorId, fieldSource.asOf());
    updateMaritalStatus(survivorId, fieldSource.maritalStatus());
    updateMothersMaidenName(survivorId, fieldSource.mothersMaidenName());
    updateNumberOfAdults(survivorId, fieldSource.numberOfAdultsInResidence());
    updateNumberOfChildren(survivorId, fieldSource.numberOfChildrenInResidence());
    updatePrimaryOccupation(survivorId, fieldSource.primaryOccupation());
    updateEducationLevel(survivorId, fieldSource.educationLevel());
    updatePrimaryLanguage(survivorId, fieldSource.primaryLanguage());
    updateSpeaksEnglish(survivorId, fieldSource.speaksEnglish());
    updateStateHivCaseId(survivorId, fieldSource.stateHivCaseId());
  }

  private void updateAsOf(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_GENERAL_AS_OF);
    }
  }

  private void updateMaritalStatus(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_MARITAL_STATUS_CD);
    }
  }

  private void updateMothersMaidenName(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_MOTHERS_MAIDEN_NAME);
    }
  }

  private void updateNumberOfAdults(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_NUMBER_OF_ADULTS);
    }
  }

  private void updateNumberOfChildren(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_NUMBER_OF_CHILDREN);
    }
  }

  private void updatePrimaryOccupation(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_OCCUPATION_CD);
    }
  }

  private void updateEducationLevel(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_EDUCATION_LEVEL_CD);
    }
  }

  private void updatePrimaryLanguage(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_PRIMARY_LANGUAGE_CD);
    }
  }

  private void updateSpeaksEnglish(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_SPEAKS_ENGLISH_CD);
    }
  }

  private void updateStateHivCaseId(String survivorId, String sourceId) {
    if (!survivorId.equals(sourceId)) {
      updatePersonField(survivorId, sourceId, UPDATE_PERSON_STATE_HIV_CASE_ID);
    }
  }

  private void updatePersonField(String survivorId, String sourceId, String query) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("survivorId", survivorId);
    params.addValue("sourceId", sourceId);
    nbsTemplate.update(query, params);
  }
}
