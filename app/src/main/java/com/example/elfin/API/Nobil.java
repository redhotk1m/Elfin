package com.example.elfin.API;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.elfin.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nobil extends AsyncTask<String, Void, String> {
    InputStreamReader in;
    BufferedReader reader;
    InputStream is;

    public Nobil(Context context){
        String responseString="";
        // Create an InputStream object. From API
        is = context.getResources().openRawResource(R.raw.nobil);
        // Create a BufferedReader object to read values from CSV file.
        in = new InputStreamReader(is);
        reader = new BufferedReader(in);
    }

    private String requestDirection(){
        String responseString="";
        // Create an InputStream object. From API
        // Create a BufferedReader object to read values from CSV file.
        String line = "";
        // Create a list of LatLng objects.
        List<LatLng> latLngList = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        try {
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
                // Split the line into different tokens (using the comma as a separator).
                /*String[] tokens = line.split(",");
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
                }*/
            }
            responseString = stringBuffer.toString();
            reader.close();
            in.close();
        } catch (IOException e1) {
            Log.e("error", "Error" + line, e1);
            e1.printStackTrace();
        }
        return responseString;
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONArray latlngJSONArray = new JSONObject(
                    requestDirection())
                    .getJSONArray("chargerstations");
            //.getJSONObject(0)
            //        .getJSONObject("csmd");
            for (int i = 0; i < latlngJSONArray.length(); i++){
                String[] latlng = latlngJSONArray.getJSONObject(i)
                        .getJSONObject("csmd")
                        .getString("Position")
                        .replace("(","")
                        .replace(")","")
                        .trim()
                        .split(",");
                System.out.println("lat: " + latlng[0] + " lon: " + latlng[1] + " element nr: " + i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
