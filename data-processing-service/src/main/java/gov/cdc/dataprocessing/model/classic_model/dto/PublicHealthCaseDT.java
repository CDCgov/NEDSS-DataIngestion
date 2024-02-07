package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
public class PublicHealthCaseDT extends AbstractVO {
    private static final long serialVersionUID = 1L;

    private boolean caseStatusDirty = false;

    private boolean isPamCase;
    private boolean isPageCase;
    private boolean isStdHivProgramAreaCode;

    private String caseTypeCd;
    private Long publicHealthCaseUid;
    private String activityDurationAmt;
    private String activityDurationUnitCd;
    private Timestamp activityFromTime;
    private Timestamp activityToTime;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String caseClassCd;
    private String cd;
    private String cdDescTxt;
    private String cdSystemCd;
    private String cdSystemDescTxt;
    private String confidentialityCd;
    private String confidentialityDescTxt;
    private String detectionMethodCd;
    private String detectionMethodDescTxt;
    private Timestamp diagnosisTime;
    private String diseaseImportedCd;
    private String diseaseImportedDescTxt;
    private String effectiveDurationAmt;
    private String effectiveDurationUnitCd;
    private Timestamp effectiveFromTime;
    private Timestamp effectiveToTime;
    private Integer groupCaseCnt;
    private String investigationStatusCd;
    private Timestamp investigatorAssignedTime;
    private String jurisdictionCd;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
    private String mmwrWeek;
    private String mmwrYear;
    private String outbreakInd;
    private Timestamp outbreakFromTime;
    private Timestamp outbreakToTime;
    private String outbreakName;
    private String outcomeCd;
    private String patAgeAtOnset;
    private String patAgeAtOnsetUnitCd;
    private Long patientGroupId;
    private String progAreaCd;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Integer repeatNbr;
    private String rptCntyCd;
    private Timestamp rptFormCmpltTime;
    private String rptSourceCd;
    private String rptSourceCdDescTxt;
    private Timestamp rptToCountyTime;
    private Timestamp rptToStateTime;
    private String statusCd;
    private Timestamp statusTime;
    private String transmissionModeCd;
    private String transmissionModeDescTxt;
    private String txt;
    private String userAffiliationTxt;
    private Long programJurisdictionOid;
    private String sharedInd;
    private Integer versionCtrlNbr;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;
    private String addUserName;
    private String lastChgUserName;
    private Long currentInvestigatorUid;
    private Long currentPatientUid;
    //private boolean isReentrant;


    // Added for Extending PHC table for common fields - ODS changes activity
    // changes
    private String hospitalizedIndCd;
    private Timestamp hospitalizedAdminTime;
    private Timestamp hospitalizedDischargeTime;
    private BigDecimal hospitalizedDurationAmt;
    private String pregnantIndCd;
    // private String dieFromIllnessIndCD;
    private String dayCareIndCd;
    private String foodHandlerIndCd;
    private String importedCountryCd;
    private String importedStateCd;
    private String importedCityDescTxt;
    private String importedCountyCd;
    private Timestamp deceasedTime;
    private Timestamp rptSentTime;
    private String countIntervalCd;
    private boolean isSummaryCase;
    private String priorityCd;
    private Timestamp infectiousFromDate;
    private Timestamp infectiousToDate;
    private String contactInvStatus;
    private String contactInvTxt;
    private String referralBasisCd;
    private String currProcessStateCd;
    private String invPriorityCd;
    private String coinfectionId;
    private Timestamp associatedSpecimenCollDate;
    private String confirmationMethodCd;

    private Timestamp confirmationMethodTime;
}
