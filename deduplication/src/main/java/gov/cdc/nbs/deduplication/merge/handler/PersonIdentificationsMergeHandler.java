package gov.cdc.nbs.deduplication.merge.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

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

  public PersonIdentificationsMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request) {
    mergePersonIdentifications(request.survivingRecord(), request.identifications());
  }

  private void mergePersonIdentifications(String survivorId,
      List<PatientMergeRequest.IdentificationId> identifications) {
    List<Integer> survivingIdentificationsSequences = new ArrayList<>();
    Map<String, List<Integer>> supersededIdentifications = new HashMap<>();
    categorizeIdentifications(survivorId, identifications, survivingIdentificationsSequences,
        supersededIdentifications);
    markUnselectedIdentificationsInactive(survivorId, survivingIdentificationsSequences);
    updateSupersededIdentifications(survivorId, supersededIdentifications);
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

  private void markUnselectedIdentificationsInactive(String survivorId, List<Integer> selectedSequences) {
    String query = selectedSequences.isEmpty() ? UPDATE_ALL_PERSON_IDENTIFICATION_INACTIVE
        : UPDATE_SELECTED_EXCLUDED_IDENTIFICATION_INACTIVE;
    Map<String, Object> params = new HashMap<>();
    params.put("personUid", survivorId);
    if (!selectedSequences.isEmpty()) {
      params.put("sequences", selectedSequences);
    }
    nbsTemplate.update(query, params);
  }

  private void updateSupersededIdentifications(String survivorId,
      Map<String, List<Integer>> supersededIdentifications) {
    for (Map.Entry<String, List<Integer>> entry : supersededIdentifications.entrySet()) {
      copySupersededIdentificationsToSurviving(entry.getKey(), entry.getValue(), survivorId);
    }
  }

  private void copySupersededIdentificationsToSurviving(String supersededUid, List<Integer> sequences,
      String survivingId) {
    int survivingMaxSeq = getMaxSequenceForPerson(survivingId);

    for (Integer seq : sequences) {
      int newSeq = ++survivingMaxSeq;
      Map<String, Object> params = new HashMap<>();
      params.put("supersededUid", supersededUid);
      params.put("oldSeq", seq);
      params.put("survivingId", survivingId);
      params.put("newSeq", newSeq);

      nbsTemplate.update(COPY_ENTITY_ID_TO_SURVIVING, params);
    }
  }

  private int getMaxSequenceForPerson(String personUid) {
    Map<String, Object> params = Collections.singletonMap("personUid", personUid);
    Integer maxSequence = nbsTemplate.queryForObject(FIND_MAX_SEQUENCE_PERSON_IDENTIFICATION, params, Integer.class);
    return maxSequence == null ? 0 : maxSequence;
  }
}
