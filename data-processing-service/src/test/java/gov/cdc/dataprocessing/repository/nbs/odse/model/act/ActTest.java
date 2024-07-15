package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActTest {

    @Test
    void testGettersAndSetters() {
        Act act = new Act();

        // Set values
        act.setActUid(1L);
        act.setClassCode("Class123");
        act.setMoodCode("Mood123");

        // Assert values
        assertEquals(1L, act.getActUid());
        assertEquals("Class123", act.getClassCode());
        assertEquals("Mood123", act.getMoodCode());
    }

    @Test
    void testNoArgsConstructor() {
        Act act = new Act();

        assertNotNull(act);
        assertNull(act.getActUid());
        assertNull(act.getClassCode());
        assertNull(act.getMoodCode());
    }
}
