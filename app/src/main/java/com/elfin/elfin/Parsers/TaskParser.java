package com.elfin.elfin.Parsers;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elfin.elfin.Activities.Station.StationMap.PolyPoint;
import com.elfin.elfin.Activities.Station.StationMap.StationDrawer;
import com.elfin.elfin.Utils.App;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String,String>>>> {
    private App applicationContext;
    private LocalBroadcastManager localBroadcastManager;
    public TaskParser(LocalBroadcastManager localBroadcastManager, App applicationContext) {
        this.localBroadcastManager = localBroadcastManager;
        this.applicationContext = applicationContext;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
        JSONObject jsonObject = null;
        List<List<HashMap<String,String>>> routes = null;
        try {
            jsonObject = new JSONObject(strings[0]);
            DirectionsParser directionsParser = new DirectionsParser();
            routes = directionsParser.parse(jsonObject);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return routes;
    }


    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        ArrayList <LatLng> points = null;
        PolylineOptions polylineOptions = null;
        for (List<HashMap<String, String>> path : lists){
            points = new ArrayList<>();
            polylineOptions = new PolylineOptions();
            for (HashMap<String, String> point : path){
                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lon"));
                points.add(new LatLng(lat,lon));
            }
            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(Color.BLACK);
            polylineOptions.geodesic(true);//Tegnes i 3D, tar hensyn til høyde osv
        }
        if (polylineOptions != null){
            applicationContext.setPolyLineOptions(polylineOptions);
            Intent intent = new Intent("polyLineOptions");
            boolean ready_to_use = true;
            intent.putExtra("polyLineOptions",ready_to_use);
            localBroadcastManager.sendBroadcast(intent);
            ArrayList<PolyPoint> polyPoints = new ArrayList<>();
            for (LatLng point : points){
                polyPoints.add(new PolyPoint(point));
            }
            applicationContext.setPolyPoints(polyPoints);
            StationDrawer stationDrawer = new StationDrawer(localBroadcastManager,applicationContext,polyPoints);
            stationDrawer.execute(applicationContext.getChargerItems());
        }
    }

}
