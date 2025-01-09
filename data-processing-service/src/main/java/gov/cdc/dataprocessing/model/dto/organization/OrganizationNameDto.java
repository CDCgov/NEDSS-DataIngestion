package gov.cdc.dataprocessing.model.dto.organization;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
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
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class OrganizationNameDto extends BaseContainer implements RootDtoInterface {
    private Long organizationUid;
    private Integer organizationNameSeq;
    private String nmTxt;
    private String nmUseCd;
    private String recordStatusCd;
    private String defaultNmInd;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;

    @Override
    public Long getLastChgUserId() {
        return organizationUid;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {

    }

    @Override
    public Timestamp getLastChgTime() {
        return TimeStampUtil.getCurrentTimeStamp(tz);
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {

    }

    @Override
    public String getLocalId() {
        return NEDSSConstant.CLASSTYPE_ENTITY;
    }

    @Override
    public void setLocalId(String aLocalId) {

    }

    @Override
    public Long getAddUserId() {
        return organizationUid;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {
        organizationUid = aAddUserId;
    }

    @Override
    public String getLastChgReasonCd() {
        return NEDSSConstant.CLASSTYPE_ENTITY;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {

    }

    @Override
    public Timestamp getRecordStatusTime() {
        return TimeStampUtil.getCurrentTimeStamp(tz);
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {

    }

    @Override
    public String getStatusCd() {
        return NEDSSConstant.CLASSTYPE_ENTITY;
    }

    @Override
    public void setStatusCd(String aStatusCd) {

    }

    @Override
    public Timestamp getStatusTime() {
        return TimeStampUtil.getCurrentTimeStamp(tz);
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {

    }

    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ENTITY;
        return superClassType;
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
    public Integer getVersionCtrlNbr() {
        return null;
    }

    private String tz;

    public OrganizationNameDto(String tz){
        this.tz = tz;
        itDirty = false;
        itNew = true;
        itDelete = false;

    }
    public OrganizationNameDto(OrganizationName organizationName, String tz){
        this.organizationUid=organizationName.getOrganizationUid();
        this.organizationNameSeq=organizationName.getOrganizationNameSeq();
        this.nmTxt=organizationName.getNameText();
        this.nmUseCd=organizationName.getNameUseCode();
        this.recordStatusCd=organizationName.getRecordStatusCode();
        this.defaultNmInd=organizationName.getDefaultNameIndicator();
        this.tz = tz;
    }
}
