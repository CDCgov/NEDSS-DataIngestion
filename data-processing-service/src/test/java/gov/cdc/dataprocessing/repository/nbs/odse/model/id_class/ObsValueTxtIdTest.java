package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObsValueTxtIdTest {

    @Test
    public void testGettersAndSetters() {
        ObsValueTxtId id = new ObsValueTxtId();
        id.setObservationUid(1L);
        id.setObsValueTxtSeq(2);

        assertEquals(1L, id.getObservationUid());
        assertEquals(2, id.getObsValueTxtSeq());
    }

    @Test
    public void testEqualsAndHashCode() {
        ObsValueTxtId id1 = new ObsValueTxtId();
        id1.setObservationUid(1L);
        id1.setObsValueTxtSeq(2);

        ObsValueTxtId id2 = new ObsValueTxtId();
        id2.setObservationUid(1L);
        id2.setObsValueTxtSeq(2);

        ObsValueTxtId id3 = new ObsValueTxtId();
        id3.setObservationUid(1L);
        id3.setObsValueTxtSeq(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
