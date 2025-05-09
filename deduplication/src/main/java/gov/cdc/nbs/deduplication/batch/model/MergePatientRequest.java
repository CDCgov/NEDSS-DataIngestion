package gov.cdc.nbs.deduplication.batch.model;

import lombok.Data;

import java.util.List;

@Data
public class MergePatientRequest {
  private String survivorPersonId;
  private List<String> supersededPersonIds;
}
