package gov.cdc.dataprocessing.repository.nbs.odse.model.phc;

import gov.cdc.dataprocessing.model.dto.phc.PatientEncounterDto;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Patient_encounter")

public class PatientEncounter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_encounter_uid")
    private Long patientEncounterUid;

    @Column(name = "activity_duration_amt")
    private String activityDurationAmt;

    @Column(name = "activity_duration_unit_cd")
    private String activityDurationUnitCd;

    @Column(name = "activity_from_time")
    private Timestamp activityFromTime;

    @Column(name = "activity_to_time")
    private Timestamp activityToTime;

    @Column(name = "acuity_level_cd")
    private String acuityLevelCd;

    @Column(name = "acuity_level_desc_txt")
    private String acuityLevelDescTxt;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "admission_source_cd")
    private String admissionSourceCd;

    @Column(name = "admission_source_desc_txt")
    private String admissionSourceDescTxt;

    @Column(name = "birth_encounter_ind")
    private String birthEncounterInd;

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

    @Column(name = "priority_cd")
    private String priorityCd;

    @Column(name = "priority_desc_txt")
    private String priorityDescTxt;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "referral_source_cd")
    private String referralSourceCd;

    @Column(name = "referral_source_desc_txt")
    private String referralSourceDescTxt;

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

    // Getters and setters
    public PatientEncounter() {

    }
    public PatientEncounter(PatientEncounterDto dto) {
        this.patientEncounterUid = dto.getPatientEncounterUid();
        this.activityDurationAmt = dto.getActivityDurationAmt();
        this.activityDurationUnitCd = dto.getActivityDurationUnitCd();
        this.activityFromTime = dto.getActivityFromTime();
        this.activityToTime = dto.getActivityToTime();
        this.acuityLevelCd = dto.getAcuityLevelCd();
        this.acuityLevelDescTxt = dto.getAcuityLevelDescTxt();
        this.addReasonCd = dto.getAddReasonCd();
        this.addTime = dto.getAddTime();
        this.addUserId = dto.getAddUserId();
        this.admissionSourceCd = dto.getAdmissionSourceCd();
        this.admissionSourceDescTxt = dto.getAdmissionSourceDescTxt();
        this.birthEncounterInd = dto.getBirthEncounterInd();
        this.cd = dto.getCd();
        this.cdDescTxt = dto.getCdDescTxt();
        this.confidentialityCd = dto.getConfidentialityCd();
        this.confidentialityDescTxt = dto.getConfidentialityDescTxt();
        this.effectiveDurationAmt = dto.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = dto.getEffectiveDurationUnitCd();
        this.effectiveFromTime = dto.getEffectiveFromTime();
        this.effectiveToTime = dto.getEffectiveToTime();
        this.lastChgReasonCd = dto.getLastChgReasonCd();
        this.lastChgTime = dto.getLastChgTime();
        this.lastChgUserId = dto.getLastChgUserId();
        this.localId = dto.getLocalId();
        this.priorityCd = dto.getPriorityCd();
        this.priorityDescTxt = dto.getPriorityDescTxt();
        this.recordStatusCd = dto.getRecordStatusCd();
        this.recordStatusTime = dto.getRecordStatusTime();
        this.referralSourceCd = dto.getReferralSourceCd();
        this.referralSourceDescTxt = dto.getReferralSourceDescTxt();
        this.repeatNbr = dto.getRepeatNbr();
        this.statusCd = dto.getStatusCd();
        this.statusTime = dto.getStatusTime();
        this.txt = dto.getTxt();
        this.userAffiliationTxt = dto.getUserAffiliationTxt();
        this.programJurisdictionOid = dto.getProgramJurisdictionOid();
        this.sharedInd = dto.getSharedInd();
        this.versionCtrlNbr = dto.getVersionCtrlNbr();
    }

}
