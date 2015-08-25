package com.bitewolf.tripsie;



import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Travis on 2/16/2015.
 */
public class TripComment {
    public DateTime Date;
    public String Comment;

    public String Username;

    @Override
    public String toString()
    {
        String dateString = "";

        DateTime today = new DateTime().minus(DateTime.now().getSecondOfDay() * 1000);


        if(Date.getMillis() > today.getMillis())
        {
            String suffix = " am";

            int hour = Date.getHourOfDay();

            if(hour > 11)
            {
                suffix = " pm";
            }

            if(hour > 12)
            {
                hour = hour - 12;
            }

            if(hour == 0)
            {
                hour = 12;
            }


            dateString = String.format("%02d", hour)  + ":" + String.format("%02d", Date.getMinuteOfHour()) + suffix;
        }

        else
        {
            dateString = String.format("%02d", Date.getMonthOfYear()) + "/" + String.format("%02d", Date.getDayOfMonth());
        }



        return dateString;
    }
}
