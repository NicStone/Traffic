package com.drivefactor.traffic.traffic;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TripNeeded extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SensorEventListener {


    private static final String TAG = TripNeeded.class.getSimpleName();

    ToggleButton logButton;
    TextView xValue;
    TextView yValue;
    TextView zValue;
    ProgressBar spinner;


    private SensorManager mSensorManager;
    private Sensor mSensor;

    private mGeofenceTransitionReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // install Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/HelveticaNeue.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );


        setContentView(R.layout.activity_trip_needed);

        // accel values for testing
        xValue = (TextView) findViewById(R.id.accel_x_value);
        yValue = (TextView) findViewById(R.id.accel_y_value);
        zValue = (TextView) findViewById(R.id.accel_z_value);

        //Define button and listen
        logButton = (ToggleButton) findViewById(R.id.log_button);
        logButton.setOnClickListener(this);



        //Initiatize the geofence,when starting the applicaition
        if(savedInstanceState == null) {
            GeofenceController.getInstance().init(this);
        }

    }


    public void onClick(View v) {

       if (logButton.isChecked()) {
            //manage and listen to accelerometers, with the fastest response time
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);


        } else {
            mSensorManager.unregisterListener(this);
        }
    }


    public void onSensorChanged(SensorEvent event) {

        xValue.setText(Float.toString(event.values.clone()[0]));
        yValue.setText(Float.toString(event.values.clone()[1]));
        zValue.setText(Float.toString(event.values.clone()[2]));
        crashEval(event);
        Log.d(TAG, "sensor: " + Arrays.toString(event.values.clone()));

    }

    public void crashEval(SensorEvent event) {


            if (Math.abs(event.values.clone()[0])<Constants.Telematics.accelThreshold
                    || Math.abs(event.values.clone()[1])<Constants.Telematics.accelThreshold
                    || Math.abs(event.values.clone()[2])<Constants.Telematics.accelThreshold
                    ) {
                Toast.makeText(getApplicationContext(), "Crash",
                        Toast.LENGTH_SHORT).show();

            }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// TODO Perhaps use this to alert bad GPS
    }





        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("SAVE LOG", logButton.isChecked());

        savedInstanceState.putCharSequence("SAVE X", xValue.getText());
        savedInstanceState.putCharSequence("SAVE Y", yValue.getText());
        savedInstanceState.putCharSequence("SAVE Z", zValue.getText());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    // restore the hierarchy
        super.onRestoreInstanceState(savedInstanceState);


        Boolean buttonState = savedInstanceState.getBoolean("SAVE LOG");

        //Re-establish the click listener
        logButton.setOnClickListener(this);

        if (buttonState) {
            onClick(logButton);

        }

        CharSequence savedX = savedInstanceState.getCharSequence("SAVE X");
        CharSequence savedY = savedInstanceState.getCharSequence("SAVE Y");
        CharSequence savedZ = savedInstanceState.getCharSequence("SAVE Z");

        logButton.setChecked(buttonState);

        xValue.setText(savedX);
        yValue.setText(savedY);
        zValue.setText(savedZ);

    }

    /*
 * Whenever the Activity resumes, reconnect the client to Location
 * Services
 */
    @Override
    protected void onResume() {
        super.onResume();


        // I think this will create a ton of receivers, will test later

        // broadcastReceiver = new mGeofenceTransitionReceiver();
        // intentFilter = new IntentFilter();
                // intentFilter.addAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION);
                // intentFilter.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        // Register the broadcast receiver to receive status updates

        //LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

    }





}
