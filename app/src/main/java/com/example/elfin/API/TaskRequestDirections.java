package com.example.elfin.API;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationMap.ChargingStationMap;
import com.example.elfin.R;
import com.example.elfin.Parsers.TaskParser;
import com.example.elfin.Utils.App;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TaskRequestDirections extends AsyncTask<String, Void, String> {

    private LocalBroadcastManager localBroadcastManager;
    private App applicationContext;
    private Activity context;
    public TaskRequestDirections(Activity activity){
        this.localBroadcastManager = LocalBroadcastManager.getInstance(activity);
        this.applicationContext = (App)activity.getApplication();
        this.context = activity;
    }
    public TaskRequestDirections(Activity activity, boolean CSV){
        this.localBroadcastManager = LocalBroadcastManager.getInstance(activity);
        this.applicationContext = (App)activity.getApplication();
        this.context = activity;
    }

    public TaskRequestDirections(LocalBroadcastManager localBroadcastManager, App applicationContext){
        this.localBroadcastManager = localBroadcastManager;
        this.applicationContext = applicationContext;
    }


    private String requestDirection(String ID){
        String responseString="";
        // Create an InputStream object. From API
        InputStream is = null;
        if (ID != null) {
            if (ID.equals("Eh9Ucm9uZGhlaW1zZ2F0YW4sIEtpc3RhLCBTdmVyaWdlIi4qLAoUChIJnT2ifeSeX0YRjiFRsjPOZckSFAoSCfHwNW05slxGEWJvrY2i67gi"))
                is = context.getResources().openRawResource(R.raw.trondheimsgatansverige);
            else if (ID.equals("ChIJU34DR5cxbUYR1PM8jyjI9ws"))
                is = context.getResources().openRawResource(R.raw.trondheimnorge);
            else if (ID.equals("EhxUcm9uZGhlaW1zdmVpZW4sIE9zbG8sIE5vcmdlIi4qLAoUChIJ_SC6wSFwQUYRBHm-qWY3eqkSFAoSCXEGy8AybkFGEbN998XfkGQV"))
                is = context.getResources().openRawResource(R.raw.trondheimsveienoslo);
        }
        else
            is = context.getResources().openRawResource(R.raw.test);
        //TODO istedenfor å bruke inputstream CSV, må vi hente data fra URL som står over.
        // Create a BufferedReader object to read values from CSV file.
        InputStreamReader in = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(in);
        String line = "";
        // Create a list of LatLng objects.
        List<LatLng> latLngList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            responseString = stringBuilder.toString();
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
        //return requestDirection(strings[0]);
        return strings[0];
    }

    @Override
    protected void onPostExecute(String s) {
        TaskParser taskParser = new TaskParser(localBroadcastManager,applicationContext);
        taskParser.execute(s);
    }


    /*
        String googleURLDirection = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin = "59.967771,10.731879";
        String destination = ID;//"59.913622,10.753237";
        String key = "AIzaSyDskTx9G4bXFvfz2T2jMiBtG8UWa5KX3KU";
        String mode = "driving";
        String depertureTime = "now";
        String parameters = googleURLDirection + "origin=" + origin + "&destination=place_id:" + destination + "&mode=" + mode + "&departure_time=" + depertureTime + "&key=" + key;
        System.out.println(parameters);
     */
}


