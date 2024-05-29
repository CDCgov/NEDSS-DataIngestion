package gov.cdc.dataprocessing.repository.nbs.odse.model.phc;

import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "case_management")
public class CaseManagement {

    @Id
    @Column(name = "case_management_uid")
    private Long caseManagementUid;

    @Column(name = "public_health_case_uid")
    private Long publicHealthCaseUid;

    @Column(name = "status_900")
    private String status900;

    @Column(name = "ehars_id")
    private String eharsId;

    @Column(name = "epi_link_id")
    private String epiLinkId;

    @Column(name = "field_foll_up_ooj_outcome")
    private String fieldFollUpOojOutcome;

    @Column(name = "field_record_number")
    private String fieldRecordNumber;

    @Column(name = "fld_foll_up_dispo")
    private String fldFollUpDispo;

    @Column(name = "fld_foll_up_dispo_date")
    private Timestamp fldFollUpDispoDate;

    @Column(name = "fld_foll_up_exam_date")
    private Timestamp fldFollUpExamDate;

    @Column(name = "fld_foll_up_expected_date")
    private Timestamp fldFollUpExpectedDate;

    @Column(name = "fld_foll_up_expected_in")
    private String fldFollUpExpectedIn;

    @Column(name = "fld_foll_up_internet_outcome")
    private String fldFollUpInternetOutcome;

    @Column(name = "fld_foll_up_notification_plan")
    private String fldFollUpNotificationPlan;

    @Column(name = "fld_foll_up_prov_diagnosis")
    private String fldFollUpProvDiagnosis;

    @Column(name = "fld_foll_up_prov_exm_reason")
    private String fldFollUpProvExmReason;

    @Column(name = "init_foll_up")
    private String initFollUp;

    @Column(name = "init_foll_up_clinic_code")
    private String initFollUpClinicCode;

    @Column(name = "init_foll_up_closed_date")
    private Timestamp initFollUpClosedDate;

    @Column(name = "init_foll_up_notifiable")
    private String initFollUpNotifiable;

    @Column(name = "internet_foll_up")
    private String internetFollUp;

    @Column(name = "ooj_agency")
    private String oojAgency;

    @Column(name = "ooj_due_date")
    private Timestamp oojDueDate;

    @Column(name = "ooj_number")
    private String oojNumber;

    @Column(name = "pat_intv_status_cd")
    private String patIntvStatusCd;

    @Column(name = "subj_complexion")
    private String subjComplexion;

    @Column(name = "subj_hair")
    private String subjHair;

    @Column(name = "subj_height")
    private String subjHeight;

    @Column(name = "subj_oth_idntfyng_info")
    private String subjOthIdntfyngInfo;

    @Column(name = "subj_size_build")
    private String subjSizeBuild;

    @Column(name = "surv_closed_date")
    private Timestamp survClosedDate;

    @Column(name = "surv_patient_foll_up")
    private String survPatientFollUp;

    @Column(name = "surv_prov_diagnosis")
    private String survProvDiagnosis;

    @Column(name = "surv_prov_exm_reason")
    private String survProvExmReason;

    @Column(name = "surv_provider_contact")
    private String survProviderContact;

    @Column(name = "act_ref_type_cd")
    private String actRefTypeCd;

    @Column(name = "initiating_agncy")
    private String initiatingAgncy;

    @Column(name = "ooj_initg_agncy_outc_due_date")
    private Timestamp oojInitgAgncyOutcDueDate;

    @Column(name = "ooj_initg_agncy_outc_snt_date")
    private Timestamp oojInitgAgncyOutcSntDate;

    @Column(name = "ooj_initg_agncy_recd_date")
    private Timestamp oojInitgAgncyRecdDate;

//    @Column(name = "is_case_management_dt_populated")
//    private boolean isCaseManagementDTPopulated;

    @Column(name = "case_review_status")
    private String caseReviewStatus;

    @Column(name = "surv_assigned_date")
    private Timestamp survAssignedDate;

    @Column(name = "foll_up_assigned_date")
    private Timestamp follUpAssignedDate;

    @Column(name = "init_foll_up_assigned_date")
    private Timestamp initFollUpAssignedDate;

    @Column(name = "interview_assigned_date")
    private Timestamp interviewAssignedDate;

    @Column(name = "init_interview_assigned_date")
    private Timestamp initInterviewAssignedDate;

    @Column(name = "case_closed_date")
    private Timestamp caseClosedDate;

