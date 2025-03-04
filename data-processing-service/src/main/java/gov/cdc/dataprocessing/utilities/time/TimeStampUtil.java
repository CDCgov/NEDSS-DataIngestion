package gov.cdc.dataprocessing.utilities.time;

import gov.cdc.dataprocessing.exception.DataProcessingException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static Timestamp getCurrentTimeStamp(String timezoneId) {
        ZoneId zoneId = ZoneId.of(timezoneId);
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(zoneId);
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }

    public static Timestamp getCurrentTimeStampPlusOneHour(String timezoneId) {
        ZoneId zoneId = ZoneId.of(timezoneId);
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(zoneId).plusHours(1);
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }

    public static Timestamp getCurrentTimeStampPlusOneDay(String timezoneId) {
        ZoneId zoneId = ZoneId.of(timezoneId);
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(zoneId).plusDays(1);
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }

    public static String convertTimestampToString(String timezoneId) {
        var timestamp = getCurrentTimeStamp(timezoneId);
        var instant = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        return formatter.format(instant);
    }

    public static Timestamp convertStringToTimestamp(String timestampString) throws DataProcessingException {
        try {
            timestampString = formatDate(timestampString);
            if (!timestampString.contains(":")) {
                timestampString += " 00:00:00"; // Append default time if time is missing
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(timestampString, formatter);
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            return timestamp;
        } catch (Exception e) {
            throw new DataProcessingException("Error parsing timestamp string: " + e.getMessage(), e);
        }
    }

    public static String formatDate(String date) {
        Pattern pattern = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})");
        Matcher matcher = pattern.matcher(date);

        if (matcher.matches()) {
            String day = matcher.group(1);
            String month = matcher.group(2);
            String year = matcher.group(3);

            if (day.length() == 1) day = "0" + day;
            if (month.length() == 1) month = "0" + month;

            return day + "/" + month + "/" + year;
        }

        return date;
    }
}
