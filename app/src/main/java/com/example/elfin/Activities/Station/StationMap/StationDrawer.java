package com.example.elfin.Activities.Station.StationMap;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.comparators.LongditudeComparator;
import com.example.elfin.Utils.StMethods;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class StationDrawer extends AsyncTask<ArrayList<ChargerItem>, Void, ArrayList<ChargerItem>> {
    private ArrayList<LatLng> points;

    final double LONG_DIFF = 0.0015, LAT_DIFF = 0.0015;
    private ChargingStations chargingStation;

    public StationDrawer(ChargingStations chargingStations, ArrayList<LatLng> points){
        this.chargingStation = chargingStations;
        this.points = points;
    }

    @Override
    protected ArrayList<ChargerItem> doInBackground(ArrayList<ChargerItem>... allChargingStationsArr) {
        ArrayList<ChargerItem>
                allChargingStations = allChargingStationsArr[0],
                validLatStations = new ArrayList<>(),
                validStations = new ArrayList<>();
        //Iterate over all points, draw all stations
        int chargingStationsSize = allChargingStations.size();
        for (Object point : points) {
            for (int i = 0; i < chargingStationsSize; i++) {
                ChargerItem found = StMethods.search(((LatLng) point).latitude, allChargingStations, true);
                if (StMethods.distanceBetweenKM(found.getLatLng().latitude,found.getLatLng().longitude,((LatLng) point).latitude,((LatLng) point).longitude) <= 1){
                    validLatStations.add(found);
                    allChargingStations.remove(found);
                }else{
                    break;
                }
            }
            validLatStations.sort(new LongditudeComparator());
            int validLatStationSize = validLatStations.size();
            for (int i = 0; i < validLatStationSize; i++) {
                ChargerItem found = StMethods.search(((LatLng) point).longitude, validLatStations, false);
                if (StMethods.distanceBetweenKM(found.getLatLng().latitude,found.getLatLng().longitude,((LatLng) point).latitude,((LatLng) point).longitude) <= 1){
                    validStations.add(found);
                    validLatStations.remove(found);
                }else{
                    break;
                }
            }

        }
        return validStations;
        //TODO: Bruke sett, feil høyde/lengde dersom punkt er innenfor i høyde,
        // men ikke i bredde vil det fjernes i fra array,
        // selv om punktet kanskje er valid i et nytt punkt senere i ruten
    }

    @Override
    protected void onPostExecute(ArrayList<ChargerItem> allValidChargingStations) {
        Intent intent = new Intent("allValidChargingStations");
        intent.putParcelableArrayListExtra("allValidChargingStations",allValidChargingStations);
        //TODO: Bør kjøres en løkke som sjekker om noen har mottat broadcastet, før asynctask avsluttes
        LocalBroadcastManager.getInstance(chargingStation.getContext()).sendBroadcast(intent);
    }
}
