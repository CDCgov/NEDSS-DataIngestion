package gov.cdc.dataprocessing.model.dto.matching;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdxPatientMatchDto extends BaseContainer {

  private Long edxPatientMatchUid;
  private Long patientUid;
  private String matchString;
  private String typeCd;
  private Long matchStringHashCode;

  private Long addUserId;
  private Long lastChgUserId;
  private Timestamp addTime;
  private Timestamp lastChgTime;
  private boolean multipleMatch = false;
}
