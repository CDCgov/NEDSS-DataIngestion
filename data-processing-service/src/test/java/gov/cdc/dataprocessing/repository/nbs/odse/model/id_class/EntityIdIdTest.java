package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityIdIdTest {

    @Test
    public void testGettersAndSetters() {
        EntityIdId id = new EntityIdId();
        id.setEntityUid(1L);
        id.setEntityIdSeq(2);

        assertEquals(1L, id.getEntityUid());
        assertEquals(2, id.getEntityIdSeq());
    }

    @Test
    public void testEqualsAndHashCode() {
        EntityIdId id1 = new EntityIdId();
        id1.setEntityUid(1L);
        id1.setEntityIdSeq(2);

        EntityIdId id2 = new EntityIdId();
        id2.setEntityUid(1L);
        id2.setEntityIdSeq(2);

        EntityIdId id3 = new EntityIdId();
        id3.setEntityUid(1L);
        id3.setEntityIdSeq(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
