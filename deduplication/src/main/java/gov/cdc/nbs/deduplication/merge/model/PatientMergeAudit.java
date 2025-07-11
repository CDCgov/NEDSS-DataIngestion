package gov.cdc.nbs.deduplication.merge.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class PatientMergeAudit {
  private Long id;
  private String survivorId;
  private List<String> supersededIds;
  private Timestamp mergeTimestamp;
  private List<RelatedTableAudit> relatedTableAudits;
  private PatientMergeRequest patientMergeRequest;
}
