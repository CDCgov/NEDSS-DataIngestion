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
