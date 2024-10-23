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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class Observation implements Serializable {
    protected static final long serialVersionUID = 1L;

    @Id
    @Column(name = "observation_uid")
    protected Long observationUid;

    @Column(name = "activity_duration_amt")
    protected String activityDurationAmt;

    @Column(name = "activity_duration_unit_cd")
    protected String activityDurationUnitCd;

    @Column(name = "activity_from_time")
    protected Timestamp activityFromTime;

    @Column(name = "activity_to_time")
    protected Timestamp activityToTime;

    @Column(name = "add_reason_cd")
    protected String addReasonCd;

    @Column(name = "add_time")
    protected Timestamp addTime;

    @Column(name = "add_user_id")
    protected Long addUserId;

    @Column(name = "cd")
    protected String cd;

    @Column(name = "cd_desc_txt")
    protected String cdDescTxt;

    @Column(name = "cd_system_cd")
    protected String cdSystemCd;

    @Column(name = "cd_system_desc_txt")
    protected String cdSystemDescTxt;

    @Column(name = "confidentiality_cd")
    protected String confidentialityCd;

    @Column(name = "confidentiality_desc_txt")
    protected String confidentialityDescTxt;

    @Column(name = "ctrl_cd_display_form")
    protected String ctrlCdDisplayForm;

    @Column(name = "ctrl_cd_user_defined_1")
    protected String ctrlCdUserDefined1;

    @Column(name = "ctrl_cd_user_defined_2")
    protected String ctrlCdUserDefined2;

    @Column(name = "ctrl_cd_user_defined_3")
    protected String ctrlCdUserDefined3;

    @Column(name = "ctrl_cd_user_defined_4")
    protected String ctrlCdUserDefined4;

    @Column(name = "derivation_exp")
    protected Integer derivationExp;

    @Column(name = "effective_duration_amt")
    protected String effectiveDurationAmt;

    @Column(name = "effective_duration_unit_cd")
    protected String effectiveDurationUnitCd;

    @Column(name = "effective_from_time")
    protected Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    protected Timestamp effectiveToTime;

    @Column(name = "electronic_ind")
    protected String electronicInd;

    @Column(name = "group_level_cd")
    protected String groupLevelCd;

    @Column(name = "jurisdiction_cd")
    protected String jurisdictionCd;

    @Column(name = "lab_condition_cd")
    protected String labConditionCd;

    @Column(name = "last_chg_reason_cd")
    protected String lastChgReasonCd;

    @Column(name = "last_chg_time")
    protected Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    protected Long lastChgUserId;

    @Column(name = "local_id")
    protected String localId;

    @Column(name = "method_cd")
    protected String methodCd;

    @Column(name = "method_desc_txt")
    protected String methodDescTxt;

    @Column(name = "obs_domain_cd")
    protected String obsDomainCd;

    @Column(name = "obs_domain_cd_st_1")
    protected String obsDomainCdSt1;

    @Column(name = "pnu_cd")
    protected String pnuCd;

    @Column(name = "priority_cd")
    protected String priorityCd;

    @Column(name = "priority_desc_txt")
    protected String priorityDescTxt;

    @Column(name = "prog_area_cd")
    protected String progAreaCd;

    @Column(name = "record_status_cd")
    protected String recordStatusCd;

    @Column(name = "record_status_time")
    protected Timestamp recordStatusTime;

    @Column(name = "repeat_nbr")
    protected Integer repeatNbr;

    @Column(name = "status_cd")
    protected String statusCd;

    @Column(name = "status_time")
    protected Timestamp statusTime;

    @Column(name = "subject_person_uid")
    protected Long subjectPersonUid;

    @Column(name = "target_site_cd")
    protected String targetSiteCd;

    @Column(name = "target_site_desc_txt")
    protected String targetSiteDescTxt;

    @Column(name = "txt")
    protected String txt;

    @Column(name = "user_affiliation_txt")
    protected String userAffiliationTxt;

    @Column(name = "value_cd")
    protected String valueCd;

    @Column(name = "ynu_cd")
    protected String ynuCd;

    @Column(name = "program_jurisdiction_oid")
    protected Long programJurisdictionOid;

    @Column(name = "shared_ind", nullable = false)
    protected String sharedInd;

    @Column(name = "version_ctrl_nbr", nullable = false)
    protected Integer versionCtrlNbr;

    @Column(name = "alt_cd")
    protected String altCd;

    @Column(name = "alt_cd_desc_txt")
    protected String altCdDescTxt;

    @Column(name = "alt_cd_system_cd")
    protected String altCdSystemCd;

    @Column(name = "alt_cd_system_desc_txt")
    protected String altCdSystemDescTxt;

    @Column(name = "cd_derived_ind")
    protected String cdDerivedInd;

    @Column(name = "rpt_to_state_time")
    protected Timestamp rptToStateTime;

    @Column(name = "cd_version")
    protected String cdVersion;

    @Column(name = "processing_decision_cd")
    protected String processingDecisionCd;

    @Column(name = "pregnant_ind_cd")
    protected String pregnantIndCd;

    @Column(name = "pregnant_week")
    protected Integer pregnantWeek;

    @Column(name = "processing_decision_txt")
    protected String processingDecisionTxt;


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
