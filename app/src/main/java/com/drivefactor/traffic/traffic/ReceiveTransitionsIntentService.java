package com.drivefactor.traffic.traffic;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.android.geofence.GeofenceUtils;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.Geofence;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent
 */
public class ReceiveTransitionsIntentService extends IntentService {

    /**
     * Sets an identifier for this class' background thread
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
    }


    private final String TAG = ReceiveTransitionsIntentService.class.getName();

    private SharedPreferences prefs;
    private Gson gson;
    private Context context;

    Intent broadcastIntent = new Intent();

    @Override
    protected void onHandleIntent(Intent intent) {

        // Create a local broadcast Intent
        Intent broadcastIntent = new Intent("com.example.android.geofence.ACTION_RECEIVE_GEOFENCE");

        // Give it the category for all intents sent by the Intent Service
        broadcastIntent
                .setAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION)
                .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        this.context = getApplicationContext();


        prefs = context.getSharedPreferences(Constants.SharedPrefs.Geofences, Context.MODE_PRIVATE);

        //gson = new GsonBuilder().registerTypeHierarchyAdapter(Geofence.class, new InterfaceAdapter<Geofence>()).create();
        gson = new Gson();

        broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
        //.putExtra(GeofenceUtils.EXTRA_GEOFENCE_ID, geofenceIds)
        //.putExtra(GeofenceUtils.EXTRA_GEOFENCE_TRANSITION_TYPE,transitionType);




        if (GeofencingEvent.fromIntent(intent).hasError()) {
            handleError(intent);
        } else {
            handleExit(intent);
        }

    }



    private void handleError(Intent intent){

        int errorCode = GeofencingEvent.fromIntent(intent).getErrorCode();

        String errorMessage = GeofenceStatusCodes.getStatusCodeString(errorCode);

        Log.e(TAG, errorMessage);

        // Broadcast the error to other components in this app
        // LocalBroadcastManager.getInstance(getContext()).sendBroadcast(
           //     broadcastIntent);
    }


    protected void handleExit(Intent intent) {

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


                GeofenceCreate geofence = gson.fromJson(jsonString,GeofenceCreate.class);

                Log.d(TAG,"geofence.id " + geofence.id);
                Log.d(TAG,"get geofence_id " + keys.get("zzDX"));
                Log.d(TAG,"geofenceId " + geofenceId);

                if(geofence.id.equals(geofenceId)) {
                    break;
                }
            }

            //Notification Text
            String contextText = "Geofence Exit: Congrats!";

            // 1. Create a NotificationManager
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // 2. Create a PendingIntent for AllGeofencesActivity
            Intent intent = new Intent(context, TripNeeded.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // 3. Create and send a notification
            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getResources().getString(R.string.Notification_Title))
                    .setContentText(contextText)
                    .setContentIntent(pendingNotificationIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contextText))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(0, notification);

            // Create an Intent to broadcast to the app
            broadcastIntent
                    .setAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION)
                    .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
            //.putExtra(GeofenceUtils.EXTRA_GEOFENCE_ID, geofenceIds)
            //.putExtra(GeofenceUtils.EXTRA_GEOFENCE_TRANSITION_TYPE,
            //transitionType);

            LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(broadcastIntent);

        }
    }

    private void onError(int i) {
        Log.e(TAG, "Geofencing Error: " + i);
    }

}