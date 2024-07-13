package gov.cdc.dataprocessing.repository.nbs.srte.model;

import gov.cdc.dataprocessing.model.container.model.ProgramAreaContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConditionCodeWithPATest {

    @Test
    void testGettersAndSetters() {
        ConditionCodeWithPA conditionCodeWithPA = new ConditionCodeWithPA();

        // Set values
        conditionCodeWithPA.setStateProgAreaCode("StateProgAreaCode");
        conditionCodeWithPA.setStateProgAreaCdDesc("StateProgAreaCdDesc");

        // Assert values
        assertEquals("StateProgAreaCode", conditionCodeWithPA.getStateProgAreaCode());
        assertEquals("StateProgAreaCdDesc", conditionCodeWithPA.getStateProgAreaCdDesc());
    }

    @Test
    void testCompareTo() {
        ConditionCodeWithPA conditionCodeWithPA1 = new ConditionCodeWithPA();
        conditionCodeWithPA1.setConditionShortNm("ShortNameA");

        ProgramAreaContainer programAreaContainer = new ProgramAreaContainer();
        programAreaContainer.setConditionShortNm("ShortNameB");

        // Assert comparison
        assertTrue(conditionCodeWithPA1.compareTo(programAreaContainer) < 0);

        conditionCodeWithPA1.setConditionShortNm("ShortNameB");
        assertEquals(0, conditionCodeWithPA1.compareTo(programAreaContainer));

        conditionCodeWithPA1.setConditionShortNm("ShortNameC");
        assertTrue(conditionCodeWithPA1.compareTo(programAreaContainer) > 0);
    }

    @Test
    void testEqualsAndHashCode() {
        ConditionCodeWithPA conditionCodeWithPA1 = new ConditionCodeWithPA();
        conditionCodeWithPA1.setConditionCd("ConditionCd");
        conditionCodeWithPA1.setStateProgAreaCode("StateProgAreaCode");

        ConditionCodeWithPA conditionCodeWithPA2 = new ConditionCodeWithPA();
        conditionCodeWithPA2.setConditionCd("ConditionCd");
        conditionCodeWithPA2.setStateProgAreaCode("StateProgAreaCode");

        ConditionCodeWithPA conditionCodeWithPA3 = new ConditionCodeWithPA();
        conditionCodeWithPA3.setConditionCd("DifferentConditionCd");
        conditionCodeWithPA3.setStateProgAreaCode("DifferentStateProgAreaCode");

        // Assert equals and hashCode
        assertEquals(conditionCodeWithPA1, conditionCodeWithPA2);
        assertEquals(conditionCodeWithPA1.hashCode(), conditionCodeWithPA2.hashCode());

        assertNotEquals(conditionCodeWithPA1, conditionCodeWithPA3);
        assertNotEquals(conditionCodeWithPA1.hashCode(), conditionCodeWithPA3.hashCode());
    }

    @Test
    void testToString() {
        ConditionCodeWithPA conditionCodeWithPA = new ConditionCodeWithPA();
        conditionCodeWithPA.setConditionCd("ConditionCd");
        conditionCodeWithPA.setStateProgAreaCode("StateProgAreaCode");
        conditionCodeWithPA.setStateProgAreaCdDesc("StateProgAreaCdDesc");

        String expectedString = "ConditionCodeWithPA(stateProgAreaCode=StateProgAreaCode, stateProgAreaCdDesc=StateProgAreaCdDesc)";
        assertTrue(conditionCodeWithPA.toString().contains(expectedString));
    }
}
