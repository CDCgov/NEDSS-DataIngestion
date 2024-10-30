package gov.cdc.dataprocessing.repository.nbs.odse.model.log;

import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "message_log")
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public class MessageLog   {

    @Id
    @Column(name = "message_log_uid")
    private Long messageLogUid;

    @Column(name = "message_txt")
    private String messageTxt;

    @Column(name = "condition_cd")
    private String conditionCd;

    @Column(name = "person_uid")
    private Long personUid;

    @Column(name = "assigned_to_uid")
    private Long assignedToUid;

    @Column(name = "event_uid")
    private Long eventUid;

    @Column(name = "event_type_cd")
    private String eventTypeCd;

    @Column(name = "message_status_cd")
    private String messageStatusCd;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    public MessageLog() {

    }

    public MessageLog(MessageLogDto messageLogDto) {
        this.messageLogUid = messageLogDto.getMessageLogUid();
        this.messageTxt = messageLogDto.getMessageTxt();
        this.conditionCd = messageLogDto.getConditionCd();
        this.personUid = messageLogDto.getPersonUid();
        this.assignedToUid = messageLogDto.getAssignedToUid();
        this.eventUid = messageLogDto.getEventUid();
        this.eventTypeCd = messageLogDto.getEventTypeCd();
        this.messageStatusCd = messageLogDto.getMessageStatusCd();
        this.recordStatusCd = messageLogDto.getRecordStatusCd();
        this.recordStatusTime = messageLogDto.getRecordStatusTime();
        this.addTime = messageLogDto.getAddTime();
        this.addUserId = messageLogDto.getUserId();
        this.lastChgTime = messageLogDto.getLastChgTime();
        this.lastChgUserId = messageLogDto.getLastChgUserId();
    }
}
