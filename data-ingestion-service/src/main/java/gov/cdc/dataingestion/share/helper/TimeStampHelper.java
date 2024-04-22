package gov.cdc.dataingestion.share.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TimeStampHelper {

    private static String TIME_ZONE;

    private TimeStampHelper() {
    }

    /**
     * Create timestamp with the given timezone. Default UTC
     * Timezones: UTC America/New_York America/Chicago America/Denver America/Phoenix America/Los_Angeles
     * @return Timestamp
     */
    public static Timestamp getCurrentTimeStamp() {
        // Another Option: Timestamp.from(ZonedDateTime.now().toInstant()) //NOSONAR
        //return Timestamp.from(Instant.now());//old implementation. //NOSONAR

        System.out.println("input timezone: " + TIME_ZONE);
        if (TIME_ZONE == null || TIME_ZONE.isBlank()) {
            TIME_ZONE="UTC";
        }
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(ZoneId.of(TIME_ZONE));
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }
    public static Instant getInstantNow() {
        return Instant.now();
    }
    @Value("${app.timezone}")
    public void setDatabase(String timeZone){
        TIME_ZONE=timeZone;
    }
}
