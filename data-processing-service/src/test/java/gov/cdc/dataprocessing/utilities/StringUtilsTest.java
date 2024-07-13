package gov.cdc.dataprocessing.utilities;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testStringToStrutsTimestamp_ValidDate() {
        String dateStr = "12/31/2022";
        Timestamp expectedTimestamp = Timestamp.valueOf("2022-12-31 00:00:00.0");
        Timestamp result = StringUtils.stringToStrutsTimestamp(dateStr);
        assertEquals(expectedTimestamp, result);
    }

    @Test
    void testStringToStrutsTimestamp_EmptyString() {
        String dateStr = "";
        Timestamp result = StringUtils.stringToStrutsTimestamp(dateStr);
        assertNull(result);
    }

    @Test
    void testStringToStrutsTimestamp_NullString() {
        String dateStr = null;
        Timestamp result = StringUtils.stringToStrutsTimestamp(dateStr);
        assertNull(result);
    }

    @Test
    void testStringToStrutsTimestamp_InvalidDate() {
        String dateStr = "invalid date";
        Timestamp result = StringUtils.stringToStrutsTimestamp(dateStr);
        assertNull(result);
    }

    @Test
    void testFormatDate_ValidDate() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = formatter.parse("12/31/2022");
        String expectedDateStr = "12/31/2022";
        String result = StringUtils.formatDate(date);
        assertEquals(expectedDateStr, result);
    }

    @Test
    void testFormatDate_NullDate() {
        Date date = null;
        String result = StringUtils.formatDate(date);
        assertEquals("", result);
    }
}
