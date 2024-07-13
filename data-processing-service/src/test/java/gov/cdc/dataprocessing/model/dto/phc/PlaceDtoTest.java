package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.Place;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class PlaceDtoTest {

    @Test
    void testGettersAndSetters() {
        PlaceDto dto = new PlaceDto();

        // Set values
        dto.setPlaceUid(1L);
        dto.setAddReasonCd("AddReasonCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setCd("Cd");
        dto.setCdDescTxt("CdDescTxt");
        dto.setDescription("Description");
        dto.setPlaceContact("PlaceContact");
        dto.setPlaceUrl("PlaceUrl");
        dto.setPlaceAppNm("PlaceAppNm");
        dto.setDurationAmt("DurationAmt");
        dto.setDurationUnitCd("DurationUnitCd");
        dto.setFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setLocalId("LocalId");
        dto.setNm("Nm");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setToTime(new Timestamp(System.currentTimeMillis()));
        dto.setUserAffiliationTxt("UserAffiliationTxt");
        dto.setVersionCtrlNbr(4);
        dto.setProgAreaCd("ProgAreaCd");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setProgramJurisdictionOid(5L);
        dto.setSharedInd("SharedInd");

        // Assert values
        assertEquals(1L, dto.getPlaceUid());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("Description", dto.getDescription());
        assertEquals("PlaceContact", dto.getPlaceContact());
        assertEquals("PlaceUrl", dto.getPlaceUrl());
        assertEquals("PlaceAppNm", dto.getPlaceAppNm());
        assertEquals("DurationAmt", dto.getDurationAmt());
        assertEquals("DurationUnitCd", dto.getDurationUnitCd());
        assertNotNull(dto.getFromTime());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("Nm", dto.getNm());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertNotNull(dto.getToTime());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(4, dto.getVersionCtrlNbr());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
    }

    @Test
    void testSpecialConstructor() {
        Place place = new Place();
        place.setPlaceUid(1L);
        place.setAddReasonCd("AddReasonCd");
        place.setAddTime(new Timestamp(System.currentTimeMillis()));
        place.setAddUserId(2L);
        place.setCd("Cd");
        place.setCdDescTxt("CdDescTxt");
        place.setDescription("Description");
        place.setStreetAddr1("StreetAddr1");
        place.setStreetAddr2("StreetAddr2");
        place.setCityCd("CityCd");
        place.setStateCd("StateCd");
        place.setZipCd("ZipCd");
        place.setCntryCd("CntryCd");
        place.setDurationAmt("DurationAmt");
        place.setDurationUnitCd("DurationUnitCd");
        place.setFromTime(new Timestamp(System.currentTimeMillis()));
        place.setLastChgReasonCd("LastChgReasonCd");
        place.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        place.setLastChgUserId(3L);
        place.setLocalId("LocalId");
        place.setNm("Nm");
        place.setRecordStatusCd("RecordStatusCd");
        place.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        place.setStatusCd("StatusCd");
        place.setStatusTime(new Timestamp(System.currentTimeMillis()));
        place.setToTime(new Timestamp(System.currentTimeMillis()));
        place.setUserAffiliationTxt("UserAffiliationTxt");
        place.setVersionCtrlNbr(4);

        PlaceDto dto = new PlaceDto(place);

        // Assert values
        assertEquals(1L, dto.getPlaceUid());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("Description", dto.getDescription());
        assertEquals("StreetAddr1 StreetAddr2, CityCd, StateCd ZipCd, CntryCd", dto.getPlaceContact());
        assertEquals("DurationAmt", dto.getDurationAmt());
        assertEquals("DurationUnitCd", dto.getDurationUnitCd());
        assertNotNull(dto.getFromTime());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("Nm", dto.getNm());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertNotNull(dto.getToTime());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(4, dto.getVersionCtrlNbr());
    }

    @Test
    void testOverriddenMethods() {
        PlaceDto dto = new PlaceDto();

        // Test overridden methods
        assertEquals(dto.getPlaceUid(), dto.getUid());
        assertNull( dto.getSuperclass());
        assertNull(dto.getLastChgUserId());
        assertNull(dto.getJurisdictionCd());
        assertNull(dto.getProgAreaCd());
        assertNull(dto.getLastChgTime());
        assertNull(dto.getLocalId());
        assertNull(dto.getAddUserId());
        assertNull(dto.getLastChgReasonCd());
        assertNull(dto.getRecordStatusCd());
        assertNull(dto.getRecordStatusTime());
        assertNull(dto.getStatusCd());
        assertNull(dto.getStatusTime());
        assertNull(dto.getUid());
        assertNull(dto.getAddTime());
        assertNull(dto.getProgramJurisdictionOid());
        assertNull(dto.getSharedInd());
        assertNull(dto.getVersionCtrlNbr());
    }
}
