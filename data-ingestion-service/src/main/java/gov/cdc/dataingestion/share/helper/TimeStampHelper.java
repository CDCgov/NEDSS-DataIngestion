package gov.cdc.dataingestion.share.helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class TimeStampHelper {
    private TimeStampHelper() {

    }
    public static Timestamp getCurrentTimeStamp(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        // Another Option: Timestamp.from(ZonedDateTime.now().toInstant()) //NOSONAR
        //return Timestamp.from(Instant.now());//old implementation. //NOSONAR
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(zoneId);
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }


    public static String convertTimestampToString(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return formatter.format(localDateTime);
    }
}
