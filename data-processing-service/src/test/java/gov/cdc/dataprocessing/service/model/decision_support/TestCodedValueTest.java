package gov.cdc.dataprocessing.service.model.decision_support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestCodedValueTest {

    @Test
    void testSettersAndGetters() {
        TestCodedValue testCodedValue = new TestCodedValue();

        String testCode = "TEST001";
        String testCodeDesc = "Test Code Description";
        String resultCode = "RESULT001";
        String resultCodeDesc = "Result Code Description";

        testCodedValue.setTestCode(testCode);
        testCodedValue.setTestCodeDesc(testCodeDesc);
        testCodedValue.setResultCode(resultCode);
        testCodedValue.setResultCodeDesc(resultCodeDesc);

        assertEquals(testCode, testCodedValue.getTestCode());
        assertEquals(testCodeDesc, testCodedValue.getTestCodeDesc());
        assertEquals(resultCode, testCodedValue.getResultCode());
        assertEquals(resultCodeDesc, testCodedValue.getResultCodeDesc());
    }

    @Test
    void testDefaultConstructor() {
        TestCodedValue testCodedValue = new TestCodedValue();

        assertNull(testCodedValue.getTestCode());
        assertNull(testCodedValue.getTestCodeDesc());
        assertNull(testCodedValue.getResultCode());
        assertNull(testCodedValue.getResultCodeDesc());
    }



}
