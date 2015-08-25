package com.bitewolf.tripsie;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.LocationListener;

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
import java.util.List;


public class TripDetailActivity extends ActionBarActivity implements android.location.LocationListener{

    public static Trip Trip;
    public static String MyCode;
    TripUser UserMe;

    private TripUserListAdapter adapter;

    ProgressDialog progress = null;

    TextView code;
    TextView dates;
    TextView description;
    ListView listView;
    RadioGroup radioGroup;
     RadioButton inButton;
     RadioButton outButton;
    TextView dateDeparting;
    TextView dateReturning;

    int[] departingDate = new int[3]; // month, day, year
    int[] returningDate = new int[3]; // month, day, year

    Menu actionBarMenu;

    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

         dates = (TextView)findViewById(R.id.TripDetailDate);
         description = (TextView)findViewById(R.id.TripDetailDescription);
         listView = (ListView)findViewById(R.id.TripDetailUserList);
         radioGroup = (RadioGroup)findViewById(R.id.TripDetailsRadioGroup);
         inButton = (RadioButton) findViewById(R.id.TripDetailInRadio);
         outButton = (RadioButton) findViewById(R.id.TripDetailOutRadio);
        code = (TextView)findViewById(R.id.TripDetailCode);

        dateDeparting = (TextView)findViewById(R.id.TripDetailDeparting);
        dateReturning = (TextView)findViewById(R.id.TripDetailReturning);

        dateDeparting.setText("");
        dateReturning.setText("");

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        progress =  new ProgressDialog(this);

        if(Trip != null)
        {
            SetupPage();
        }

        else
        {
           Intent i = getIntent();

           Bundle b = i.getExtras();

           String tripCode = (String)i.getExtras().get("tripCode");
           MyCode =  (String)i.getExtras().get("tripUserCode");

           progress.setTitle("Loading Trip");
           progress.setMessage("Please wait...");
           progress.show();

          new GetTripTask().execute(tripCode);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        Trip = null;
    }

