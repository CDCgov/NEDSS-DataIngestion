package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.EntityGroup;
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
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class EntityGroupDto extends BaseContainer implements RootDtoInterface
{
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
        return null;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {

    }

    @Override
    public Timestamp getRecordStatusTime() {
        return null;
    }

    @Override
    public void setRecordStatusTime(Timestamp aRecordStatusTime) {

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
