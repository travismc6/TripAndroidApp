package com.bitewolf.tripsie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;


public class TripListActivity extends ActionBarActivity {

    ProgressDialog progress = null;

    String myCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        progress =  new ProgressDialog(this);

        final ListView listView = (ListView) findViewById(R.id.TripListTrips);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                TripCode itemValue = (TripCode) listView.getItemAtPosition(position);

                progress.setTitle("Loading Trip");
                progress.setMessage("Please wait...");
                progress.show();

                myCode = itemValue.MyCode;

                new GetTripTask().execute(itemValue.Code);

            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Trips", Context.MODE_PRIVATE);

        String current = pref.getString("MyTrips", "");

        if(current != "") {

            current.split(";");

            ArrayList<TripCode> list = new ArrayList<>();

            for (String trip : current.split(";")) {

                try {
                    String[] details = trip.split(",");

                    // code, display
                    TripCode code = new TripCode();
                    code.Code = details[0];
                    code.Destination = details[1];
                    code.StartDate = details[2];
                    code.EndDate = details[3];
                    code.MyCode = details[4];

                     list.add(code);
                }

                catch (Exception ex)
                {
                    // @TODO: why is there an error?
                }
            }

            TripCodeListAdapter adapter = new TripCodeListAdapter(this, list);
            listView.setAdapter(adapter);
        }


    }

    private class GetTripTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String result) {

            if(result == "")
            {
                Toast.makeText(TripListActivity.this,"Unable to retrieve a trip. Please try again.",
                        Toast.LENGTH_LONG).show();
            }

            else
            {
                // Parse json
                try {

                    JSONObject jObject = new JSONObject(result);

                    Trip trip = Common.ParseTrip(result);

                    Intent intent = new Intent(TripListActivity.this, TripDetailActivity.class);

                    TripDetailActivity.Trip = trip;
                    TripDetailActivity.MyCode = myCode;

                    startActivity(intent);

                    finish();
                }

                catch (Exception ex)
                {
                    Toast.makeText(TripListActivity.this,"Unable to retrieve trip. Please try again.",
                            Toast.LENGTH_LONG).show();
                }

            }

            progress.dismiss();

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_list, menu);
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
