package gov.cdc.nbs.deduplication.duplicates.model;

import lombok.Data;

import java.util.List;

@Data
public class MergePatientRequest {
  private String survivorPersonId;
  private List<String> supersededPersonIds;
}
