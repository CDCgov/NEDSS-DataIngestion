package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObservationInterpIdTest {

    @Test
    public void testGettersAndSetters() {
        ObservationInterpId id = new ObservationInterpId();
        id.setObservationUid(1L);
        id.setInterpretationCd("testCd");

        assertEquals(1L, id.getObservationUid());
        assertEquals("testCd", id.getInterpretationCd());
    }

    @Test
    public void testEqualsAndHashCode() {
        ObservationInterpId id1 = new ObservationInterpId();
        id1.setObservationUid(1L);
        id1.setInterpretationCd("testCd");

        ObservationInterpId id2 = new ObservationInterpId();
        id2.setObservationUid(1L);
        id2.setInterpretationCd("testCd");

        ObservationInterpId id3 = new ObservationInterpId();
        id3.setObservationUid(1L);
        id3.setInterpretationCd("differentCd");

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
