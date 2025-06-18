package gov.cdc.dataprocessing.utilities.time;

import gov.cdc.dataprocessing.exception.DataProcessingException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
