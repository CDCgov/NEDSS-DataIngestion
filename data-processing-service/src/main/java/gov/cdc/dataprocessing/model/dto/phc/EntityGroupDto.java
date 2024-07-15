package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.EntityGroup;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class EntityGroupDto extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private Long entityGroupUid;

    private String addReasonCd;

    private Timestamp addTime;

    private Long addUserId;

    private String cd;

    private String cdDescTxt;

    private String description;

    private String durationAmt;

    private String durationUnitCd;

    private Timestamp fromTime;

    private Integer groupCnt;

    private String lastChgReasonCd;

    private Timestamp lastChgTime;

    private Long lastChgUserId;

    private String localId;

    private String nm;

    private String recordStatusCd;

    private Timestamp recordStatusTime;

    private String statusCd;

    private Timestamp statusTime;

    private Timestamp toTime;

    private String userAffiliationTxt;

    private Integer versionCtrlNbr;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    public EntityGroupDto() {

    }

    public EntityGroupDto(EntityGroup entityGroup) {
        this.entityGroupUid = entityGroup.getEntityGroupUid();
        this.addReasonCd = entityGroup.getAddReasonCd();
        this.addTime = entityGroup.getAddTime();
        this.addUserId = entityGroup.getAddUserId();
        this.cd = entityGroup.getCd();
        this.cdDescTxt = entityGroup.getCdDescTxt();
        this.description = entityGroup.getDescription();
        this.durationAmt = entityGroup.getDurationAmt();
        this.durationUnitCd = entityGroup.getDurationUnitCd();
        this.fromTime = entityGroup.getFromTime();
        this.groupCnt = entityGroup.getGroupCnt();
        this.lastChgReasonCd = entityGroup.getLastChgReasonCd();
        this.lastChgTime = entityGroup.getLastChgTime();
        this.lastChgUserId = entityGroup.getLastChgUserId();
        this.localId = entityGroup.getLocalId();
        this.nm = entityGroup.getNm();
        this.recordStatusCd = entityGroup.getRecordStatusCd();
        this.recordStatusTime = entityGroup.getRecordStatusTime();
        this.statusCd = entityGroup.getStatusCd();
        this.statusTime = entityGroup.getStatusTime();
        this.toTime = entityGroup.getToTime();
        this.userAffiliationTxt = entityGroup.getUserAffiliationTxt();
        this.versionCtrlNbr = entityGroup.getVersionCtrlNbr();
    }

    @Override
    public Long getLastChgUserId() {
        return lastChgUserId;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {
        // No operation needed for setLastChgUserId()
        lastChgUserId = aLastChgUserId;
    }

    @Override
    public String getJurisdictionCd() {
        return jurisdictionCd;
    }

    @Override
    public void setJurisdictionCd(String aJurisdictionCd) {
        // No operation needed for setJurisdictionCd()
         jurisdictionCd = aJurisdictionCd;
    }

    @Override
    public String getProgAreaCd() {
        return progAreaCd;
    }

    @Override
    public void setProgAreaCd(String aProgAreaCd) {
        // No operation needed for setProgAreaCd()
        progAreaCd = aProgAreaCd;
    }

    @Override
    public Timestamp getLastChgTime() {
        return lastChgTime;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {
        // No operation needed for setLastChgTime()
        lastChgTime = aLastChgTime;
    }

    @Override
    public String getLocalId() {
        return localId;
    }

    @Override
    public void setLocalId(String aLocalId) {
        // No operation needed for setLocalId()
        localId = aLocalId;
    }

    @Override
    public Long getAddUserId() {
        return addUserId;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {
        // No operation needed for setAddUserId()
        addUserId = addUserId;
    }

    @Override
    public String getLastChgReasonCd() {
        return lastChgReasonCd;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {
        // No operation needed for setLastChgReasonCd()
        lastChgReasonCd = aLastChgReasonCd;
    }

    @Override
    public String getRecordStatusCd() {
        return recordStatusCd;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {
        // No operation needed for setRecordStatusCd()
        recordStatusCd = aRecordStatusCd;
    }

    @Override
    public Timestamp getRecordStatusTime() {
        return recordStatusTime;
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {
        // No operation needed for setRecordStatusTime()
        recordStatusTime = aRecordStatusTime;
    }

    @Override
    public String getStatusCd() {
        return statusCd;
    }

    @Override
    public void setStatusCd(String aStatusCd) {
        // No operation needed for setStatusCd()
        statusCd = aStatusCd;
    }

    @Override
    public Timestamp getStatusTime() {
        return statusTime;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {
        // No operation needed for setStatusTime()
        statusTime = aStatusTime;
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
    public Timestamp getAddTime() {
        return addTime;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {
        // No operation needed for setAddTime()
        addTime = aAddTime;
    }

    @Override
    public Long getProgramJurisdictionOid() {
        return programJurisdictionOid;
    }

    @Override
    public void setProgramJurisdictionOid(Long aProgramJurisdictionOid) {
        // No operation needed for setProgramJurisdictionOid()
        programJurisdictionOid = aProgramJurisdictionOid;
    }

    @Override
    public String getSharedInd() {
        return sharedInd;
    }

    @Override
    public void setSharedInd(String aSharedInd) {
        // No operation needed for setSharedInd()
        sharedInd = aSharedInd;
    }

    @Override
    public Integer getVersionCtrlNbr() {
        return versionCtrlNbr;
    }
}
