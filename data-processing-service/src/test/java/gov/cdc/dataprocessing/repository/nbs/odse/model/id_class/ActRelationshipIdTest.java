package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActRelationshipIdTest {

    @Test
    public void testGettersAndSetters() {
        ActRelationshipId id = new ActRelationshipId();
        id.setSourceActUid(1L);
        id.setTargetActUid(2L);
        id.setTypeCd("type");

        assertEquals(1L, id.getSourceActUid());
        assertEquals(2L, id.getTargetActUid());
        assertEquals("type", id.getTypeCd());
    }

    @Test
    public void testEqualsAndHashCode() {
        ActRelationshipId id1 = new ActRelationshipId();
        id1.setSourceActUid(1L);
        id1.setTargetActUid(2L);
        id1.setTypeCd("type");

        ActRelationshipId id2 = new ActRelationshipId();
        id2.setSourceActUid(1L);
        id2.setTargetActUid(2L);
        id2.setTypeCd("type");

        ActRelationshipId id3 = new ActRelationshipId();
        id3.setSourceActUid(1L);
        id3.setTargetActUid(3L);
        id3.setTypeCd("type");

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
