package gov.cdc.dataprocessing.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RulesEngineUtil {
    private static final Logger logger = LoggerFactory.getLogger(RulesEngineUtil.class); //NOSONAR

    public static int[] CalcMMWR(String pDate)
    {
        //  Create return variable.
        int[] r = {0,0};
        if(pDate == null || pDate.trim().isEmpty())
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
            //  Create temp variables.
            long t = calJan1.getTimeInMillis();
            Date d;
            int h;
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
