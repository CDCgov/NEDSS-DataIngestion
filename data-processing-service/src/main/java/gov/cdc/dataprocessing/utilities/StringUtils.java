package gov.cdc.dataprocessing.utilities;

import java.sql.Timestamp;
import java.util.Date;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public class StringUtils {
    public static Timestamp stringToStrutsTimestamp(String strTime) {

        String input = strTime;
        java.util.Date t;
        try {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
            if (input != null && input.trim().length() > 0) {
                t = formatter.parse(input);
                java.sql.Timestamp ts = new java.sql.Timestamp(t.getTime());
                return ts;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
        if (date == null) {
            return new String("");
        }
        else {
            return formatter.format(date);
        }

    }
}
