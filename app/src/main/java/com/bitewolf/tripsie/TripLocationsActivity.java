package com.bitewolf.tripsie;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;

public class TripLocationsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    String tripId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_locations);
        tripId =  getIntent().getStringExtra("TripId");
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tripId =  getIntent().getStringExtra("TripId");

        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // load trips
        mMap.clear();

        new GetTripTask().execute(tripId);
    }

    private class GetTripTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String result) {

            if(result == "")
            {
                Toast.makeText(TripLocationsActivity.this, "Unable to retrieve locations.",
                        Toast.LENGTH_LONG).show();
            }

            else
            {
                // Parse json
                try {

                    JSONObject jObject = new JSONObject(result);

                    Trip trip = Common.ParseTrip(result);

                    TripUser last =null;

                    for(TripUser user : trip.Users)
                    {
                        if(user.Lat != 0) {

                             mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(user.Lat, user.Lon))
                                    .title(user.DisplayName));

                            last = user;

                        }
                    }

                    if(last != null)
                    {
                        LatLng ltln = new LatLng(last.Lat, last.Lon);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltln, 10));
                    }



                }

                catch (Exception ex)
                {
                    Toast.makeText(TripLocationsActivity.this,"Unable to retrieve locations.",
                            Toast.LENGTH_LONG).show();
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
}
