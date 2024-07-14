package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NNDActivityLogIdTest {

    @Test
    public void testGettersAndSetters() {
        NNDActivityLogId id = new NNDActivityLogId();
        id.setNndActivityLogUid(1L);
        id.setNndActivityLogSeq(2);

        assertEquals(1L, id.getNndActivityLogUid());
        assertEquals(2, id.getNndActivityLogSeq());
    }

    @Test
    public void testEqualsAndHashCode() {
        NNDActivityLogId id1 = new NNDActivityLogId();
        id1.setNndActivityLogUid(1L);
        id1.setNndActivityLogSeq(2);

        NNDActivityLogId id2 = new NNDActivityLogId();
        id2.setNndActivityLogUid(1L);
        id2.setNndActivityLogSeq(2);

        NNDActivityLogId id3 = new NNDActivityLogId();
        id3.setNndActivityLogUid(1L);
        id3.setNndActivityLogSeq(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
