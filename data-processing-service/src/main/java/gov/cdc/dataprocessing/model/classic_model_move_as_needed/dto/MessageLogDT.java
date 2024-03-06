package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.MessageLog;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MessageLogDT  extends AbstractVO {
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

    public MessageLogDT() {

    }

    public MessageLogDT(MessageLog messageLog) {
        this.messageLogUid = messageLog.getMessageLogUid();
        this.messageTxt = messageLog.getMessageTxt();
        this.conditionCd = messageLog.getConditionCd();
        this.personUid = messageLog.getPersonUid();
        this.assignedToUid = messageLog.getAssignedToUid();
        this.eventUid = messageLog.getEventUid();
        this.eventTypeCd = messageLog.getEventTypeCd();
        this.messageStatusCd = messageLog.getMessageStatusCd();
        this.recordStatusCd = messageLog.getRecordStatusCd();
        this.recordStatusTime = messageLog.getRecordStatusTime();
        this.addTime = messageLog.getAddTime();
        this.userId = messageLog.getAddUserId();
        this.lastChgTime = messageLog.getLastChgTime();
        this.lastChgUserId = messageLog.getLastChgUserId();
    }

}
