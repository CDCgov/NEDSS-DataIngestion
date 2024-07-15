package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.LdfBaseContainer;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LdfBaseContainerTest {

    @Test
    void testGettersAndSetters() {
        LdfBaseContainer container = new LdfBaseContainer();

        // Test default values
        assertNull(container.getLdfUids());
        assertNull(container.getTheStateDefinedFieldDataDTCollection());

        // Prepare a list of StateDefinedFieldDataDto
        List<StateDefinedFieldDataDto> stateDefinedFieldDataDtoList = new ArrayList<>();
        StateDefinedFieldDataDto dto1 = new StateDefinedFieldDataDto();
        dto1.setLdfUid(1L);
        dto1.setLdfValue("Value1");

        StateDefinedFieldDataDto dto2 = new StateDefinedFieldDataDto();
        dto2.setLdfUid(2L);
        dto2.setLdfValue("Value2");

        StateDefinedFieldDataDto dto3 = new StateDefinedFieldDataDto();
        dto3.setLdfUid(3L);
        dto3.setLdfValue(null); // This should be discarded

        stateDefinedFieldDataDtoList.add(dto1);
        stateDefinedFieldDataDtoList.add(dto2);
        stateDefinedFieldDataDtoList.add(dto3);

        // Set the list
        container.setTheStateDefinedFieldDataDTCollection(stateDefinedFieldDataDtoList);

        // Verify that the ldfs collection contains only dto1 and dto2
        Collection<Object> ldfs = container.getTheStateDefinedFieldDataDTCollection();
        assertNotNull(ldfs);
        assertEquals(2, ldfs.size());
        assertTrue(ldfs.contains(dto1));
        assertTrue(ldfs.contains(dto2));
        assertFalse(ldfs.contains(dto3));

        // Verify that the ldfUids collection contains all UIDs
        Collection<Object> ldfUids = container.getLdfUids();
        assertNotNull(ldfUids);
        assertEquals(3, ldfUids.size());
        assertTrue(ldfUids.contains(1L));
        assertTrue(ldfUids.contains(2L));
        assertTrue(ldfUids.contains(3L));
    }
}
