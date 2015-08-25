package com.bitewolf.tripsie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TripActivityActivities extends ActionBarActivity {

    String tripUserId;

    public static Trip Trip;

    ArrayList<TripActivity> list;

    private TripActivityListAdapter adapter;

    ProgressDialog progress = null;
    Menu actionBarMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripactivities);

        Intent i = getIntent();

        tripUserId = i.getStringExtra("tripUserId");

        progress =  new ProgressDialog(this);

        TextView createNew = (TextView) findViewById(R.id.TripActivitiesCreateNew);

        ListView listView = (ListView)findViewById(R.id.TripActivitiesList);

        list = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     int vote = 0;
                     TripActivity activity = list.get(position);

                        if(view.getId() == R.id.ActivityRowThumbsUp)
                        {
                            vote = 1;
                        }

                     else if(view.getId() == R.id.ActivityRowThumbsDown)
                        {
                            vote = -1;
                        }

                        else if(view.getId() == R.id.ActivityRowActivityText)
                        {
                            Intent detailIntent = new Intent(TripActivityActivities.this, ActivityDetails.class);
                            ActivityDetails.Trip = Trip;
                            ActivityDetails.TripActivity = activity;

                           startActivity(detailIntent);

                            return;
                        }

                        else if(view.getId() == R.id.ActivityRowActivityComplete)
                        {
                            progress.setTitle("Updating");
                            progress.setMessage("Please wait...");
                            progress.show();

                            String idString = String.valueOf(activity.Id);
                            String complete = !activity.IsComplete ? "true" : "false";

                            new ActivityCompleteTask((CheckBox)view, idString).execute( idString,  complete);

                            return;
                        }

                     else
                        {


                            return;
                        }

                     progress.setTitle("Sending Vote");
                     progress.setMessage("Please wait...");
                     progress.show();

                     new ActivityVoteTask().execute( String.valueOf( activity.Id), String.valueOf(vote));
                 }
        });


        createNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TripActivityActivities.this, TripCreateActivity.class);
                intent.putExtra("userMe", tripUserId);

                TripCreateActivity.trip = Trip;

                startActivityForResult(intent, 1);
            }
        });


        progress.setTitle("Loading Trip");
        progress.setMessage("Please wait...");
        progress.show();

        if(Trip != null)
        {
            new TripActivitiesTask().execute();
        }

        else
        {
            String tripId = getIntent().getStringExtra("tripCode");

            new GetTripTask().execute(tripId);
        }
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        Trip = null;
    }

    private class GetTripTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String result) {

            if(result == "")
            {
                Toast.makeText(TripActivityActivities.this,"Unable to load activities.",
                        Toast.LENGTH_LONG).show();
            }

            else
            {
                // Parse json
                try {

                    JSONObject jObject = new JSONObject(result);

                    Trip trip = Common.ParseTrip(result);
                    Trip = trip;

                    SetupActionBar();

                    new TripActivitiesTask().execute();

                }

                catch (Exception ex)
                {
                    Toast.makeText(TripActivityActivities.this,"Unable to load activities.",
                            Toast.LENGTH_LONG).show();

                    progress.dismiss();
                }

            }



        }

        @Override
        protected String doInBackground(String... params) {
            String responseXml = "";

            String code = params[0];

            String endpoint = Common.API_ENDPOINT + "/api/Trips/" + code;

            URI uri = null;
            try {
                uri = new URI(endpoint);
                HttpGet request = new HttpGet();
                request.setURI(uri);

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                responseXml = EntityUtils.toString(responseEntity);


                return responseXml;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseXml;
        }
    }


    private class TripActivitiesTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                ListView listView = (ListView)findViewById(R.id.TripActivitiesList);


                list = Common.ParseActivities(result);

                Collections.sort(list, (Comparator<? super TripActivity>) new TripActivityComparator());

                adapter = new TripActivityListAdapter(TripActivityActivities.this, list);


                listView.setAdapter(adapter);

            }

            catch (Exception ex)
            {
                Toast.makeText(TripActivityActivities.this, "Unable to retrieve activities. Please try again.",
                        Toast.LENGTH_LONG).show();
            }


            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String responseXml = "";

            String endpoint = Common.API_ENDPOINT + "/api/TripActivities/" + Trip.Id;

            URI uri = null;

            try
            {
                uri = new URI(endpoint);
                HttpGet request = new HttpGet();
                request.setURI(uri);

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                responseXml = EntityUtils.toString(responseEntity);
            }

            catch (Exception ex)
            {

            }

            return responseXml;
        }
    }


    private class ActivityVoteTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

            if(result != null)
            {
                try
                {
                    boolean found = false;

                    TripActivityVote vote = Common.ParseTripActivityVote(result);

                    for(int i=0; i < list.size(); i++)
                    {
                        TripActivity activity =  list.get(i);

                        if(activity.Id == vote.TripActivity_Id)
                        {
                            for (int j = 0; j < activity.TripActivityVotes.size(); j++) {
                                TripActivityVote v = activity.TripActivityVotes.get(j);

                                if (v.Id == vote.Id) {
                                    v.Vote = vote.Vote;
                                    found = true;
                                    break;
                                }

                            }

                            if(!found)
                            {
                                activity.TripActivityVotes.add(vote);
                            }

                            break;
                        }
                    }

                    Collections.sort(list, (Comparator<? super TripActivity>) new TripActivityComparator());
                    adapter.notifyDataSetChanged();



                }

                catch (Exception ex)
                {
                    Toast.makeText(TripActivityActivities.this, "Unable send vote. Please try again.",
                            Toast.LENGTH_LONG).show();
                }

            }

            else
            {
                Toast.makeText(TripActivityActivities.this, "Unable send vote. Please try again.",
                        Toast.LENGTH_LONG).show();
            }

            progress.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {

            String responseXml = "";

            try
            {
                URI uri = new URI(Common.API_ENDPOINT + "/api/ActivityVotes");

                HttpPost request = new HttpPost();
                request.setURI(uri);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("TripUserId", tripUserId));
                nameValuePairs.add(new BasicNameValuePair("TripActivityId", params[0]));
                nameValuePairs.add(new BasicNameValuePair("Vote", params[1]));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                responseXml = EntityUtils.toString(responseEntity);
            }

            catch (Exception ex)
            {

            }


            return responseXml;
        }
    }

    private class ActivityCompleteTask extends AsyncTask<String, Void, Boolean> {
        String id;
        CheckBox cb;

        public ActivityCompleteTask(CheckBox cb, String id)
        {
            this.cb = cb;
            this.id = id;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(!result)
            {
                cb.setChecked(!cb.isChecked());

                Toast.makeText(TripActivityActivities.this, "Unable to update. Please try again.",
                        Toast.LENGTH_LONG).show();
            }

            else
            {
                TripActivity a;

                for(TripActivity activity : list)
                {
                    if( String.valueOf( activity.Id).equals(id))
                    {
                        activity.IsComplete = !activity.IsComplete;

                        Collections.sort(list, (Comparator<? super TripActivity>) new TripActivityComparator());
                        adapter.notifyDataSetChanged();

                        break;
                    }
                }
            }

            progress.dismiss();

        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean success = true;

            try
            {
                URI uri = new URI(Common.API_ENDPOINT + "/api/TripActivities/" + params[0] + "/Complete/" + params[1]);

                HttpPut request = new HttpPut();
                request.setURI(uri);

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                if(responseStatus.getStatusCode() ==  200)
                {
                    success = true;
                }

                else
                {
                    success = false;
                }
            }

            catch (Exception ex)
            {
                success = false;
            }


            return success;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == 1 && resultCode == Activity.RESULT_OK) {
            TripActivity activity = new TripActivity();

            activity.Activity = data.getStringExtra("activity");
            activity.Id = data.getIntExtra("id", -1);
            activity.TripUserId = data.getIntExtra("tripuserid", -1);
            //activity.Lat = Float.parseFloat( data.getStringExtra("lat"));
            //activity.Lon = Float.parseFloat( data.getStringExtra("lon"));
            activity.Details = data.getStringExtra("details");
            activity.TripId = data.getIntExtra("tripid", -1);
            activity.TripActivityVotes = new ArrayList<>();

            list.add(0, activity);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar actionbar = getSupportActionBar();
        actionBarMenu = menu;

        if(Trip != null) {

            SetupActionBar();

        }

        else
        {
            actionbar.setTitle("Trip");
        }

        return true;
    }

    public void SetupActionBar()
    {
        ActionBar actionbar = getSupportActionBar();

        actionbar.setTitle(Trip.Destination + " - " + "Activities");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip, actionBarMenu);
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


