package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationHist;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationHistTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        OrganizationHist organizationHist = new OrganizationHist();

        // Assert
        assertNull(organizationHist.getOrganizationUid());
        assertEquals(0, organizationHist.getVersionCtrlNbr());
        assertNull(organizationHist.getAddReasonCd());
        assertNull(organizationHist.getAddTime());
        assertNull(organizationHist.getAddUserId());
        assertNull(organizationHist.getCd());
        assertNull(organizationHist.getCdDescTxt());
        assertNull(organizationHist.getDescription());
        assertNull(organizationHist.getDurationAmt());
        assertNull(organizationHist.getDurationUnitCd());
        assertNull(organizationHist.getFromTime());
        assertNull(organizationHist.getLastChgReasonCd());
        assertNull(organizationHist.getLastChgTime());
        assertNull(organizationHist.getLastChgUserId());
        assertNull(organizationHist.getLocalId());
        assertNull(organizationHist.getRecordStatusCd());
        assertNull(organizationHist.getRecordStatusTime());
        assertNull(organizationHist.getStandardIndustryClassCd());
        assertNull(organizationHist.getStandardIndustryDescTxt());
        assertNull(organizationHist.getStatusCd());
        assertNull(organizationHist.getStatusTime());
        assertNull(organizationHist.getToTime());
        assertNull(organizationHist.getUserAffiliationTxt());
        assertNull(organizationHist.getDisplayNm());
        assertNull(organizationHist.getStreetAddr1());
        assertNull(organizationHist.getStreetAddr2());
        assertNull(organizationHist.getCityCd());
        assertNull(organizationHist.getCityDescTxt());
        assertNull(organizationHist.getStateCd());
        assertNull(organizationHist.getCntyCd());
        assertNull(organizationHist.getCntryCd());
        assertNull(organizationHist.getZipCd());
        assertNull(organizationHist.getPhoneNbr());
        assertNull(organizationHist.getPhoneCntryCd());
        assertNull(organizationHist.getElectronicInd());
        assertNull(organizationHist.getEdxInd());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        OrganizationHist organizationHist = new OrganizationHist();

        Long organizationUid = 1L;
        int versionCtrlNbr = 1;
        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "cd";
        String cdDescTxt = "cdDescTxt";
        String description = "description";
        String durationAmt = "durationAmt";
        String durationUnitCd = "durationUnitCd";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "lastChgReasonCd";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 4L;
        String localId = "localId";
        String recordStatusCd = "recordStatusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String standardIndustryClassCd = "standardIndustryClassCd";
        String standardIndustryDescTxt = "standardIndustryDescTxt";
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String userAffiliationTxt = "userAffiliationTxt";
        String displayNm = "displayNm";
        String streetAddr1 = "streetAddr1";
        String streetAddr2 = "streetAddr2";
        String cityCd = "cityCd";
        String cityDescTxt = "cityDescTxt";
        String stateCd = "stateCd";
        String cntyCd = "cntyCd";
        String cntryCd = "cntryCd";
        String zipCd = "zipCd";
        String phoneNbr = "phoneNbr";
        String phoneCntryCd = "phoneCntryCd";
        String electronicInd = "electronicInd";
        String edxInd = "Y";

        // Act
        organizationHist.setOrganizationUid(organizationUid);
        organizationHist.setVersionCtrlNbr(versionCtrlNbr);
        organizationHist.setAddReasonCd(addReasonCd);
        organizationHist.setAddTime(addTime);
        organizationHist.setAddUserId(addUserId);
        organizationHist.setCd(cd);
        organizationHist.setCdDescTxt(cdDescTxt);
        organizationHist.setDescription(description);
        organizationHist.setDurationAmt(durationAmt);
        organizationHist.setDurationUnitCd(durationUnitCd);
        organizationHist.setFromTime(fromTime);
        organizationHist.setLastChgReasonCd(lastChgReasonCd);
        organizationHist.setLastChgTime(lastChgTime);
        organizationHist.setLastChgUserId(lastChgUserId);
        organizationHist.setLocalId(localId);
        organizationHist.setRecordStatusCd(recordStatusCd);
        organizationHist.setRecordStatusTime(recordStatusTime);
        organizationHist.setStandardIndustryClassCd(standardIndustryClassCd);
        organizationHist.setStandardIndustryDescTxt(standardIndustryDescTxt);
        organizationHist.setStatusCd(statusCd);
        organizationHist.setStatusTime(statusTime);
        organizationHist.setToTime(toTime);
        organizationHist.setUserAffiliationTxt(userAffiliationTxt);
        organizationHist.setDisplayNm(displayNm);
        organizationHist.setStreetAddr1(streetAddr1);
        organizationHist.setStreetAddr2(streetAddr2);
        organizationHist.setCityCd(cityCd);
        organizationHist.setCityDescTxt(cityDescTxt);
        organizationHist.setStateCd(stateCd);
        organizationHist.setCntyCd(cntyCd);
        organizationHist.setCntryCd(cntryCd);
        organizationHist.setZipCd(zipCd);
        organizationHist.setPhoneNbr(phoneNbr);
        organizationHist.setPhoneCntryCd(phoneCntryCd);
        organizationHist.setElectronicInd(electronicInd);
        organizationHist.setEdxInd(edxInd);

        // Assert
        assertEquals(organizationUid, organizationHist.getOrganizationUid());
        assertEquals(versionCtrlNbr, organizationHist.getVersionCtrlNbr());
        assertEquals(addReasonCd, organizationHist.getAddReasonCd());
        assertEquals(addTime, organizationHist.getAddTime());
        assertEquals(addUserId, organizationHist.getAddUserId());
        assertEquals(cd, organizationHist.getCd());
        assertEquals(cdDescTxt, organizationHist.getCdDescTxt());
        assertEquals(description, organizationHist.getDescription());
        assertEquals(durationAmt, organizationHist.getDurationAmt());
        assertEquals(durationUnitCd, organizationHist.getDurationUnitCd());
        assertEquals(fromTime, organizationHist.getFromTime());
        assertEquals(lastChgReasonCd, organizationHist.getLastChgReasonCd());
        assertEquals(lastChgTime, organizationHist.getLastChgTime());
        assertEquals(lastChgUserId, organizationHist.getLastChgUserId());
        assertEquals(localId, organizationHist.getLocalId());
        assertEquals(recordStatusCd, organizationHist.getRecordStatusCd());
        assertEquals(recordStatusTime, organizationHist.getRecordStatusTime());
        assertEquals(standardIndustryClassCd, organizationHist.getStandardIndustryClassCd());
        assertEquals(standardIndustryDescTxt, organizationHist.getStandardIndustryDescTxt());
        assertEquals(statusCd, organizationHist.getStatusCd());
        assertEquals(statusTime, organizationHist.getStatusTime());
        assertEquals(toTime, organizationHist.getToTime());
        assertEquals(userAffiliationTxt, organizationHist.getUserAffiliationTxt());
        assertEquals(displayNm, organizationHist.getDisplayNm());
        assertEquals(streetAddr1, organizationHist.getStreetAddr1());
        assertEquals(streetAddr2, organizationHist.getStreetAddr2());
        assertEquals(cityCd, organizationHist.getCityCd());
        assertEquals(cityDescTxt, organizationHist.getCityDescTxt());
        assertEquals(stateCd, organizationHist.getStateCd());
        assertEquals(cntyCd, organizationHist.getCntyCd());
        assertEquals(cntryCd, organizationHist.getCntryCd());
        assertEquals(zipCd, organizationHist.getZipCd());
        assertEquals(phoneNbr, organizationHist.getPhoneNbr());
        assertEquals(phoneCntryCd, organizationHist.getPhoneCntryCd());
        assertEquals(electronicInd, organizationHist.getElectronicInd());
        assertEquals(edxInd, organizationHist.getEdxInd());
    }
}
