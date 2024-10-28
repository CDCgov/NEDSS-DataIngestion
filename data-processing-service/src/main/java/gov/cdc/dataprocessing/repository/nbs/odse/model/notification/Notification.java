package gov.cdc.dataprocessing.repository.nbs.odse.model.notification;

import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "Notification")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class Notification   {

    @Id
    @Column(name = "notification_uid")
    private Long notificationUid;

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

    @Column(name = "case_condition_cd")
    private String caseConditionCd;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "confidentiality_cd")
    private String confidentialityCd;

    @Column(name = "confidentiality_desc_txt")
    private String confidentialityDescTxt;

    @Column(name = "confirmation_method_cd")
    private String confirmationMethodCd;

    @Column(name = "effective_duration_amt")
    private String effectiveDurationAmt;

    @Column(name = "effective_duration_unit_cd")
    private String effectiveDurationUnitCd;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

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

    @Lob
    @Column(name = "message_txt")
    private String messageTxt;

    @Column(name = "method_cd")
    private String methodCd;

    @Column(name = "method_desc_txt")
    private String methodDescTxt;

    @Column(name = "mmwr_week")
    private String mmwrWeek;

    @Column(name = "mmwr_year")
    private String mmwrYear;

    @Column(name = "nedss_version_nbr")
    private String nedssVersionNbr;

    @Column(name = "prog_area_cd")
    private String progAreaCd;

    @Column(name = "reason_cd")
    private String reasonCd;

    @Column(name = "reason_desc_txt")
    private String reasonDescTxt;

    @Column(name = "record_count")
    private String recordCount;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "repeat_nbr")
    private Integer repeatNbr;

    @Column(name = "rpt_sent_time")
    private Timestamp rptSentTime;

    @Column(name = "rpt_source_cd")
    private String rptSourceCd;

    @Column(name = "rpt_source_type_cd")
    private String rptSourceTypeCd;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

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

    @Column(name = "auto_resend_ind")
    private String autoResendInd;

    @Column(name = "export_receiving_facility_uid")
    private Long exportReceivingFacilityUid;

    @Column(name = "nbs_interface_uid")
    private Long nbsInterfaceUid;

    // Constructors, getters, and setters

    public Notification() {

    }

    public Notification(NotificationDto dto) {
        this.notificationUid = dto.getNotificationUid();
        this.activityDurationAmt = dto.getActivityDurationAmt();
        this.activityDurationUnitCd = dto.getActivityDurationUnitCd();
        this.activityFromTime = dto.getActivityFromTime();
        this.activityToTime = dto.getActivityToTime();
        this.addReasonCd = dto.getAddReasonCd();
        this.addTime = dto.getAddTime();
        this.addUserId = dto.getAddUserId();
        this.caseClassCd = dto.getCaseClassCd();
        this.caseConditionCd = dto.getCaseConditionCd();
        this.cd = dto.getCd();
        this.cdDescTxt = dto.getCdDescTxt();
        this.confidentialityCd = dto.getConfidentialityCd();
        this.confidentialityDescTxt = dto.getConfidentialityDescTxt();
        this.confirmationMethodCd = dto.getConfirmationMethodCd();
        this.effectiveDurationAmt = dto.getEffectiveDurationAmt();
        this.effectiveDurationUnitCd = dto.getEffectiveDurationUnitCd();
        this.effectiveFromTime = dto.getEffectiveFromTime();
        this.effectiveToTime = dto.getEffectiveToTime();
        this.jurisdictionCd = dto.getJurisdictionCd();
        this.lastChgReasonCd = dto.getLastChgReasonCd();
        this.lastChgTime = dto.getLastChgTime();
        this.lastChgUserId = dto.getLastChgUserId();
        this.localId = dto.getLocalId();
        this.messageTxt = dto.getMessageTxt();
        this.methodCd = dto.getMethodCd();
        this.methodDescTxt = dto.getMethodDescTxt();
        this.mmwrWeek = dto.getMmwrWeek();
        this.mmwrYear = dto.getMmwrYear();
        this.nedssVersionNbr = dto.getNedssVersionNbr();
        this.progAreaCd = dto.getProgAreaCd();
        this.reasonCd = dto.getReasonCd();
        this.reasonDescTxt = dto.getReasonDescTxt();
        this.recordCount = dto.getRecordCount();
        this.recordStatusCd = dto.getRecordStatusCd();
        this.recordStatusTime = dto.getRecordStatusTime();
        this.repeatNbr = dto.getRepeatNbr();
        this.rptSentTime = dto.getRptSentTime();
        this.rptSourceCd = dto.getRptSourceCd();
        this.rptSourceTypeCd = dto.getRptSourceTypeCd();
        this.statusCd = dto.getStatusCd();
        this.statusTime = dto.getStatusTime();
        this.txt = dto.getTxt();
        this.userAffiliationTxt = dto.getUserAffiliationTxt();
        this.programJurisdictionOid = dto.getProgramJurisdictionOid();
        this.sharedInd = dto.getSharedInd();
        this.versionCtrlNbr = dto.getVersionCtrlNbr();
        this.autoResendInd = dto.getAutoResendInd();
        this.exportReceivingFacilityUid = dto.getExportReceivingFacilityUid();
        this.nbsInterfaceUid = dto.getNbsInterfaceUid();
    }

}
