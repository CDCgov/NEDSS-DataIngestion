package gov.cdc.dataprocessing.model.dto.notification;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class NotificationDto extends BaseContainer implements RootDtoInterface {
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



    private Long  exportReceivingFacilityUid;

    private String receiving_system_nm;

    private Long nbsInterfaceUid;

    private String nndInd;

    private String labReportEnableInd;

    private String vaccineEnableInd;

    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ACT;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return notificationUid;
    }

    public NotificationDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public NotificationDto(Notification domain) {
        this.notificationUid = domain.getNotificationUid();
        this.activityDurationAmt = domain.getActivityDurationAmt();
        this.activityDurationUnitCd = domain.getActivityDurationUnitCd();
        this.activityFromTime = domain.getActivityFromTime();
        this.activityToTime = domain.getActivityToTime();
        this.addReasonCd = domain.getAddReasonCd();
        this.addTime = domain.getAddTime();
        this.addUserId = domain.getAddUserId();
        this.caseClassCd = domain.getCaseClassCd();
        this.caseConditionCd = domain.getCaseConditionCd();
        this.cd = domain.getCd();
        this.cdDescTxt = domain.getCdDescTxt();
        this.confidentialityCd = domain.getConfidentialityCd();
        this.confidentialityDescTxt = domain.getConfidentialityDescTxt();
        this.confirmationMethodCd = domain.getConfirmationMethodCd();
        this.effectiveDurationAmt = domain.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = domain.getEffectiveDurationUnitCd();
        this.effectiveFromTime = domain.getEffectiveFromTime();
        this.effectiveToTime = domain.getEffectiveToTime();
        this.jurisdictionCd = domain.getJurisdictionCd();
        this.lastChgReasonCd = domain.getLastChgReasonCd();
        this.lastChgTime = domain.getLastChgTime();
        this.lastChgUserId = domain.getLastChgUserId();
        this.localId = domain.getLocalId();
        this.messageTxt = domain.getMessageTxt();
        this.methodCd = domain.getMethodCd();
        this.methodDescTxt = domain.getMethodDescTxt();
        this.mmwrWeek = domain.getMmwrWeek();
        this.mmwrYear = domain.getMmwrYear();
        this.nedssVersionNbr = domain.getNedssVersionNbr();
        this.progAreaCd = domain.getProgAreaCd();
        this.reasonCd = domain.getReasonCd();
        this.reasonDescTxt = domain.getReasonDescTxt();
        this.recordCount = domain.getRecordCount();
        this.recordStatusCd = domain.getRecordStatusCd();
        this.recordStatusTime = domain.getRecordStatusTime();
        this.repeatNbr = domain.getRepeatNbr();
        this.rptSentTime = domain.getRptSentTime();
        this.rptSourceCd = domain.getRptSourceCd();
        this.rptSourceTypeCd = domain.getRptSourceTypeCd();
        this.statusCd = domain.getStatusCd();
        this.statusTime = domain.getStatusTime();
        this.txt = domain.getTxt();
        this.userAffiliationTxt = domain.getUserAffiliationTxt();
        this.programJurisdictionOid = domain.getProgramJurisdictionOid();
        this.sharedInd = domain.getSharedInd();
        this.versionCtrlNbr = domain.getVersionCtrlNbr();
        this.autoResendInd = domain.getAutoResendInd();
        this.exportReceivingFacilityUid = domain.getExportReceivingFacilityUid();
        this.nbsInterfaceUid = domain.getNbsInterfaceUid();
    }

}
