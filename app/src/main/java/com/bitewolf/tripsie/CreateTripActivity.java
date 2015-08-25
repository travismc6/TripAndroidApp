package com.bitewolf.tripsie;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateTripActivity extends ActionBarActivity {

    private final int INVITE_USERS_CODE = 3;

    public static ArrayList<TripUser> tripUsers = new ArrayList<TripUser>();
    ProgressDialog progress = null;

    private  DatePicker mDatePicker;

    int[] departingDate = new int[3]; // month, day, year
    int[] returningDate = new int[3]; // month, day, year

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        tripUsers = new ArrayList<>();

        progress =  new ProgressDialog(this);

        Calendar c = Calendar.getInstance();

        departingDate[2] = c.get(Calendar.YEAR);
        departingDate[1] = c.get(Calendar.MONTH);
        departingDate[0] = c.get(Calendar.DAY_OF_MONTH);

        returningDate[2] = c.get(Calendar.YEAR);
        returningDate[1] = c.get(Calendar.MONTH);
        returningDate[0] = c.get(Calendar.DAY_OF_MONTH);


        Button next = (Button)findViewById(R.id.CreateTripNext);

        final TextView CreateTripDeparting = (TextView)findViewById(R.id.CreateTripDeparting);
        final TextView CreateTripReturning = (TextView)findViewById(R.id.CreateTripReturning);

        CreateTripDeparting.setText("");
        CreateTripReturning.setText("");

        CreateTripDeparting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });

        CreateTripDeparting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // @TODO Validation!
                DatePickerDialog dpd = new DatePickerDialog(CreateTripActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                CreateTripDeparting.setText( (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

                                departingDate[0] = dayOfMonth;
                                departingDate[1] = monthOfYear;
                                departingDate[2] = year;
                            }
                        }, departingDate[2], departingDate[1], departingDate[0]);
                dpd.show();

            }
        });


        CreateTripReturning.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // @TODO Validation!
                DatePickerDialog dpd = new DatePickerDialog(CreateTripActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                CreateTripReturning.setText( (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

                                returningDate[0] = dayOfMonth;
                                returningDate[1] = monthOfYear;
                                returningDate[2] = year;
                            }
                        }, returningDate[2], returningDate[1], returningDate[0]);
                dpd.show();

            }
        });

        // @TODO Validation!
        // @TODO default value
        CreateTripReturning.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                 int mYear = c.get(Calendar.YEAR);
                 int mMonth = c.get(Calendar.MONTH);
                 int mDay = c.get(Calendar.DAY_OF_MONTH);

                // @TODO Validation!
                DatePickerDialog dpd = new DatePickerDialog(CreateTripActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                CreateTripReturning.setText( (monthOfYear + 1 ) + "/" + dayOfMonth + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();

            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // validation checks
                TextView tvDestination = (TextView)findViewById(R.id.CreateTripDestination);
                TextView tvMyName = (TextView)findViewById(R.id.CreateTripName);
                TextView tvDescription = (TextView)findViewById(R.id.CreateTripDescription);

                TextView tvDeparting = (TextView)findViewById(R.id.CreateTripDeparting);
                TextView tvReturning = (TextView)findViewById(R.id.CreateTripReturning);

                String destination = tvDestination.getText().toString();
                String myName = tvMyName.getText().toString();
                String departing  = tvDeparting.getText().toString();
                String returning = tvReturning.getText().toString();

                if(myName.trim().length() == 0)
                {
                    Toast.makeText(CreateTripActivity.this,"Please enter a valid name.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if(destination.trim().length() == 0)
                {
                    Toast.makeText(CreateTripActivity.this,"Please enter a destination.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                else if(departing.trim().length() == 0)
                {
                    Toast.makeText(CreateTripActivity.this,"Please enter a departing date.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                else if(returning.trim().length() == 0)
                {
                    Toast.makeText(CreateTripActivity.this,"Please enter a returning date..",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(CreateTripActivity.this, TripUserListActivity.class);
                intent.putExtra("users", tripUsers);
                intent.putExtra("title", destination + "  " + departing + " - " + returning);
                startActivityForResult(intent, INVITE_USERS_CODE);

            }
        });
    }

    private class PostTripTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPostExecute(String result) {
            if(result == "")
            {
                Toast.makeText(CreateTripActivity.this,"Unable to create a trip. Please try again.",
                        Toast.LENGTH_LONG).show();
            }

            else
            {
                try {
                    Trip trip = Common.ParseTrip(result);

                    String myCode = "";
                    String myId = "";

                    if (trip != null) {
                        Toast.makeText(CreateTripActivity.this, "Trip created!",
                                Toast.LENGTH_SHORT).show();

                        TextView tvMyName = (TextView) findViewById(R.id.CreateTripName);
                        TextView tvDestination = (TextView) findViewById(R.id.CreateTripDestination);

                        String website = "http://tripsieappweb.azurewebsites.net/Trip/Detail/";

                        for (TripUser user : trip.Users) {
                            if (user.Phone != "") {
                                String message = tvMyName.getText().toString().trim() + " sent you an invite for to" + tvDestination.getText() + "!" +
                                        " Download Tripsie on Android or visit " + website + "/"+ user.Code + " to get started! Your code is " + user.Code + ".";
                                SmsManager smsManager = SmsManager.getDefault();

                                String message1 =  "Let's Tripsie! " + tvMyName.getText().toString().trim() + " sent you an invite for to " + tvDestination.getText() + "!" + " Your secret code is " + user.Code;

                                String message2 = "Download the Tripsie app for Android or visit " + website + user.Code + " to get started!";

                                smsManager.sendTextMessage(user.Phone, null, message1, null, null);
                                smsManager.sendTextMessage(user.Phone, null, message2, null, null);

                                if (user.IsCreator) {
                                    myCode = user.Code;
                                    myId = user.Id;
                                }
                            }
                        }

                        Common.SaveTrip(getApplicationContext(), trip.Code, trip.Destination, trip.StartDate, trip.EndDate, myCode);

                        Intent intent = new Intent(CreateTripActivity.this, TripDetailActivity.class);

                        TripDetailActivity.Trip = trip;
                        TripDetailActivity.MyCode = myCode;

                        startActivity(intent);

                        Common.SendTripUserRegistration(getApplicationContext(), myId);

                        finish();

                    } else {
                        Toast.makeText(CreateTripActivity.this, "Unable to create a trip. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }

                }

                catch(Exception ex)
                {
                    Toast.makeText(CreateTripActivity.this,"Unable to create a trip. Please try again.",
                            Toast.LENGTH_LONG).show();
                }

            }

            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... params)
        {
            TextView tvDestination = (TextView)findViewById(R.id.CreateTripDestination);
            TextView tvMyName = (TextView)findViewById(R.id.CreateTripName);
            TextView tvDescription = (TextView)findViewById(R.id.CreateTripDescription);

            TextView tvDeparting = (TextView)findViewById(R.id.CreateTripDeparting);
            TextView tvReturning = (TextView)findViewById(R.id.CreateTripReturning);

            String destination = tvDestination.getText().toString();
            String description  = tvDescription.getText().toString();
            String myName = tvMyName.getText().toString();
            String departing = tvDeparting.getText().toString();
            String returning = tvReturning.getText().toString();


            String responseXml = "";

            try {
                    URI uri = new URI(Common.API_ENDPOINT + "/api/Trips");

                HttpPost request = new HttpPost();
                request.setURI(uri);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("Destination", destination));
                nameValuePairs.add(new BasicNameValuePair("Description", description));
                nameValuePairs.add(new BasicNameValuePair("MyName", myName));
                nameValuePairs.add(new BasicNameValuePair("StartDate", departing));
                nameValuePairs.add(new BasicNameValuePair("EndDate", returning));

                JSONObject object = new JSONObject();

                JSONArray ja = new JSONArray();

                for(TripUser user : tripUsers) {
                    JSONObject jo = new JSONObject();
                    jo.put("DisplayName", user.DisplayName);
                    jo.put("Phone", user.Phone);


                    ja.put(jo);
                }

                object.put("users", ja);

                nameValuePairs.add(new BasicNameValuePair("UserJson", object.toString()));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                responseXml = EntityUtils.toString(responseEntity);

            }

            catch (Exception ex)
            {
                String s = ex.getMessage();
            }




            return responseXml;
        }

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode == INVITE_USERS_CODE && resultCode == Activity.RESULT_OK)
        {

            if(tripUsers.size() > 0) {
                progress.setTitle("Creating Trip");
                progress.setMessage("Please wait...");
                progress.show();

                new PostTripTask().execute();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_trip, menu);
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
