package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.ProgramAreaContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgramAreaContainerTest {

    @Test
    void testGettersAndSetters() {
        ProgramAreaContainer programAreaContainer = new ProgramAreaContainer();

        programAreaContainer.setConditionCd("cond123");
        programAreaContainer.setConditionShortNm("ShortName");
        programAreaContainer.setStateProgAreaCode("AreaCode");
        programAreaContainer.setStateProgAreaCdDesc("AreaDesc");
        programAreaContainer.setInvestigationFormCd("FormCd");

        assertEquals("cond123", programAreaContainer.getConditionCd());
        assertEquals("ShortName", programAreaContainer.getConditionShortNm());
        assertEquals("AreaCode", programAreaContainer.getStateProgAreaCode());
        assertEquals("AreaDesc", programAreaContainer.getStateProgAreaCdDesc());
        assertEquals("FormCd", programAreaContainer.getInvestigationFormCd());
    }

    @Test
    void testCompareTo() {
        ProgramAreaContainer pac1 = new ProgramAreaContainer();
        pac1.setConditionShortNm("ShortName1");

        ProgramAreaContainer pac2 = new ProgramAreaContainer();
        pac2.setConditionShortNm("ShortName2");

        assertEquals(-1, pac1.compareTo(pac2));
        assertEquals(1, pac2.compareTo(pac1));

        pac2.setConditionShortNm("ShortName1");
        assertEquals(0, pac1.compareTo(pac2));
    }
}
