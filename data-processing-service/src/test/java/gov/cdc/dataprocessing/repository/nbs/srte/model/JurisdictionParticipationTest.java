package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class JurisdictionParticipationTest {

    @Test
    void testGettersAndSetters() {
        JurisdictionParticipation jurisdictionParticipation = new JurisdictionParticipation();

        // Set values
        jurisdictionParticipation.setJurisdictionCd("JurisdictionCd");
        jurisdictionParticipation.setFipsCd("FipsCd");
        jurisdictionParticipation.setTypeCd("TypeCd");
        jurisdictionParticipation.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        jurisdictionParticipation.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));

        // Assert values
        assertEquals("JurisdictionCd", jurisdictionParticipation.getJurisdictionCd());
        assertEquals("FipsCd", jurisdictionParticipation.getFipsCd());
        assertEquals("TypeCd", jurisdictionParticipation.getTypeCd());
        assertNotNull(jurisdictionParticipation.getEffectiveFromTime());
        assertNotNull(jurisdictionParticipation.getEffectiveToTime());
    }


}
