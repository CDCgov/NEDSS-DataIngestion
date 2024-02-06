package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MessageLogDT extends AbstractVO {
    private static final long serialVersionUID = 1L;
    private Long messageLogUid;
    private String  messageTxt;
    private String  conditionCd;
    private Long personUid;
    private Long assignedToUid;
    private Long eventUid;
    private String  eventTypeCd;
    private String  messageStatusCd;
    private String  recordStatusCd;
    private Timestamp recordStatusTime;
    private Timestamp addTime;
    private Long userId;
    private Timestamp lastChgTime;
    private Long lastChgUserId;

}
