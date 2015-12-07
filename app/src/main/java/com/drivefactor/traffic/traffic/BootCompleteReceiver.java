package com.drivefactor.traffic.traffic;

/**
 * Created by Nicholas on 12/7/2015.
 */

        import android.app.PendingIntent.CanceledException;
        import android.content.Context;
        import android.content.Intent;
        import android.support.v4.content.WakefulBroadcastReceiver;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GooglePlayServicesUtil;
        import com.google.android.gms.location.Geofence;

public class BootCompleteReceiver extends WakefulBroadcastReceiver
{
    private static final String TAG = "TrafficBootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        GeofenceController.getInstance().init(context);
        //TODO add better handling
    }
}
