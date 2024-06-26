package gov.cdc.dataprocessing.utilities;

import java.sql.Timestamp;
import java.util.Date;

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
