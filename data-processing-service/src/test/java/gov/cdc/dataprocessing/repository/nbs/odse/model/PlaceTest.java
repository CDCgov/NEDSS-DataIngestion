package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.phc.PlaceDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.Place;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class PlaceTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        Place place = new Place();

        // Assert
        assertNull(place.getPlaceUid());
        assertNull(place.getAddReasonCd());
        assertNull(place.getAddTime());
        assertNull(place.getAddUserId());
        assertNull(place.getCd());
        assertNull(place.getCdDescTxt());
        assertNull(place.getDescription());
        assertNull(place.getDurationAmt());
        assertNull(place.getDurationUnitCd());
        assertNull(place.getFromTime());
        assertNull(place.getLastChgReasonCd());
        assertNull(place.getLastChgTime());
        assertNull(place.getLastChgUserId());
        assertNull(place.getLocalId());
        assertNull(place.getNm());
        assertNull(place.getRecordStatusCd());
        assertNull(place.getRecordStatusTime());
        assertNull(place.getStatusCd());
        assertNull(place.getStatusTime());
        assertNull(place.getToTime());
        assertNull(place.getUserAffiliationTxt());
        assertNull(place.getStreetAddr1());
        assertNull(place.getStreetAddr2());
        assertNull(place.getCityCd());
        assertNull(place.getCityDescTxt());
        assertNull(place.getStateCd());
        assertNull(place.getZipCd());
        assertNull(place.getCntyCd());
        assertNull(place.getCntryCd());
        assertNull(place.getPhoneNbr());
        assertNull(place.getPhoneCntryCd());
        assertNull(place.getVersionCtrlNbr());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long placeUid = 1L;
        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "cd";
        String cdDescTxt = "cdDesc";
        String description = "description";
        String durationAmt = "durationAmt";
        String durationUnitCd = "durationUnitCd";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "chgReasonCd";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String nm = "name";
        String recordStatusCd = "statusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String userAffiliationTxt = "affiliation";
        String streetAddr1 = "street1";
        String streetAddr2 = "street2";
        String cityCd = "cityCd";
        String cityDescTxt = "cityDesc";
        String stateCd = "stateCd";
        String zipCd = "zipCd";
        String cntyCd = "cntyCd";
        String cntryCd = "cntryCd";
        String phoneNbr = "phoneNbr";
        String phoneCntryCd = "phoneCntryCd";
        Integer versionCtrlNbr = 1;

        PlaceDto placeDto = new PlaceDto();
        placeDto.setPlaceUid(placeUid);
        placeDto.setAddReasonCd(addReasonCd);
        placeDto.setAddTime(addTime);
        placeDto.setAddUserId(addUserId);
        placeDto.setCd(cd);
        placeDto.setCdDescTxt(cdDescTxt);
        placeDto.setDescription(description);
        placeDto.setDurationAmt(durationAmt);
        placeDto.setDurationUnitCd(durationUnitCd);
        placeDto.setFromTime(fromTime);
        placeDto.setLastChgReasonCd(lastChgReasonCd);
        placeDto.setLastChgTime(lastChgTime);
        placeDto.setLastChgUserId(lastChgUserId);
        placeDto.setLocalId(localId);
        placeDto.setNm(nm);
        placeDto.setRecordStatusCd(recordStatusCd);
        placeDto.setRecordStatusTime(recordStatusTime);
        placeDto.setStatusCd(statusCd);
        placeDto.setStatusTime(statusTime);
        placeDto.setToTime(toTime);
        placeDto.setUserAffiliationTxt(userAffiliationTxt);
        placeDto.setVersionCtrlNbr(versionCtrlNbr);

        // Act
        Place place = new Place(placeDto);

        // Assert
        assertEquals(placeUid, place.getPlaceUid());
        assertEquals(addReasonCd, place.getAddReasonCd());
        assertEquals(addTime, place.getAddTime());
        assertEquals(addUserId, place.getAddUserId());
        assertEquals(cd, place.getCd());
        assertEquals(cdDescTxt, place.getCdDescTxt());
        assertEquals(description, place.getDescription());
        assertEquals(durationAmt, place.getDurationAmt());
        assertEquals(durationUnitCd, place.getDurationUnitCd());
        assertEquals(fromTime, place.getFromTime());
        assertEquals(lastChgReasonCd, place.getLastChgReasonCd());
        assertEquals(lastChgTime, place.getLastChgTime());
        assertEquals(lastChgUserId, place.getLastChgUserId());
        assertEquals(localId, place.getLocalId());
        assertEquals(nm, place.getNm());
        assertEquals(recordStatusCd, place.getRecordStatusCd());
        assertEquals(recordStatusTime, place.getRecordStatusTime());
        assertEquals(statusCd, place.getStatusCd());
        assertEquals(statusTime, place.getStatusTime());
        assertEquals(toTime, place.getToTime());
        assertEquals(userAffiliationTxt, place.getUserAffiliationTxt());
