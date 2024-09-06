package gov.cdc.nbsDedup.nbs.odse.model.participation;


import gov.cdc.nbsDedup.model.dto.participation.ParticipationDto;
import gov.cdc.nbsDedup.nbs.odse.model.id_class.ParticipationHistId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Participation_hist")
@Data
@IdClass(ParticipationHistId.class)
public class ParticipationHist  implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "subject_entity_uid")
    @Id
    private Long subjectEntityUid;

    @Id
    @Column(name = "act_uid")
    private Long actUid;

    @Id
    @Column(name = "type_cd")
    private String typeCd;

    @Id
    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    @Column(name = "act_class_cd")
    private String actClassCd;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "awareness_cd")
    private String awarenessCd;

    @Column(name = "awareness_desc_txt")
    private String awarenessDescTxt;

    @Column(name = "cd")
    private String cd;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "role_seq")
    private Long roleSeq;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "subject_class_cd")
    private String subjectClassCd;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "type_desc_txt")
    private String typeDescTxt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    public ParticipationHist() {

    }


    public ParticipationHist(ParticipationDto participationDto) {
        this.subjectEntityUid = participationDto.getSubjectEntityUid();
        this.actUid = participationDto.getActUid();
        this.typeCd = participationDto.getTypeCd();
        this.actClassCd = participationDto.getActClassCd();
        this.addReasonCd = participationDto.getAddReasonCd();
        this.addTime = participationDto.getAddTime();
        this.addUserId = participationDto.getAddUserId();
        this.awarenessCd = participationDto.getAwarenessCd();
        this.awarenessDescTxt = participationDto.getAwarenessDescTxt();
        this.cd = participationDto.getCd();
        this.durationAmt = participationDto.getDurationAmt();
        this.durationUnitCd = participationDto.getDurationUnitCd();
        this.fromTime = participationDto.getFromTime();
        this.lastChgReasonCd = participationDto.getLastChgReasonCd();
        this.lastChgTime = participationDto.getLastChgTime();
        this.lastChgUserId = participationDto.getLastChgUserId();
        this.recordStatusCd = participationDto.getRecordStatusCd();
        this.recordStatusTime = participationDto.getRecordStatusTime();
        this.roleSeq = participationDto.getRoleSeq();
        this.statusCd = participationDto.getStatusCd();
        this.statusTime = participationDto.getStatusTime();
        this.subjectClassCd = participationDto.getSubjectClassCd();
        this.toTime = participationDto.getToTime();
        this.typeDescTxt = participationDto.getTypeDescTxt();
        this.userAffiliationTxt = participationDto.getUserAffiliationTxt();
    }


}
