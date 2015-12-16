package com.drivefactor.traffic.traffic;

/**
 * Created by Nicholas on 12/7/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class BootCompleteReceiver extends WakefulBroadcastReceiver
{
    private static final String TAG = "TrafficBootComplete";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "Traffic application restarted");
        GeofenceController.getInstance().init(context);
        //TODO add better handling / get it to work
    }
}
