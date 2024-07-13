package gov.cdc.dataprocessing.service.model.decision_support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TestNumericValueTest {

    @Test
    void testSettersAndGetters() {
        TestNumericValue testNumericValue = new TestNumericValue();

        String testCode = "TEST001";
        String testCodeDesc = "Test Code Description";
        String comparatorCode = "EQ";
        String comparatorCodeDesc = "Equal";
        BigDecimal value1 = new BigDecimal("10.5");
        String separatorCode = ":";
        BigDecimal value2 = new BigDecimal("20.5");
        String unitCode = "mg/dL";
        String unitCodeDesc = "Milligrams per deciliter";

        testNumericValue.setTestCode(testCode);
        testNumericValue.setTestCodeDesc(testCodeDesc);
        testNumericValue.setComparatorCode(comparatorCode);
        testNumericValue.setComparatorCodeDesc(comparatorCodeDesc);
        testNumericValue.setValue1(value1);
        testNumericValue.setSeparatorCode(separatorCode);
        testNumericValue.setValue2(value2);
        testNumericValue.setUnitCode(unitCode);
        testNumericValue.setUnitCodeDesc(unitCodeDesc);

        assertEquals(testCode, testNumericValue.getTestCode());
        assertEquals(testCodeDesc, testNumericValue.getTestCodeDesc());
        assertEquals(comparatorCode, testNumericValue.getComparatorCode());
        assertEquals(comparatorCodeDesc, testNumericValue.getComparatorCodeDesc());
        assertEquals(value1, testNumericValue.getValue1());
        assertEquals(separatorCode, testNumericValue.getSeparatorCode());
        assertEquals(value2, testNumericValue.getValue2());
        assertEquals(unitCode, testNumericValue.getUnitCode());
        assertEquals(unitCodeDesc, testNumericValue.getUnitCodeDesc());
    }

    @Test
    void testDefaultConstructor() {
        TestNumericValue testNumericValue = new TestNumericValue();

        assertNull(testNumericValue.getTestCode());
        assertNull(testNumericValue.getTestCodeDesc());
        assertNull(testNumericValue.getComparatorCode());
        assertNull(testNumericValue.getComparatorCodeDesc());
        assertNull(testNumericValue.getValue1());
        assertNull(testNumericValue.getSeparatorCode());
        assertNull(testNumericValue.getValue2());
        assertNull(testNumericValue.getUnitCode());
        assertNull(testNumericValue.getUnitCodeDesc());
    }
}
