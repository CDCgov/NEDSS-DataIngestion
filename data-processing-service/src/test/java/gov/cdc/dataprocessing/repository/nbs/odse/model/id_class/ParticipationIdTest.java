package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipationIdTest {

    @Test
    public void testGettersAndSetters() {
        ParticipationId id = new ParticipationId();
        id.setSubjectEntityUid(1L);
        id.setActUid(2L);
        id.setTypeCode("testType");

        assertEquals(1L, id.getSubjectEntityUid());
        assertEquals(2L, id.getActUid());
        assertEquals("testType", id.getTypeCode());
    }

    @Test
    public void testEqualsAndHashCode() {
        ParticipationId id1 = new ParticipationId();
        id1.setSubjectEntityUid(1L);
        id1.setActUid(2L);
        id1.setTypeCode("testType");

        ParticipationId id2 = new ParticipationId();
        id2.setSubjectEntityUid(1L);
        id2.setActUid(2L);
        id2.setTypeCode("testType");

        ParticipationId id3 = new ParticipationId();
        id3.setSubjectEntityUid(1L);
        id3.setActUid(2L);
        id3.setTypeCode("differentType");

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
