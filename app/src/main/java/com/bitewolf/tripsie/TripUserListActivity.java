package com.bitewolf.tripsie;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TripUserListActivity extends ActionBarActivity {

    static ArrayList<TripUser> tripUsers = new ArrayList<>();
    ArrayAdapter<TripUser> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_user_list);

        ListView listView = (ListView) findViewById(R.id.TripUserListView);

        Bundle b = this.getIntent().getExtras();
        tripUsers = (ArrayList<TripUser>) b.get("users");

        String title = b.getString("title");



        this.setTitle(title);



        adapter= new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, tripUsers);

        listView.setAdapter(adapter);

        TextView addContact = (TextView) findViewById(R.id.TripUsersAddContact);

        addContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        Button buttonNext = (Button) findViewById(R.id.TripUsersCreate);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(tripUsers.size() == 0)
                {
                    Toast.makeText(TripUserListActivity.this, "Please invite at least on person.",
                            Toast.LENGTH_LONG).show();
                    return;
                }


                else
                {
                    Intent i = new Intent();


                    CreateTripActivity.tripUsers = tripUsers;

                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        super.onActivityResult(reqCode, resultCode, data);

        String email="";
        String phone="";

        String displayName = "";

        if(reqCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();

            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(contactData,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {


                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            System.out.println("phone" + phone);
                        }
                        pCur.close();


                        // get email and type
                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            email = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        }
                        emailCur.close();
                    }
                }
            }

            TripUser user = new TripUser();
            user.DisplayName = displayName;

            // @TODO support email
            if (email != "" && false) {
                user.Email = email;
                //tripUsers.add(user);
            }

            if (phone != "") {
                user.Phone = phone;
                tripUsers.add(user);
            }

            adapter.notifyDataSetChanged();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tirp_user_list, menu);
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
