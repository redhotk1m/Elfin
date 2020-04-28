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
import com.example.elfin.car.Elbil;
import com.example.elfin.comparators.LatitudeComparator;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NobilAPIHandler extends AsyncTask<String,Void,ArrayList<ChargerItem>> {
    private ArrayList<ChargerItem> chargingStationCoordinates = new ArrayList<>();
    LocalBroadcastManager localBroadcastManager;
    private App applicationContext;
    private Elbil elbil;

    NobilAPIHandler(LocalBroadcastManager localBroadcastManager, App applicationContext){
        this.localBroadcastManager = localBroadcastManager;
        this.applicationContext = applicationContext;
        //this.elbil = applicationContext.getElbil();

    }

    // chademo --> EV79022
    // etron --> EV40513

    public ArrayList<ChargerItem> getChargingStationCoordinates() {
        return chargingStationCoordinates;
    }



    @Override
    protected ArrayList<ChargerItem> doInBackground(String... jsonString) {



        String CCS = "";
        String CCSLighning = "";

        String chademo = "";
        String lightCharger = "150 kW DC";
        String fastCharger = "50 kW - 500VDC max 100A";

        int counterLightning = 0;
        int counterCCS = 0;
        int counterChademo = 0;

        String ligtning = "";
        String fast = "";
        String fastTimeCCS= "";
        String ligtningTime = "";
        String fastTime = "";
        String everyone = "everyone";


        if (true) {
            try {
                JSONArray latlngJSONArray = new JSONObject(jsonString[0])
                        .getJSONArray("chargerstations");
                for (int i = 0; i < latlngJSONArray.length(); i++) {
                    JSONObject object = latlngJSONArray.getJSONObject(i).getJSONObject("csmd");
                    JSONObject objectConnectors = latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn");
                    chademo = "CHAdeMO";
                    CCS = "CCS/Combo";
                    CCSLighning = "CCS/Combo";
                    ligtning = "Lyn Ladning";
                    fast = "Hurtig ladning";
                    ligtningTime = "ca 30 min";
                    fastTime = "ca 75 min";

                    try {
                        for (int j = 1; j < 20; j++) {
                            if (objectConnectors.getJSONObject("" + j).getJSONObject("4").
                                    getString("trans").equals(chademo) && objectConnectors.getJSONObject("" + j).getJSONObject("5").
                                    getString("trans").equals(fastCharger)) {
                                counterChademo++;
                            }



                            if (objectConnectors.getJSONObject("" + j).getJSONObject("4").
                                    getString("trans").equals(CCS) && objectConnectors.getJSONObject("" + j).getJSONObject("5").
                                    getString("trans").equals(fastCharger)) {
                                counterCCS++;
                            }

                            if (objectConnectors.getJSONObject("" + j).getJSONObject("4").
                                    getString("trans").equals(CCS) &&
                                    objectConnectors.getJSONObject("" + j).getJSONObject("5").
                                            getString("trans").equals(lightCharger)) {
                                counterLightning++;
                            }




                        }
                    } catch (JSONException e) {
                        //System.out.println("ÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅÅ");
                    }

                    int imageFast = R.drawable.ic_charger;
                    int imageLightning = R.drawable.ic_lightning;
                    String counterCHademoString = "" + counterChademo + " ladepunkt";
                    String counterLightString = "" + counterLightning + " ladepunkt";
                    String counterFastCSS = "" + counterCCS + " ladepunkt";


                    if (counterLightning <= 0) {
                        CCSLighning = "";
                        counterLightString = "";
                        imageLightning = 0;
                        ligtning = "";
                        ligtningTime = "";
                    }

                    if (counterCCS <= 0) {
                        CCS = "";
                        counterFastCSS = "";
                        imageFast = 0;
                        fast = "";
                        fastTime = "";
                    }
                    if (counterChademo <= 0) {
                        chademo = "";
                        counterCHademoString = "";
                        imageFast = 0;
                        fast = "";
                        fastTime = "";
                    }
                    if(counterChademo >= 1 || counterCCS >=1){
                        fast = "Hurtig ladning";
                        imageFast = R.drawable.ic_charger;
                        fastTime = "ca 75 min";
                    }


                    if (counterLightning >= 1 || counterChademo >= 1 || counterCCS >= 1) {
                        chargingStationCoordinates.add(new ChargerItem(
                                        object.getString("Street"),
                                        object.getString("House_number"),
                                        object.getString("City"),
                                        object.getString("Description_of_location"),
                                        object.getString("Owned_by"),
                                        object.getString("User_comment"),
                                        object.getString("Contact_info"),
                                        object.getString("Position")
                                                .replace("(", "")
                                                .replace(")", "")
                                                .split(","), chademo, counterCHademoString, fastTime, CCS, counterFastCSS,
                                        fastTime, imageFast, imageLightning, CCSLighning, counterLightString, ligtningTime, fast, ligtning
                                )
                        );
                    }

                    counterChademo = 0;
                    counterLightning = 0;
                    counterCCS = 0;
                    object = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
