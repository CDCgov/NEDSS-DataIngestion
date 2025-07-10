package gov.cdc.nbs.deduplication.merge;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.merge.handler.SectionMergeHandler;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MergeService {

  private final List<SectionMergeHandler> handlers;

  @Transactional
  public void performMerge(Long matchId, PatientMergeRequest request) {
    String matchIdStr = matchId.toString();
    List<SectionMergeHandler> orderedHandlers = new ArrayList<>(handlers);
    AnnotationAwareOrderComparator.sort(orderedHandlers);

    for (SectionMergeHandler handler : orderedHandlers) {
      handler.handleMerge(matchIdStr, request);
    }
  }
}
