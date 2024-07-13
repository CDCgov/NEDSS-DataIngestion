package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;

@Getter
@Setter
public class ResultedTestSummaryContainer extends BaseContainer implements RootDtoInterface {
    private Long sourceActUid;
    private String localId;
    private Long observationUid;
    private String ctrlCdUserDefined1;
    private String resultedTest;
    private String codedResultValue;
    private String organismName;
    private String numericResultCompare;
    private BigDecimal numericResultValue1;
    private String numericResultSeperator;
    private BigDecimal numericResultValue2;
    private String numericResultUnits;
    private String textResultValue;
    private String type;
    private String status;
    private String resultedTestStatusCd;
    private String resultedTestStatus;
    private String drugName;
    private String orderedTest;
    private Collection<Object> theSusTestSummaryVOColl;
    private String cdSystemCd;
    private String resultedTestCd;
    private String organismCodeSystemCd;
    private String recordStatusCode;
    private String highRange;
    private Integer numericScale1;
    private String lowRange;
    private String uniqueMapKey;
    private Integer numericScale2;

    private Long lastChgUserId;
    private String jurisdictionCd;
    private String progAreaCd;
    private Timestamp lastChgTime;
    private Long addUserId;
    private String lastChgReasonCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private Long programJurisdictionOid;
    private String sharedInd;
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
        return addUserId;
    }

    @Override
    public void setAddUserId(Long aAddUserId) {
        this.addUserId = aAddUserId;
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
    public String getRecordStatusCd() {
        return recordStatusCode;
    }

    @Override
    public void setRecordStatusCd(String aRecordStatusCd) {
        this.recordStatusCode = aRecordStatusCd;
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
        return observationUid;
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
