package com.bitewolf.tripsie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;


public class EnterCodeActivity extends ActionBarActivity {

    ProgressDialog progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        progress =  new ProgressDialog(this);

        final EditText codeText = (EditText)findViewById(R.id.EnterCodeCode);
        codeText.setText("");

        codeText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        Button submit = (Button)findViewById(R.id.EnterCodeSubmitButton);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // verify code
                if(codeText.getText().length() <5)
                {
                    Toast.makeText(EnterCodeActivity.this, "Please enter a valid code.",
                            Toast.LENGTH_LONG).show();
                }

                // attempt to get trip
                else
                {
                    progress.setTitle("Loading Trip");
                    progress.setMessage("Please wait...");
                    progress.show();

                    new EnterCodeTask().execute();
                }
            }
            });
    }

    private class EnterCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

            try {
                EditText codeText = (EditText)findViewById(R.id.EnterCodeCode);

                Trip trip = Common.ParseTrip(result);

                Common.SaveTrip(getApplicationContext(), trip.Code, trip.Destination, trip.StartDate, trip.EndDate, codeText.getText().toString());

                Intent intent = new Intent(EnterCodeActivity.this, TripDetailActivity.class);

                TripDetailActivity.Trip = trip;
                TripDetailActivity.MyCode = codeText.getText().toString();

                startActivity(intent);

                TripUser userMe = null;

                for (int i=0; i < trip.Users.size(); i++)
                {
                    TripUser user = trip.Users.get(i);

                    if(  user.Code.equals(codeText.getText().toString()))
                    {
                        userMe = user;
                    }
                }

                if(userMe != null) {
                    Common.SendTripUserRegistration(getApplicationContext(), userMe.Id);
                }

                finish();
            }

            catch (Exception ex)
            {
                Toast.makeText(EnterCodeActivity.this,"Unable to find trip. Please try again.",
                        Toast.LENGTH_LONG).show();
            }

            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String responseXml = "";

            EditText codeText = (EditText)findViewById(R.id.EnterCodeCode);

            try
            {
                URI uri = new URI(Common.API_ENDPOINT + "/api/UserCode/" + codeText.getText());

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_code, menu);
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
