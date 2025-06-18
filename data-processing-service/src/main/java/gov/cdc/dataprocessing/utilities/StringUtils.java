package gov.cdc.dataprocessing.utilities;

import java.sql.Timestamp;
import java.util.Date;


public class StringUtils {
    public static Timestamp stringToStrutsTimestamp(String strTime) {

        java.util.Date t;
        try {
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
            if (strTime != null && !strTime.trim().isEmpty()) {
                t = formatter.parse(strTime);
                return new Timestamp(t.getTime());
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
            return "";
        }
        else {
            return formatter.format(date);
        }

    }
}
