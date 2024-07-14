package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipationHistIdTest {

    @Test
    public void testGettersAndSetters() {
        ParticipationHistId id = new ParticipationHistId();
        id.setSubjectEntityUid(1L);
        id.setActUid(2L);
        id.setTypeCd("testType");
        id.setVersionCtrlNbr(3);

        assertEquals(1L, id.getSubjectEntityUid());
        assertEquals(2L, id.getActUid());
        assertEquals("testType", id.getTypeCd());
        assertEquals(3, id.getVersionCtrlNbr());
    }

    @Test
    public void testEqualsAndHashCode() {
        ParticipationHistId id1 = new ParticipationHistId();
        id1.setSubjectEntityUid(1L);
        id1.setActUid(2L);
        id1.setTypeCd("testType");
        id1.setVersionCtrlNbr(3);

        ParticipationHistId id2 = new ParticipationHistId();
        id2.setSubjectEntityUid(1L);
        id2.setActUid(2L);
        id2.setTypeCd("testType");
        id2.setVersionCtrlNbr(3);

        ParticipationHistId id3 = new ParticipationHistId();
        id3.setSubjectEntityUid(1L);
        id3.setActUid(2L);
        id3.setTypeCd("differentType");
        id3.setVersionCtrlNbr(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
