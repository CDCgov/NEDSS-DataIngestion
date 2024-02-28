package gov.cdc.dataprocessing.utilities.time;

import java.sql.Date;
import java.sql.Timestamp;

public class TimeStampUtil {
    public static Timestamp getCurrentTimeStamp() {
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        return currentTimestamp;
    }
}
