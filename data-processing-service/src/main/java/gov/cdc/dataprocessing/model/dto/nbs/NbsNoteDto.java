package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class NbsNoteDto extends BaseContainer implements RootDtoInterface {

    private Long nbsNoteUid;
    private Long noteParentUid;
    private Timestamp addTime;
    private Long addUserId;
    private String recordStatusCode;
    private Timestamp recordStatusTime;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String note;
    private String privateIndCd;
    private String typeCd;
    private String lastChgUserNm;

    @Override
    public Long getLastChgUserId() {
        return this.lastChgUserId;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {
        this.lastChgUserId = aLastChgUserId;
    }

    @Override
    public String getJurisdictionCd() {
        return null;
    }

    @Override
    public void setJurisdictionCd(String aJurisdictionCd) {
        // No operation needed for setJurisdictionCd()
    }

    @Override
    public String getProgAreaCd() {
        return null;
    }

    @Override
    public void setProgAreaCd(String aProgAreaCd) {
        // No operation needed for setProgAreaCd()
    }

    @Override
    public Timestamp getLastChgTime() {
        return this.lastChgTime;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {
        this.lastChgTime = aLastChgTime;
    }

    @Override
    public String getLocalId() {
        return null;
    }

    @Override
    public void setLocalId(String aLocalId) {
        // No operation needed for setLocalId()
    }

    @Override
    public Long getAddUserId() {
        return this.addUserId;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {
        this.addUserId = aAddUserId;
    }

    @Override
    public String getLastChgReasonCd() {
        return null;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {
        // No operation needed for setLastChgReasonCd()
    }

    @Override
    public String getRecordStatusCd() {
        return this.recordStatusCode;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {
        this.recordStatusCode = aRecordStatusCd;
    }

    @Override
    public Timestamp getRecordStatusTime() {
        return this.recordStatusTime;
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
        // No operation needed for setStatusCd()
    }

    @Override
    public Timestamp getStatusTime() {
        return null;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {
        // No operation needed for setStatusTime()
    }

    @Override
    public String getSuperclass() {
        return null;
    }

    @Override
    public Long getUid() {
        return this.nbsNoteUid;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {
        this.addTime = aAddTime;
    }

    @Override
    public Timestamp getAddTime() {
        return this.addTime;
    }

    @Override
    public Long getProgramJurisdictionOid() {
        return null;
    }

    @Override
    public void setProgramJurisdictionOid(Long aProgramJurisdictionOid) {
        // No operation needed for setProgramJurisdictionOid()
    }

    @Override
    public String getSharedInd() {
        return null;
    }

    @Override
    public void setSharedInd(String aSharedInd) {
        // No operation needed for setSharedInd()
    }

    @Override
    public Integer getVersionCtrlNbr() {
        return null;
    }
}
