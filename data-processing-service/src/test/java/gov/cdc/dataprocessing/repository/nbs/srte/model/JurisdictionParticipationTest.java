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

    @Test
    void testEqualsAndHashCode() {
        JurisdictionParticipation jurisdictionParticipation1 = new JurisdictionParticipation();
        jurisdictionParticipation1.setJurisdictionCd("JurisdictionCd");
        jurisdictionParticipation1.setFipsCd("FipsCd");
        jurisdictionParticipation1.setTypeCd("TypeCd");

        JurisdictionParticipation jurisdictionParticipation2 = new JurisdictionParticipation();
        jurisdictionParticipation2.setJurisdictionCd("JurisdictionCd");
        jurisdictionParticipation2.setFipsCd("FipsCd");
        jurisdictionParticipation2.setTypeCd("TypeCd");

        JurisdictionParticipation jurisdictionParticipation3 = new JurisdictionParticipation();
        jurisdictionParticipation3.setJurisdictionCd("DifferentJurisdictionCd");
        jurisdictionParticipation3.setFipsCd("DifferentFipsCd");
        jurisdictionParticipation3.setTypeCd("DifferentTypeCd");

        // Assert equals and hashCode
        assertEquals(jurisdictionParticipation1, jurisdictionParticipation2);
        assertEquals(jurisdictionParticipation1.hashCode(), jurisdictionParticipation2.hashCode());

        assertNotEquals(jurisdictionParticipation1, jurisdictionParticipation3);
        assertNotEquals(jurisdictionParticipation1.hashCode(), jurisdictionParticipation3.hashCode());
    }

    @Test
    void testToString() {
        JurisdictionParticipation jurisdictionParticipation = new JurisdictionParticipation();
        jurisdictionParticipation.setJurisdictionCd("JurisdictionCd");
        jurisdictionParticipation.setFipsCd("FipsCd");
        jurisdictionParticipation.setTypeCd("TypeCd");
        jurisdictionParticipation.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        jurisdictionParticipation.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));

        String expectedString = "JurisdictionParticipation(jurisdictionCd=JurisdictionCd, fipsCd=FipsCd, typeCd=TypeCd, effectiveFromTime=" + jurisdictionParticipation.getEffectiveFromTime() + ", effectiveToTime=" + jurisdictionParticipation.getEffectiveToTime() + ")";
        assertEquals(expectedString, jurisdictionParticipation.toString());
    }
}
