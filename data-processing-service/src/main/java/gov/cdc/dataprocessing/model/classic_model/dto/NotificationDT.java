package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NotificationDT extends AbstractVO {
    private static final long serialVersionUID = 1L;

    private Long notificationUid;

    private String activityDurationAmt;

    private String activityDurationUnitCd;

    private Timestamp activityFromTime;

    private Timestamp activityToTime;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private String caseClassCd;

    private String caseConditionCd;

    private String cd;

    private String cdDescTxt;

    private String confidentialityCd;

    private String confidentialityDescTxt;

    private String confirmationMethodCd;

    private String effectiveDurationAmt;

    private String effectiveDurationUnitCd;

    private Timestamp effectiveFromTime;

    private Timestamp effectiveToTime;

    private String jurisdictionCd;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String localId;

    private String messageTxt;

    private String methodCd;

    private String methodDescTxt;

    private String mmwrWeek;

    private String mmwrYear;

    private String nedssVersionNbr;

    private String progAreaCd;

    private String reasonCd;

    private String reasonDescTxt;

    private String recordCount;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private Integer repeatNbr;

    private Timestamp rptSentTime;

    private String rptSourceCd;

    private String rptSourceTypeCd;

    private String statusCd;

    private Timestamp statusTime;

    private String txt;

    private String userAffiliationTxt;

    private Long programJurisdictionOid;

    private String sharedInd;

    private Integer versionCtrlNbr;

    private String autoResendInd;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    private Long  exportReceivingFacilityUid;

    private String receiving_system_nm;

    private Long nbsInterfaceUid;

    private String nndInd;

    private String labReportEnableInd;

    private String vaccineEnableInd;
}
