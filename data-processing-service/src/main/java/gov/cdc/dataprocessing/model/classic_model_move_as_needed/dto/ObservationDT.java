package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ObservationDT extends AbstractVO {
    private static final long serialVersionUID = 1L;
    private Long observationUid;

    private String activityDurationAmt;

    private String activityDurationUnitCd;

    private Timestamp activityFromTime;

    private Timestamp activityToTime;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private String addUserName; // BB - civil00012298 - add Name field to
    // display now instead of Id

    private String altCd;

    private String altCdDescTxt;

    private String altCdSystemCd;

    private String altCdSystemDescTxt;

    private String cd;

    private String cdDerivedInd;

    private String cdDescTxt;

    private String cdSystemCd;

    private String cdSystemDescTxt;

    private String confidentialityCd;

    private String confidentialityDescTxt;

    private String ctrlCdDisplayForm;

    private String ctrlCdUserDefined1;

    private String ctrlCdUserDefined2;

    private String ctrlCdUserDefined3;

    private String ctrlCdUserDefined4;

    private Integer derivationExp;

    private String effectiveDurationAmt;

    private String effectiveDurationUnitCd;

    private Timestamp effectiveFromTime;

    private Timestamp effectiveToTime;

    private String electronicInd;

    private String groupLevelCd;

    private String jurisdictionCd;

    private String labConditionCd;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String lastChgUserName; // BB - civil00012298 - add Name field to
    // display now instead of Id

    private String localId;

    private String methodCd;

    private String methodDescTxt;

    private String obsDomainCd;

    private String obsDomainCdSt1;

    private String pnuCd;

    private String priorityCd;

    private String priorityDescTxt;

    private String progAreaCd;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private Integer repeatNbr;

    private Timestamp rptToStateTime;

    private String statusCd;

    private Timestamp statusTime;

    private Long subjectPersonUid;

    private String targetSiteCd;

    private String targetSiteDescTxt;

    private String txt;

    private String userAffiliationTxt;

    private String valueCd;

    private String ynuCd;

    private Long programJurisdictionOid;

    private String sharedInd;

    private Integer versionCtrlNbr;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    private String cdVersion;

    private String searchResultOT;

    private String searchResultRT;

    private String cdSystemCdOT;

    private String cdSystemCdRT;

    private String hiddenCd;

    private String codedResultCd;
    private String organismCd;
    private String susceptabilityVal;
    private String resultedMethodCd;
    private String drugNameCd;
    private String interpretiveFlagCd;

    private String processingDecisionCd;
    private String processingDecisionTxt;

    // Task: #2567, #2566
    private String pregnantIndCd;
    private Integer pregnantWeek;
}
