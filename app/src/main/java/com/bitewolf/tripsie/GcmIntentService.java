package com.bitewolf.tripsie;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Travis on 3/1/2015.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType))
            {

            }

            else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType))
            {

                // If it's a regular GCM message, do some work.
            }

            else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType))
            {

                // Post notification of received message.
                sendNotification(extras);

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle extras) {

        SharedPreferences prefs = Common.getPreferences(GcmIntentService.this, "Settings");
        boolean showNotification = prefs.getBoolean("Notifications", true);

        if(!showNotification) {
            return;
        }


        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri path = Uri.parse("android.resource://com.bitewolf.tripsie/raw/hey");

        String title = extras.getString("titleMsg");
        String msg = extras.getString("contentMsg");
        String tripId = extras.getString("tripId");
        String tripUserId = extras.getString("tripUserId");
        String type = extras.getString("type");
        String tripCode = extras.getString("tripCode");
        String tripUserCode = extras.getString("tripUserCode");
        String tripUserName = extras.getString("tripUserName");
        String tripDisplayName = extras.getString("tripDisplayName");

        PendingIntent contentIntent = null;

        if(type.toLowerCase().equals("details")) {


            Intent i = new Intent(this, TripDetailActivity.class);
            i.putExtra("tripCode", tripCode);
            i.putExtra("tripId", tripId.toString());
            i.putExtra("tripUserCode", tripUserCode);


            //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);



            android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);

            stackBuilder.addParentStack(TripDetailActivity.class);
            stackBuilder.addNextIntent(i);

            contentIntent = stackBuilder.getPendingIntent(Integer.valueOf(tripId), PendingIntent.FLAG_CANCEL_CURRENT);

        }

        else if(type.toLowerCase().equals("comments")) {

             path = Uri.parse("android.resource://com.bitewolf.tripsie/raw/whatsgoing");

            Intent i = new Intent(this, TripConversationActivity.class);
            i.putExtra("TripId", tripId);
            i.putExtra("userId", tripUserId);
            i.putExtra("userName", tripUserName);
            i.putExtra("DisplayName", tripDisplayName);

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);

            stackBuilder.addParentStack(TripDetailActivity.class);


            Intent detail = new Intent(this, TripDetailActivity.class);
            detail.putExtra("tripCode", tripCode);
            detail.putExtra("tripId", tripId.toString());
            detail.putExtra("tripUserCode", tripUserCode);

            stackBuilder.addNextIntent(detail);
            stackBuilder.addNextIntent(i);

            contentIntent = stackBuilder.getPendingIntent(Integer.valueOf(tripId), PendingIntent.FLAG_CANCEL_CURRENT);

        }

        else if(type.toLowerCase().equals("activities")) {

            path = Uri.parse("android.resource://com.bitewolf.tripsie/raw/yeahyeah");

            Intent i = new Intent(this, TripActivityActivities.class);
            i.putExtra("tripId", tripId);
            i.putExtra("tripUserId", tripUserId);
            i.putExtra("userName", tripUserName);
            i.putExtra("DisplayName", tripDisplayName);
            i.putExtra("tripCode", tripCode);

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);

            stackBuilder.addParentStack(TripDetailActivity.class);


            Intent detail = new Intent(this, TripDetailActivity.class);
            detail.putExtra("tripCode", tripCode);
            detail.putExtra("tripId", tripId.toString());
            detail.putExtra("tripUserCode", tripUserCode);

            stackBuilder.addNextIntent(detail);
            stackBuilder.addNextIntent(i);

            contentIntent = stackBuilder.getPendingIntent(Integer.valueOf(tripId), PendingIntent.FLAG_CANCEL_CURRENT);

        }

        else if(type.toLowerCase().equals("location")) {

            path = Uri.parse("android.resource://com.bitewolf.tripsie/raw/yeah");

            Intent i = new Intent(this, TripLocationsActivity.class);
            i.putExtra("TripId", tripCode);

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);

            stackBuilder.addParentStack(TripDetailActivity.class);


            Intent detail = new Intent(this, TripDetailActivity.class);
            detail.putExtra("tripCode", tripCode);
            detail.putExtra("tripId", tripId.toString());
            detail.putExtra("tripUserCode", tripUserCode);

            stackBuilder.addNextIntent(detail);
            stackBuilder.addNextIntent(i);

            contentIntent = stackBuilder.getPendingIntent(Integer.valueOf(tripId), PendingIntent.FLAG_CANCEL_CURRENT);

        }

        else
        {

            contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);
        }



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.vintage_suitcase)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(title))
                        .setContentText(msg).setAutoCancel(true);

        boolean sound = prefs.getBoolean("Sound", true);

        if(sound) {

            mBuilder.setSound(path);
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(Integer.valueOf(tripId), mBuilder.build());

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }
}
