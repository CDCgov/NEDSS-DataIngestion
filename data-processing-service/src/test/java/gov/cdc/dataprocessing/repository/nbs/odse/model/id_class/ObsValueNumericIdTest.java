package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObsValueNumericIdTest {

    @Test
    public void testGettersAndSetters() {
        ObsValueNumericId id = new ObsValueNumericId();
        id.setObservationUid(1L);
        id.setObsValueNumericSeq(2);

        assertEquals(1L, id.getObservationUid());
        assertEquals(2, id.getObsValueNumericSeq());
    }

    @Test
    public void testEqualsAndHashCode() {
        ObsValueNumericId id1 = new ObsValueNumericId();
        id1.setObservationUid(1L);
        id1.setObsValueNumericSeq(2);

        ObsValueNumericId id2 = new ObsValueNumericId();
        id2.setObservationUid(1L);
        id2.setObsValueNumericSeq(2);

        ObsValueNumericId id3 = new ObsValueNumericId();
        id3.setObservationUid(1L);
        id3.setObsValueNumericSeq(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
