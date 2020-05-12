package com.elfin.elfin.API;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elfin.elfin.Activities.Station.StationList.ChargerItem;
import com.elfin.elfin.R;
import com.elfin.elfin.Utils.App;
import com.elfin.elfin.car.Elbil;
import com.elfin.elfin.comparators.LatitudeComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NobilAPIHandler extends AsyncTask<String,Void,ArrayList<ChargerItem>> {
    private ArrayList<ChargerItem> chargingStationCoordinates = new ArrayList<>();
    private LocalBroadcastManager localBroadcastManager;
    private App applicationContext;

    /**
     * This is the handler for the repsonse we get from the Nobil API.
     * Here we create all the chargers in the background, filtering out those the car can't use
     * If no car is provided, we find all the chargers (Fast/Lightning, not normal speed).
     * After it's done, we sort the list by Latitude, so we save some time later.
     * @param localBroadcastManager
     * @param applicationContext
     */

    NobilAPIHandler(LocalBroadcastManager localBroadcastManager, App applicationContext){
        this.localBroadcastManager = localBroadcastManager;
        this.applicationContext = applicationContext;
    }

    @Override
    protected ArrayList<ChargerItem> doInBackground(String... jsonString) {

        String CCSName = "CCS/Combo";

        String CCSLighning = "";

        String chademoName = "CHAdeMO";

        String lightCharger = "150 kW DC";
        String fastCharger = "50 kW - 500VDC max 100A";

        int counterLightning = 0;
        int counterCCS = 0;
        int counterChademo = 0;

        String ligtning = "";
        String fast = "";
        String ligtningTime = "";
        String fastTime = "";
        double CCSChargerEffect = 0;

        try {
            JSONArray latlngJSONArray = new JSONObject(jsonString[0])
                    .getJSONArray("chargerstations");
            for (int i = 0; i < latlngJSONArray.length(); i++) {
                JSONObject object = latlngJSONArray.getJSONObject(i).getJSONObject("csmd");
                JSONObject objectConnectors = latlngJSONArray.getJSONObject(i).getJSONObject("attr").getJSONObject("conn");
                ligtning = "Lynlading";
                chademoName = "CHAdeMO";
                CCSName = "CCS/Combo";
                fast = "Hurtiglading";
                ligtningTime = "Ladetid ukjent";
                fastTime = "Ladetid ukjent";

                try {
                    for (int j = 1; j < 20; j++) {
                        if (objectConnectors.getJSONObject("" + j).getJSONObject("4").
                                getString("trans").equals(chademoName) && objectConnectors.getJSONObject("" + j).getJSONObject("5").
                                getString("trans").equals(fastCharger)) {
                            counterChademo++;
                        }


                        if (objectConnectors.getJSONObject("" + j).getJSONObject("4").
                                getString("trans").equals(CCSName)) {
                            String str = objectConnectors.getJSONObject("" + j).getJSONObject("5").
                                    getString("trans");
                            String sub = str.substring(0, 3);
                            sub = sub.replaceAll("\\s", "");
                            sub = sub.replaceAll(",", ".");
                            CCSChargerEffect = Double.parseDouble(sub);

                            if (CCSChargerEffect >= 50 && CCSChargerEffect < 150) {
                                counterCCS++;
                            }
                            if (CCSChargerEffect == 150) {
                                CCSLighning = "CCS/Combo\n" + lightCharger;
                                counterLightning++;
                            }
                            if (CCSChargerEffect == 350) {
                                CCSLighning = "CCS/Combo\n" + "350 kW DC";
                                counterLightning++;
                            }

                            if (CCSChargerEffect > 150 && CCSChargerEffect < 350) {
                                CCSLighning = "CCS/Combo\n" + "+150 kW DC";
                                counterLightning++;

                            }

                        }

                    }
                } catch (JSONException e) {
                    //TODO: Popup beskjed til bruker
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
                    CCSName = "";
                    counterFastCSS = "";
                    imageFast = 0;
                    fast = "";
                    fastTime = "";
                }
                if (counterChademo <= 0) {
                    chademoName = "";
                    counterCHademoString = "";
                    imageFast = 0;
                    fast = "";
                    fastTime = "";
                }
                if (counterChademo >= 1 || counterCCS >= 1) {
                    if (counterCCS == 1) {
                        counterCHademoString = "1 ladepunkt";
                    }

                    fast = "Hurtiglading";
                    imageFast = R.drawable.ic_charger;
                    fastTime = "Ladetid ukjent";
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
                                            .split(","), chademoName, counterCHademoString, fastTime, CCSName, counterFastCSS,
                                    fastTime, imageFast, imageLightning, CCSLighning, counterLightString, ligtningTime, fast, ligtning
                            )
                    );
                }

                counterChademo = 0;
                counterLightning = 0;
                counterCCS = 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //TODO: Håndter dersom man ikke finner noen ladestasjoner
        chargingStationCoordinates.sort(new LatitudeComparator());
        return chargingStationCoordinates;
    }


    @Override
    protected void onPostExecute(ArrayList<ChargerItem> chargerItems) {
        applicationContext.setChargerItems(chargerItems);
        Intent intent = new Intent("allStations");
        intent.putExtra("case","allStations");
        localBroadcastManager.sendBroadcast(intent);
        System.out.println("har sendt chargerItems");
    }
}
