package com.example.elfin.API;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.elfin.AsyncResponse;
import com.example.elfin.ChargingStationMap;
import com.example.elfin.ChargingStations;
import com.example.elfin.R;
import com.example.elfin.staticMethods.Distance;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Nobil extends AsyncTask<Void, Void, ArrayList<String>>{
    InputStreamReader in;
    BufferedReader reader;
    InputStream is;
    AsyncResponse asyncResponse;
    GoogleMap googleMap;
    ChargingStationMap chargingStationMap;
    ArrayList points;

    public Nobil(ChargingStationMap chargingStationMap,ArrayList arrayList){
        points = arrayList;
        String responseString="";
        // Create an InputStream object. From API
        is = ChargingStations.chargingStationContext.getResources().openRawResource(R.raw.nobil);
        // Create a BufferedReader object to read values from CSV file.
        in = new InputStreamReader(is);
        reader = new BufferedReader(in);
        googleMap = chargingStationMap.getgMapStatic();
        this.chargingStationMap = chargingStationMap;
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
    protected ArrayList<String> doInBackground(Void... arrayLists) {
        ArrayList<String> chargingStationCoordinates = new ArrayList<>();
        try {
            JSONArray latlngJSONArray = new JSONObject(
                    requestDirection())
                    .getJSONArray("chargerstations");
            //.getJSONObject(0)
            //        .getJSONObject("csmd");
            for (int i = 0; i < latlngJSONArray.length(); i++){
                String latlng = latlngJSONArray.getJSONObject(i)
                        .getJSONObject("csmd")
                        .getString("Position")
                        .replace("(","")
                        .replace(")","")
                        .trim();
                chargingStationCoordinates.add(latlng);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chargingStationCoordinates;
    }

    @Override
    protected void onPostExecute(ArrayList<String> arrayList) {
        HashSet<LatLng> chargingStationsEnroute = new HashSet<LatLng>();
        //TODO Iterere gjennom dobbelt, sånn at den ikke viser antall i forhold til den minste listen
        int i = 0;
        double lat1, lon1;
        if (points.size() <= arrayList.size()){
            for (Object point : points){
                String[] a = arrayList.get(i++).split(",");
                lat1 = Double.parseDouble(a[0]);
                lon1 = Double.parseDouble(a[1]);
                //googleMap.addCircle(new CircleOptions().center(new LatLng(((LatLng) point).latitude,((LatLng) point).longitude)).radius(500));
                if (Distance.distanceBetweenKM(lat1, lon1, ((LatLng) point).latitude, ((LatLng) point).longitude) < 1.1) {
                    chargingStationsEnroute.add(new LatLng(lat1, lon1));
                }
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(((LatLng) point).latitude,((LatLng) point).longitude)).title("point"));
            }
        }else {
            for (String latlng : arrayList) {
                String[] a = latlng.split(",");
                LatLng latLng = (LatLng) points.get(i++);
                lat1 = Double.parseDouble(a[0]);
                lon1 = Double.parseDouble(a[1]);
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(lat1,lon1)).title("test").snippet("testDesc"));
                if (Distance.distanceBetweenKM(lat1, lon1, latLng.latitude, latLng.longitude) < 1) {
                    chargingStationsEnroute.add(new LatLng(lat1, lon1));
                }
            }
        }
        for (LatLng latLng : chargingStationsEnroute) {
            chargingStationMap.addAllChargingStations(latLng);
            //googleMap.addMarker(new MarkerOptions().position(latLng).title("test123").snippet("test9321"));
        }
        //asyncResponse.processFinishSet(set);
    }


}
