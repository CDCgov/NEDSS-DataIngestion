package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.Place;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class PlaceDto extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private Long placeUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String cd;
    private String cdDescTxt;
    private String description;
    private String placeContact;
    private String placeUrl;
    private String placeAppNm;
    private String durationAmt;
    private String durationUnitCd;
    private Timestamp fromTime;
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

    public PlaceDto() {
    }

    public PlaceDto(Place place) {
        this.placeUid = place.getPlaceUid();
        this.addReasonCd = place.getAddReasonCd();
        this.addTime = place.getAddTime();
        this.addUserId = place.getAddUserId();
        this.cd = place.getCd();
        this.cdDescTxt = place.getCdDescTxt();
        this.description = place.getDescription();

        this.placeContact = place.getStreetAddr1() + " " + place.getStreetAddr2() + ", " +
                place.getCityCd() + ", " + place.getStateCd() + " " + place.getZipCd() + ", " +
                place.getCntryCd();
        // Assuming the above logic is used to construct the place contact information

        this.durationAmt = place.getDurationAmt();
        this.durationUnitCd = place.getDurationUnitCd();
        this.fromTime = place.getFromTime();
        this.lastChgReasonCd = place.getLastChgReasonCd();
        this.lastChgTime = place.getLastChgTime();
        this.lastChgUserId = place.getLastChgUserId();
        this.localId = place.getLocalId();
        this.nm = place.getNm();
        this.recordStatusCd = place.getRecordStatusCd();
        this.recordStatusTime = place.getRecordStatusTime();
        this.statusCd = place.getStatusCd();
        this.statusTime = place.getStatusTime();
        this.toTime = place.getToTime();
        this.userAffiliationTxt = place.getUserAffiliationTxt();
        this.versionCtrlNbr = place.getVersionCtrlNbr();
    }

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
        return superClassType;
    }

    @Override
    public Long getUid() {
        return placeUid;
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
}
