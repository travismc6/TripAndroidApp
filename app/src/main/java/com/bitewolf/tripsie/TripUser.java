package com.bitewolf.tripsie;

/**
 * Created by Travis on 2/4/2015.
 */
public class TripUser
{
    public String Email;
    public String Phone;
    public String DisplayName;
    public String UsersJson;
    public String Id;
    public String Code;
    public boolean IsCreator;
    public int TripStatus;
    public double Lat;
    public double Lon;

    @Override
    public String toString()
    {
        return DisplayName;
    }
}
