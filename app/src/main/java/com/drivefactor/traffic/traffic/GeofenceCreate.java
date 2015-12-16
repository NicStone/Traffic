package com.drivefactor.traffic.traffic;

import android.location.Location;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.location.Geofence;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Nicholas on 11/23/2015.
 */
public class GeofenceCreate implements Serializable {

    // region Properties


    @SerializedName("zzDX")
    public String id;

    public String name;

    @SerializedName("zzaKR")
    public double lat;

    @SerializedName("zzaKS")
    public double lon;

    @SerializedName("zzaKT")
    public float radius;


    // Primitive
    public Geofence mGeofence(Location location) {
        id = UUID.randomUUID().toString();
        name = "";
        lat = location.getLatitude();
        lon = location.getLongitude();
        radius = (float)Constants.Geometry.Radius;

        return new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(lat, lon, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }



    public interface AddGeofenceFragmentListener {
        void onDialogPositiveClick(DialogFragment dialog, Geofence geofence);
        void onDialogNegativeClick(DialogFragment dialog);
    }

}
