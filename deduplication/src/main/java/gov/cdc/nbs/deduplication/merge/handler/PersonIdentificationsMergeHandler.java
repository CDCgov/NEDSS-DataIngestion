
package gov.cdc.nbs.deduplication.merge.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.cdc.nbs.deduplication.merge.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(6)
public class PersonIdentificationsMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;

  static final String UPDATE_ALL_PERSON_IDENTIFICATION_INACTIVE = """
      UPDATE Entity_id
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE()
      WHERE entity_uid = :personUid
      """;

  static final String UPDATE_SELECTED_EXCLUDED_IDENTIFICATION_INACTIVE = """
      UPDATE Entity_id
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE()
      WHERE entity_uid = :personUid
        AND entity_id_seq NOT IN (:sequences)
      """;

  static final String FIND_MAX_SEQUENCE_PERSON_IDENTIFICATION = """
      SELECT MAX(entity_id_seq)
      FROM Entity_id
      WHERE entity_uid = :personUid
      """;

  static final String COPY_ENTITY_ID_TO_SURVIVING = """
      INSERT INTO entity_id (
          entity_uid,
          entity_id_seq,
          add_reason_cd,
          add_time,
          add_user_id,
          assigning_authority_cd,
          assigning_authority_desc_txt,
          duration_amt,
          duration_unit_cd,
          effective_from_time,
          effective_to_time,
          last_chg_reason_cd,
          last_chg_time,
          last_chg_user_id,
          record_status_cd,
          record_status_time,
          root_extension_txt,
          status_cd,
          status_time,
          type_cd,
          type_desc_txt,
          user_affiliation_txt,
          valid_from_time,
          valid_to_time,
          as_of_date,
          assigning_authority_id_type
      )
      SELECT
          :survivingId AS entity_uid,
          :newSeq AS entity_id_seq,
          'MERGE' AS add_reason_cd,
          GETDATE() AS add_time,
          add_user_id,
          assigning_authority_cd,
          assigning_authority_desc_txt,
          duration_amt,
          duration_unit_cd,
          effective_from_time,
          effective_to_time,
          last_chg_reason_cd,
          GETDATE() AS last_chg_time,
          last_chg_user_id,
          'ACTIVE' AS record_status_cd,
          GETDATE() AS record_status_time,
          root_extension_txt,
          status_cd,
          status_time,
          type_cd,
          type_desc_txt,
          user_affiliation_txt,
          valid_from_time,
          valid_to_time,
          as_of_date,
          assigning_authority_id_type
      FROM entity_id
      WHERE entity_uid = :supersededUid
        AND entity_id_seq = :oldSeq
      """;

  static final String FIND_ALL_IDENTIFICATIONS_FOR_AUDIT = """
      SELECT entity_uid, entity_id_seq, record_status_cd
      FROM Entity_id
      WHERE entity_uid = :personUid
      """;

  static final String FIND_UNSELECTED_IDENTIFICATIONS_FOR_AUDIT = """
      SELECT entity_uid, entity_id_seq, record_status_cd
      FROM Entity_id
      WHERE entity_uid = :personUid
        AND entity_id_seq NOT IN (:sequences)
      """;

  static final String FIND_SUPERSEDED_IDENTIFICATIONS_FOR_AUDIT = """
      SELECT entity_uid, entity_id_seq
      FROM entity_id
      WHERE entity_uid = :supersededUid
        AND entity_id_seq = :seq
      """;

  public PersonIdentificationsMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit audit) {
    mergePersonIdentifications(request.survivingRecord(), request.identifications(), audit);
  }

  private void mergePersonIdentifications(String survivorId,
      List<PatientMergeRequest.IdentificationId> identifications,
      PatientMergeAudit audit) {
    List<Integer> survivingIdentificationsSequences = new ArrayList<>();
    Map<String, List<Integer>> supersededIdentifications = new HashMap<>();

    categorizeIdentifications(
        survivorId, identifications, survivingIdentificationsSequences, supersededIdentifications);

    List<AuditUpdateAction> updateActions = markUnselectedIdentificationsInactive(
        survivorId, survivingIdentificationsSequences);
    List<AuditInsertAction> insertActions = updateSupersededIdentifications(
        survivorId, supersededIdentifications);

    audit.getRelatedTableAudits().add(
        new RelatedTableAudit("Entity_id", updateActions, insertActions));
  }

  private void categorizeIdentifications(String survivorId, List<PatientMergeRequest.IdentificationId> identifications,
      List<Integer> survivingSequences, Map<String, List<Integer>> supersededIdentifications) {
    for (PatientMergeRequest.IdentificationId identification : identifications) {
      String personUid = identification.personUid();
      Integer seq = Integer.parseInt(identification.sequence());

      if (personUid.equals(survivorId)) {
        survivingSequences.add(seq);
      } else {
        supersededIdentifications.computeIfAbsent(personUid, k -> new ArrayList<>()).add(seq);
      }
    }
  }

  private List<AuditUpdateAction> markUnselectedIdentificationsInactive(String survivorId,
      List<Integer> selectedSequences) {
    String query = selectedSequences.isEmpty() ? UPDATE_ALL_PERSON_IDENTIFICATION_INACTIVE
        : UPDATE_SELECTED_EXCLUDED_IDENTIFICATION_INACTIVE;

    Map<String, Object> params = new HashMap<>();
    params.put("personUid", survivorId);

    List<Map<String, Object>> rowsToUpdate;

    if (selectedSequences.isEmpty()) {
      rowsToUpdate = nbsTemplate.queryForList(
          FIND_ALL_IDENTIFICATIONS_FOR_AUDIT, params);
    } else {
      params.put("sequences", selectedSequences);
      rowsToUpdate = nbsTemplate.queryForList(
          FIND_UNSELECTED_IDENTIFICATIONS_FOR_AUDIT, params);
    }

    List<AuditUpdateAction> auditUpdates = buildAuditUpdateActions(rowsToUpdate);

    nbsTemplate.update(query, params);
    return auditUpdates;
  }

  private List<AuditUpdateAction> buildAuditUpdateActions(List<Map<String, Object>> rows) {
    return rows.stream()
        .map(row -> {
          Map<String, Object> values = new HashMap<>();
          values.put("record_status_cd", row.get("record_status_cd"));

          return new AuditUpdateAction(
              Map.of("entity_uid", row.get("entity_uid"), "entity_id_seq", row.get("entity_id_seq")), // NOSONAR
              values);
        })
        .toList();
  }

  private List<AuditInsertAction> updateSupersededIdentifications(
      String survivorId, Map<String, List<Integer>> supersededIdentifications) {
    List<AuditInsertAction> insertActions = new ArrayList<>();

    for (Map.Entry<String, List<Integer>> entry : supersededIdentifications.entrySet()) {
      String supersededUid = entry.getKey();
      List<Integer> sequences = entry.getValue();
      insertActions.addAll(copySupersededIdentificationsToSurviving(supersededUid, sequences, survivorId));
    }

    return insertActions;
  }

  private List<AuditInsertAction> copySupersededIdentificationsToSurviving(
      String supersededUid, List<Integer> sequences, String survivingId) {

    int survivingMaxSeq = getMaxSequenceForPerson(survivingId);
    List<AuditInsertAction> insertActions = new ArrayList<>();

    for (Integer oldSeq : sequences) {
      int newSeq = ++survivingMaxSeq;

      Map<String, Object> params = Map.of(
          "supersededUid", supersededUid,
          "seq", oldSeq);

      List<Map<String, Object>> rowsToInsert = nbsTemplate.queryForList(
          FIND_SUPERSEDED_IDENTIFICATIONS_FOR_AUDIT, params);

      if (!rowsToInsert.isEmpty()) {
        insertActions.add(buildAuditInsertAction(survivingId, newSeq));
      }

      copyIdentificationToSurvivor(supersededUid, oldSeq, survivingId, newSeq);
    }

    return insertActions;
  }

  private void copyIdentificationToSurvivor(String supersededUid, Integer oldSeq, String survivingId, int newSeq) {
    Map<String, Object> params = new HashMap<>();
    params.put("supersededUid", supersededUid);
    params.put("oldSeq", oldSeq);
    params.put("survivingId", survivingId);
    params.put("newSeq", newSeq);

    nbsTemplate.update(COPY_ENTITY_ID_TO_SURVIVING, params);
  }

  private AuditInsertAction buildAuditInsertAction(String survivingId, int newSeq) {
    return new AuditInsertAction(Map.of(
        "entity_uid", survivingId,
        "entity_id_seq", newSeq));
  }

  private int getMaxSequenceForPerson(String personUid) {
    Map<String, Object> params = Collections.singletonMap("personUid", personUid);
    Integer maxSequence = nbsTemplate.queryForObject(FIND_MAX_SEQUENCE_PERSON_IDENTIFICATION, params, Integer.class);
    return maxSequence == null ? 0 : maxSequence;
  }
}
