package gov.cdc.dataprocessing.utilities.time;

import gov.cdc.dataprocessing.exception.DataProcessingException;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String convertTimestampToString() {
        var timestamp = getCurrentTimeStamp();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(timestamp);
    }
    public static Timestamp convertStringToTimestamp(String timestampString) throws DataProcessingException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            java.util.Date parsedDate = sdf.parse(timestampString);
            return new Timestamp(parsedDate.getTime());
        }catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }

    }
}
