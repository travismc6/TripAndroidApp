package com.bitewolf.tripsie;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import java.util.List;

/**
 * Created by Travis on 2/8/2015.
 */
public class Common {

    public static final String TripPreferences = "Trips";
    public static final String GcmPreferences = "GcmRegistration";
    public static final String PROPERTY_REG_ID = "registration_id";

    //debug
    public static  String  API_ENDPOINT = "http://teambitewolf.azurewebsites.net";

    /**
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getPreferences(Context context, String type) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(type,
                Context.MODE_PRIVATE);
    }

    public static void SetAPIEndpoint(boolean remote)
    {
        if(remote)
        {
           //API_ENDPOINT = "http://services.teambitewolf.com/TripService"; //godaddy
            API_ENDPOINT = "http://teambitewolf.azurewebsites.net"; // azure
        }

        else
        {
            API_ENDPOINT = "http://192.168.0.4:49669";
        }
    }

    // production
    //public static final String  API_ENDPOINT = "http://services.teambitewolf.com/TripService";

    public static boolean SaveTrip(Context context, String code, String Destination, String Start, String End, String myUserCode)
    {
        SharedPreferences pref = getPreferences(context, TripPreferences);
        SharedPreferences.Editor editor = pref.edit();

        String current = pref.getString("MyTrips", "");

        current += code + "," + Destination + "," + Start + "," + End + "," + myUserCode + ";";
        editor.putString("MyTrips", current);

        editor.commit();

        return true;
    }
    public static Trip ParseTrip(String result)
    {
        Trip trip = null;

        try {
            String myCode = "";
            JSONObject jObject = new JSONObject(result);

            trip = new Trip();
            trip.Description = jObject.getString("Description");
            trip.Destination = jObject.getString("Destination");
            trip.EndDate = jObject.getString("EndDate");
            trip.StartDate = jObject.getString("StartDate");
            trip.Users = new ArrayList<>();
            trip.Code = jObject.getString("Code");
            trip.Id = jObject.getInt("Id");

            JSONArray jsonArray = jObject.getJSONArray("Users");

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject o = jsonArray.getJSONObject(j);

                TripUser user = new TripUser();
                user.Phone = o.getString("Phone");
                user.Email = o.getString("Email");
                user.DisplayName = o.getString("DisplayName");
                user.Id = o.getString("Id");
                user.Code = o.getString("TripCode");
                user.IsCreator = o.getBoolean("IsCreator");
                user.TripStatus = o.getInt("TripStatus");
                user.Lat = o.getDouble("Lat");
                user.Lon = o.getDouble("Lon");

                trip.Users.add(user);

            }
        }

        catch(Exception ex)
            {

            }

        return trip;
    }

    public static TripUser ParseTripUser(String result)
    {
        TripUser user = new TripUser();

        try {
            JSONObject o = new JSONObject(result);

            user.Phone = o.getString("Phone");
            user.Email = o.getString("Email");
            user.DisplayName = o.getString("DisplayName");
            user.Id = o.getString("Id");
            user.Code = o.getString("TripCode");
            user.IsCreator = o.getBoolean("IsCreator");
            user.TripStatus = o.getInt("TripStatus");
            user.Lat = o.getDouble("Lat");
            user.Lon = o.getDouble("Lon");
        }

        catch (Exception ex)
        {
            return null;
        }




        return user;
    }

    public static TripActivityVote ParseTripActivityVote(String result)
    {
        TripActivityVote vote = new TripActivityVote();

        try
        {
            JSONObject jsonObject = new JSONObject(result);

            vote.Id = jsonObject.getInt("Id");
            vote.TripActivity_Id = jsonObject.getInt("TripActivityId");
            vote.TripUserId = jsonObject.getInt("TripUserId");
            vote.Vote = jsonObject.getInt("Vote");
        }

        catch (Exception ex)
        {
            vote = null;
        }

        return  vote;

    }

    public static ArrayList<TripActivityVote> ParseTripActivityVotes(String result)
    {
        ArrayList<TripActivityVote> list = new ArrayList<TripActivityVote>();

        try
        {
            JSONArray jsonArray = new JSONArray(result);

            for(int i=0; i < jsonArray.length(); i++)
            {

                TripActivityVote vote = ParseTripActivityVote(jsonArray.getJSONObject(i).toString(0));

                if(vote != null)
                list.add(vote);
            }
        }

        catch (Exception ex)
        {

        }

        return list;
    }

    public static TripActivity ParseActivity(String result)
    {
        TripActivity activity = new TripActivity();

        try
        {
                JSONObject jsonObject = new JSONObject(result);

                activity.Id = jsonObject.getInt("Id");
                activity.Activity = jsonObject.getString("Activity");
                activity.Details = jsonObject.getString("Details");
                activity.TripId = jsonObject.getInt("TripId");
                activity.TripUserId = jsonObject.getInt("TripUserId");
            activity.IsComplete = jsonObject.getBoolean("IsComplete");

                activity.TripActivityVotes = ParseTripActivityVotes(jsonObject.getString("ActivityVotes"));
        }

        catch (Exception ex)
        {

        }


        return activity;
    }

    public static ArrayList<TripActivity> ParseActivities(String result)
    {

        ArrayList<TripActivity> list = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(result);

            for (int i = 0; i < array.length(); i++) {


                JSONObject object = array.getJSONObject(i);

                TripActivity activity = ParseActivity( object.toString(0) );

                /*
                TripActivity activity = new TripActivity();

                activity.Id = object.getInt("Id");
                activity.Activity = object.getString("Activity");
                activity.Details = object.getString("Details");
                activity.Lat = Float.parseFloat(object.getString("Lat"));
                activity.Lon = Float.parseFloat(object.getString("Lon"));
                activity.TripActivityVotes = new ArrayList<>();
                */

                // @TODO get votes

                list.add(activity);
            }

        }

        catch (Exception ex)
        {

        }

        return  list;
    }

    public static void SendTripUserRegistration(Context context, String id)
    {

        final SharedPreferences prefs = Common.getPreferences(context, Common.GcmPreferences);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (!registrationId.isEmpty()) {

            new PostPushNotificationRegistrationTask().execute(registrationId, id);

        }

    }

    private static class PostPushNotificationRegistrationTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result)
        {
            RegistrationPushNotification notification = new RegistrationPushNotification();

            try
            {
                JSONObject object = new JSONObject(result);

                notification.Id = object.getInt("Id");
                notification.RegistrationId = object.getString("RegistrationId");
                notification.TripUserId = object.getString("TripUserId");

            }

            catch (Exception ex)
            {
                // @TODO Request failed

            }

        }

        @Override
        protected String doInBackground(String... params)
        {
            String responseXml = "";

            try
            {
                URI uri = new URI(Common.API_ENDPOINT + "/api/PushRegistrations");

                HttpPost request = new HttpPost();
                request.setURI(uri);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("RegistrationId", params[0]));
                nameValuePairs.add(new BasicNameValuePair("TripUserId", params[1]));

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
}
