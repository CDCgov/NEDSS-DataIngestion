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

    @Test
    void testEqualsAndHashCode() {
        LOINCCode loincCode1 = new LOINCCode();
        loincCode1.setLoincCode("LOINCCode");
        loincCode1.setComponentName("ComponentName");

        LOINCCode loincCode2 = new LOINCCode();
        loincCode2.setLoincCode("LOINCCode");
        loincCode2.setComponentName("ComponentName");

        LOINCCode loincCode3 = new LOINCCode();
        loincCode3.setLoincCode("DifferentLOINCCode");
        loincCode3.setComponentName("DifferentComponentName");

        // Assert equals and hashCode
        assertEquals(loincCode1, loincCode2);
        assertEquals(loincCode1.hashCode(), loincCode2.hashCode());

        assertNotEquals(loincCode1, loincCode3);
        assertNotEquals(loincCode1.hashCode(), loincCode3.hashCode());
    }

    @Test
    void testToString() {
        LOINCCode loincCode = new LOINCCode();
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

        String expectedString = "LOINCCode(loincCode=LOINCCode, componentName=ComponentName, property=Property, timeAspect=TimeAspect, systemCode=SystemCode, scaleType=ScaleType, methodType=MethodType, displayName=DisplayName, nbsUid=123, effectiveFromTime=" + loincCode.getEffectiveFromTime() + ", effectiveToTime=" + loincCode.getEffectiveToTime() + ", relatedClassCode=RelatedClassCode, paDerivationExcludeCode=ExcludeCode)";
        assertEquals(expectedString, loincCode.toString());
    }
}
