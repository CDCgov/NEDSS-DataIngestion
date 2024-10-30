package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
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
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class NotificationSummaryContainer extends BaseContainer implements RootDtoInterface
{

    /**
     * from NotificationDT.notificationtionUid
     * (Not for display purposes)
     */
    private Long notificationUid;

    /**
     * from NotificationDT.addTime
     */
    private Timestamp addTime;

    /**
     * from NotificationDT.rptSentTime
     */
    private Timestamp rptSentTime;


    private Timestamp recordStatusTime;


    /**
     * from PublicHealthCaseDto.cd
     */
    private String cd;

    /**
     * from PublicHealthCase.caseClassCd
     */
    private String caseClassCd;

    /**
     * from NotificationDT.localId
     */
    private String localId;

    /**
     * from Notification.txt
     */
    private String txt;

    /**
     * from NotificationDT.lastChgTime
     */
    private Timestamp lastChgTime;

    private String addUserName;
    /**
     * from NotificationDT.addUserId
     */
    private Long addUserId;

    /**
     * from PublicHealthCase.jurisdictionCd
     */
    private String jurisdictionCd;

    /**
     * from publicHealthCaseDT.publicHealthCaseUid
     */
    private Long publicHealthCaseUid;

    /**
     * from Code_Value_General.code where Code_Value_General.code_set_nm = 'PHC_TYPE'
     */
    private String cdTxt;

    /**
     * from Code_Value_General.code_desc_txt where Code_Value_General.code_set_nm =
     * 'S_JURDIC_C'
     */
    private String jurisdictionCdTxt;

    /**
     * from publicHealthCaseDT.localId
     */
    private String publicHealthCaseLocalId;

    /**
     * from Code_Value_General.code where Code_Value_General.code_set_nm = 'PHC_CLASS'
     */
    private String caseClassCdTxt;

    /**
     * from Notification.record_status_cd
     */
    private String recordStatusCd;
    private String lastNm;
    private String firstNm;
    private String currSexCd;
    private Timestamp birthTimeCalc;
    private String autoResendInd;
    public String isHistory;
    //Needed for Auto Resend
    private String progAreaCd;
    private String sharedInd;
    private String currSexCdDesc;

    private Long MPRUid;

    //This is for cd in the Notification table
    private String cdNotif;
    // This is for approve notification checking variable for popup
    private boolean nndAssociated;
    private boolean isCaseReport;
    private Long programJurisdictionOid;
    private boolean shareAssocaited;


    /**
     * Notification.cd
     */



    //Need for the new Notification Queue to add links for patient and condition
    private String patientFullName;
    private String patientFullNameLnk;
    private String conditionCodeTextLnk;
    private String approveLink;
    private String rejectLink;
    private String notificationCd;
    private String notificationSrtDescCd;
    private String recipient;
    private String exportRecFacilityUid;
    //The following variable is required to control the Special Character's of Recipient which is coming from the manageSystems
    private String codeConverterTemp;
    private String codeConverterCommentTemp;
    private boolean isPendingNotification;
    private String nndInd;

    @Override
    public Long getLastChgUserId() {
        return null;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {

    }

    @Override
    public String getJurisdictionCd() {
        return null;
    }

    @Override
    public void setJurisdictionCd(String aJurisdictionCd) {

    }

    @Override
    public String getProgAreaCd() {
        return null;
    }

    @Override
    public void setProgAreaCd(String aProgAreaCd) {

    }

    @Override
    public Timestamp getLastChgTime() {
        return null;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {

    }

    @Override
    public String getLocalId() {
        return null;
    }

    @Override
    public void setLocalId(String aLocalId) {

    }

    @Override
    public Long getAddUserId() {
        return null;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {

    }

    @Override
    public String getLastChgReasonCd() {
        return null;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {

    }

    @Override
    public String getRecordStatusCd() {
        return recordStatusCd;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {
        this.recordStatusCd = aRecordStatusCd;
    }

    @Override
    public Timestamp getRecordStatusTime() {
        return recordStatusTime;
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {
        this.recordStatusTime = aRecordStatusTime;
    }

    @Override
    public String getStatusCd() {
        return null;
    }

    @Override
    public void setStatusCd(String aStatusCd) {

    }

    @Override
    public Timestamp getStatusTime() {
        return null;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {

    }

    @Override
    public String getSuperclass() {
        return null;
    }

    @Override
    public Long getUid() {
        return null;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {

    }

    @Override
    public Timestamp getAddTime() {
        return null;
    }

    @Override
    public Long getProgramJurisdictionOid() {
        return null;
    }

    @Override
    public void setProgramJurisdictionOid(Long aProgramJurisdictionOid) {

    }

    @Override
    public String getSharedInd() {
        return null;
    }

    @Override
    public void setSharedInd(String aSharedInd) {

    }

    @Override
    public Integer getVersionCtrlNbr() {
        return null;
    }
}
