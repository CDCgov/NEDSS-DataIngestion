package gov.cdc.dataprocessing.utilities.time;

import gov.cdc.dataprocessing.exception.DataProcessingException;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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

    public static Timestamp getCurrentTimeStampPlusOneDay() {
        Instant now = Instant.now();
        Instant plusOneHour = now.plus(1, ChronoUnit.DAYS);
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
            if (!timestampString.contains(":")) {
                timestampString += " 00:00:00";  // Append default time if time is missing
            }
            java.util.Date parsedDate = sdf.parse(timestampString);
            return new Timestamp(parsedDate.getTime());
        }catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }
}
