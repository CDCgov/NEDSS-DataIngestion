package gov.cdc.dataprocessing.model.dto.log;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.MessageLog;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class MessageLogDto extends BaseContainer {
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

    public MessageLogDto() {

    }

    public MessageLogDto(MessageLog messageLog) {
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
