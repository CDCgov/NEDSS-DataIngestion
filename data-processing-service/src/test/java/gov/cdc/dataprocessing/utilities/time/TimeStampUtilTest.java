package gov.cdc.dataprocessing.utilities.time;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

public class TimeStampUtilTest {

    @Test
    void testConvertStringToTimestamp_ValidString() throws DataProcessingException, ParseException {
        String timestampString = "12/31/2022 23:59:59";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        java.util.Date parsedDate = sdf.parse(timestampString);
        Timestamp expectedTimestamp = new Timestamp(parsedDate.getTime());
        Timestamp result = TimeStampUtil.convertStringToTimestamp(timestampString);
        assertEquals(expectedTimestamp, result);
    }

    @Test
    void testConvertStringToTimestamp_InvalidString() {
        String timestampString = "invalid date";
        Exception exception = assertThrows(DataProcessingException.class, () -> {
            TimeStampUtil.convertStringToTimestamp(timestampString);
        });
        assertEquals("Unparseable date: \"invalid date\"", exception.getMessage());
    }

    @Test
    void testConvertStringToTimestamp_NullString() {
        String timestampString = null;
        Exception exception = assertThrows(DataProcessingException.class, () -> {
            TimeStampUtil.convertStringToTimestamp(timestampString);
        });
        assertNotNull(exception.getMessage());
    }
}
