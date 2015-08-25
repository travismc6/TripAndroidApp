package com.bitewolf.tripsie;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


public class Settings extends ActionBarActivity {

    ToggleButton soundButton;
    ToggleButton notificationButton;
    ToggleButton servicesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        soundButton = (ToggleButton)findViewById(R.id.SettingsSound);
        notificationButton = (ToggleButton)findViewById(R.id.SettingsNotifications);
        servicesButton = (ToggleButton)findViewById(R.id.SettingsServices);

        SharedPreferences prefs = Common.getPreferences(Settings.this, "Settings");
        final SharedPreferences.Editor editor = prefs.edit();

        boolean sound = prefs.getBoolean("Sound", true);
        boolean notifications = prefs.getBoolean("Notifications", true);
        boolean services = prefs.getBoolean("RemoteServices", true);

        soundButton.setChecked(sound);
        notificationButton.setChecked(notifications);
        servicesButton.setChecked(services);

        soundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("Sound", buttonView.isChecked());
                editor.commit();
            }
        });

        notificationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("Notifications", buttonView.isChecked());

                editor.commit();
            }
        });

        servicesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("RemoteServices", buttonView.isChecked());
                Common.SetAPIEndpoint(buttonView.isChecked());
                editor.commit();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
