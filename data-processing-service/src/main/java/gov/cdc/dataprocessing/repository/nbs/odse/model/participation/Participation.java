package gov.cdc.dataprocessing.repository.nbs.odse.model.participation;

import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ParticipationId;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@IdClass(ParticipationId.class)
@Table(name = "Participation")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class Participation {

    @Id
    @Column(name = "subject_entity_uid", nullable = false)
    private Long subjectEntityUid;

    @Id
    @Column(name = "act_uid", nullable = false)
    private Long actUid;

    @Id
    @Column(name = "type_cd", length = 50, nullable = false)
    private String typeCode;

    @Column(name = "act_class_cd", length = 10)
    private String actClassCode;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCode;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "awareness_cd", length = 20)
    private String awarenessCode;

    @Column(name = "awareness_desc_txt", length = 100)
    private String awarenessDescription;

    @Column(name = "cd", length = 40)
    private String code;

    @Column(name = "duration_amt", length = 20)
    private String durationAmount;

    @Column(name = "duration_unit_cd", length = 20)
    private String durationUnitCode;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChangeReasonCode;

    @Column(name = "last_chg_time")
    private Timestamp lastChangeTime;

    @Column(name = "last_chg_user_id")
    private Long lastChangeUserId;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCode;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "role_seq")
    private Long roleSeq;

    @Column(name = "status_cd", length = 1)
    private String statusCode;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "subject_class_cd", length = 10)
    private String subjectClassCode;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "type_desc_txt", length = 100)
    private String typeDescription;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationText;

    public Participation() {
    }

    public Participation(ParticipationDto participationDto) {
        this.subjectEntityUid = participationDto.getSubjectEntityUid();
        this.actUid = participationDto.getActUid();
        this.typeCode = participationDto.getTypeCd();
        this.actClassCode = participationDto.getActClassCd();
        this.addReasonCode = participationDto.getAddReasonCd();
        this.addTime = participationDto.getAddTime();
        this.addUserId = participationDto.getAddUserId();
        this.awarenessCode = participationDto.getAwarenessCd();
        this.awarenessDescription = participationDto.getAwarenessDescTxt();
        this.code = participationDto.getCd();
        this.durationAmount = participationDto.getDurationAmt();
        this.durationUnitCode = participationDto.getDurationUnitCd();
        this.fromTime = participationDto.getFromTime();
        this.lastChangeReasonCode = participationDto.getLastChgReasonCd();
        this.lastChangeTime = participationDto.getLastChgTime();
        this.lastChangeUserId = participationDto.getLastChgUserId();
        this.recordStatusCode = participationDto.getRecordStatusCd();
        this.recordStatusTime = participationDto.getRecordStatusTime();
        this.roleSeq = participationDto.getRoleSeq();
        this.statusCode = participationDto.getStatusCd();
        this.statusTime = participationDto.getStatusTime();
        this.subjectClassCode = participationDto.getSubjectClassCd();
        this.toTime = participationDto.getToTime();
        this.typeDescription = participationDto.getTypeDescTxt();
        this.userAffiliationText = participationDto.getUserAffiliationTxt();
    }
}
