package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CaseManagementDT extends AbstractVO {
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

}
