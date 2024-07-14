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


}
