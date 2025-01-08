package gov.cdc.dataprocessing.utilities.time;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimeStampUtilTest {
    @Test
    void getTimeStamp() {
        var test = TimeStampUtil.getCurrentTimeStamp("America/New_York");
        var testAz = TimeStampUtil.getCurrentTimeStamp("America/Phoenix");

        Assertions.assertTrue(test.after(testAz));
    }

    @Test
    void getCurrentTimeStampPlusOneHourTest() {
        var test = TimeStampUtil.getCurrentTimeStampPlusOneHour("America/New_York");
        var testBeforeOneHour = TimeStampUtil.getCurrentTimeStamp("America/New_York");
        Assertions.assertTrue(test.after(testBeforeOneHour));
    }

    @Test
    void getCurrentTimeStampPlusOneDayTest() {
        var test = TimeStampUtil.getCurrentTimeStampPlusOneDay("America/New_York");
        var testBeforeOneDay = TimeStampUtil.getCurrentTimeStamp("America/New_York");
        Assertions.assertTrue(test.after(testBeforeOneDay));
    }

    @Test
    void convertTimestampToStringTest() {
        var test = TimeStampUtil.convertTimestampToString("America/New_York");
        Assertions.assertNotNull(test);
    }

    // 01/08/2025 16:15:01

    @Test
    void convertStringToTimestampTest() throws DataProcessingException {
        var timeStr = "01/08/2025 16:15:01";
        var test = TimeStampUtil.convertStringToTimestamp(timeStr);
        Assertions.assertNotNull(test);
    }
}
