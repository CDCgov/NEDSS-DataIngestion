package gov.cdc.dataprocessing.repository.nbs.odse.model.phc;

import gov.cdc.dataprocessing.model.dto.phc.ReferralDto;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Referral")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referral_uid")
    private Long referralUid;

    @Column(name = "activity_duration_amt")
    private String activityDurationAmt;

    @Column(name = "activity_duration_unit_cd")
    private String activityDurationUnitCd;

    @Column(name = "activity_from_time")
    private Timestamp activityFromTime;

    @Column(name = "activity_to_time")
    private Timestamp activityToTime;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "confidentiality_cd")
    private String confidentialityCd;

    @Column(name = "confidentiality_desc_txt")
    private String confidentialityDescTxt;

    @Column(name = "effective_duration_amt")
    private String effectiveDurationAmt;

    @Column(name = "effective_duration_unit_cd")
    private String effectiveDurationUnitCd;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "reason_txt")
    private String reasonTxt;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "referral_desc_txt")
    private String referralDescTxt;

    @Column(name = "repeat_nbr")
    private Integer repeatNbr;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "txt")
    private String txt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "program_jurisdiction_oid")
    private Long programJurisdictionOid;

    @Column(name = "shared_ind")
    private String sharedInd;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    // Getters and Setters
    public Referral() {

    }

    public Referral(ReferralDto referralDto) {
        this.referralUid = referralDto.getReferralUid();
        this.activityDurationAmt = referralDto.getActivityDurationAmt();
        this.activityDurationUnitCd = referralDto.getActivityDurationUnitCd();
        this.activityFromTime = referralDto.getActivityFromTime();
        this.activityToTime = referralDto.getActivityToTime();
        this.addReasonCd = referralDto.getAddReasonCd();
        this.addTime = referralDto.getAddTime();
        this.addUserId = referralDto.getAddUserId();
        this.cd = referralDto.getCd();
        this.cdDescTxt = referralDto.getCdDescTxt();
        this.confidentialityCd = referralDto.getConfidentialityCd();
        this.confidentialityDescTxt = referralDto.getConfidentialityDescTxt();
        this.effectiveDurationAmt = referralDto.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = referralDto.getEffectiveDurationUnitCd();
        this.effectiveFromTime = referralDto.getEffectiveFromTime();
        this.effectiveToTime = referralDto.getEffectiveToTime();
        this.lastChgReasonCd = referralDto.getLastChgReasonCd();
        this.lastChgTime = referralDto.getLastChgTime();
        this.lastChgUserId = referralDto.getLastChgUserId();
        this.localId = referralDto.getLocalId();
        this.reasonTxt = referralDto.getReasonTxt();
        this.recordStatusCd = referralDto.getRecordStatusCd();
        this.recordStatusTime = referralDto.getRecordStatusTime();
        this.referralDescTxt = referralDto.getReferralDescTxt();
        this.repeatNbr = referralDto.getRepeatNbr();
        this.statusCd = referralDto.getStatusCd();
        this.statusTime = referralDto.getStatusTime();
        this.txt = referralDto.getTxt();
        this.userAffiliationTxt = referralDto.getUserAffiliationTxt();
        this.programJurisdictionOid = referralDto.getProgramJurisdictionOid();
        this.sharedInd = referralDto.getSharedInd();
        this.versionCtrlNbr = referralDto.getVersionCtrlNbr();
    }


}
