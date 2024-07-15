package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityLocatorParticipationIdTest {

    @Test
    public void testGettersAndSetters() {
        EntityLocatorParticipationId id = new EntityLocatorParticipationId();
        id.setEntityUid(1L);
        id.setLocatorUid(2L);

        assertEquals(1L, id.getEntityUid());
        assertEquals(2L, id.getLocatorUid());
    }

    @Test
    public void testEqualsAndHashCode() {
        EntityLocatorParticipationId id1 = new EntityLocatorParticipationId();
        id1.setEntityUid(1L);
        id1.setLocatorUid(2L);

        EntityLocatorParticipationId id2 = new EntityLocatorParticipationId();
        id2.setEntityUid(1L);
        id2.setLocatorUid(2L);

        EntityLocatorParticipationId id3 = new EntityLocatorParticipationId();
        id3.setEntityUid(1L);
        id3.setLocatorUid(3L);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
