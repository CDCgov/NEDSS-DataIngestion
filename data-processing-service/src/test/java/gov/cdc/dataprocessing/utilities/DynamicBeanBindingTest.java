package gov.cdc.dataprocessing.utilities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicBeanBindingTest {

    @Test
    public void testPopulateBeanString() throws Exception {
        TestBean bean = new TestBean();
        DynamicBeanBinding.populateBean(bean, "string_value", "test");
        assertEquals("test", bean.getStringValue());
    }

    @Test
    public void testPopulateBeanLong() throws Exception {
        TestBean bean = new TestBean();
        DynamicBeanBinding.populateBean(bean, "long_value", "123456789");
        assertEquals(123456789L, bean.getLongValue());
    }

    @Test
    public void testPopulateBeanInteger() throws Exception {
        TestBean bean = new TestBean();
        DynamicBeanBinding.populateBean(bean, "integer_value", "12345");
        assertEquals(12345, bean.getIntegerValue());
    }

    @Test
    public void testPopulateBeanBigDecimal() throws Exception {
        TestBean bean = new TestBean();
        DynamicBeanBinding.populateBean(bean, "big_decimal_value", "123456789");
        assertEquals(BigDecimal.valueOf(123456789), bean.getBigDecimalValue());
    }

    @Test
    public void testPopulateBeanTimestamp() throws Exception {
        TestBean bean = new TestBean();
        String dateStr = "12/31/2020";
        Timestamp expectedTimestamp = new Timestamp(new SimpleDateFormat("MM/dd/yyyy").parse(dateStr).getTime());
        DynamicBeanBinding.populateBean(bean, "timestamp_value", dateStr);
        assertEquals(expectedTimestamp, bean.getTimestampValue());
    }

    @Test
    public void testPopulateBeanBoolean() throws Exception {
        TestBean bean = new TestBean();
        DynamicBeanBinding.populateBean(bean, "boolean_value", "true");
        assertTrue(bean.isBooleanValue());
    }

    @Test
    public void testPopulateBeanNullValue() throws Exception {
        TestBean bean = new TestBean();
        DynamicBeanBinding.populateBean(bean, "string_value", null);
        assertNull(bean.getStringValue());
    }

    @Test
    public void testPopulateBeanEmptyValue() throws Exception {
        TestBean bean = new TestBean();
        DynamicBeanBinding.populateBean(bean, "string_value", "");
        assertNull(bean.getStringValue());
    }

    @Test
    public void testPopulateBeanInvalidMethod() throws Exception {
        TestBean bean = new TestBean();
        // Test with a column name that doesn't have a corresponding setter
        DynamicBeanBinding.populateBean(bean, "non_existing_field", "test");
        // Ensure no exception is thrown and no value is set
        assertNull(bean.getStringValue());
    }


    @Test
    public void testPopulateBeanWithInvalidTimestamp() {
        TestBean bean = new TestBean();
        Exception exception = assertThrows(Exception.class, () -> {
            DynamicBeanBinding.populateBean(bean, "timestamp_value", "invalid date");
        });
        assertTrue(exception.getCause() instanceof java.text.ParseException);
    }

    @Test
    public void testPopulateBeanWithInvalidLong() {
        TestBean bean = new TestBean();
        Exception exception = assertThrows(Exception.class, () -> {
            DynamicBeanBinding.populateBean(bean, "long_value", "invalid long");
        });
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

    @Test
    public void testPopulateBeanWithInvalidInteger() {
        TestBean bean = new TestBean();
        Exception exception = assertThrows(Exception.class, () -> {
            DynamicBeanBinding.populateBean(bean, "integer_value", "invalid integer");
        });
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

    @Test
    public void testPopulateBeanWithInvalidBigDecimal() {
        TestBean bean = new TestBean();
        Exception exception = assertThrows(Exception.class, () -> {
            DynamicBeanBinding.populateBean(bean, "big_decimal_value", "invalid bigdecimal");
        });
        assertTrue(exception.getCause() instanceof NumberFormatException);
    }

//    @Test
//    public void testPopulateBeanWithIllegalAccessException() {
//        Exception exception = assertThrows(Exception.class, () -> {
//            DynamicBeanBinding.populateBean(new PrivateBean(), "private_field", "test");
//        });
//        assertTrue(exception.getCause() instanceof IllegalAccessException);
//    }
//
//    @Test
//    public void testPopulateBeanWithIllegalArgumentException() {
//        TestBean bean = new TestBean();
//        Exception exception = assertThrows(Exception.class, () -> {
//            DynamicBeanBinding.populateBean(bean, "boolean_value", "not_a_boolean");
//        });
//        assertTrue(exception.getCause() instanceof IllegalArgumentException);
//    }
//
//    @Test
//    public void testPopulateBeanWithSecurityException() {
//        Exception exception = assertThrows(Exception.class, () -> {
//            DynamicBeanBinding.getMethods(null);
//        });
//        assertTrue(exception.getCause() instanceof SecurityException);
//    }

    private static class PrivateBean {
        private String privateField;

        private String getPrivateField() {
            return privateField;
        }

        private void setPrivateField(String privateField) {
            this.privateField = privateField;
        }
    }
}
