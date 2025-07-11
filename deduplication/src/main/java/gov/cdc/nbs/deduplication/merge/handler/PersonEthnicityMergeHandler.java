package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;


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

  static final String FIND_PREEXISTING_PERSON_ETHNIC_GROUPS_FOR_AUDIT = """
      SELECT person_uid, ethnic_group_cd, record_status_cd
      FROM person_ethnic_group
      WHERE person_uid = :survivorId
      """;


  static final String FIND_SUPERSEDED_ETHNIC_GROUPS_FOR_AUDIT = """
      SELECT  ethnic_group_cd
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
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit patientMergeAudit) {
    mergePersonEthnicity(request.survivingRecord(), request.ethnicitySource(), patientMergeAudit);
  }

  private void mergePersonEthnicity(String survivorId, String ethnicitySourcePersonId, PatientMergeAudit audit) {
    updatePersonEthnicityIndicator(survivorId, ethnicitySourcePersonId);
    List<AuditUpdateAction> updateActions = performPreexistingEntriesInactivation(survivorId);
    List<AuditInsertAction> insertActions = Collections.emptyList();

    if (!isEthnicityIndicatorNull(ethnicitySourcePersonId)) {
      insertActions = performCopySupersededEntriesToSurviving(survivorId, ethnicitySourcePersonId);
    }

    audit.getRelatedTableAudits().add(
        new RelatedTableAudit("person_ethnic_group", updateActions, insertActions)
    );
  }



  private List<AuditInsertAction> performCopySupersededEntriesToSurviving(String survivorId, String supersededId) {
    Map<String, Object> params = Map.of("supersededId", supersededId);

    List<Map<String, Object>> rowsToInsert = nbsTemplate.queryForList(
        FIND_SUPERSEDED_ETHNIC_GROUPS_FOR_AUDIT, params
    );

    List<AuditInsertAction> insertActions = buildAuditInsertActions(survivorId, rowsToInsert);

    copySupersededEthnicGroupsToSurvivor(survivorId, supersededId);

    return insertActions;
  }

  private List<AuditInsertAction> buildAuditInsertActions(String survivorId, List<Map<String, Object>> rows) {
    return rows.stream()
        .map(row -> new AuditInsertAction(
            Map.of("person_uid", survivorId, "ethnic_group_cd", row.get("ethnic_group_cd"))//NOSONAR
        ))
        .toList();
  }

  private void copySupersededEthnicGroupsToSurvivor(String survivorId, String supersededId) {
    nbsTemplate.update(
        COPY_SUPERSEDED_ETHNIC_GROUPS_TO_SURVIVING,
        Map.of("survivorId", survivorId, "supersededId", supersededId)//NOSONAR
    );
  }



  private void updatePersonEthnicityIndicator(String survivorId, String ethnicitySourcePersonId) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("survivorId", survivorId);//NOSONAR
    parameters.addValue("ethnicitySourcePersonId", ethnicitySourcePersonId);
    nbsTemplate.update(UPDATE_PERSON_ETHNICITY_IND, parameters);
  }



  private List<AuditUpdateAction> performPreexistingEntriesInactivation(String survivorId) {
    Map<String, Object> params = Map.of("survivorId", survivorId);

    List<Map<String, Object>> rowsToUpdate = nbsTemplate.queryForList(
        FIND_PREEXISTING_PERSON_ETHNIC_GROUPS_FOR_AUDIT, params
    );

    List<AuditUpdateAction> updateActions = buildAuditUpdateActions(rowsToUpdate);

    inactivatePreexistingEntries(survivorId);

    return updateActions;
  }

  private void inactivatePreexistingEntries(String survivorId) {
    nbsTemplate.update(
        UPDATE_PRE_EXISTING_ENTRIES_TO_INACTIVE,
        Map.of("survivorId", survivorId)
    );
  }


  private List<AuditUpdateAction> buildAuditUpdateActions(List<Map<String, Object>> rows) {
    return rows.stream()
        .map(row -> new AuditUpdateAction(
            Map.of("person_uid", row.get("person_uid"), "ethnic_group_cd", row.get("ethnic_group_cd")),
            Map.of("record_status_cd", row.get("record_status_cd"))
        ))
        .toList();
  }



  private boolean isEthnicityIndicatorNull(String personUid) {
    String ethnicityInd = nbsTemplate.queryForObject(
        FETCH_SUPERSEDED_PERSON_ETHNICITY_IND,
        new MapSqlParameterSource("personUid", personUid),
        String.class
    );
    return ethnicityInd == null || ethnicityInd.isBlank();
  }



}
