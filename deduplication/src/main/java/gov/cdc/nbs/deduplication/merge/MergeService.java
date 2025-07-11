package gov.cdc.nbs.deduplication.merge;

import java.util.ArrayList;
import java.util.List;

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

  private final List<SectionMergeHandler> handlers;
  private final JdbcClient deduplicationClient;

  public MergeService(
      final List<SectionMergeHandler> handlers,
      final @Qualifier("deduplicationJdbcClient") JdbcClient deduplicationClient) {
    List<SectionMergeHandler> orderedHandlers = new ArrayList<>(handlers);
    AnnotationAwareOrderComparator.sort(orderedHandlers);
    this.handlers = orderedHandlers;
    this.deduplicationClient = deduplicationClient;
  }

  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.REQUIRED)
  public void performMerge(Long matchId, PatientMergeRequest request) {
    String matchIdStr = matchId.toString();

    for (SectionMergeHandler handler : handlers) {
      handler.handleMerge(matchIdStr, request);
    }

    markPatientsMerged(matchId);
  }

  private void markPatientsMerged(long matchId) {
    deduplicationClient.sql(MARK_PATIENTS_AS_MERGED)
        .param("matchId", matchId)
        .update();
  }
}
