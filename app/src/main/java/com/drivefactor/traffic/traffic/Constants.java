package com.drivefactor.traffic.traffic;

/**
 * Created by Nicholas on 11/23/2015.
 */
public class Constants {

    public static class Geometry {
        public static double MinLatitude = -90.0;
        public static double MaxLatitude = 90.0;
        public static double MinLongitude = -180.0;
        public static double MaxLongitude = 180.0;
        public static double Radius = 100; // meters
        public static float Accuracy = 50;

    }

    public static class Telematics {
        public static double accelThreshold = 11;

    }


    public static class SharedPrefs {
        public static String Geofences = "SHARED_PREFS_GEOFENCES";
    }
}
