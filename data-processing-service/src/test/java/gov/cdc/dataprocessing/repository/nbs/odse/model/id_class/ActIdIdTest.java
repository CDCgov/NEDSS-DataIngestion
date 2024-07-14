package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActIdIdTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        ActIdId actIdId = new ActIdId();
        Long actUid = 123L;
        Integer actIdSeq = 456;

        // Act
        actIdId.setActUid(actUid);
        actIdId.setActIdSeq(actIdSeq);

        // Assert
        assertEquals(actUid, actIdId.getActUid());
        assertEquals(actIdSeq, actIdId.getActIdSeq());
    }

    @Test
    void testDefaultValues() {
        // Arrange
        ActIdId actIdId = new ActIdId();

        // Assert
        assertNull(actIdId.getActUid());
        assertNull(actIdId.getActIdSeq());
    }

}
