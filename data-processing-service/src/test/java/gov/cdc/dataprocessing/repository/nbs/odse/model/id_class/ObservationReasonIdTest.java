package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObservationReasonIdTest {

    @Test
    public void testGettersAndSetters() {
        ObservationReasonId id = new ObservationReasonId();
        id.setObservationUid(1L);
        id.setReasonCd("reason");

        assertEquals(1L, id.getObservationUid());
        assertEquals("reason", id.getReasonCd());
    }

    @Test
    public void testEqualsAndHashCode() {
        ObservationReasonId id1 = new ObservationReasonId();
        id1.setObservationUid(1L);
        id1.setReasonCd("reason");

        ObservationReasonId id2 = new ObservationReasonId();
        id2.setObservationUid(1L);
        id2.setReasonCd("reason");

        ObservationReasonId id3 = new ObservationReasonId();
        id3.setObservationUid(1L);
        id3.setReasonCd("differentReason");

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
