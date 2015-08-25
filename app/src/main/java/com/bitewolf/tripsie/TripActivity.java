package com.bitewolf.tripsie;

import java.util.ArrayList;

/**
 * Created by Travis on 2/21/2015.
 */
public class TripActivity
{
    public int Id;
    public String Activity;
    public int TripUserId;
    public float Lat;
    public float Lon;
    public String Details;
    public int TripId;
    public boolean IsComplete;

    public ArrayList<TripActivityVote> TripActivityVotes;

    @Override
    public String toString()
    {
        return Activity;
    }
}
