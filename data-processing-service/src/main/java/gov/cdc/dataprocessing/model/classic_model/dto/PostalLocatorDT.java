package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PostalLocatorDT extends AbstractVO {
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
    private boolean itDirty = false;
    private boolean itNew = false;
    private boolean itDelete = false;
    private String censusTract;
}