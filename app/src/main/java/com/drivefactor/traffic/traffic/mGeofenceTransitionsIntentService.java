package com.drivefactor.traffic.traffic;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicholas on 11/24/2015.
 */
public class mGeofenceTransitionsIntentService extends IntentService {

    private final String TAG = mGeofenceTransitionsIntentService.class.getName();

    private SharedPreferences prefs;
    private Gson gson;

    public mGeofenceTransitionsIntentService() {
         super("mGeofenceTransitionsIntentService");
    }



    protected void onHandleIntent(Intent intent) {

        prefs = getApplicationContext().getSharedPreferences(Constants.SharedPrefs.Geofences, Context.MODE_PRIVATE);

        gson = new GsonBuilder().registerTypeHierarchyAdapter(Geofence.class, new InterfaceAdapter<Geofence>()).create();

        // Get the event
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event !=null) {
            if (event.hasError()) {
                onError(event.getErrorCode());
            } else {

                // Get what type
                int transition = event.getGeofenceTransition();
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                        transition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                        transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    List<String> geofenceIds = new ArrayList<>();

                    // List of geofences involved in the event
                    for (Geofence geofence : event.getTriggeringGeofences()) {
                        geofenceIds.add(geofence.getRequestId());
                    }
                    if (transition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                            transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                        //If exited, pass to notification method
                        onExitGeofences(geofenceIds);
                    }
                }
            }
        }

    }



    private void onExitGeofences(List<String> geofenceIds) {

        //Loop all geofences exited
        for (String geofenceId : geofenceIds) {
            String geofenceName ="";

        //Loop stored keys and compare
            Map<String,?> keys = prefs.getAll();
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                String jsonString = prefs.getString(entry.getKey(),null);
                Geofence geofence = gson.fromJson(jsonString,Geofence.class);
                if(geofence.getRequestId().equals(geofenceId)) {
                    break;
                }
            }

            //Notification Text
            String contextText = "Geofence Exit: Congrats!";

            // 1. Create a NotificationManager
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            // 2. Create a PendingIntent for AllGeofencesActivity
            Intent intent = new Intent(this, TripNeeded.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // 3. Create and send a notification
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(this.getResources().getString(R.string.Notification_Title))
                    .setContentText(contextText)
                    .setContentIntent(pendingNotificationIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contextText))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(0, notification);

        }
    }

    private void onError(int i) {
        Log.e(TAG, "Geofencing Error: " + i);
    }

}
