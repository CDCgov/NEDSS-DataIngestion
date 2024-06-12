package gov.cdc.dataprocessing.utilities.time;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeStampUtil {
    public static Timestamp getCurrentTimeStamp() {
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        return new Timestamp(currentDate.getTime());
    }

    public static Timestamp getCurrentTimeStampPlusOneHour() {
        Instant now = Instant.now();
        Instant plusOneHour = now.plus(1, ChronoUnit.HOURS);
        return Timestamp.from(plusOneHour);
    }
}
