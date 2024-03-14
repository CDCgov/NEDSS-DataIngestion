package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Observation")
@Data
public class Observation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

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

    @Column(name = "cd_system_cd")
    private String cdSystemCd;

    @Column(name = "cd_system_desc_txt")
    private String cdSystemDescTxt;

    @Column(name = "confidentiality_cd")
    private String confidentialityCd;

    @Column(name = "confidentiality_desc_txt")
    private String confidentialityDescTxt;

    @Column(name = "ctrl_cd_display_form")
    private String ctrlCdDisplayForm;

    @Column(name = "ctrl_cd_user_defined_1")
    private String ctrlCdUserDefined1;

    @Column(name = "ctrl_cd_user_defined_2")
    private String ctrlCdUserDefined2;

    @Column(name = "ctrl_cd_user_defined_3")
    private String ctrlCdUserDefined3;

    @Column(name = "ctrl_cd_user_defined_4")
    private String ctrlCdUserDefined4;

    @Column(name = "derivation_exp")
    private Integer derivationExp;

    @Column(name = "effective_duration_amt")
    private String effectiveDurationAmt;

    @Column(name = "effective_duration_unit_cd")
    private String effectiveDurationUnitCd;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "electronic_ind")
    private String electronicInd;

    @Column(name = "group_level_cd")
    private String groupLevelCd;

    @Column(name = "jurisdiction_cd")
    private String jurisdictionCd;

    @Column(name = "lab_condition_cd")
    private String labConditionCd;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "method_cd")
    private String methodCd;

    @Column(name = "method_desc_txt")
    private String methodDescTxt;

    @Column(name = "obs_domain_cd")
    private String obsDomainCd;

    @Column(name = "obs_domain_cd_st_1")
    private String obsDomainCdSt1;

    @Column(name = "pnu_cd")
    private String pnuCd;

    @Column(name = "priority_cd")
    private String priorityCd;

    @Column(name = "priority_desc_txt")
    private String priorityDescTxt;

    @Column(name = "prog_area_cd")
    private String progAreaCd;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "repeat_nbr")
    private Integer repeatNbr;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "subject_person_uid")
    private Long subjectPersonUid;

    @Column(name = "target_site_cd")
    private String targetSiteCd;

    @Column(name = "target_site_desc_txt")
    private String targetSiteDescTxt;

    @Column(name = "txt")
    private String txt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "value_cd")
    private String valueCd;

    @Column(name = "ynu_cd")
    private String ynuCd;

    @Column(name = "program_jurisdiction_oid")
    private Long programJurisdictionOid;

    @Column(name = "shared_ind", nullable = false)
    private String sharedInd;

    @Column(name = "version_ctrl_nbr", nullable = false)
    private Integer versionCtrlNbr;

    @Column(name = "alt_cd")
    private String altCd;

    @Column(name = "alt_cd_desc_txt")
    private String altCdDescTxt;

    @Column(name = "alt_cd_system_cd")
    private String altCdSystemCd;

    @Column(name = "alt_cd_system_desc_txt")
    private String altCdSystemDescTxt;

    @Column(name = "cd_derived_ind")
    private String cdDerivedInd;

    @Column(name = "rpt_to_state_time")
    private Timestamp rptToStateTime;

    @Column(name = "cd_version")
    private String cdVersion;

    @Column(name = "processing_decision_cd")
    private String processingDecisionCd;

    @Column(name = "pregnant_ind_cd")
    private String pregnantIndCd;

    @Column(name = "pregnant_week")
    private Integer pregnantWeek;

    @Column(name = "processing_decision_txt")
    private String processingDecisionTxt;

    public Observation() {

    }

    // Constructors, getters, and setters
    public Observation(ObservationDto observationDto) {
        this.observationUid = observationDto.getObservationUid();
        this.activityDurationAmt = observationDto.getActivityDurationAmt();
        this.activityDurationUnitCd = observationDto.getActivityDurationUnitCd();
        this.activityFromTime = observationDto.getActivityFromTime();
        this.activityToTime = observationDto.getActivityToTime();
        this.addReasonCd = observationDto.getAddReasonCd();
        this.addTime = observationDto.getAddTime();
        this.addUserId = observationDto.getAddUserId();
//        this.addUserName = observationDto.getAddUserName();
        this.altCd = observationDto.getAltCd();
        this.altCdDescTxt = observationDto.getAltCdDescTxt();
        this.altCdSystemCd = observationDto.getAltCdSystemCd();
        this.altCdSystemDescTxt = observationDto.getAltCdSystemDescTxt();
        this.cd = observationDto.getCd();
        this.cdDerivedInd = observationDto.getCdDerivedInd();
        this.cdDescTxt = observationDto.getCdDescTxt();
        this.cdSystemCd = observationDto.getCdSystemCd();
        this.cdSystemDescTxt = observationDto.getCdSystemDescTxt();
        this.confidentialityCd = observationDto.getConfidentialityCd();
        this.confidentialityDescTxt = observationDto.getConfidentialityDescTxt();
        this.ctrlCdDisplayForm = observationDto.getCtrlCdDisplayForm();
        this.ctrlCdUserDefined1 = observationDto.getCtrlCdUserDefined1();
        this.ctrlCdUserDefined2 = observationDto.getCtrlCdUserDefined2();
        this.ctrlCdUserDefined3 = observationDto.getCtrlCdUserDefined3();
        this.ctrlCdUserDefined4 = observationDto.getCtrlCdUserDefined4();
        this.derivationExp = observationDto.getDerivationExp();
        this.effectiveDurationAmt = observationDto.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = observationDto.getEffectiveDurationUnitCd();
        this.effectiveFromTime = observationDto.getEffectiveFromTime();
        this.effectiveToTime = observationDto.getEffectiveToTime();
        this.electronicInd = observationDto.getElectronicInd();
        this.groupLevelCd = observationDto.getGroupLevelCd();
        this.jurisdictionCd = observationDto.getJurisdictionCd();
        this.labConditionCd = observationDto.getLabConditionCd();
        this.lastChgReasonCd = observationDto.getLastChgReasonCd();
        this.lastChgTime = observationDto.getLastChgTime();
        this.lastChgUserId = observationDto.getLastChgUserId();
//        this.lastChgUserName = observationDto.getLastChgUserName();
        this.localId = observationDto.getLocalId();
        this.methodCd = observationDto.getMethodCd();
        this.methodDescTxt = observationDto.getMethodDescTxt();
        this.obsDomainCd = observationDto.getObsDomainCd();
        this.obsDomainCdSt1 = observationDto.getObsDomainCdSt1();
        this.pnuCd = observationDto.getPnuCd();
        this.priorityCd = observationDto.getPriorityCd();
        this.priorityDescTxt = observationDto.getPriorityDescTxt();
        this.progAreaCd = observationDto.getProgAreaCd();
        this.recordStatusCd = observationDto.getRecordStatusCd();
        this.recordStatusTime = observationDto.getRecordStatusTime();
        this.repeatNbr = observationDto.getRepeatNbr();
        this.statusCd = observationDto.getStatusCd();
        this.statusTime = observationDto.getStatusTime();
        this.subjectPersonUid = observationDto.getSubjectPersonUid();
        this.targetSiteCd = observationDto.getTargetSiteCd();
        this.targetSiteDescTxt = observationDto.getTargetSiteDescTxt();
        this.txt = observationDto.getTxt();
        this.userAffiliationTxt = observationDto.getUserAffiliationTxt();
        this.valueCd = observationDto.getValueCd();
        this.ynuCd = observationDto.getYnuCd();
        this.programJurisdictionOid = observationDto.getProgramJurisdictionOid();
        this.sharedInd = observationDto.getSharedInd();
        this.versionCtrlNbr = observationDto.getVersionCtrlNbr();
        this.altCd = observationDto.getAltCd();
        this.altCdDescTxt = observationDto.getAltCdDescTxt();
        this.altCdSystemCd = observationDto.getAltCdSystemCd();
        this.altCdSystemDescTxt = observationDto.getAltCdSystemDescTxt();
        this.cdDerivedInd = observationDto.getCdDerivedInd();
        this.rptToStateTime = observationDto.getRptToStateTime();
        this.cdVersion = observationDto.getCdVersion();
        this.processingDecisionCd = observationDto.getProcessingDecisionCd();
        this.pregnantIndCd = observationDto.getPregnantIndCd();
        this.pregnantWeek = observationDto.getPregnantWeek();
        this.processingDecisionTxt = observationDto.getProcessingDecisionTxt();
    }

// Other constructors, getters, and setters

}
