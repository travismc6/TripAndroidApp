package com.bitewolf.tripsie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Travis on 2/17/2015.
 */
public class TripActivityListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<TripActivity> tripActivities;

    public TripActivityListAdapter(Activity activity, List<TripActivity> tripActivities) {
        this.activity = activity;
        this.tripActivities = tripActivities;
    }

    @Override
    public int getCount() {
        return tripActivities.size();
    }

    @Override
    public Object getItem(int location) {
        return tripActivities.get(location);
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
            convertView = inflater.inflate(R.layout.activity_row, null);

        TripActivity t = tripActivities.get(position);

        TextView name = (TextView)convertView.findViewById(R.id.ActivityRowActivityText);

        name.setText(t.Activity.trim());


        int up=0,down=0;

        for(TripActivityVote vote : t.TripActivityVotes)
        {
            if(vote.Vote > 0)
                up ++;
            else
            {
                down++;
            }

        }

        TextView upTv = (TextView)convertView.findViewById(R.id.ActivityRowUpCount);
        upTv.setText(String.valueOf(up));

        TextView downTv = (TextView)convertView.findViewById(R.id.ActivityRowDownCount);
        downTv.setText(String.valueOf(down));

        ImageView imgUp = (ImageView)convertView.findViewById(R.id.ActivityRowThumbsUp);
        ImageView imgDown = (ImageView)convertView.findViewById(R.id.ActivityRowThumbsDown);

        CheckBox cb = (CheckBox)convertView.findViewById(R.id.ActivityRowActivityComplete);

        cb.setChecked(t.IsComplete);

        final  ViewGroup parentViewGroup = parent;
        final int positionFinal = position;

        name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ListView) parentViewGroup).performItemClick(v, positionFinal, 0);
            }
        });

        imgUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ListView) parentViewGroup).performItemClick(v, positionFinal, 0);
            }
        });

        imgDown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ListView) parentViewGroup).performItemClick(v, positionFinal, 0);
            }
        });

        cb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((ListView) parentViewGroup).performItemClick(v, positionFinal, 0);
            }
        });


        return convertView;
    }

}
