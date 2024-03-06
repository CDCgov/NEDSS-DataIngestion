package gov.cdc.dataprocessing.repository.nbs.odse.model.log;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.MessageLogDT;
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

    public MessageLog(MessageLogDT messageLogDT) {
        this.messageLogUid = messageLogDT.getMessageLogUid();
        this.messageTxt = messageLogDT.getMessageTxt();
        this.conditionCd = messageLogDT.getConditionCd();
        this.personUid = messageLogDT.getPersonUid();
        this.assignedToUid = messageLogDT.getAssignedToUid();
        this.eventUid = messageLogDT.getEventUid();
        this.eventTypeCd = messageLogDT.getEventTypeCd();
        this.messageStatusCd = messageLogDT.getMessageStatusCd();
        this.recordStatusCd = messageLogDT.getRecordStatusCd();
        this.recordStatusTime = messageLogDT.getRecordStatusTime();
        this.addTime = messageLogDT.getAddTime();
        this.addUserId = messageLogDT.getUserId();
        this.lastChgTime = messageLogDT.getLastChgTime();
        this.lastChgUserId = messageLogDT.getLastChgUserId();
    }
}
