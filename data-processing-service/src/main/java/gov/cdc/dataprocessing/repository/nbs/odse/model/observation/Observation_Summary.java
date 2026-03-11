package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class Observation_Summary {
  private Long uid;
  private Timestamp addTime;
  private String addReasonCd;
}