//        assertEquals(streetAddr1, place.getStreetAddr1());
//        assertEquals(streetAddr2, place.getStreetAddr2());
//        assertEquals(cityCd, place.getCityCd());
//        assertEquals(cityDescTxt, place.getCityDescTxt());
//        assertEquals(stateCd, place.getStateCd());
//        assertEquals(zipCd, place.getZipCd());
//        assertEquals(cntyCd, place.getCntyCd());
//        assertEquals(cntryCd, place.getCntryCd());
//        assertEquals(phoneNbr, place.getPhoneNbr());
//        assertEquals(phoneCntryCd, place.getPhoneCntryCd());
        assertEquals(versionCtrlNbr, place.getVersionCtrlNbr());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Place place = new Place();

        Long placeUid = 1L;
        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "cd";
        String cdDescTxt = "cdDesc";
        String description = "description";
        String durationAmt = "durationAmt";
        String durationUnitCd = "durationUnitCd";
        Timestamp fromTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "chgReasonCd";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String nm = "name";
        String recordStatusCd = "statusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        Timestamp toTime = new Timestamp(System.currentTimeMillis());
        String userAffiliationTxt = "affiliation";
        String streetAddr1 = "street1";
        String streetAddr2 = "street2";
        String cityCd = "cityCd";
        String cityDescTxt = "cityDesc";
        String stateCd = "stateCd";
        String zipCd = "zipCd";
        String cntyCd = "cntyCd";
        String cntryCd = "cntryCd";
        String phoneNbr = "phoneNbr";
        String phoneCntryCd = "phoneCntryCd";
        Integer versionCtrlNbr = 1;

        // Act
        place.setPlaceUid(placeUid);
        place.setAddReasonCd(addReasonCd);
        place.setAddTime(addTime);
        place.setAddUserId(addUserId);
        place.setCd(cd);
        place.setCdDescTxt(cdDescTxt);
        place.setDescription(description);
        place.setDurationAmt(durationAmt);
        place.setDurationUnitCd(durationUnitCd);
        place.setFromTime(fromTime);
        place.setLastChgReasonCd(lastChgReasonCd);
        place.setLastChgTime(lastChgTime);
        place.setLastChgUserId(lastChgUserId);
        place.setLocalId(localId);
        place.setNm(nm);
        place.setRecordStatusCd(recordStatusCd);
        place.setRecordStatusTime(recordStatusTime);
        place.setStatusCd(statusCd);
        place.setStatusTime(statusTime);
        place.setToTime(toTime);
        place.setUserAffiliationTxt(userAffiliationTxt);
        place.setStreetAddr1(streetAddr1);
        place.setStreetAddr2(streetAddr2);
        place.setCityCd(cityCd);
        place.setCityDescTxt(cityDescTxt);
        place.setStateCd(stateCd);
        place.setZipCd(zipCd);
        place.setCntyCd(cntyCd);
        place.setCntryCd(cntryCd);
        place.setPhoneNbr(phoneNbr);
        place.setPhoneCntryCd(phoneCntryCd);
        place.setVersionCtrlNbr(versionCtrlNbr);

        // Assert
        assertEquals(placeUid, place.getPlaceUid());
        assertEquals(addReasonCd, place.getAddReasonCd());
        assertEquals(addTime, place.getAddTime());
        assertEquals(addUserId, place.getAddUserId());
        assertEquals(cd, place.getCd());
        assertEquals(cdDescTxt, place.getCdDescTxt());
        assertEquals(description, place.getDescription());
        assertEquals(durationAmt, place.getDurationAmt());
        assertEquals(durationUnitCd, place.getDurationUnitCd());
        assertEquals(fromTime, place.getFromTime());
        assertEquals(lastChgReasonCd, place.getLastChgReasonCd());
        assertEquals(lastChgTime, place.getLastChgTime());
        assertEquals(lastChgUserId, place.getLastChgUserId());
        assertEquals(localId, place.getLocalId());
        assertEquals(nm, place.getNm());
        assertEquals(recordStatusCd, place.getRecordStatusCd());
        assertEquals(recordStatusTime, place.getRecordStatusTime());
        assertEquals(statusCd, place.getStatusCd());
        assertEquals(statusTime, place.getStatusTime());
        assertEquals(toTime, place.getToTime());
        assertEquals(userAffiliationTxt, place.getUserAffiliationTxt());
        assertEquals(streetAddr1, place.getStreetAddr1());
        assertEquals(streetAddr2, place.getStreetAddr2());
        assertEquals(cityCd, place.getCityCd());
        assertEquals(cityDescTxt, place.getCityDescTxt());
        assertEquals(stateCd, place.getStateCd());
        assertEquals(zipCd, place.getZipCd());
        assertEquals(cntyCd, place.getCntyCd());
        assertEquals(cntryCd, place.getCntryCd());
        assertEquals(phoneNbr, place.getPhoneNbr());
        assertEquals(phoneCntryCd, place.getPhoneCntryCd());
        assertEquals(versionCtrlNbr, place.getVersionCtrlNbr());
    }
}
