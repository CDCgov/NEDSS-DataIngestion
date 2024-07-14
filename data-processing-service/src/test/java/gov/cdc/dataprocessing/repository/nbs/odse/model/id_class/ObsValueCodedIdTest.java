package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObsValueCodedIdTest {

    @Test
    public void testGettersAndSetters() {
        ObsValueCodedId id = new ObsValueCodedId();
        id.setObservationUid(1L);
        id.setCode("testCode");

        assertEquals(1L, id.getObservationUid());
        assertEquals("testCode", id.getCode());
    }

    @Test
    public void testEqualsAndHashCode() {
        ObsValueCodedId id1 = new ObsValueCodedId();
        id1.setObservationUid(1L);
        id1.setCode("testCode");

        ObsValueCodedId id2 = new ObsValueCodedId();
        id2.setObservationUid(1L);
        id2.setCode("testCode");

        ObsValueCodedId id3 = new ObsValueCodedId();
        id3.setObservationUid(1L);
        id3.setCode("differentCode");

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
