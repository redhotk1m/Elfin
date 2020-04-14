package com.example.elfin.API;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.MainActivity;
import com.example.elfin.Utils.App;
import com.example.elfin.comparators.LatitudeComparator;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
public class NobilAPIHandler extends AsyncTask<String,Void,ArrayList<ChargerItem>> {
    private ArrayList<ChargerItem> chargingStationCoordinates = new ArrayList<>();
    LocalBroadcastManager localBroadcastManager;
    private App applicationContext;
    NobilAPIHandler(LocalBroadcastManager localBroadcastManager, App applicationContext){
        this.localBroadcastManager = localBroadcastManager;
        this.applicationContext = applicationContext;
    }

    public ArrayList<ChargerItem> getChargingStationCoordinates() {
        return chargingStationCoordinates;
    }

    @Override
    protected ArrayList<ChargerItem> doInBackground(String... jsonString) {
        try {
            JSONArray latlngJSONArray = new JSONObject(jsonString[0])
                    .getJSONArray("chargerstations");
            for (int i = 0; i < latlngJSONArray.length(); i++){
                JSONObject object = latlngJSONArray.getJSONObject(i).getJSONObject("csmd");
                chargingStationCoordinates.add(new ChargerItem(
                                object.getString("Street"),
                                object.getString("House_number"),
                                object.getString("Zipcode"),
                                object.getString("City"),
                                object.getString("Municipality"),
                                object.getString("County"),
                                object.getString("Description_of_location"),
                                object.getString("Owned_by"),
                                object.getString("Number_charging_points"),
                                object.getString("Image"),
                                object.getString("Available_charging_points"),
                                object.getString("User_comment"),
                                object.getString("Contact_info"),
                                object.getString("Created"),
                                object.getString("Updated"),
                                object.getString("Station_status"),
                                object.getString("Position")
                                        .replace("(","")
                                        .replace(")","")
                                        .split(",")
                        )
                );
                object = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO: Endre fra CSV til Nobil API, håndter dersom man ikke finner noen stasjoner
        chargingStationCoordinates.sort(new LatitudeComparator());
        return chargingStationCoordinates;
    }

    @Override
    protected void onPostExecute(ArrayList<ChargerItem> chargerItems) {
        applicationContext.setChargerItems(chargerItems);
        Intent intent = new Intent("jsonString");
        //intent.putParcelableArrayListExtra("test",chargerItems);
        localBroadcastManager.sendBroadcast(intent);
        System.out.println("har sendt chargerItems");
    }
}
