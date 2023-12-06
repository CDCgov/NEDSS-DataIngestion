package gov.cdc.dataingestion.share.helper;

import java.sql.Timestamp;
import java.time.Instant;

public class TimeStampHelper {
    public static Timestamp getCurrentTimeStamp() {
        // Another Option: Timestamp.from(ZonedDateTime.now().toInstant())
        return Timestamp.from(Instant.now());
    }
    public static Instant getInstantNow() {
        return Instant.now();
    }
}
