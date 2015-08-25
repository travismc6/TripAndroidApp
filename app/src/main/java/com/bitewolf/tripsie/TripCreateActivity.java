package com.bitewolf.tripsie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class TripCreateActivity extends ActionBarActivity {

    ProgressDialog progress = null;
    public static Trip trip;
    public static String tripUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activity);

        progress =  new ProgressDialog(this);

        final EditText destination = (EditText)findViewById(R.id.CreateTripDestination);
        final EditText details = (EditText)findViewById(R.id.CreateTripDescription);

        Button createButton = (Button)findViewById(R.id.CreateActivityCreate);

        Intent intent = getIntent();

        tripUser = intent.getStringExtra("userMe");

        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(destination.getText().toString().trim() == "")
                {
                    Toast.makeText(TripCreateActivity.this, "Please enter a activity name.",
                            Toast.LENGTH_LONG).show();

                    return;
                }

                else
                {
                    progress.setTitle("Creating Activity");
                    progress.setMessage("Please wait...");
                    progress.show();

                    new CreateActivityTask().execute(destination.getText().toString(), details.getText().toString());
                }
            }
        });
    }

    private class CreateActivityTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            if (result != "") {

                try
                {
                    TripActivity activity = new TripActivity();

                    activity = Common.ParseActivity(result);

                    if(activity != null) {

                        Intent i = new Intent();
                        i.putExtra("id", activity.Id);
                        i.putExtra("activity", activity.Activity);
                        i.putExtra("tripuserid", activity.TripUserId);
                        i.putExtra("lat", activity.Lat);
                        i.putExtra("lon", activity.Lon);
                        i.putExtra("details", activity.Details);
                        i.putExtra("tripid", activity.TripId);

                        setResult(RESULT_OK, i);

                        finish();
                    }


                }

                catch (Exception ex)
                {
                    Toast.makeText(TripCreateActivity.this, "Unable to create Activity. Please try again.",
                            Toast.LENGTH_LONG).show();
                }
            }

            else
            {
                Toast.makeText(TripCreateActivity.this, "Unable to create Activity. Please try again.",
                        Toast.LENGTH_LONG).show();
            }

            progress.dismiss();
        }

        @Override
        protected String doInBackground (String...params)
        {
            String responseXml = "";

            final EditText activity = (EditText)findViewById(R.id.CreateTripDestination);
            final EditText details = (EditText)findViewById(R.id.CreateTripDescription);

            try
            {
                URI uri = new URI(Common.API_ENDPOINT + "/api/TripActivities");

                HttpPost request = new HttpPost();
                request.setURI(uri);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("Activity", activity.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("Details", details.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("TripId", String.valueOf( trip.Id )));
                nameValuePairs.add(new BasicNameValuePair("TripUserId", tripUser ));

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(trip.Destination + " - " + "Create Activity");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_create, menu);
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
