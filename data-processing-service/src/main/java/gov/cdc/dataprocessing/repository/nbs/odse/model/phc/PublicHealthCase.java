package gov.cdc.dataprocessing.repository.nbs.odse.model.phc;

import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
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


    public PublicHealthCase(PublicHealthCaseDto publicHealthCaseDto) {
        // Copy values from PublicHealthCaseDto to PublicHealthCase

        this.publicHealthCaseUid = publicHealthCaseDto.getPublicHealthCaseUid();
        this.activityDurationAmt = publicHealthCaseDto.getActivityDurationAmt();
        this.activityDurationUnitCd = publicHealthCaseDto.getActivityDurationUnitCd();
        this.activityFromTime = publicHealthCaseDto.getActivityFromTime();
        this.activityToTime = publicHealthCaseDto.getActivityToTime();
        this.addReasonCd = publicHealthCaseDto.getAddReasonCd();
        this.addTime = publicHealthCaseDto.getAddTime();
        this.addUserId = publicHealthCaseDto.getAddUserId();
        this.caseClassCd = publicHealthCaseDto.getCaseClassCd();
        this.caseTypeCd = publicHealthCaseDto.getCaseTypeCd();
        this.cd = publicHealthCaseDto.getCd();
        this.cdDescTxt = publicHealthCaseDto.getCdDescTxt();
        this.cdSystemCd = publicHealthCaseDto.getCdSystemCd();
        this.cdSystemDescTxt = publicHealthCaseDto.getCdSystemDescTxt();
        this.confidentialityCd = publicHealthCaseDto.getConfidentialityCd();
        this.confidentialityDescTxt = publicHealthCaseDto.getConfidentialityDescTxt();
        this.detectionMethodCd = publicHealthCaseDto.getDetectionMethodCd();
        this.detectionMethodDescTxt = publicHealthCaseDto.getDetectionMethodDescTxt();
        this.diagnosisTime = publicHealthCaseDto.getDiagnosisTime();
        this.diseaseImportedCd = publicHealthCaseDto.getDiseaseImportedCd();
        this.diseaseImportedDescTxt = publicHealthCaseDto.getDiseaseImportedDescTxt();
        this.effectiveDurationAmt = publicHealthCaseDto.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = publicHealthCaseDto.getEffectiveDurationUnitCd();
        this.effectiveFromTime = publicHealthCaseDto.getEffectiveFromTime();
        this.effectiveToTime = publicHealthCaseDto.getEffectiveToTime();
        this.groupCaseCnt = publicHealthCaseDto.getGroupCaseCnt();
        this.investigationStatusCd = publicHealthCaseDto.getInvestigationStatusCd();
        this.investigatorAssignedTime = publicHealthCaseDto.getInvestigatorAssignedTime();
        this.jurisdictionCd = publicHealthCaseDto.getJurisdictionCd();
        this.lastChgReasonCd = publicHealthCaseDto.getLastChgReasonCd();
        this.lastChgTime = publicHealthCaseDto.getLastChgTime();
        this.lastChgUserId = publicHealthCaseDto.getLastChgUserId();
        this.localId = publicHealthCaseDto.getLocalId();
        this.mmwrWeek = publicHealthCaseDto.getMmwrWeek();
        this.mmwrYear = publicHealthCaseDto.getMmwrYear();
        this.outbreakInd = publicHealthCaseDto.getOutbreakInd();
        this.outbreakFromTime = publicHealthCaseDto.getOutbreakFromTime();
        this.outbreakToTime = publicHealthCaseDto.getOutbreakToTime();
        this.outbreakName = publicHealthCaseDto.getOutbreakName();
        this.outcomeCd = publicHealthCaseDto.getOutcomeCd();
        this.patAgeAtOnset = publicHealthCaseDto.getPatAgeAtOnset();
        this.patAgeAtOnsetUnitCd = publicHealthCaseDto.getPatAgeAtOnsetUnitCd();
        this.patientGroupId = publicHealthCaseDto.getPatientGroupId();
        this.progAreaCd = publicHealthCaseDto.getProgAreaCd();
        this.recordStatusCd = publicHealthCaseDto.getRecordStatusCd();
        this.recordStatusTime = publicHealthCaseDto.getRecordStatusTime();
        this.repeatNbr = publicHealthCaseDto.getRepeatNbr();
        this.rptCntyCd = publicHealthCaseDto.getRptCntyCd();
        this.rptFormCmpltTime = publicHealthCaseDto.getRptFormCmpltTime();
        this.rptSourceCd = publicHealthCaseDto.getRptSourceCd();
        this.rptSourceCdDescTxt = publicHealthCaseDto.getRptSourceCdDescTxt();
        this.rptToCountyTime = publicHealthCaseDto.getRptToCountyTime();
        this.rptToStateTime = publicHealthCaseDto.getRptToStateTime();
        this.statusCd = publicHealthCaseDto.getStatusCd();
        this.statusTime = publicHealthCaseDto.getStatusTime();
        this.transmissionModeCd = publicHealthCaseDto.getTransmissionModeCd();
        this.transmissionModeDescTxt = publicHealthCaseDto.getTransmissionModeDescTxt();
        this.txt = publicHealthCaseDto.getTxt();
        this.userAffiliationTxt = publicHealthCaseDto.getUserAffiliationTxt();
        this.programJurisdictionOid = publicHealthCaseDto.getProgramJurisdictionOid();
        this.sharedInd = publicHealthCaseDto.getSharedInd();
        this.versionCtrlNbr = publicHealthCaseDto.getVersionCtrlNbr();
        this.hospitalizedIndCd = publicHealthCaseDto.getHospitalizedIndCd();
        this.hospitalizedAdminTime = publicHealthCaseDto.getHospitalizedAdminTime();
        this.hospitalizedDischargeTime = publicHealthCaseDto.getHospitalizedDischargeTime();
        this.hospitalizedDurationAmt = publicHealthCaseDto.getHospitalizedDurationAmt();
        this.pregnantIndCd = publicHealthCaseDto.getPregnantIndCd();
        this.dayCareIndCd = publicHealthCaseDto.getDayCareIndCd();
        this.foodHandlerIndCd = publicHealthCaseDto.getFoodHandlerIndCd();
        this.importedCountryCd = publicHealthCaseDto.getImportedCountryCd();
        this.importedStateCd = publicHealthCaseDto.getImportedStateCd();
        this.importedCityDescTxt = publicHealthCaseDto.getImportedCityDescTxt();
        this.importedCountyCd = publicHealthCaseDto.getImportedCountyCd();
        this.deceasedTime = publicHealthCaseDto.getDeceasedTime();
//        this.rptSentTime = publicHealthCaseDto.getRptSentTime();
        this.countIntervalCd = publicHealthCaseDto.getCountIntervalCd();
//        this.isSummaryCase = publicHealthCaseDto.isSummaryCase();
        this.priorityCd = publicHealthCaseDto.getPriorityCd();
        this.infectiousFromDate = publicHealthCaseDto.getInfectiousFromDate();
        this.infectiousToDate = publicHealthCaseDto.getInfectiousToDate();
//        this.contactInvStatus = publicHealthCaseDto.getContactInvStatus();
        this.contactInvTxt = publicHealthCaseDto.getContactInvTxt();
        this.referralBasisCd = publicHealthCaseDto.getReferralBasisCd();
        this.currProcessStateCd = publicHealthCaseDto.getCurrProcessStateCd();
        this.invPriorityCd = publicHealthCaseDto.getInvPriorityCd();
        this.coinfectionId = publicHealthCaseDto.getCoinfectionId();

//        this.associatedSpecimenCollDate = publicHealthCaseDto.getAssociatedSpecimenCollDate();
//        this.confirmationMethodCd = publicHealthCaseDto.getConfirmationMethodCd();
//        this.confirmationMethodTime = publicHealthCaseDto.getConfirmationMethodTime();
//        this.addUserName = publicHealthCaseDto.getAddUserName();
//        this.lastChgUserName = publicHealthCaseDto.getLastChgUserName();
//        this.currentInvestigatorUid = publicHealthCaseDto.getCurrentInvestigatorUid();
//        this.currentPatientUid = publicHealthCaseDto.getCurrentPatientUid();
    }

}
