package gov.cdc.dataprocessing.model.dto.matching;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdxEntityMatchDto extends BaseContainer {
  private Long edxEntityMatchUid;
  private Long entityUid;
  private String matchString;
  private String typeCd;
  private Long matchStringHashCode;

  private Long addUserId;
  private Long lastChgUserId;
  private Timestamp addTime;
  private Timestamp lastChgTime;
}
