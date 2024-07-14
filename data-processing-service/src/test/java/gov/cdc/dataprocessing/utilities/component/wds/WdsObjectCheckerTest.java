package gov.cdc.dataprocessing.utilities.component.wds;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.utilities.StringUtils;
import gov.cdc.dataprocessing.utilities.TestBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class WdsObjectCheckerTest {

    private WdsObjectChecker wdsObjectChecker;

    @BeforeEach
    public void setUp() {
        wdsObjectChecker = new WdsObjectChecker();
    }

    @Test
    public void testCheckNbsObjectTextEquals() throws Exception {
        TestBean bean = new TestBean();
        bean.setStringValue("test");

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("TEXT");
        metaData.setDataLocation("STRING_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("=");
        edxRuleManageDT.setValue("test");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectTextNotEquals() throws Exception {
        TestBean bean = new TestBean();
        bean.setStringValue("test");

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("TEXT");
        metaData.setDataLocation("STRING_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("!=");
        edxRuleManageDT.setValue("different");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectTextContains() throws Exception {
        TestBean bean = new TestBean();
        bean.setStringValue("test,example");

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("TEXT");
        metaData.setDataLocation("STRING_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("CT");
        edxRuleManageDT.setValue("example");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectNumericGreaterThan() throws Exception {
        TestBean bean = new TestBean();
        bean.setLongValue(20L);

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("NUMERIC");
        metaData.setDataLocation("LONG_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic(">");
        edxRuleManageDT.setValue("10");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectNumericLessThan() throws Exception {
        TestBean bean = new TestBean();
        bean.setLongValue(5L);

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("NUMERIC");
        metaData.setDataLocation("LONG_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("<");
        edxRuleManageDT.setValue("10");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectDateEquals() throws Exception {
        TestBean bean = new TestBean();
        Timestamp timestamp = Timestamp.valueOf("2023-01-01 00:00:00");
        bean.setTimestampValue(timestamp);

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("DATETIME");
        metaData.setDataLocation("TIMESTAMP_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("=");
        edxRuleManageDT.setValue("01/01/2023");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectNullValueNotEquals() throws Exception {
        TestBean bean = new TestBean();
        bean.setStringValue(null);

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("TEXT");
        metaData.setDataLocation("STRING_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("!=");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectNullValueEquals() throws Exception {
        TestBean bean = new TestBean();
        bean.setStringValue(null);

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("TEXT");
        metaData.setDataLocation("STRING_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("=");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertFalse(result);
    }

    @Test
    public void testCheckNbsObjectGreaterThanEquals() throws Exception {
        TestBean bean = new TestBean();
        bean.setLongValue(20L);

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("NUMERIC");
        metaData.setDataLocation("LONG_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic(">=");
        edxRuleManageDT.setValue("20");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectLessThanEquals() throws Exception {
        TestBean bean = new TestBean();
        bean.setLongValue(5L);

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("NUMERIC");
        metaData.setDataLocation("LONG_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("<=");
        edxRuleManageDT.setValue("5");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }

    @Test
    public void testCheckNbsObjectDateNotEquals() throws Exception {
        TestBean bean = new TestBean();
        Timestamp timestamp = Timestamp.valueOf("2023-01-01 00:00:00");
        bean.setTimestampValue(timestamp);

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("DATETIME");
        metaData.setDataLocation("TIMESTAMP_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("!=");
        edxRuleManageDT.setValue("01/02/2023");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertTrue(result);
    }





    @Test
    public void testCheckNbsObjectNullMetaData() {
        TestBean bean = new TestBean();
        bean.setStringValue("test");

        NbsQuestionMetadata metaData = null;

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("=");

        Exception exception = assertThrows(NullPointerException.class, () -> {
            wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        });

        assertNotNull(exception);
    }


    @Test
    public void testCheckNbsObjectInvalidGetMethod() {
        TestBean bean = new TestBean();
        bean.setStringValue("test");

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("TEXT");
        metaData.setDataLocation("INVALID_METHOD");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("=");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertFalse(result);
    }

    @Test
    public void testCheckNbsObjectInvalidDataType() {
        TestBean bean = new TestBean();
        bean.setStringValue("test");

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("INVALID_TYPE");
        metaData.setDataLocation("STRING_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("=");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertFalse(result);
    }

    @Test
    public void testCheckNbsObjectElseBlock() throws Exception {
        TestBean bean = new TestBean();
        bean.setStringValue("test");

        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setDataType("NUMERIC");
        metaData.setDataLocation("STRING_VALUE");

        EdxRuleManageDto edxRuleManageDT = new EdxRuleManageDto();
        edxRuleManageDT.setLogic("INVALID_LOGIC");

        boolean result = wdsObjectChecker.checkNbsObject(edxRuleManageDT, bean, metaData);
        assertFalse(result);
    }

    // Additional test cases for other scenarios...
}
