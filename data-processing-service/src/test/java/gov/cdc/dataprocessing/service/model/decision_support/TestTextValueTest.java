package gov.cdc.dataprocessing.service.model.decision_support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestTextValueTest {

    @Test
    void testSettersAndGetters() {
        TestTextValue testTextValue = new TestTextValue();

        String testCode = "TEST001";
        String testCodeDesc = "Test Code Description";
        String comparatorCode = "EQ";
        String comparatorCodeDesc = "Equal";
        String textValue = "Test Text Value";

        testTextValue.setTestCode(testCode);
        testTextValue.setTestCodeDesc(testCodeDesc);
        testTextValue.setComparatorCode(comparatorCode);
        testTextValue.setComparatorCodeDesc(comparatorCodeDesc);
        testTextValue.setTextValue(textValue);

        assertEquals(testCode, testTextValue.getTestCode());
        assertEquals(testCodeDesc, testTextValue.getTestCodeDesc());
        assertEquals(comparatorCode, testTextValue.getComparatorCode());
        assertEquals(comparatorCodeDesc, testTextValue.getComparatorCodeDesc());
        assertEquals(textValue, testTextValue.getTextValue());
    }

    @Test
    void testDefaultConstructor() {
        TestTextValue testTextValue = new TestTextValue();

        assertNull(testTextValue.getTestCode());
        assertNull(testTextValue.getTestCodeDesc());
        assertNull(testTextValue.getComparatorCode());
        assertNull(testTextValue.getComparatorCodeDesc());
        assertNull(testTextValue.getTextValue());
    }
}
