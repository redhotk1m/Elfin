package com.elfin.elfin.Utils;

import android.content.Context;
import android.util.Log;

import com.elfin.elfin.Activities.Station.StationMap.ChargingStationMap;
import com.elfin.elfin.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    /**
     * Quite sure we don't use this class at all anymore, not sure enough to delete. Bad coding habits
     */
    private static final String LOG_TAG = ChargingStationMap.class.getName();
    public static final String ENCODED_POINTS = "encodedPoints";
    public static final String LAT_LNG_POINT = "latLngPoint";
    public static final String MARKER = "marker";
    /** * Helper method to get polyline points by decoding an encoded coordinates string read from CSV file. */
    public static List<LatLng> readEncodedPolyLinePointsFromCSV(Context context, String lineKeyword) {
        String googleURLDirection = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin = "41.43206,-81.38992";
        String destination = "42.43206,-80.38992";
        String key = "AIzaSyDskTx9G4bXFvfz2T2jMiBtG8UWa5KX3KU";
        String mode = "driving";
        String depertureTime = "now";
        String parameters = googleURLDirection + "origin=" + origin + "&" + destination + "&" + mode + "&" + depertureTime + "&" + key;
        // Create an InputStream object. From API
        InputStream is = context.getResources().openRawResource(R.raw.test);
        // Create a BufferedReader object to read values from CSV file.
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line = "";
        // Create a list of LatLng objects.
        List<LatLng> latLngList = new ArrayList<>();
        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(",");
                // Only add the right latlng points to a desired line by color.
                if (tokens[0].trim().equals(lineKeyword) && tokens[1].trim().equals(ENCODED_POINTS)) {
                    // Use PolyUtil to decode the polylines path into list of LatLng objects.
                    latLngList.addAll(PolyUtil.decode(tokens[2].trim().replace("\\\\", "\\")));
                    Log.d(LOG_TAG + lineKeyword, tokens[2].trim());
                    for (LatLng lat : latLngList) {
                        Log.d(LOG_TAG + lineKeyword, lat.latitude + ", " + lat.longitude);
                    }
                } else {
                    Log.d(LOG_TAG, "null");
                }
            }
        } catch (IOException e1) {
            Log.e(LOG_TAG, "Error" + line, e1);
            e1.printStackTrace();
        }
        return latLngList;
    }
}
