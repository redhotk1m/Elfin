package com.example.elfin.Parsers;

import android.graphics.Color;
import android.os.AsyncTask;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationMap.ChargingStationMap;
import com.example.elfin.Activities.Station.StationMap.StationDrawer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String,String>>>> {
    private ChargingStations chargingStations;
    public TaskParser(ChargingStations chargingStations) {
        this.chargingStations = chargingStations;
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
        //System.out.println(routes);
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
            System.out.println("FERDIG@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            //chargingStationMap.drawPolyLines(polylineOptions);
            //chargingStationMap.addAllChargingStations();
            //TODO: Bruke applicationContext, notify chargingStationMap om at polyline kan tegnes
            chargingStations.getPagerAdapter().getChargingStationMap().setPolyLineOptions(polylineOptions);
            StationDrawer stationDrawer = new StationDrawer(chargingStations,points);
            stationDrawer.execute(chargingStations.getAllChargingStations());
        }
    }

}
