package gov.cdc.dataprocessing.model.dto.locator;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
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
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class PostalLocatorDto extends BaseContainer {
    private Long postalLocatorUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String censusBlockCd;
    private String censusMinorCivilDivisionCd;
    private String censusTrackCd;
    private String cityCd;
    private String cityDescTxt;
    private String cntryCd;
    private String cntryDescTxt;
    private String cntyCd;
    private String cntyDescTxt;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String MSACongressDistrictCd;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String regionDistrictCd;
    private String stateCd;
    private String streetAddr1;
    private String streetAddr2;
    private String userAffiliationTxt;
    private String zipCd;
    private String geocodeMatchInd;
    private String withinCityLimitsInd;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;

    private String censusTract;

    public PostalLocatorDto(PostalLocator postalLocator) {
        this.postalLocatorUid = postalLocator.getPostalLocatorUid();
        this.addReasonCd = postalLocator.getAddReasonCd();
        this.addTime = postalLocator.getAddTime();
        this.addUserId = postalLocator.getAddUserId();
        this.censusBlockCd = postalLocator.getCensusBlockCd();
        this.censusMinorCivilDivisionCd = postalLocator.getCensusMinorCivilDivisionCd();
        this.censusTrackCd = postalLocator.getCensusTrackCd();
        this.cityCd = postalLocator.getCityCd();
        this.cityDescTxt = postalLocator.getCityDescTxt();
        this.cntryCd = postalLocator.getCntryCd();
        this.cntryDescTxt = postalLocator.getCntryDescTxt();
        this.cntyCd = postalLocator.getCntyCd();
        this.cntyDescTxt = postalLocator.getCntyDescTxt();
        this.lastChgReasonCd = postalLocator.getLastChgReasonCd();
        this.lastChgTime = postalLocator.getLastChgTime();
        this.lastChgUserId = postalLocator.getLastChgUserId();
        this.MSACongressDistrictCd = postalLocator.getMsaCongressDistrictCd();
        this.recordStatusCd = postalLocator.getRecordStatusCd();
        this.recordStatusTime = postalLocator.getRecordStatusTime();
        this.regionDistrictCd = postalLocator.getRegionDistrictCd();
        this.stateCd = postalLocator.getStateCd();
        this.streetAddr1 = postalLocator.getStreetAddr1();
        this.streetAddr2 = postalLocator.getStreetAddr2();
        this.userAffiliationTxt = postalLocator.getUserAffiliationTxt();
        this.zipCd = postalLocator.getZipCd();
        this.geocodeMatchInd = postalLocator.getGeocodeMatchInd();
        this.withinCityLimitsInd = postalLocator.getWithinCityLimitsInd();
        this.censusTract = postalLocator.getCensusTract();
    }

    public PostalLocatorDto() {
        // Default constructor
        itDirty = false;
        itNew = false;
        itDelete = false;
    }

}