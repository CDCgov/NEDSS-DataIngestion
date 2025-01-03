package gov.cdc.dataprocessing.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public class RulesEngineUtil {
    private static final Logger logger = LoggerFactory.getLogger(RulesEngineUtil.class); //NOSONAR

    public static int[] CalcMMWR(String pDate)
    {
        //  Create return variable.
        int[] r = {0,0};
        if(pDate == null || pDate.trim().equals(""))
            return r;
        try{
            //  Define constants.
            int SECOND = 1000;
            int MINUTE = 60 * SECOND;
            int HOUR = 60 * MINUTE;
            int DAY = 24 * HOUR;
            int WEEK = 7 * DAY;
            //  Convert to date object.
            Date varDate = new SimpleDateFormat("MM/dd/yyyy").parse(pDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(varDate);
            long varTime = cal.getTimeInMillis();
            //  Get January 1st of given year.

            Date varJan1Date = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/"+cal.get(Calendar.YEAR));
            Calendar calJan1 = Calendar.getInstance();
            calJan1.setTime(varJan1Date);
            int varJan1Day = calJan1.get(Calendar.DAY_OF_WEEK);
            long varJan1Time = calJan1.getTimeInMillis();
            //  Create temp variables.
            long t = varJan1Time;
            Date d = null;
            int h = 0;
            //  MMWR Year.
            int y = calJan1.get(Calendar.YEAR);
            //  MMWR Week.
            int w = 0;
            //  Find first day of MMWR Year.
            if(varJan1Day < 5)
            {
                //  If SUN, MON, TUE, or WED, go back to nearest Sunday.
                t -= ((varJan1Day-1) * DAY);
                //  Loop through each week until we reach the given date.
                while(t < varTime)
                {
                    //  Increment the week counter.
                    w++;
                    t += WEEK;
                    //  Adjust for daylight savings time as necessary.
                    d = new Date(t);
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(d);
                    h = cal1.get(Calendar.HOUR);
                    if(h == 1)
                    {
                        t -= HOUR;
                    }
                    if(h == 23 || h == 11)
                    {
                        t += HOUR;
                    }
                }
                //  If at end of year, move on to next year if this week has
                //  more days from next year than from this year.
                if(w == 53)
                {
                    Date varNextJan1Date = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/"+(cal.get(Calendar.YEAR)+1));
                    Calendar varNextJan1Cal = Calendar.getInstance();
                    varNextJan1Cal.setTime(varNextJan1Date);
                    int varNextJan1Day = varNextJan1Cal.get(Calendar.DAY_OF_WEEK);
                    if(varNextJan1Day < 5)
                    {
                        y++;
                        w = 1;
                    }
                }
            }
            else
            {
                //  If THU, FRI, or SAT, go forward to nearest Sunday.
                t += ((7 - (varJan1Day-1)) * DAY);
                //  Loop through each week until we reach the given date.
                while(t <= varTime)
                {
                    //  Increment the week counter.
                    w++;
                    d = new Date(t);
//	          s += "b, " + w + "&nbsp;&nbsp;&nbsp;&nbsp;" + d.toString() + "<br/>";
                    //  Move on to the next week.
                    t += WEEK;
                    //  Adjust for daylight savings time as necessary.
                    d = new Date(t);
                    Calendar dCal = Calendar.getInstance();
                    dCal.setTime(d);
                    h = dCal.get(Calendar.HOUR);
                    if(h == 1)
                    {
                        t -= HOUR;
                    }
                    if(h == 23)
                    {
                        t += HOUR;
                    }
                }
                //  If at beginning of year, move back to previous year if this week has
                //  more days from last year than from this year.

                if(w == 0)
                {
                    d = new Date(t);
                    Calendar dCal1 = Calendar.getInstance();
                    dCal1.setTime(d);
                    if( (dCal1.get(Calendar.MONTH) == 0) && (dCal1.get(Calendar.DAY_OF_WEEK) <= 5) )
                    {
                        y--;
                        int a[] = CalcMMWR("12/31/" + y);
                        w = a[0];
                    }
                }
            }
            //  Zero pad left.
//	    if(w < 10)
//	    {
//	        w = "0" + w;
//	    }
            //  Assemble result.
            r[0] = w;
            r[1] = y;
        }
        catch(Exception ex)
        {
            logger.info(ex.getMessage()); // NOSONAR
        }
        //  Return result.
        return r;
    }

}
