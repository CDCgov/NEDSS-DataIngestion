package gov.cdc.nbs.deduplication.merge.model;

import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientMergeAudit {
  public PatientMergeAudit(List<RelatedTableAudit> relatedTableAudits) {
    this.relatedTableAudits = relatedTableAudits;
  }

  private Long id;
  private String survivorId;
  private List<String> supersededIds;
  private Timestamp mergeTimestamp;
  private List<RelatedTableAudit> relatedTableAudits;
  private PatientMergeRequest patientMergeRequest;
}
