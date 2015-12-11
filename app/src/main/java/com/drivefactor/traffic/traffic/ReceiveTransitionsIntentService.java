package com.drivefactor.traffic.traffic;


import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.android.geofence.GeofenceUtils;


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


    @Override
    protected void onHandleIntent(Intent intent) {

        // Create a local broadcast Intent
        Intent broadcastIntent = new Intent();

        // Give it the category for all intents sent by the Intent Service
        broadcastIntent
                .setAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION)
                .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }
}