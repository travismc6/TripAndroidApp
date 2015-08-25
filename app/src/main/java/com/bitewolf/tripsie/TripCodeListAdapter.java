package com.bitewolf.tripsie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Travis on 2/17/2015.
 */
public class TripCodeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<TripCode> trips;

    public TripCodeListAdapter(Activity activity, List<TripCode> trips) {
        this.activity = activity;
        this.trips = trips;
    }

    @Override
    public int getCount() {
        return trips.size();
    }

    @Override
    public Object getItem(int location) {
        return trips.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        TripCode trip = trips.get(position);


        if (convertView == null)
            convertView = inflater.inflate(R.layout.trip_list_row, null);

        TextView destiniation = (TextView)convertView.findViewById(R.id.tripListRowDestination);
        destiniation.setText(trip.Destination);

        TextView dates = (TextView)convertView.findViewById(R.id.tripListRowDates);
        dates.setText(trip.StartDate + " - " + trip.EndDate);

        return convertView;
    }

}
