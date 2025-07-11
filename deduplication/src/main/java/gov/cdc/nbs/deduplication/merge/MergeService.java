package gov.cdc.nbs.deduplication.merge;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.merge.handler.SectionMergeHandler;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MergeService {

  private static final String SAVE_PATIENT_MERGE_AUDIT_SQL = """
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
  private final NamedParameterJdbcTemplate nbsTemplate;
  private final ObjectMapper objectMapper;

  public MergeService(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate,
      List<SectionMergeHandler> handlers,
      ObjectMapper objectMapper) {
    this.handlers = handlers;
    this.nbsTemplate = nbsTemplate;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public void performMerge(Long matchId, PatientMergeRequest request) {
    String matchIdStr = matchId.toString();
    List<SectionMergeHandler> orderedHandlers = new ArrayList<>(handlers);
    AnnotationAwareOrderComparator.sort(orderedHandlers);

    PatientMergeAudit patientMergeAudit = initPatientMergeAudit(request);

    for (SectionMergeHandler handler : orderedHandlers) {
      handler.handleMerge(matchIdStr, request, patientMergeAudit);
    }
    saveAuditToDatabase(patientMergeAudit);
  }

  private PatientMergeAudit initPatientMergeAudit(PatientMergeRequest request) {
    PatientMergeAudit patientMergeAudit = new PatientMergeAudit();
    patientMergeAudit.setPatientMergeRequest(request);
    patientMergeAudit.setSurvivorId(request.survivingRecord());
    patientMergeAudit.setRelatedTableAudits(new ArrayList<>());
    return patientMergeAudit;
  }

  private void saveAuditToDatabase(PatientMergeAudit audit) {
    try {
      String relatedAuditsJson = objectMapper.writeValueAsString(audit.getRelatedTableAudits());
      String mergeRequestJson = objectMapper.writeValueAsString(audit.getPatientMergeRequest());

      Map<String, Object> params = new HashMap<>();
      params.put("survivorId", audit.getSurvivorId());
      params.put("supersededIds", String.join(",", audit.getSupersededIds()));
      params.put("relatedAuditsJson", relatedAuditsJson);
      params.put("mergeRequestJson", mergeRequestJson);


      nbsTemplate.update(SAVE_PATIENT_MERGE_AUDIT_SQL, params);

    } catch (Exception e) {
      throw new RuntimeException(e);//NOSONAR
    }
  }
}
