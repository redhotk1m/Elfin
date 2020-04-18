package com.example.elfin.API;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.MainActivity;
import com.example.elfin.R;
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

        String CCS = "";
        String ligtningCCS = "";
        String chademo = "";
        String lightCharger = "150 kW DC";
        String fastCharger = "50 kW - 500VDC max 100A";

        String ligtningText= "";

        Boolean boolLighting = false;
        Boolean boolFast = false;
        Boolean boolCha = false;

        int counterlight = 0;
        int counterCHD = 0;
        int counterFast = 0;

        String ligtning = "";
        String fast = "";
        String ligtningTime = "";
        String fastTime = "";

        try {
            JSONArray latlngJSONArray = new JSONObject(jsonString[0])
                    .getJSONArray("chargerstations");
            for (int i = 0; i < latlngJSONArray.length(); i++){
                JSONObject object = latlngJSONArray.getJSONObject(i).getJSONObject("csmd");
                chademo = "CHAdeMO";
                CCS = "CCS/Combo";
                ligtningCCS = "CCS/Combo";
                ligtning = "Lyn Ladning";
                fast = "Hurtig ladning";
                ligtningTime = "40 min til fullladet";
                fastTime ="75 min til fullladet";

                try {
                    for (int j = 1; j < 10; j++) {
                        if (latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                getJSONObject("4").getString("trans").equals(chademo)){
                            counterCHD++;
                        }


                        if (latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                getJSONObject("4").getString("trans").equals(CCS) &&
                                latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                        getJSONObject("5").getString("trans").equals(lightCharger)){
                            counterFast++;
                        }

                        if (latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                getJSONObject("4").getString("trans").equals(CCS) &&
                                latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                        getJSONObject("5").getString("trans").equals(lightCharger)){
                            counterlight++;
                        }


                        /*

                        if (latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                getJSONObject("4").getString("trans").equals(lightning) &&
                                latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                        getJSONObject("5").getString("trans").equals(lightCharger)) {
                            counterlight++;
                        }

                         */



                    }
                } catch (JSONException e) {
                    //System.out.println("ÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅ");
                }
                int imageFast= R.drawable.baseline_battery_charging_full_black_24dp;
                int imageSlow= R.drawable.baseline_battery_charging_full_black_24dp;
                int imageLigtning= R.drawable.image;


                String counterChademo = ""+counterCHD;
                String counterCcsFast = "" + counterFast;
                String counterLignitning = "" + counterlight;

                if(counterFast <= 0){
                    CCS = "";
                    counterCcsFast = "";
                    imageSlow = 0;
                    ligtning = "";
                    ligtningTime = "";
                }

                if(counterlight <= 0){
                    counterLignitning = "";
                    ligtningCCS = "";
                    imageLigtning = 0;

                }

                if(counterCHD <= 0){
                    chademo = "";
                    counterChademo = "";
                    imageFast=0;
                    fast = "";
                    fastTime = "";
                }

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
                                .split(","),chademo,counterChademo,CCS,counterCcsFast,
                        imageFast, imageSlow,fast,ligtning, ligtningTime, fastTime
                        )
                );
                counterCHD = 0;
                counterFast = 0;
                counterlight = 0;
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
        Intent intent = new Intent("allStations");
        intent.putExtra("case","allStations");
        //intent.putParcelableArrayListExtra("test",chargerItems);
        localBroadcastManager.sendBroadcast(intent);
        System.out.println("har sendt chargerItems");
    }
}
