package com.bitewolf.tripsie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Travis on 2/17/2015.
 */
public class TripUserListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<TripUser> tripUsers;

    private  String userMe;

    public TripUserListAdapter(Activity activity, List<TripUser> tripUsers, String userMe) {
        this.activity = activity;
        this.tripUsers = tripUsers;

        this.userMe = userMe;
    }

    @Override
    public int getCount() {
        return tripUsers.size();
    }

    @Override
    public Object getItem(int location) {
        return tripUsers.get(location);
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




        if (convertView == null)
            convertView = inflater.inflate(R.layout.trip_row, null);

        TripUser t = tripUsers.get(position);

        TextView name = (TextView)convertView.findViewById(R.id.TripRowName);
        name.setText(t.DisplayName);

        ImageView icon  = (ImageView)convertView.findViewById(R.id.TripRowIcon);

        TextView edit = (TextView)convertView.findViewById(R.id.TripEditText);

        if(t.Id.equals(userMe))
        {
            edit.setVisibility(View.VISIBLE);
        }

        else
        {
            edit.setVisibility(View.GONE);
        }

        final  ViewGroup parentViewGroup = parent;
        final int positionFinal = position;

        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ListView) parentViewGroup).performItemClick(v, positionFinal, 0);
            }
        });

        if(t.TripStatus == 1)
        {
            icon.setImageResource(R.drawable.yes);
        }

        else if(t.TripStatus == 2)
        {
            icon.setImageResource(R.drawable.no);
        }

        return convertView;
    }

}
