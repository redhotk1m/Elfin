package com.example.elfin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TaskRequestDirections extends AsyncTask<String, Void, String> {

    Context context;
    TaskRequestDirections(Context context){
        this.context = context;
    }

    private String requestDirection(){
        String responseString="";
        // Create an InputStream object. From API
        InputStream is = context.getResources().openRawResource(R.raw.test);
        // Create a BufferedReader object to read values from CSV file.
        InputStreamReader in = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(in);
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
        String responseString = "";
        responseString = requestDirection();
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        TaskParser taskParser = new TaskParser();
        taskParser.execute(s);
    }
}


