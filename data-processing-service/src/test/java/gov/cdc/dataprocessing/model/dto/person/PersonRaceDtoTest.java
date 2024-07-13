package gov.cdc.dataprocessing.model.dto.person;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class PersonRaceDtoTest {

    @Test
    void testGettersAndSetters() {
        PersonRaceDto dto = new PersonRaceDto();

        // Set values
        dto.setPersonUid(1L);
        dto.setRaceCd("RaceCd");
        dto.setAddReasonCd("AddReasonCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setRaceCategoryCd("RaceCategoryCd");
        dto.setRaceDescTxt("RaceDescTxt");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setUserAffiliationTxt("UserAffiliationTxt");
        dto.setProgAreaCd("ProgAreaCd");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setProgramJurisdictionOid(4L);
        dto.setSharedInd("SharedInd");
        dto.setVersionCtrlNbr(5);
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setStatusCd("StatusCd");
        dto.setLocalId("LocalId");

        // Assert values
        assertEquals(1L, dto.getPersonUid());
        assertEquals("RaceCd", dto.getRaceCd());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertNotNull(dto.getAsOfDate());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("RaceCategoryCd", dto.getRaceCategoryCd());
        assertEquals("RaceDescTxt", dto.getRaceDescTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertEquals(4L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(5, dto.getVersionCtrlNbr());
        assertNotNull(dto.getStatusTime());
        assertEquals("StatusCd", dto.getStatusCd());
        assertEquals("LocalId", dto.getLocalId());
    }

    @Test
    void testSpecialConstructor() {
        PersonRace personRace = new PersonRace();
        personRace.setPersonUid(1L);
        personRace.setRaceCd("RaceCd");
        personRace.setAddReasonCd("AddReasonCd");
        personRace.setAddTime(new Timestamp(System.currentTimeMillis()));
        personRace.setAddUserId(2L);
        personRace.setLastChgReasonCd("LastChgReasonCd");
        personRace.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        personRace.setLastChgUserId(3L);
        personRace.setRaceCategoryCd("RaceCategoryCd");
        personRace.setRaceDescTxt("RaceDescTxt");
        personRace.setRecordStatusCd("RecordStatusCd");
        personRace.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        personRace.setUserAffiliationTxt("UserAffiliationTxt");
        personRace.setAsOfDate(new Timestamp(System.currentTimeMillis()));

        PersonRaceDto dto = new PersonRaceDto(personRace);

        // Assert values
        assertEquals(1L, dto.getPersonUid());
        assertEquals("RaceCd", dto.getRaceCd());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertNotNull(dto.getAsOfDate());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("RaceCategoryCd", dto.getRaceCategoryCd());
        assertEquals("RaceDescTxt", dto.getRaceDescTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
    }

    @Test
    void testOverriddenMethods() {
        PersonRaceDto dto = new PersonRaceDto();

        // Test overridden methods
        dto.setPersonUid(1L);
        assertEquals(1L, dto.getUid());
        assertEquals("Entity", dto.getSuperclass());

        // These methods do not perform any operation
        dto.setLastChgUserId(2L);
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLocalId("LocalId");
        dto.setAddUserId(2L);
        dto.setLastChgReasonCd("ReasonCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));

        // Assert that no changes occurred due to no-operation methods
        assertNotNull(dto.getLastChgUserId());
        assertNotNull(dto.getLastChgTime());
        assertNotNull(dto.getLocalId());
        assertNotNull(dto.getAddUserId());
        assertNotNull(dto.getLastChgReasonCd());
        assertNotNull(dto.getRecordStatusTime());
        assertNotNull(dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertNotNull(dto.getAddTime());
    }
}
