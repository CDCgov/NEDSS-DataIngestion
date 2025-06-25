package gov.cdc.dataprocessing.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
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

    public static String getRootStackTraceAsString(Exception exception) {
        Throwable root = exception;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        root.printStackTrace(pw);
        return sw.toString();
    }

}
