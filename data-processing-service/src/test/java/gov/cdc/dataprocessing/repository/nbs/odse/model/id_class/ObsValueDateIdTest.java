package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObsValueDateIdTest {

    @Test
    public void testGettersAndSetters() {
        ObsValueDateId id = new ObsValueDateId();
        id.setObservationUid(1L);
        id.setObsValueDateSeq(2);

        assertEquals(1L, id.getObservationUid());
        assertEquals(2, id.getObsValueDateSeq());
    }

    @Test
    public void testEqualsAndHashCode() {
        ObsValueDateId id1 = new ObsValueDateId();
        id1.setObservationUid(1L);
        id1.setObsValueDateSeq(2);

        ObsValueDateId id2 = new ObsValueDateId();
        id2.setObservationUid(1L);
        id2.setObsValueDateSeq(2);

        ObsValueDateId id3 = new ObsValueDateId();
        id3.setObservationUid(1L);
        id3.setObsValueDateSeq(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
