package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Order(3)
public class PersonNamesMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;

  static final String UPDATE_ALL_PERSON_NAMES_INACTIVE = """
      UPDATE person_name
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE()
      WHERE person_uid = :personUid
      """;

  static final String UPDATE_SELECTED_EXCLUDED_NAMES_INACTIVE = """
      UPDATE person_name
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE()
      WHERE person_uid = :personUid
        AND person_name_seq NOT IN (:sequences)
      """;

  static final String FIND_MAX_SEQUENCE_PERSON_NAME = """
      SELECT MAX(person_name_seq)
      FROM person_name
      WHERE person_uid = :personUid
      """;

  static final String COPY_PERSON_NAME_TO_SURVIVING = """
      INSERT INTO person_name (
          person_uid, person_name_seq, add_reason_cd, add_time, add_user_id,
          default_nm_ind, duration_amt, duration_unit_cd, first_nm, first_nm_sndx,
          from_time, last_chg_reason_cd, last_chg_time, last_chg_user_id,
          last_nm, last_nm_sndx, last_nm2, last_nm2_sndx, middle_nm, middle_nm2,
          nm_degree, nm_prefix, nm_suffix, nm_use_cd, record_status_cd,
          record_status_time, status_cd, status_time, to_time, user_affiliation_txt, as_of_date
      )
      SELECT
          :survivingId AS person_uid,
          :newSeq AS person_name_seq,
          'MERGE' AS add_reason_cd,
          GETDATE() AS add_time,
          add_user_id,
          default_nm_ind,
          duration_amt,
          duration_unit_cd,
          first_nm,
          first_nm_sndx,
          from_time,
          last_chg_reason_cd,
          GETDATE() AS last_chg_time,
          last_chg_user_id,
          last_nm,
          last_nm_sndx,
          last_nm2,
          last_nm2_sndx,
          middle_nm,
          middle_nm2,
          nm_degree,
          nm_prefix,
          nm_suffix,
          nm_use_cd,
          'ACTIVE' AS record_status_cd,
          GETDATE() AS record_status_time,
          status_cd,
          status_time,
          to_time,
          user_affiliation_txt,
          as_of_date
      FROM person_name
      WHERE person_uid = :supersededUid
        AND person_name_seq = :oldSeq
      """;

  public PersonNamesMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request) {
    mergePersonNames(request.survivingRecord(), request.names());
  }

  private void mergePersonNames(String survivorId, List<PatientMergeRequest.NameId> names) {
    List<Integer> survivingNamesSequences = new ArrayList<>();
    Map<String, List<Integer>> supersededNames = new HashMap<>();
    categorizeNames(survivorId, names, survivingNamesSequences, supersededNames);
    markUnselectedNamesInactive(survivorId, survivingNamesSequences);
    updateSupersededNames(survivorId, supersededNames);
  }

  private void categorizeNames(String survivorId, List<PatientMergeRequest.NameId> names,
      List<Integer> survivingSequences, Map<String, List<Integer>> supersededNames) {
    for (PatientMergeRequest.NameId name : names) {
      String personUid = name.personUid();
      Integer seq = Integer.parseInt(name.sequence());

      if (personUid.equals(survivorId)) {
        survivingSequences.add(seq);
      } else {
        supersededNames.computeIfAbsent(personUid, k -> new ArrayList<>()).add(seq);
      }
    }
  }

  private void markUnselectedNamesInactive(String survivorId, List<Integer> selectedSequences) {
    String query = selectedSequences.isEmpty() ? UPDATE_ALL_PERSON_NAMES_INACTIVE
        : UPDATE_SELECTED_EXCLUDED_NAMES_INACTIVE;
    Map<String, Object> params = new HashMap<>();
    params.put("personUid", survivorId);
    if (!selectedSequences.isEmpty()) {
      params.put("sequences", selectedSequences);
    }
    nbsTemplate.update(query, params);
  }

  private void updateSupersededNames(String survivorId, Map<String, List<Integer>> supersededNames) {
    for (Map.Entry<String, List<Integer>> entry : supersededNames.entrySet()) {
      copySupersededNamesToSurviving(entry.getKey(), entry.getValue(), survivorId);
    }
  }

  private void copySupersededNamesToSurviving(String supersededUid, List<Integer> sequences, String survivingId) {
    int survivingMaxSeq = getMaxSequenceForPerson(survivingId);

    for (Integer seq : sequences) {
      int newSeq = ++survivingMaxSeq;
      Map<String, Object> params = new HashMap<>();
      params.put("supersededUid", supersededUid);
      params.put("oldSeq", seq);
      params.put("survivingId", survivingId);
      params.put("newSeq", newSeq);

      nbsTemplate.update(COPY_PERSON_NAME_TO_SURVIVING, params);
    }
  }

  private int getMaxSequenceForPerson(String personUid) {
    Map<String, Object> params = Collections.singletonMap("personUid", personUid);
    Integer maxSequence = nbsTemplate.queryForObject(FIND_MAX_SEQUENCE_PERSON_NAME, params, Integer.class);
    return maxSequence == null ? 0 : maxSequence;
  }
}
