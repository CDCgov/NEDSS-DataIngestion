package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.CaseManagement;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class CaseManagementDto extends BaseContainer {
    private static final long serialVersionUID = -5127476121435352079L;

    private Long              caseManagementUid;
    private Long              publicHealthCaseUid;
    private String            status900;
    private String            eharsId;
    private String            epiLinkId;
    private String            fieldFollUpOojOutcome;
    private String            fieldRecordNumber;
    private String            fldFollUpDispo;
    private Timestamp         fldFollUpDispoDate;
    private Timestamp         fldFollUpExamDate;
    private Timestamp         fldFollUpExpectedDate;
    private String            fldFollUpExpectedIn;
    private String            fldFollUpInternetOutcome;
    private String            fldFollUpNotificationPlan;
    private String            fldFollUpProvDiagnosis;
    private String            fldFollUpProvExmReason;
    private String            initFollUp;
    private String            initFollUpClinicCode;
    private Timestamp         initFollUpClosedDate;
    private String            initFollUpNotifiable;
    private String            internetFollUp;
    private String            oojAgency;
    private Timestamp         oojDueDate;
    private String            oojNumber;
    private String            patIntvStatusCd;
    private String            subjComplexion;
    private String            subjHair;
    private String           subjHeight;
    private String            subjOthIdntfyngInfo;
    private String            subjSizeBuild;
    private Timestamp         survClosedDate;
    private String            survPatientFollUp;
    private String            survProvDiagnosis;
    private String            survProvExmReason;
    private String            survProviderContact;
    private String            actRefTypeCd;
    private String            initiatingAgncy;
    private Timestamp         oojInitgAgncyOutcDueDate;
    private Timestamp         oojInitgAgncyOutcSntDate;
    private Timestamp         oojInitgAgncyRecdDate;
    public boolean            isCaseManagementDTPopulated;
    public String             caseReviewStatus;
    private Timestamp         survAssignedDate;
    private Timestamp         follUpAssignedDate;
    private Timestamp         initFollUpAssignedDate;
    private Timestamp         interviewAssignedDate;
    private Timestamp initInterviewAssignedDate;
    private Timestamp         caseClosedDate;
    private Timestamp         caseReviewStatusDate;

    // not in db
    private String            localId;


    public CaseManagementDto() {

    }

    public CaseManagementDto(CaseManagement caseManagement) {
        this.caseManagementUid = caseManagement.getCaseManagementUid();
        this.publicHealthCaseUid = caseManagement.getPublicHealthCaseUid();
        this.status900 = caseManagement.getStatus900();
        this.eharsId = caseManagement.getEharsId();
        this.epiLinkId = caseManagement.getEpiLinkId();
        this.fieldFollUpOojOutcome = caseManagement.getFieldFollUpOojOutcome();
        this.fieldRecordNumber = caseManagement.getFieldRecordNumber();
        this.fldFollUpDispo = caseManagement.getFldFollUpDispo();
        this.fldFollUpDispoDate = caseManagement.getFldFollUpDispoDate();
        this.fldFollUpExamDate = caseManagement.getFldFollUpExamDate();
        this.fldFollUpExpectedDate = caseManagement.getFldFollUpExpectedDate();
        this.fldFollUpExpectedIn = caseManagement.getFldFollUpExpectedIn();
        this.fldFollUpInternetOutcome = caseManagement.getFldFollUpInternetOutcome();
        this.fldFollUpNotificationPlan = caseManagement.getFldFollUpNotificationPlan();
        this.fldFollUpProvDiagnosis = caseManagement.getFldFollUpProvDiagnosis();
        this.fldFollUpProvExmReason = caseManagement.getFldFollUpProvExmReason();
        this.initFollUp = caseManagement.getInitFollUp();
        this.initFollUpClinicCode = caseManagement.getInitFollUpClinicCode();
        this.initFollUpClosedDate = caseManagement.getInitFollUpClosedDate();
        this.initFollUpNotifiable = caseManagement.getInitFollUpNotifiable();
        this.internetFollUp = caseManagement.getInternetFollUp();
        this.oojAgency = caseManagement.getOojAgency();
        this.oojDueDate = caseManagement.getOojDueDate();
        this.oojNumber = caseManagement.getOojNumber();
        this.patIntvStatusCd = caseManagement.getPatIntvStatusCd();
        this.subjComplexion = caseManagement.getSubjComplexion();
        this.subjHair = caseManagement.getSubjHair();
        this.subjHeight = caseManagement.getSubjHeight();
        this.subjOthIdntfyngInfo = caseManagement.getSubjOthIdntfyngInfo();
        this.subjSizeBuild = caseManagement.getSubjSizeBuild();
        this.survClosedDate = caseManagement.getSurvClosedDate();
        this.survPatientFollUp = caseManagement.getSurvPatientFollUp();
        this.survProvDiagnosis = caseManagement.getSurvProvDiagnosis();
        this.survProvExmReason = caseManagement.getSurvProvExmReason();
        this.survProviderContact = caseManagement.getSurvProviderContact();
        this.actRefTypeCd = caseManagement.getActRefTypeCd();
        this.initiatingAgncy = caseManagement.getInitiatingAgncy();
        this.oojInitgAgncyOutcDueDate = caseManagement.getOojInitgAgncyOutcDueDate();
        this.oojInitgAgncyOutcSntDate = caseManagement.getOojInitgAgncyOutcSntDate();
        this.oojInitgAgncyRecdDate = caseManagement.getOojInitgAgncyRecdDate();
//        this.isCaseManagementDTPopulated = caseManagement.isCaseManagementDTPopulated();
        this.caseReviewStatus = caseManagement.getCaseReviewStatus();
        this.survAssignedDate = caseManagement.getSurvAssignedDate();
        this.follUpAssignedDate = caseManagement.getFollUpAssignedDate();
        this.initFollUpAssignedDate = caseManagement.getInitFollUpAssignedDate();
        this.interviewAssignedDate = caseManagement.getInterviewAssignedDate();
        this.initInterviewAssignedDate = caseManagement.getInitInterviewAssignedDate();
        this.caseClosedDate = caseManagement.getCaseClosedDate();
        this.caseReviewStatusDate = caseManagement.getCaseReviewStatusDate();
    }

}
