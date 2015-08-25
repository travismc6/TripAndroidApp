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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class TripConversationActivity extends ActionBarActivity {

    int code = -1;

    String displayName;
    String userId;

    ProgressDialog progress = null;

    ArrayList<TripComment> tripComments = new ArrayList<>();
    TripCommentListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_conversation);

        Intent intent = getIntent();

        String id = intent.getStringExtra("TripId");
        code = Integer.valueOf(id);
        userId = intent.getStringExtra("userId");
        displayName = intent.getStringExtra("userName");

        progress =  new ProgressDialog(this);

        progress.setTitle("Loading comments");
        progress.setMessage("Please wait...");
        progress.show();

        new LoadCommentsTask().execute(String.valueOf(code));

        Button btnSend = (Button)findViewById(R.id.TripConversationNewCommentSend);
        final EditText text = (EditText)findViewById(R.id.TripConversationNewComment);

        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(text.getText().toString().trim() != "") {

                    progress.setTitle("Posting comment");
                    progress.setMessage("Please wait...");
                    progress.show();

                    new PostCommentTask().execute(userId, text.getText().toString());
                }
            }
        });

    }

    private class PostCommentTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

            try
            {
                JSONObject jObject = new JSONObject(result);

                TripComment comment = new TripComment();
                comment.Username = displayName;
                comment.Comment = jObject.getString("Comment");
                String dateString =  jObject.getString("Date");
                comment.Date = new DateTime(dateString) ;

                tripComments.add(0, comment);

                adapter.notifyDataSetChanged();

                EditText text = (EditText)findViewById(R.id.TripConversationNewComment);
                text.setText("");
            }

            catch (Exception ex)
            {
                Toast.makeText(TripConversationActivity.this, "Unable to post comment. Please try again.",
                        Toast.LENGTH_LONG).show();
            }


            progress.dismiss();

        }

        @Override
        protected String doInBackground(String... params) {

            String xml = "";

            try {
                URI uri = new URI(Common.API_ENDPOINT + "/api/TripComments");

                HttpPost request = new HttpPost();
                request.setURI(uri);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("TripUserId", params[0]));
                nameValuePairs.add(new BasicNameValuePair("Comment", params[1]));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                xml = EntityUtils.toString(responseEntity);

            }

            catch (Exception ex)
            {

            }

            return xml;

        }
    }

    private class LoadCommentsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String result) {

            final ListView listView = (ListView) findViewById(R.id.TripConversationList);

            try
            {

                JSONArray array = new JSONArray(result);

                for (int  j = 0; j < array.length(); j++)
                {
                    TripComment comment = new TripComment();

                    JSONObject jObject = array.getJSONObject(j);

                    comment.Comment = jObject.getString("Comment");

                    JSONObject user = jObject.getJSONObject("TripUser");
                    comment.Username = user.getString("DisplayName");

                    String dateString =  jObject.getString("Date");



                    comment.Date = new DateTime(dateString) ;

                    TimeZone tz = TimeZone.getDefault();
                    Date now = new Date();
                    int offsetFromUtc = tz.getOffset(now.getTime());

                    comment.Date = comment.Date.plus(offsetFromUtc);

                    tripComments.add(comment);
                }

                Collections.sort(tripComments, (Comparator<? super TripComment>) new CommentComparator());

                adapter = new TripCommentListAdapter(TripConversationActivity.this, tripComments);



                listView.setAdapter(adapter);
            }

            catch (Exception ex)
            {
                Toast.makeText(TripConversationActivity.this, "Unable load comments. Please try again.",
                        Toast.LENGTH_LONG).show();
            }

            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String xml = "";

            Boolean result = false;

            try
            {

                URI uri = new URI(Common.API_ENDPOINT + "/api/TripComments/List/" + params[0]);

                HttpGet request = new HttpGet();
                request.setURI(uri);

                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();

                xml = EntityUtils.toString(responseEntity);

            }

            catch (Exception ex)
            {

            }



            return xml;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Intent intent = getIntent();
        String display = intent.getStringExtra("DisplayName");

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(display);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_conversation, menu);
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


