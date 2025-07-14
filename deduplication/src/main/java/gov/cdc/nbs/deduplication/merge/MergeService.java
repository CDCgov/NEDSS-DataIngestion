package gov.cdc.nbs.deduplication.merge;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.merge.handler.SectionMergeHandler;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

@Service
public class MergeService {

  static final String MARK_PATIENTS_AS_MERGED = """
      UPDATE match_candidates
      SET is_merge = 1
      WHERE  match_id = :matchId
      AND is_merge IS NULL;
      """;

  static final String SAVE_PATIENT_MERGE_AUDIT = """
          INSERT INTO patient_merge_audit (
              survivor_id,
              superseded_ids,
              merge_time,
              related_table_audits_json,
              patient_merge_request_json
          )
          VALUES (
              :survivorId,
              :supersededIds,
              GETDATE(),
              :relatedAuditsJson,
              :mergeRequestJson
          )
      """;

  private final List<SectionMergeHandler> handlers;
  private final JdbcClient deduplicationClient;
  private final ObjectMapper objectMapper;

  public MergeService(
      final List<SectionMergeHandler> handlers,
      final @Qualifier("deduplicationJdbcClient") JdbcClient deduplicationClient,
      ObjectMapper objectMapper) {
    List<SectionMergeHandler> orderedHandlers = new ArrayList<>(handlers);
    AnnotationAwareOrderComparator.sort(orderedHandlers);
    this.handlers = orderedHandlers;
    this.deduplicationClient = deduplicationClient;
    this.objectMapper = objectMapper;
  }

  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.REQUIRED)
  public void performMerge(Long matchId, PatientMergeRequest request) throws JsonProcessingException {
    String matchIdStr = matchId.toString();

    PatientMergeAudit patientMergeAudit = initPatientMergeAudit(request);
    for (SectionMergeHandler handler : handlers) {
      handler.handleMerge(matchIdStr, request, patientMergeAudit);
    }

    markPatientsMerged(matchId);
    saveAuditToDatabase(patientMergeAudit);
  }

  private void markPatientsMerged(long matchId) {
    deduplicationClient.sql(MARK_PATIENTS_AS_MERGED)
        .param("matchId", matchId)
        .update();
  }

  private PatientMergeAudit initPatientMergeAudit(PatientMergeRequest request) {
    PatientMergeAudit patientMergeAudit = new PatientMergeAudit();
    patientMergeAudit.setPatientMergeRequest(request);
    patientMergeAudit.setSurvivorId(request.survivingRecord());
    patientMergeAudit.setRelatedTableAudits(new ArrayList<>());
    return patientMergeAudit;
  }

  private void saveAuditToDatabase(PatientMergeAudit audit) throws JsonProcessingException {
    String relatedAuditsJson = objectMapper.writeValueAsString(audit.getRelatedTableAudits());
    String mergeRequestJson = objectMapper.writeValueAsString(audit.getPatientMergeRequest());

    deduplicationClient.sql(SAVE_PATIENT_MERGE_AUDIT)
        .param("survivorId", audit.getSurvivorId())
        .param("relatedAuditsJson", relatedAuditsJson)
        .param("mergeRequestJson", mergeRequestJson)
        .param("supersededIds",
            String.join(",", audit.getSupersededIds() != null ? audit.getSupersededIds() : List.of()))
        .update();
  }
}
