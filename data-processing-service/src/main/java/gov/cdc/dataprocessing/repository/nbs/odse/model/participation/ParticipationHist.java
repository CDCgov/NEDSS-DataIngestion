package gov.cdc.dataprocessing.repository.nbs.odse.model.participation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Participation_hist")
@Data
public class ParticipationHist {
    @Column(name = "subject_entity_uid")
    @Id
    private Long subjectEntityUid;

    @Column(name = "act_uid")
    private Long actUid;

    @Column(name = "type_cd")
    private String typeCd;

    @Column(name = "version_ctrl_nbr")
 //   @Version
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


    public ParticipationHist(ParticipationDT participationDT) {
        this.subjectEntityUid = participationDT.getSubjectEntityUid();
        this.actUid = participationDT.getActUid();
        this.typeCd = participationDT.getTypeCd();
        this.actClassCd = participationDT.getActClassCd();
        this.addReasonCd = participationDT.getAddReasonCd();
        this.addTime = participationDT.getAddTime();
        this.addUserId = participationDT.getAddUserId();
        this.awarenessCd = participationDT.getAwarenessCd();
        this.awarenessDescTxt = participationDT.getAwarenessDescTxt();
        this.cd = participationDT.getCd();
        this.durationAmt = participationDT.getDurationAmt();
        this.durationUnitCd = participationDT.getDurationUnitCd();
        this.fromTime = participationDT.getFromTime();
        this.lastChgReasonCd = participationDT.getLastChgReasonCd();
        this.lastChgTime = participationDT.getLastChgTime();
        this.lastChgUserId = participationDT.getLastChgUserId();
        this.recordStatusCd = participationDT.getRecordStatusCd();
        this.recordStatusTime = participationDT.getRecordStatusTime();
        this.roleSeq = participationDT.getRoleSeq();
        this.statusCd = participationDT.getStatusCd();
        this.statusTime = participationDT.getStatusTime();
        this.subjectClassCd = participationDT.getSubjectClassCd();
        this.toTime = participationDT.getToTime();
        this.typeDescTxt = participationDT.getTypeDescTxt();
        this.userAffiliationTxt = participationDT.getUserAffiliationTxt();
    }


}
