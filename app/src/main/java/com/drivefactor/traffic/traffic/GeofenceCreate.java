package com.drivefactor.traffic.traffic;

import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;

import java.util.UUID;

/**
 * Created by Nicholas on 11/23/2015.
 */
public class GeofenceCreate {

    // region Properties

    public String id;

    public double lat;
    public double lon;
    public float radius;


    // Primitive
    public Geofence mGeofence(double lat,double lon) {
        id = UUID.randomUUID().toString();
        return new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(lat, lon, (float) Constants.Geometry.Radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }


    public interface AddGeofenceFragmentListener {
        void onDialogPositiveClick(DialogFragment dialog, Geofence geofence);
        void onDialogNegativeClick(DialogFragment dialog);
    }

}