    @Column(name = "case_review_status_date")
    private Timestamp caseReviewStatusDate;

//    @Column(name = "local_id")
//    private String localId;

    // Constructors, getters, and setters
    public CaseManagement() {

    }

    public CaseManagement(CaseManagementDto caseManagementDto) {
        this.caseManagementUid = caseManagementDto.getCaseManagementUid();
        this.publicHealthCaseUid = caseManagementDto.getPublicHealthCaseUid();
        this.status900 = caseManagementDto.getStatus900();
        this.eharsId = caseManagementDto.getEharsId();
        this.epiLinkId = caseManagementDto.getEpiLinkId();
        this.fieldFollUpOojOutcome = caseManagementDto.getFieldFollUpOojOutcome();
        this.fieldRecordNumber = caseManagementDto.getFieldRecordNumber();
        this.fldFollUpDispo = caseManagementDto.getFldFollUpDispo();
        this.fldFollUpDispoDate = caseManagementDto.getFldFollUpDispoDate();
        this.fldFollUpExamDate = caseManagementDto.getFldFollUpExamDate();
        this.fldFollUpExpectedDate = caseManagementDto.getFldFollUpExpectedDate();
        this.fldFollUpExpectedIn = caseManagementDto.getFldFollUpExpectedIn();
        this.fldFollUpInternetOutcome = caseManagementDto.getFldFollUpInternetOutcome();
        this.fldFollUpNotificationPlan = caseManagementDto.getFldFollUpNotificationPlan();
        this.fldFollUpProvDiagnosis = caseManagementDto.getFldFollUpProvDiagnosis();
        this.fldFollUpProvExmReason = caseManagementDto.getFldFollUpProvExmReason();
        this.initFollUp = caseManagementDto.getInitFollUp();
        this.initFollUpClinicCode = caseManagementDto.getInitFollUpClinicCode();
        this.initFollUpClosedDate = caseManagementDto.getInitFollUpClosedDate();
        this.initFollUpNotifiable = caseManagementDto.getInitFollUpNotifiable();
        this.internetFollUp = caseManagementDto.getInternetFollUp();
        this.oojAgency = caseManagementDto.getOojAgency();
        this.oojDueDate = caseManagementDto.getOojDueDate();
        this.oojNumber = caseManagementDto.getOojNumber();
        this.patIntvStatusCd = caseManagementDto.getPatIntvStatusCd();
        this.subjComplexion = caseManagementDto.getSubjComplexion();
        this.subjHair = caseManagementDto.getSubjHair();
        this.subjHeight = caseManagementDto.getSubjHeight();
        this.subjOthIdntfyngInfo = caseManagementDto.getSubjOthIdntfyngInfo();
        this.subjSizeBuild = caseManagementDto.getSubjSizeBuild();
        this.survClosedDate = caseManagementDto.getSurvClosedDate();
        this.survPatientFollUp = caseManagementDto.getSurvPatientFollUp();
        this.survProvDiagnosis = caseManagementDto.getSurvProvDiagnosis();
        this.survProvExmReason = caseManagementDto.getSurvProvExmReason();
        this.survProviderContact = caseManagementDto.getSurvProviderContact();
        this.actRefTypeCd = caseManagementDto.getActRefTypeCd();
        this.initiatingAgncy = caseManagementDto.getInitiatingAgncy();
        this.oojInitgAgncyOutcDueDate = caseManagementDto.getOojInitgAgncyOutcDueDate();
        this.oojInitgAgncyOutcSntDate = caseManagementDto.getOojInitgAgncyOutcSntDate();
        this.oojInitgAgncyRecdDate = caseManagementDto.getOojInitgAgncyRecdDate();
//        this.isCaseManagementDTPopulated = caseManagementDto.isCaseManagementDTPopulated();
        this.caseReviewStatus = caseManagementDto.getCaseReviewStatus();
        this.survAssignedDate = caseManagementDto.getSurvAssignedDate();
        this.follUpAssignedDate = caseManagementDto.getFollUpAssignedDate();
        this.initFollUpAssignedDate = caseManagementDto.getInitFollUpAssignedDate();
        this.interviewAssignedDate = caseManagementDto.getInterviewAssignedDate();
        this.initInterviewAssignedDate = caseManagementDto.getInitInterviewAssignedDate();
        this.caseClosedDate = caseManagementDto.getCaseClosedDate();
        this.caseReviewStatusDate = caseManagementDto.getCaseReviewStatusDate();
//        this.localId = caseManagementDto.getLocalId();
    }

}
