package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.PageContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageContainerTest {

    @Test
    void testGettersAndSetters() {
        PageContainer pageContainer = new PageContainer();

        boolean isCurrInvestgtrDynamic = true;
        pageContainer.setCurrInvestgtrDynamic(isCurrInvestgtrDynamic);

        assertEquals(isCurrInvestgtrDynamic, pageContainer.isCurrInvestgtrDynamic());
    }
}
