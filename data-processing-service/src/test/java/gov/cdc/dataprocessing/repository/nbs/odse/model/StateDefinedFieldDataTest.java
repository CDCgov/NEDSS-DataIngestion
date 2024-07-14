package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.StateDefinedFieldData;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StateDefinedFieldDataTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        StateDefinedFieldData stateDefinedFieldData = new StateDefinedFieldData();
        Long ldfUid = 1L;
        Long businessObjectUid = 2L;
        Date addTime = new Date();
        String businessObjectName = "BusinessObjectName";
        Date lastChangeTime = new Date();
        String ldfValue = "LdfValue";
        Short versionControlNumber = 1;

        // Act
        stateDefinedFieldData.setLdfUid(ldfUid);
        stateDefinedFieldData.setBusinessObjectUid(businessObjectUid);
        stateDefinedFieldData.setAddTime(addTime);
        stateDefinedFieldData.setBusinessObjectName(businessObjectName);
        stateDefinedFieldData.setLastChangeTime(lastChangeTime);
        stateDefinedFieldData.setLdfValue(ldfValue);
        stateDefinedFieldData.setVersionControlNumber(versionControlNumber);

        // Assert
        assertEquals(ldfUid, stateDefinedFieldData.getLdfUid());
        assertEquals(businessObjectUid, stateDefinedFieldData.getBusinessObjectUid());
        assertEquals(addTime, stateDefinedFieldData.getAddTime());
        assertEquals(businessObjectName, stateDefinedFieldData.getBusinessObjectName());
        assertEquals(lastChangeTime, stateDefinedFieldData.getLastChangeTime());
        assertEquals(ldfValue, stateDefinedFieldData.getLdfValue());
        assertEquals(versionControlNumber, stateDefinedFieldData.getVersionControlNumber());
    }

    @Test
    void testDefaultValues() {
        // Arrange
        StateDefinedFieldData stateDefinedFieldData = new StateDefinedFieldData();

        // Assert
        assertNull(stateDefinedFieldData.getLdfUid());
        assertNull(stateDefinedFieldData.getBusinessObjectUid());
        assertNull(stateDefinedFieldData.getAddTime());
        assertNull(stateDefinedFieldData.getBusinessObjectName());
        assertNull(stateDefinedFieldData.getLastChangeTime());
        assertNull(stateDefinedFieldData.getLdfValue());
        assertNull(stateDefinedFieldData.getVersionControlNumber());
    }
}
