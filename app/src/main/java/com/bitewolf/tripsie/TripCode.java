package com.bitewolf.tripsie;

/**
 * Created by Travis on 2/11/2015.
 */
public class TripCode
{
    public String Code;
    public String Destination;
    public String StartDate;
    public String EndDate;
    public String MyCode;

    @Override
    public String toString()
    {
        return Destination + "\t\t" + StartDate + " - " + EndDate;

    }
}

