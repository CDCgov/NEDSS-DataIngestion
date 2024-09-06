package gov.cdc.nbsDedup.model.dto.phc;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.model.dto.RootDtoInterface;
import gov.cdc.nbsDedup.nbs.odse.model.phc.Place;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PlaceDto extends BaseContainer implements RootDtoInterface
{
    private static final long serialVersionUID       = 1L;
    private Long              placeUid;
    private String            addReasonCd;
    private Timestamp         addTime;
    private Long              addUserId;
    private String            cd;
    private String            cdDescTxt;
    private String            description;
    private String            placeContact;
    private String            placeUrl;
    private String            placeAppNm;
    private String            durationAmt;
    private String            durationUnitCd;
    private Timestamp         fromTime;
    private String            lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long              lastChgUserId;
    private String            localId;
    private String            nm;
    private String            recordStatusCd;
    private Timestamp         recordStatusTime;
    private String            statusCd;
    private Timestamp         statusTime;
    private Timestamp         toTime;
    private String            userAffiliationTxt;
    private Integer           versionCtrlNbr;
    private String            progAreaCd             = null;
    private String            jurisdictionCd         = null;
    private Long              programJurisdictionOid = null;
    private String            sharedInd              = null;

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
