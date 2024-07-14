package gov.cdc.dataprocessing.repository.nbs.srte.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JurisdictionParticipationIdTest {

    @Test
    void testGettersAndSetters() {
        JurisdictionParticipationId id = new JurisdictionParticipationId();

        // Set values
        id.setJurisdictionCd("JurisdictionCd");
        id.setFipsCd("FipsCd");
        id.setTypeCd("TypeCd");

        // Assert values
        assertEquals("JurisdictionCd", id.getJurisdictionCd());
        assertEquals("FipsCd", id.getFipsCd());
        assertEquals("TypeCd", id.getTypeCd());
    }

    @Test
    void testEqualsAndHashCode() {
        JurisdictionParticipationId id1 = new JurisdictionParticipationId();
        id1.setJurisdictionCd("JurisdictionCd");
        id1.setFipsCd("FipsCd");
        id1.setTypeCd("TypeCd");

        JurisdictionParticipationId id2 = new JurisdictionParticipationId();
        id2.setJurisdictionCd("JurisdictionCd");
        id2.setFipsCd("FipsCd");
        id2.setTypeCd("TypeCd");

        JurisdictionParticipationId id3 = new JurisdictionParticipationId();
        id3.setJurisdictionCd("DifferentJurisdictionCd");
        id3.setFipsCd("DifferentFipsCd");
        id3.setTypeCd("DifferentTypeCd");


        assertNotEquals(id1, id3);
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }

    @Test
    void testToString() {
        JurisdictionParticipationId id = new JurisdictionParticipationId();
        id.setJurisdictionCd("JurisdictionCd");
        id.setFipsCd("FipsCd");
        id.setTypeCd("TypeCd");

        assertNotNull(id.toString());
    }
}
