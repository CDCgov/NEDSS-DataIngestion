package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
@Getter
@Setter
public class DocumentSummaryContainer extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private Long nbsDocumentUid;
    private String docPayload;
    private String docType;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Long addUserID;
    private String txt;
    private Long MPRUid;
    private String jurisdiction;
    private String programArea;
    private String type;
    private Timestamp dateReceived;
    private String localId;
    private Collection<Object> theDocumentResultedTestSummaryVO;
    private String cd;
    private String cdDescTxt;
    private String firstName;
    private String lastName;
    private Timestamp addTime;
    private Map<Object, Object> associationMap;
    private String sendingFacilityNm;
    private String progAreaCd;

    private Long lastChgUserId;
    private String jurisdictionCd;
    private String progAreaCdOverride;
    private Timestamp lastChgTime;
    private String lastChgReasonCd;
    private String statusCd;
    private Timestamp statusTime;
    private String sharedInd;
    private Long programJurisdictionOid;
    private Integer versionCtrlNbr;

    @Override
    public Long getLastChgUserId() {
        return lastChgUserId;
    }

    @Override
    public void setLastChgUserId(Long aLastChgUserId) {
        this.lastChgUserId = aLastChgUserId;
    }

    @Override
    public String getJurisdictionCd() {
        return jurisdictionCd;
    }

    @Override
    public void setJurisdictionCd(String aJurisdictionCd) {
        this.jurisdictionCd = aJurisdictionCd;
    }

    @Override
    public String getProgAreaCd() {
        return progAreaCd;
    }

    @Override
    public void setProgAreaCd(String aProgAreaCd) {
        this.progAreaCd = aProgAreaCd;
    }

    @Override
    public Timestamp getLastChgTime() {
        return lastChgTime;
    }

    @Override
    public void setLastChgTime(Timestamp aLastChgTime) {
        this.lastChgTime = aLastChgTime;
    }

    @Override
    public String getLocalId() {
        return localId;
    }

    @Override
    public void setLocalId(String aLocalId) {
        this.localId = aLocalId;
    }

    @Override
    public Long getAddUserId() {
        return addUserID;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {
        this.addUserID = aAddUserId;
    }

    @Override
    public String getLastChgReasonCd() {
        return lastChgReasonCd;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {
        this.lastChgReasonCd = aLastChgReasonCd;
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
        return statusCd;
    }

    @Override
    public void setStatusCd(String aStatusCd) {
        this.statusCd = aStatusCd;
    }

    @Override
    public Timestamp getStatusTime() {
        return statusTime;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {
        this.statusTime = aStatusTime;
    }

    @Override
    public String getSuperclass() {
        return this.getClass().getSuperclass().getName();
    }

    @Override
    public Long getUid() {
        return nbsDocumentUid;
    }

    @Override
    public void setAddTime(Timestamp aAddTime) {
        this.addTime = aAddTime;
    }

    @Override
    public Timestamp getAddTime() {
        return addTime;
    }

    @Override
    public Long getProgramJurisdictionOid() {
        return programJurisdictionOid;
    }

    @Override
    public void setProgramJurisdictionOid(Long aProgramJurisdictionOid) {
        this.programJurisdictionOid = aProgramJurisdictionOid;
    }

    @Override
    public String getSharedInd() {
        return sharedInd;
    }

    @Override
    public void setSharedInd(String aSharedInd) {
        this.sharedInd = aSharedInd;
    }

    @Override
    public Integer getVersionCtrlNbr() {
        return versionCtrlNbr;
    }

    public void setVersionCtrlNbr(Integer versionCtrlNbr) {
        this.versionCtrlNbr = versionCtrlNbr;
    }
}