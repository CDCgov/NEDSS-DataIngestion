package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LOINCCodeTest {

    @Test
    void testGettersAndSetters() {
        LOINCCode loincCode = new LOINCCode();

        // Set values
        loincCode.setLoincCode("LOINCCode");
        loincCode.setComponentName("ComponentName");
        loincCode.setProperty("Property");
        loincCode.setTimeAspect("TimeAspect");
        loincCode.setSystemCode("SystemCode");
        loincCode.setScaleType("ScaleType");
        loincCode.setMethodType("MethodType");
        loincCode.setDisplayName("DisplayName");
        loincCode.setNbsUid(123L);
        loincCode.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        loincCode.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        loincCode.setRelatedClassCode("RelatedClassCode");
        loincCode.setPaDerivationExcludeCode("ExcludeCode");

        // Assert values
        assertEquals("LOINCCode", loincCode.getLoincCode());
        assertEquals("ComponentName", loincCode.getComponentName());
        assertEquals("Property", loincCode.getProperty());
        assertEquals("TimeAspect", loincCode.getTimeAspect());
        assertEquals("SystemCode", loincCode.getSystemCode());
        assertEquals("ScaleType", loincCode.getScaleType());
        assertEquals("MethodType", loincCode.getMethodType());
        assertEquals("DisplayName", loincCode.getDisplayName());
        assertEquals(123L, loincCode.getNbsUid());
        assertNotNull(loincCode.getEffectiveFromTime());
        assertNotNull(loincCode.getEffectiveToTime());
        assertEquals("RelatedClassCode", loincCode.getRelatedClassCode());
        assertEquals("ExcludeCode", loincCode.getPaDerivationExcludeCode());
    }


}
