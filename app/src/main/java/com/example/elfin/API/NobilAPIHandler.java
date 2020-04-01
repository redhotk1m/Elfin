package com.example.elfin.API;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.MainActivity;
import com.example.elfin.comparators.LatitudeComparator;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NobilAPIHandler {
    private ArrayList<LatLng> chargingStationCoordinates = new ArrayList<>();
    public NobilAPIHandler(String jsonString){
        try {
            JSONArray latlngJSONArray = new JSONObject(jsonString)
                    .getJSONArray("chargerstations");
            for (int i = 0; i < latlngJSONArray.length(); i++){
                String[] latlng = latlngJSONArray.getJSONObject(i)
                        .getJSONObject("csmd")
                        .getString("Position")
                        .replace("(","")
                        .replace(")","")
                        .trim().split(",");
                chargingStationCoordinates.add(
                        new LatLng(
                                Double.valueOf(latlng[0]),
                                Double.valueOf(latlng[1])
                        ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO: Endre fra CSV til Nobil API, håndter dersom man ikke finner noen stasjoner
        chargingStationCoordinates.sort(new LatitudeComparator());
    }

    NobilAPIHandler(ChargingStations chargingStations, String jsonString){
        ArrayList<LatLng> chargingStationCoordinates = new ArrayList<>();
        try {
            JSONArray latlngJSONArray = new JSONObject(jsonString)
                    .getJSONArray("chargerstations");
            for (int i = 0; i < latlngJSONArray.length(); i++){
                String[] latlng = latlngJSONArray.getJSONObject(i)
                        .getJSONObject("csmd")
                        .getString("Position")
                        .replace("(","")
                        .replace(")","")
                        .trim().split(",");
                chargingStationCoordinates.add(
                        new LatLng(
                                Double.valueOf(latlng[0]),
                                Double.valueOf(latlng[1])
                        ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO: Endre fra CSV til Nobil API, håndter dersom man ikke finner noen stasjoner
        chargingStationCoordinates.sort(new LatitudeComparator());
        //chargingStations.setAllChargingStations(chargingStationCoordinates);
        //chargingStations.setChargingStationsFound(true);
    }

    public ArrayList<LatLng> getChargingStationCoordinates() {
        return chargingStationCoordinates;
    }
}
