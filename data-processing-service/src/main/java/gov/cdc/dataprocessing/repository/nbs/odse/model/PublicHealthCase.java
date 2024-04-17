package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "Public_health_case")
public class PublicHealthCase {

    @Id
    @Column(name = "public_health_case_uid")
    private Long publicHealthCaseUid;

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

    @Column(name = "case_class_cd")
    private String caseClassCd;

    @Column(name = "case_type_cd")
    private String caseTypeCd;

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

    @Column(name = "detection_method_cd")
    private String detectionMethodCd;

    @Column(name = "detection_method_desc_txt")
    private String detectionMethodDescTxt;

    @Column(name = "diagnosis_time")
    private Timestamp diagnosisTime;

    @Column(name = "disease_imported_cd")
    private String diseaseImportedCd;

    @Column(name = "disease_imported_desc_txt")
    private String diseaseImportedDescTxt;

    @Column(name = "effective_duration_amt")
    private String effectiveDurationAmt;

    @Column(name = "effective_duration_unit_cd")
    private String effectiveDurationUnitCd;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "group_case_cnt")
    private Integer groupCaseCnt;

    @Column(name = "investigation_status_cd")
    private String investigationStatusCd;

    @Column(name = "jurisdiction_cd")
    private String jurisdictionCd;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "mmwr_week")
    private String mmwrWeek;

    @Column(name = "mmwr_year")
    private String mmwrYear;

    @Column(name = "outbreak_ind")
    private String outbreakInd;

    @Column(name = "outbreak_from_time")
    private Timestamp outbreakFromTime;

    @Column(name = "outbreak_to_time")
    private Timestamp outbreakToTime;

    @Column(name = "outbreak_name")
    private String outbreakName;

    @Column(name = "outcome_cd")
    private String outcomeCd;

    @Column(name = "pat_age_at_onset")
    private String patAgeAtOnset;

    @Column(name = "pat_age_at_onset_unit_cd")
    private String patAgeAtOnsetUnitCd;

    @Column(name = "patient_group_id")
    private Long patientGroupId;

    @Column(name = "prog_area_cd")
    private String progAreaCd;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "repeat_nbr")
    private Integer repeatNbr;

    @Column(name = "rpt_cnty_cd")
    private String rptCntyCd;

    @Column(name = "rpt_form_cmplt_time")
    private Timestamp rptFormCmpltTime;

    @Column(name = "rpt_source_cd")
    private String rptSourceCd;

    @Column(name = "rpt_source_cd_desc_txt")
    private String rptSourceCdDescTxt;

    @Column(name = "rpt_to_county_time")
    private Timestamp rptToCountyTime;

    @Column(name = "rpt_to_state_time")
    private Timestamp rptToStateTime;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "transmission_mode_cd")
    private String transmissionModeCd;

    @Column(name = "transmission_mode_desc_txt")
    private String transmissionModeDescTxt;

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

    @Column(name = "investigator_assigned_time")
    private Timestamp investigatorAssignedTime;

    @Column(name = "hospitalized_ind_cd")
    private String hospitalizedIndCd;

    @Column(name = "hospitalized_admin_time")
    private Timestamp hospitalizedAdminTime;

    @Column(name = "hospitalized_discharge_time")
    private Timestamp hospitalizedDischargeTime;

    @Column(name = "hospitalized_duration_amt")
    private BigDecimal hospitalizedDurationAmt;

    @Column(name = "pregnant_ind_cd")
    private String pregnantIndCd;

    @Column(name = "day_care_ind_cd")
    private String dayCareIndCd;

    @Column(name = "food_handler_ind_cd")
    private String foodHandlerIndCd;

    @Column(name = "imported_country_cd")
    private String importedCountryCd;

    @Column(name = "imported_state_cd")
    private String importedStateCd;

    @Column(name = "imported_city_desc_txt")
    private String importedCityDescTxt;

    @Column(name = "imported_county_cd")
    private String importedCountyCd;

    @Column(name = "deceased_time")
    private Timestamp deceasedTime;

    @Column(name = "count_interval_cd")
    private String countIntervalCd;

    @Column(name = "priority_cd")
    private String priorityCd;

    @Column(name = "contact_inv_txt")
    private String contactInvTxt;

    @Column(name = "infectious_from_date")
    private Timestamp infectiousFromDate;

    @Column(name = "infectious_to_date")
    private Timestamp infectiousToDate;

    @Column(name = "contact_inv_status_cd")
    private String contactInvStatusCd;

    @Column(name = "referral_basis_cd")
    private String referralBasisCd;

    @Column(name = "curr_process_state_cd")
    private String currProcessStateCd;

    @Column(name = "inv_priority_cd")
    private String invPriorityCd;

    @Column(name = "coinfection_id")
    private String coinfectionId;

    // Getters and setters for all fields

    // Constructors (if needed)

    public PublicHealthCase() {

    }


    public PublicHealthCase(PublicHealthCaseDT publicHealthCaseDT) {
        // Copy values from PublicHealthCaseDT to PublicHealthCase
        this.publicHealthCaseUid = publicHealthCaseDT.getPublicHealthCaseUid();
        this.activityDurationAmt = publicHealthCaseDT.getActivityDurationAmt();
        this.activityDurationUnitCd = publicHealthCaseDT.getActivityDurationUnitCd();
        this.activityFromTime = publicHealthCaseDT.getActivityFromTime();
        this.activityToTime = publicHealthCaseDT.getActivityToTime();
        this.addReasonCd = publicHealthCaseDT.getAddReasonCd();
        this.addTime = publicHealthCaseDT.getAddTime();
        this.addUserId = publicHealthCaseDT.getAddUserId();
        this.caseClassCd = publicHealthCaseDT.getCaseClassCd();
        this.caseTypeCd = publicHealthCaseDT.getCaseTypeCd();
        this.cd = publicHealthCaseDT.getCd();
        this.cdDescTxt = publicHealthCaseDT.getCdDescTxt();
        this.cdSystemCd = publicHealthCaseDT.getCdSystemCd();
        this.cdSystemDescTxt = publicHealthCaseDT.getCdSystemDescTxt();
        this.confidentialityCd = publicHealthCaseDT.getConfidentialityCd();
        this.confidentialityDescTxt = publicHealthCaseDT.getConfidentialityDescTxt();
        this.detectionMethodCd = publicHealthCaseDT.getDetectionMethodCd();
        this.detectionMethodDescTxt = publicHealthCaseDT.getDetectionMethodDescTxt();
        this.diagnosisTime = publicHealthCaseDT.getDiagnosisTime();
        this.diseaseImportedCd = publicHealthCaseDT.getDiseaseImportedCd();
        this.diseaseImportedDescTxt = publicHealthCaseDT.getDiseaseImportedDescTxt();
        this.effectiveDurationAmt = publicHealthCaseDT.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = publicHealthCaseDT.getEffectiveDurationUnitCd();
        this.effectiveFromTime = publicHealthCaseDT.getEffectiveFromTime();
        this.effectiveToTime = publicHealthCaseDT.getEffectiveToTime();
        this.groupCaseCnt = publicHealthCaseDT.getGroupCaseCnt();
        this.investigationStatusCd = publicHealthCaseDT.getInvestigationStatusCd();
        this.investigatorAssignedTime = publicHealthCaseDT.getInvestigatorAssignedTime();
        this.jurisdictionCd = publicHealthCaseDT.getJurisdictionCd();
        this.lastChgReasonCd = publicHealthCaseDT.getLastChgReasonCd();
        this.lastChgTime = publicHealthCaseDT.getLastChgTime();
        this.lastChgUserId = publicHealthCaseDT.getLastChgUserId();
        this.localId = publicHealthCaseDT.getLocalId();
        this.mmwrWeek = publicHealthCaseDT.getMmwrWeek();
        this.mmwrYear = publicHealthCaseDT.getMmwrYear();
        this.outbreakInd = publicHealthCaseDT.getOutbreakInd();
        this.outbreakFromTime = publicHealthCaseDT.getOutbreakFromTime();
        this.outbreakToTime = publicHealthCaseDT.getOutbreakToTime();
        this.outbreakName = publicHealthCaseDT.getOutbreakName();
        this.outcomeCd = publicHealthCaseDT.getOutcomeCd();
        this.patAgeAtOnset = publicHealthCaseDT.getPatAgeAtOnset();
        this.patAgeAtOnsetUnitCd = publicHealthCaseDT.getPatAgeAtOnsetUnitCd();
        this.patientGroupId = publicHealthCaseDT.getPatientGroupId();
        this.progAreaCd = publicHealthCaseDT.getProgAreaCd();
        this.recordStatusCd = publicHealthCaseDT.getRecordStatusCd();
        this.recordStatusTime = publicHealthCaseDT.getRecordStatusTime();
        this.repeatNbr = publicHealthCaseDT.getRepeatNbr();
        this.rptCntyCd = publicHealthCaseDT.getRptCntyCd();
        this.rptFormCmpltTime = publicHealthCaseDT.getRptFormCmpltTime();
        this.rptSourceCd = publicHealthCaseDT.getRptSourceCd();
        this.rptSourceCdDescTxt = publicHealthCaseDT.getRptSourceCdDescTxt();
        this.rptToCountyTime = publicHealthCaseDT.getRptToCountyTime();
        this.rptToStateTime = publicHealthCaseDT.getRptToStateTime();
        this.statusCd = publicHealthCaseDT.getStatusCd();
        this.statusTime = publicHealthCaseDT.getStatusTime();
        this.transmissionModeCd = publicHealthCaseDT.getTransmissionModeCd();
        this.transmissionModeDescTxt = publicHealthCaseDT.getTransmissionModeDescTxt();
        this.txt = publicHealthCaseDT.getTxt();
        this.userAffiliationTxt = publicHealthCaseDT.getUserAffiliationTxt();
        this.programJurisdictionOid = publicHealthCaseDT.getProgramJurisdictionOid();
        this.sharedInd = publicHealthCaseDT.getSharedInd();
        this.versionCtrlNbr = publicHealthCaseDT.getVersionCtrlNbr();
        this.hospitalizedIndCd = publicHealthCaseDT.getHospitalizedIndCd();
        this.hospitalizedAdminTime = publicHealthCaseDT.getHospitalizedAdminTime();
        this.hospitalizedDischargeTime = publicHealthCaseDT.getHospitalizedDischargeTime();
        this.hospitalizedDurationAmt = publicHealthCaseDT.getHospitalizedDurationAmt();
        this.pregnantIndCd = publicHealthCaseDT.getPregnantIndCd();
        this.dayCareIndCd = publicHealthCaseDT.getDayCareIndCd();
        this.foodHandlerIndCd = publicHealthCaseDT.getFoodHandlerIndCd();
        this.importedCountryCd = publicHealthCaseDT.getImportedCountryCd();
        this.importedStateCd = publicHealthCaseDT.getImportedStateCd();
        this.importedCityDescTxt = publicHealthCaseDT.getImportedCityDescTxt();
        this.importedCountyCd = publicHealthCaseDT.getImportedCountyCd();
        this.deceasedTime = publicHealthCaseDT.getDeceasedTime();
//        this.rptSentTime = publicHealthCaseDT.getRptSentTime();
        this.countIntervalCd = publicHealthCaseDT.getCountIntervalCd();
//        this.isSummaryCase = publicHealthCaseDT.isSummaryCase();
        this.priorityCd = publicHealthCaseDT.getPriorityCd();
        this.infectiousFromDate = publicHealthCaseDT.getInfectiousFromDate();
        this.infectiousToDate = publicHealthCaseDT.getInfectiousToDate();
//        this.contactInvStatus = publicHealthCaseDT.getContactInvStatus();
        this.contactInvTxt = publicHealthCaseDT.getContactInvTxt();
        this.referralBasisCd = publicHealthCaseDT.getReferralBasisCd();
        this.currProcessStateCd = publicHealthCaseDT.getCurrProcessStateCd();
        this.invPriorityCd = publicHealthCaseDT.getInvPriorityCd();
        this.coinfectionId = publicHealthCaseDT.getCoinfectionId();
//        this.associatedSpecimenCollDate = publicHealthCaseDT.getAssociatedSpecimenCollDate();
//        this.confirmationMethodCd = publicHealthCaseDT.getConfirmationMethodCd();
//        this.confirmationMethodTime = publicHealthCaseDT.getConfirmationMethodTime();
//        this.addUserName = publicHealthCaseDT.getAddUserName();
//        this.lastChgUserName = publicHealthCaseDT.getLastChgUserName();
//        this.currentInvestigatorUid = publicHealthCaseDT.getCurrentInvestigatorUid();
//        this.currentPatientUid = publicHealthCaseDT.getCurrentPatientUid();
    }

}
