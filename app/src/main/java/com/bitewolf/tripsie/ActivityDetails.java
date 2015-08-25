package com.bitewolf.tripsie;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ActivityDetails extends ActionBarActivity {

    public static Trip Trip;
    public static TripActivity TripActivity;

    TextView SuggestedBy;
    TextView Details;

    ListView UpVotes;
    ListView DownVotes;

    ArrayList<String> UpNames;
    ArrayList<String> DownNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_details);

        SuggestedBy = (TextView)findViewById(R.id.ActivityDetailsSuggestedBy);
        Details = (TextView)findViewById(R.id.ActivityDetailsDetails);

        UpVotes = (ListView)findViewById(R.id.ActivityDetailUpVotes);
        DownVotes = (ListView)findViewById(R.id.ActivityDetailDownVotes);

        TripUser creator = new TripUser();

        for(TripUser user : Trip.Users)
        {
            if(user.Id.equals(String.valueOf(TripActivity.TripUserId)))
            {
                creator = user;
                break;
            }
        }

        SuggestedBy.setText( creator.DisplayName);

        if(Details != null)
            Details.setText(TripActivity.Details);

        else
        {
            Details.setText("");
        }

        UpNames = new ArrayList<String>();
        DownNames = new ArrayList<String>();

        for(TripActivityVote vote : TripActivity.TripActivityVotes)
        {
            for(TripUser user : Trip.Users)
            {
                if(user.Id.equals(String.valueOf(vote.TripUserId)))
                {
                    if(vote.Vote > 0) {
                        UpNames.add(user.DisplayName);
                    }
                    else if(vote.Vote < 0)
                    {
                        DownNames.add(user.DisplayName);
                    }
                }
            }
        }

        ArrayAdapter upAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                UpNames);

        ArrayAdapter downAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                DownNames);

        UpVotes.setAdapter(upAdapter);
        DownVotes.setAdapter(downAdapter);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        Trip = null;
        TripActivity=null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar actionbar = getSupportActionBar();

        actionbar.setTitle(TripActivity.Activity);


        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