    private void SetupPage()
    {
        for(TripUser user : Trip.Users)
        {
            if(user.Code.equals(MyCode))
            {
                UserMe = user;
            }
        }

        String[] departingString = Trip.StartDate.split("/");
        String[] returningString = Trip.EndDate.split("/");

        departingDate[0] = Integer.valueOf( departingString[0]);
        departingDate[1] = Integer.valueOf( departingString[1]);
        departingDate[2] = Integer.valueOf( departingString[2]);

        returningDate[0] = Integer.valueOf( returningString[0]);
        returningDate[1] = Integer.valueOf( returningString[1]);
        returningDate[2] = Integer.valueOf( returningString[2]);


        dateReturning.setText(Trip.EndDate);
        dateDeparting.setText(Trip.StartDate);

        if(UserMe.IsCreator) {

            dateDeparting.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    // @TODO Validation!
                    DatePickerDialog dpd = new DatePickerDialog(TripDetailActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    progress.setTitle("Updating Departure Date");
                                    progress.setMessage("Please wait...");
                                    progress.show();

                                    new UpdateTripDate().execute("start", (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);


                                }
                            }, departingDate[2], departingDate[0] - 1, departingDate[1]);
                    dpd.show();

                }
            });

            dateReturning.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    // @TODO Validation!
                    DatePickerDialog dpd = new DatePickerDialog(TripDetailActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    progress.setTitle("Updating Return Date");
                                    progress.setMessage("Please wait...");
                                    progress.show();

                                    new UpdateTripDate().execute("end", (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

                                }
                            }, returningDate[2], returningDate[0] - 1, returningDate[1]);
                    dpd.show();

                }
            });
        }

        code.setText(UserMe.Code);

        dates.setText(Trip.StartDate + " - " + Trip.EndDate);
        description.setText(Trip.Description);

        adapter = new TripUserListAdapter(this, Trip.Users, UserMe.Id);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater li = LayoutInflater.from(TripDetailActivity.this);
                View promptsView = li.inflate(R.layout.message_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        TripDetailActivity.this);


                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.messagePromptMessage);

                final TextView title = (TextView) promptsView
                        .findViewById(R.id.messagePromptHeader);

                title.setText("");

                userInput.setText(UserMe.DisplayName);

                alertDialogBuilder
                        .setTitle("Update Display Name")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        progress.setTitle("Updating your name");
                                        progress.setMessage("Please wait...");
                                        progress.show();


                                        new UpdateUserTask().execute(UserMe.Id, userInput.getText().toString(), String.valueOf(Trip.Id));

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        if(UserMe.IsCreator)
        {
            radioGroup.setVisibility(View.GONE);
        }

        else
        {

            if (UserMe.TripStatus == 1)
            {
                inButton.setChecked(true);
            }
            else if (UserMe.TripStatus == 2)
            {
                outButton.setChecked(true);
            }

            inButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    outButton.setChecked(false);

                    progress.setTitle("Sending your response");
                    progress.setMessage("Please wait...");
                    progress.show();

                    new TripResponseTask().execute("true");

                }
            });

            outButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    ShowOutPrompt();
                }
            });
        }
    }

    private void ShowOutPrompt()
    {
        LayoutInflater li = LayoutInflater.from(TripDetailActivity.this);
        View promptsView = li.inflate(R.layout.message_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TripDetailActivity.this);


        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.messagePromptMessage);

        // set dialog message
        alertDialogBuilder
                .setTitle("Are you sure you're out?")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                inButton.setChecked(false);

                                progress.setTitle("Sending your response");
                                progress.setMessage("Please wait...");
                                progress.show();

                                new TripResponseTask().execute("false", userInput.getText().toString());

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                outButton.setChecked(false);

                                if (UserMe.TripStatus == 1) {
                                    inButton.setChecked(true);
                                }
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationManager.removeUpdates((android.location.LocationListener) this);

        if(location.getAccuracy() < 200)
        {
            UserMe.Lat = location.getLatitude();
            UserMe.Lon = location.getLongitude();

            progress.setTitle("Sending your response");
            progress.setMessage("Please wait...");
            progress.show();



            new UpdateLocationTask().execute();

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class UpdateLocationTask extends AsyncTask<Double, Boolean, Boolean> {
        @Override
        protected void onPostExecute(Boolean result) {

            if(result)
            {
                Toast.makeText(TripDetailActivity.this,"Updated your location.",
                        Toast.LENGTH_SHORT).show();
            }


            progress.dismiss();

        }

        @Override
        protected Boolean doInBackground(Double... params) {
            boolean result = false;

            String endpoint = Common.API_ENDPOINT + "/api/TripUsers/UpdateLocation/" + UserMe.Code + "/" + String.valueOf( UserMe.Lat) + "/" + String.valueOf( UserMe.Lon) + "/";

            URI uri = null;
            try {
                uri = new URI(endpoint);
                HttpPut request = new HttpPut();
                request.setURI(uri);

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                if(responseStatus.getStatusCode() == 200)
                {
                    result = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;

        }
    }

    private class GetTripTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String result) {

            if(result == "")
            {
                Toast.makeText(TripDetailActivity.this,"Unable to retrieve a trip.",
                        Toast.LENGTH_LONG).show();
            }

            else
            {
                // Parse json
                try {

                    JSONObject jObject = new JSONObject(result);

                    Trip trip = Common.ParseTrip(result);
                    Trip = trip;

                    SetActionBar();
                    SetupPage();

                }

                catch (Exception ex)
                {
                    Toast.makeText(TripDetailActivity.this,"Unable to retrieve trip.",
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

    private class TripResponseTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPostExecute(Boolean result) {

            final RadioButton inButton = (RadioButton) findViewById(R.id.TripDetailInRadio);
            final RadioButton outButton = (RadioButton) findViewById(R.id.TripDetailOutRadio);

            if (!result)
            {
                /*
                Toast.makeText(TripDetailActivity.this, "Unable to set your response. Please try again..",
                        Toast.LENGTH_LONG).show();
                        */

                inButton.setChecked(!inButton.isChecked());
                outButton.setChecked(!outButton.isChecked());
            }

            else
            {
                UserMe.TripStatus = inButton.isChecked() ? 1 : 2;

                adapter.notifyDataSetChanged();
            }

            progress.dismiss();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Boolean result = false;

            try
            {
                String comment = "-1111";

                if(params.length > 1 && !params[1].isEmpty())
                {
                    comment = params[1];
                }

                URI uri = new URI(Common.API_ENDPOINT + "/api/TripUsers/Response/" + UserMe.Code + "/" + params[0] + "/" + comment);

                HttpPut request = new HttpPut();
                request.setURI(uri);

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                String s  = EntityUtils.toString(responseEntity);

                if(responseStatus.getStatusCode() == 200)
                {
                    result = true;
                }

                else
                {
                    Toast.makeText(TripDetailActivity.this, responseEntity.getContent().toString(),
                            Toast.LENGTH_LONG).show();
                }

            }

            catch (Exception ex)
            {
                Toast.makeText(TripDetailActivity.this, ex.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

            return result;
        }
    }

    private class UpdateTripDate extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

            if (result.isEmpty())
            {
                Toast.makeText(TripDetailActivity.this, "Unable update date. Please try again..",
                        Toast.LENGTH_LONG).show();

            }

            else {
                try {


                    Trip newTrip = Common.ParseTrip(result);

                    dateDeparting.setText(newTrip.StartDate);
                    dateReturning.setText(newTrip.EndDate);

                    Trip.StartDate = newTrip.StartDate;
                    Trip.EndDate = newTrip.EndDate;

                    Toast.makeText(TripDetailActivity.this, "Updated trip date!",
                            Toast.LENGTH_LONG).show();

                    String[] departingString = Trip.StartDate.split("/");
                    String[] returningString = Trip.EndDate.split("/");

                    departingDate[0] = Integer.valueOf( departingString[0]);
                    departingDate[1] = Integer.valueOf( departingString[1]);
                    departingDate[2] = Integer.valueOf( departingString[2]);

                    returningDate[0] = Integer.valueOf( returningString[0]);
                    returningDate[1] = Integer.valueOf( returningString[1]);
                    returningDate[2] = Integer.valueOf(returningString[2]);

                }

                catch(Exception ex)
                {
                    Toast.makeText(TripDetailActivity.this, "Unable update date. Please try again..",
                            Toast.LENGTH_LONG).show();
                }
            }

            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            String value = params[1].replace("/", "-");
            String route = params[0];


            try
            {
                URI uri = new URI(Common.API_ENDPOINT + "/api/Trips/" + Trip.Id + "/" + route + "/" + value);

                HttpPut request = new HttpPut();
                request.setURI(uri);

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();


                result = EntityUtils.toString(responseEntity);
            }

            catch (Exception ex)
            {

            }

            return result;
        }
    }

    private class UpdateUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {


            if (result.isEmpty())
            {
                Toast.makeText(TripDetailActivity.this, "Unable to set your Display Name. Please try again..",
                        Toast.LENGTH_LONG).show();

            }

            else
            {
                //@TODO Update list
                TripUser user = Common.ParseTripUser(result);

                try {

                    for (TripUser u : Trip.Users) {
                        if (u.Id.equals(UserMe.Id)) {
                            u.DisplayName = user.DisplayName;
                            UserMe.DisplayName = user.DisplayName;
                            break;
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                catch (Exception ex)
                {
                    Toast.makeText(TripDetailActivity.this, "Unable to set your Display Name. Please try again..",
                            Toast.LENGTH_LONG).show();
                }
            }

            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "";

            try
            {
                URI uri = new URI(Common.API_ENDPOINT + "/api/TripUsers/" + params[0] + "/name/" + params[1]);

                HttpPost request = new HttpPost();
                request.setURI(uri);

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                result = EntityUtils.toString(responseEntity);


            }

            catch (Exception ex)
            {

            }

            return result;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar actionbar = getSupportActionBar();
        actionBarMenu = menu;

        if(Trip != null) {
            SetActionBar();
        }

        else
        {
            actionbar.setTitle("Trip");
        }

        return true;
    }

    private void SetActionBar() {
        ActionBar actionbar = getSupportActionBar();

        actionbar.setTitle(Trip.Destination);

        //actionbar.setTitle(Trip.Destination + " (" + UserMe.Code + ")");

        getMenuInflater().inflate(R.menu.menu_trip, actionBarMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.trip_comments) {

            Intent intent = new Intent(TripDetailActivity.this, TripConversationActivity.class);

            intent.putExtra("TripId", String.valueOf( Trip.Id) );
            intent.putExtra("DisplayName", Trip.Destination + " " + Trip.StartDate + " - " + Trip.EndDate);
            intent.putExtra("userName", this.UserMe.DisplayName);
            intent.putExtra("userId", this.UserMe.Id);

            startActivity(intent);

            return true;
        }

        else if(id == R.id.trip_activities)
        {
            Intent intent = new Intent(TripDetailActivity.this, TripActivityActivities.class);

            TripActivityActivities.Trip = Trip;

            intent.putExtra("TripId", Trip.Id);
            intent.putExtra("DisplayName", Trip.Destination + " " + Trip.StartDate + " - " + Trip.EndDate);
            intent.putExtra("tripUserId", this.UserMe.Id);

            startActivity(intent);

            return true;
        }

        else if(id == R.id.trip_update_location)
        {
            /*
            UserMe.Lat = 41;
            UserMe.Lon = -87;

            progress.setTitle("Sending your response");
            progress.setMessage("Please wait...");
            progress.show();

            new UpdateLocationTask().execute();

            */


            try {
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);
            }

            catch (Exception ex)
            {
                int i = 0;
                int j = 1;
            }
        }

        else if(id == R.id.trip_location)
        {
            Intent intent = new Intent(TripDetailActivity.this, TripLocationsActivity.class);
            intent.putExtra("TripId", Trip.Code);


            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
