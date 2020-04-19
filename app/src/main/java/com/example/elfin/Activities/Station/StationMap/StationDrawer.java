package com.example.elfin.Activities.Station.StationMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.TimingLogger;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.Utils.App;
import com.example.elfin.comparators.LongditudeComparator;
import com.example.elfin.Utils.StMethods;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class StationDrawer extends AsyncTask<ArrayList<ChargerItem>, Void, ArrayList<ChargerItem>> {
    private ArrayList<LatLng> points;

    private App applicationContext;
    private LocalBroadcastManager localBroadcastManager;
    public StationDrawer(LocalBroadcastManager localBroadcastManager, App applicationContext, ArrayList<LatLng> points){
        this.localBroadcastManager = localBroadcastManager;
        this.applicationContext = applicationContext;
        this.points = points;
    }

    @Override
    protected ArrayList<ChargerItem> doInBackground(ArrayList<ChargerItem>... allChargingStationsArr) {
        TimingLogger timingLogger = new TimingLogger("MyTag","okay");
        long start = System.currentTimeMillis();
        timingLogger.addSplit("start");
        ArrayList<ChargerItem>
                allChargingStations = allChargingStationsArr[0],
                validLatStations = new ArrayList<>(),
                validStations = new ArrayList<>();
        //Iterate over all points, draw all stations
        int chargingStationsSize = allChargingStations.size();
        for (int k = 1; k<points.size(); k++) {
            LatLng currentPoint = points.get(k);
            for (int i = 0; i < chargingStationsSize; i++) {
                ChargerItem found = StMethods.search(((LatLng) currentPoint).latitude, allChargingStations, true);
                if (StMethods.distanceBetweenKM(found.getLatLng().latitude,found.getLatLng().longitude,((LatLng) currentPoint).latitude,((LatLng) currentPoint).longitude) <= 1){
                    validLatStations.add(found);
                    allChargingStations.remove(found);
                }else{
                    break;
                }
            }
            validLatStations.sort(new LongditudeComparator());
            int validLatStationSize = validLatStations.size();
            for (int i = 0; i < validLatStationSize; i++) {
                ChargerItem found = StMethods.search(((LatLng) currentPoint).longitude, validLatStations, false);
                if (StMethods.distanceBetweenKM(found.getLatLng().latitude,found.getLatLng().longitude,((LatLng) currentPoint).latitude,((LatLng) currentPoint).longitude) <= 1){
                    validStations.add(found);
                    validLatStations.remove(found);
                }else{
                    break;
                }
            }


        }
        System.out.println(start-System.currentTimeMillis());

        timingLogger.addSplit("ferdig");
        timingLogger.dumpToLog();
        return validStations;
        //TODO: Bruke sett, feil høyde/lengde dersom punkt er innenfor i høyde,
        // men ikke i bredde vil det fjernes i fra array,
        // selv om punktet kanskje er valid i et nytt punkt senere i ruten
    }

    @Override
    protected void onPostExecute(ArrayList<ChargerItem> allValidChargingStations) {
        Intent intent = new Intent("allValidStations");
        intent.putExtra("case","allValidStations");
        //TODO: Bør kjøres en løkke som sjekker om noen har mottat broadcastet, før asynctask avsluttes
        applicationContext.setAllValidChargingStations(allValidChargingStations);
        localBroadcastManager.sendBroadcast(intent);
    }
}
