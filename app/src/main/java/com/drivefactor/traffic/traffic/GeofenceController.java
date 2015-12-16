package com.drivefactor.traffic.traffic;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;




/**
 * Created by Nicholas on 11/23/2015.
 *
 */
public class GeofenceController extends Activity implements LocationListener {

    private final String TAG = GeofenceController.class.getName();


    private Context context;
    private GoogleApiClient googleApiClient;
    private Gson gson;
    private SharedPreferences prefs;

    private boolean LocationFound;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;


        private ArrayList<Object> knownGeofences;

    public ArrayList<Object> getKnownGeofences() {
        return knownGeofences;
    }

    private Geofence geofenceToAdd;


    // Singleton to create and access instance
    private static GeofenceController INSTANCE;

    public static GeofenceController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeofenceController();
        }
        return INSTANCE;
    }




    // Callbacks for successful creation and failed connection
    private GoogleApiClient.ConnectionCallbacks connectAddListener =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {

                    if(LocationFound) {
                        //Create new receiver
                        mGeofenceTransitionReceiver broadcastReceiver = new mGeofenceTransitionReceiver();

                        // Pending intent, connected to the broadcast receiver

                        Intent intent = new Intent(context,ReceiveTransitionsIntentService.class);

                        //Intent intent = new Intent("com.example.android.geofence.ACTION_RECEIVE_GEOFENCE");


                        // Intent intent = new Intent(context,mGeofenceTransitionReceiver.class)
                        // .setAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION)
                        // .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

                        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);



                        // Associate PendingIntent to geofence
                        PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(googleApiClient, getAddGeofencingRequest(), pendingIntent);


                        // Implement PendingResult callback
                        result.setResultCallback(new ResultCallback<Status>() {

                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    // If successful, save the geofence
                                    knownGeofences.add(geofenceToAdd);
                                    Log.d(TAG, "Geofence Added");

                                } else {
                                    // If not successful, log error
                                    Log.e(TAG, "Registering geofence failed: " + status.getStatusMessage() +
                                            " : " + status.getStatusCode());

                                }
                            }
                        });
                    } else {
                        mLocationRequest = getAddLocationRequest();

                        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, GeofenceController.INSTANCE);
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {
                    Log.e(TAG, "Connection to GoogleApiClient suspended.");
                }
            };

    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {

                }
            };

// Start
    // Main

    public void init(Context context) {
        // update context
        this.context = context.getApplicationContext();

        gson = new Gson();
        knownGeofences = new ArrayList<>();

        prefs = this.context.getSharedPreferences(Constants.SharedPrefs.Geofences, Context.MODE_PRIVATE);

        LocationFound = false;

        // Find current location
        // TODO if geofence is not available / too old

        // connect Location Services API client
        connectWithCallbacks(connectAddListener);

        // TODO Load geofences and compare to list
        // loadGeofences();

    }






    // Add geofence(s) to list and build the request
    private GeofencingRequest getAddGeofencingRequest() {
        List<Geofence> geofencesToAdd = new ArrayList<>();
        geofencesToAdd.add(geofenceToAdd);
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .addGeofences(geofencesToAdd);
        return builder.build();

    }

    // Request location information
    private LocationRequest getAddLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setNumUpdates(1); // 1 update

                return locationRequest;
    }

    // Start instance of Google API Client, with callbacks for connection/disconnection
    private void connectWithCallbacks(GoogleApiClient.ConnectionCallbacks callbacks) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(callbacks) // callbacks specific to the service
                .addOnConnectionFailedListener(connectionFailedListener) // callbacks when cannot connect to the client
                .build();
        Log.d(TAG,googleApiClient.toString());
        googleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "Last location: " + location.toString());

        // make sure last location is reasonable
        if (isValid(location)) {

         LocationFound = true;
            // Create geofence
            Geofence currentGeofence = new GeofenceCreate().mGeofence(location);

            Log.d(TAG, "Geofence Created");


            // LocationServices.FusedLocationApi.removeLocationUpdates(
                    // googleApiClient, GeofenceController.INSTANCE);

            // Stop locationRequest
            googleApiClient.disconnect();

            // Add geofence to list of geofences
            addGeofence(currentGeofence);
        } else {
            //TODO handle this better

            googleApiClient.disconnect();
            connectWithCallbacks(connectAddListener);
        }
    }

    // TODO handle inaccuracy
    private boolean isValid(Location location) {
        boolean validLocation = true;

        if (location.getLatitude() >= Constants.Geometry.MinLatitude &&
                location.getLatitude() <= Constants.Geometry.MaxLatitude &&
                location.getLongitude() >= Constants.Geometry.MinLongitude &&
                location.getLongitude() <= Constants.Geometry.MaxLongitude &&
                location.getAccuracy() <= Constants.Geometry.Accuracy) {

        } else {
            validLocation = false;
        }
        return validLocation;
    }

    public void addGeofence(Geofence geofence
                            // , GeofenceControllerListener listener
    ) {
        this.geofenceToAdd = geofence;

        saveGeofence();

        connectWithCallbacks(connectAddListener);
    }

    private void saveGeofence() {
        String json = gson.toJson(geofenceToAdd);
        Log.d(TAG, "JSON of Geofence Added: " + json);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(geofenceToAdd.getRequestId(), json);
        editor.apply();

        DatabaseHandler dbHandler = new DatabaseHandler(context);

        dbHandler.setTableName("testTable")
                .setPrimaryColumnName("column_1")
                .setPrimaryColumnType("Integer");

        SQLiteDatabase geofenceStorage = context.openOrCreateDatabase("Geofences.db",MODE_PRIVATE,null);


        dbHandler.setTableName("testTable").addColumn("column_2","Integer");

        Log.d(TAG, geofenceStorage.toString());

    }

}