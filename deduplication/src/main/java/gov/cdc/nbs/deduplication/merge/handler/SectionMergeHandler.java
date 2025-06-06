package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

public interface SectionMergeHandler {
  void handleMerge(String matchId ,PatientMergeRequest request);
}
