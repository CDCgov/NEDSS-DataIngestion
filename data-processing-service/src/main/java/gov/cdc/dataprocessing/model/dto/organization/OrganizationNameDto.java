package gov.cdc.dataprocessing.model.dto.organization;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
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
        return getCurrentTimeStamp();
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
        return getCurrentTimeStamp();
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
        return getCurrentTimeStamp();
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

    public OrganizationNameDto(){
        itDirty = false;
        itNew = true;
        itDelete = false;

    }
    public OrganizationNameDto(OrganizationName organizationName){
        this.organizationUid=organizationName.getOrganizationUid();
        this.organizationNameSeq=organizationName.getOrganizationNameSeq();
        this.nmTxt=organizationName.getNameText();
        this.nmUseCd=organizationName.getNameUseCode();
        this.recordStatusCd=organizationName.getRecordStatusCode();
        this.defaultNmInd=organizationName.getDefaultNameIndicator();
    }
}
