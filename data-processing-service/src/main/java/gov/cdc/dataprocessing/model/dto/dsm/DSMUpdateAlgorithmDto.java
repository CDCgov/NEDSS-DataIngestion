package gov.cdc.dataprocessing.model.dto.dsm;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter

public class DSMUpdateAlgorithmDto extends BaseContainer implements RootDtoInterface {

    private static final long serialVersionUID = 1L;

    private Long dsmUpdateAlgorithmUid;
    private String conditionCd;
    private String sendingSystemNm;
    private String updateIndCd;
    private String updateClosedBehaviour;
    private String updateMultiClosedBehaviour;
    private String updateMultiOpenBehaviour;
    private String updateIgnoreList;
    private String updateTimeframe;
    private String adminComment;
    private String statusCd;
    private Timestamp statusTime;
    private Long addUserId;
    private Timestamp addTime;
    private Long lastChgUserId;
    private Timestamp lastChgTime;
    private String dsmUpdateAlgorithmMapKey;

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
