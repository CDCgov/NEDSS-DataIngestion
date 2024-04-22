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

    private static String timeZone;

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

        if (timeZone == null || timeZone.isBlank()) {
            timeZone="UTC";
        }
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(ZoneId.of(timeZone));
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }
    public static Instant getInstantNow() {
        return Instant.now();
    }
    @Value("${app.timezone}")
    public void setEnvTimeZone(String envTimeZone){
        timeZone=envTimeZone;
    }
    public String getEnvTimeZone(){
        return timeZone;
    }
}
