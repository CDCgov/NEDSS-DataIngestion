package gov.cdc.dataingestion.share.helper;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class TimeStampHelper {
    private TimeStampHelper() {

    }
    public static Timestamp getCurrentTimeStamp() {
        // Another Option: Timestamp.from(ZonedDateTime.now().toInstant()) //NOSONAR
        //return Timestamp.from(Instant.now());//old implementation. //NOSONAR
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }
    public static Instant getInstantNow() {
        return Instant.now();
    }
}
