package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(3)
public class PersonNamesMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;

  static final String UPDATE_ALL_PERSON_NAMES_INACTIVE = """
      UPDATE person_name
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE(),
          last_chg_reason_cd='MERGE'
      WHERE person_uid = :personUid
      """;

  static final String UPDATE_SELECTED_EXCLUDED_NAMES_INACTIVE = """
      UPDATE person_name
      SET record_status_cd = 'INACTIVE',
          last_chg_time = GETDATE(),
          last_chg_reason_cd='MERGE'
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

  private static final String FIND_PERSON_NAMES_FOR_INACTIVATION = """
      SELECT person_uid, person_name_seq, record_status_cd
      FROM person_name
      WHERE person_uid = :personUid
      """;

  private static final String FIND_EXCLUDED_PERSON_NAMES_FOR_INACTIVATION = """
      SELECT person_uid, person_name_seq, record_status_cd
      FROM person_name
      WHERE person_uid = :personUid
        AND person_name_seq NOT IN (:sequences)
      """;

  private static final String FIND_SUPERSEDED_NAMES_FOR_AUDIT = """
      SELECT person_uid, person_name_seq
      FROM person_name
      WHERE person_uid = :supersededUid
        AND person_name_seq = :oldSeq
      """;

  public PersonNamesMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  @Override
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit patientMergeAudit) {
    mergePersonNames(request.survivingRecord(), request.names(), patientMergeAudit);
  }

  private void mergePersonNames(String survivorId, List<PatientMergeRequest.NameId> names, PatientMergeAudit audit) {
    List<Integer> survivingSequences = new ArrayList<>();
    Map<String, List<Integer>> supersededNames = new HashMap<>();

    categorizeNames(survivorId, names, survivingSequences, supersededNames);

    List<AuditUpdateAction> updateActions = performNameInactivation(survivorId, survivingSequences);
    List<AuditInsertAction> insertActions = performNameCopyToSurvivor(survivorId, supersededNames);

    audit.getRelatedTableAudits().add(new RelatedTableAudit("person_name", updateActions, insertActions));
  }

  private void categorizeNames(String survivorId, List<PatientMergeRequest.NameId> names,
      List<Integer> survivingSequences, Map<String, List<Integer>> supersededNames) {
    names.forEach(name -> {
      if (name.personUid().equals(survivorId)) {
        survivingSequences.add(Integer.parseInt(name.sequence()));
      } else {
        supersededNames.computeIfAbsent(name.personUid(), k -> new ArrayList<>())
            .add(Integer.parseInt(name.sequence()));
      }
    });
  }

  private List<AuditUpdateAction> performNameInactivation(String survivorId, List<Integer> selectedSequences) {
    String query =
        selectedSequences.isEmpty() ? UPDATE_ALL_PERSON_NAMES_INACTIVE : UPDATE_SELECTED_EXCLUDED_NAMES_INACTIVE;
    Map<String, Object> params = new HashMap<>();
    params.put("personUid", survivorId);//NOSONAR
    if (!selectedSequences.isEmpty()) {
      params.put("sequences", selectedSequences);
    }


    List<Map<String, Object>> rowsToUpdate = fetchRowsForInactivation(survivorId, selectedSequences);
    List<AuditUpdateAction> auditUpdates = buildAuditUpdateActions(rowsToUpdate);

    nbsTemplate.update(query, params);
    return auditUpdates;
  }

  private List<Map<String, Object>> fetchRowsForInactivation(String survivorId, List<Integer> selectedSequences) {
    if (selectedSequences.isEmpty()) {
      return nbsTemplate.queryForList(
          FIND_PERSON_NAMES_FOR_INACTIVATION,
          Collections.singletonMap("personUid", survivorId)
      );
    }

    return nbsTemplate.queryForList(
        FIND_EXCLUDED_PERSON_NAMES_FOR_INACTIVATION,
        Map.of("personUid", survivorId, "sequences", selectedSequences)
    );
  }

  private List<AuditUpdateAction> buildAuditUpdateActions(List<Map<String, Object>> rowsToUpdate) {
    return rowsToUpdate.stream()
        .map(row -> new AuditUpdateAction(
            Map.of("person_uid", row.get("person_uid"), "person_name_seq", row.get("person_name_seq")),//NOSONAR
            Map.of("record_status_cd", row.get("record_status_cd"))
        ))
        .toList();
  }

  private List<AuditInsertAction> performNameCopyToSurvivor(String survivorId,
      Map<String, List<Integer>> supersededNames) {

    AtomicInteger maxSeq = new AtomicInteger(getMaxSequenceForPerson(survivorId));
    List<AuditInsertAction> insertActions = new ArrayList<>();

    supersededNames.forEach((supersededUid, sequences) -> {
      for (Integer oldSeq : sequences) {
        int newSeq = maxSeq.incrementAndGet();

        Map<String, Object> params = new HashMap<>();
        params.put("supersededUid", supersededUid);
        params.put("oldSeq", oldSeq);

        List<Map<String, Object>> rowsToInsert = nbsTemplate.queryForList(
            FIND_SUPERSEDED_NAMES_FOR_AUDIT, params);


        if (!rowsToInsert.isEmpty()) {
          insertActions.add(buildAuditInsertAction(survivorId, newSeq));
        }

        copyPersonNameToSurvivor(survivorId, supersededUid, oldSeq, newSeq);
      }
    });

    return insertActions;
  }



  private void copyPersonNameToSurvivor(String survivorId, String supersededUid, Integer oldSeq, int newSeq) {
    Map<String, Object> params = new HashMap<>();
    params.put("survivingId", survivorId);
    params.put("supersededUid", supersededUid);
    params.put("oldSeq", oldSeq);
    params.put("newSeq", newSeq);

    nbsTemplate.update(COPY_PERSON_NAME_TO_SURVIVING, params);
  }

  private AuditInsertAction buildAuditInsertAction(String survivorId, int newSeq) {
    return new AuditInsertAction(Map.of(
        "person_uid", survivorId,
        "person_name_seq", newSeq
    ));
  }

  private int getMaxSequenceForPerson(String personUid) {
    Integer maxSequence = nbsTemplate.queryForObject(
        FIND_MAX_SEQUENCE_PERSON_NAME,
        Collections.singletonMap("personUid", personUid),
        Integer.class
    );
    return maxSequence == null ? 0 : maxSequence;
  }
}
