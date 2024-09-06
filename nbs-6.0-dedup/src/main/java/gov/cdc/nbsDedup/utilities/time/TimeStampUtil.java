package gov.cdc.nbsDedup.utilities.time;


import java.sql.Date;
import java.sql.Timestamp;

public class TimeStampUtil {
    public static Timestamp getCurrentTimeStamp() {
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);
        return new Timestamp(currentDate.getTime());
    }
}
