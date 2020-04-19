package com.example.elfin.Activities.Station.StationMap;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.TimingLogger;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
        TimingLogger logger = new TimingLogger("MyTag", "MethodAAA");
        logger.addSplit("A");
        ArrayList<ChargerItem>
                allChargingStations = allChargingStationsArr[0],
                validLatStations = new ArrayList<>(),
                validStations = new ArrayList<>();
        //Iterate over all points, draw all stations
        int chargingStationsSize = allChargingStations.size();

        float totalDistance = 0;
        LatLng currPoint;
        Location currLocation = new Location("this");
        LatLng lastPoint;
        Location lastLocation = new Location("this");

        for (int k = 1; k < points.size(); k++){

            currPoint = points.get(k);

            currLocation.setLatitude(currPoint.latitude);
            currLocation.setLongitude(currPoint.longitude);
            lastPoint = points.get(k - 1);
            lastLocation.setLatitude(lastPoint.latitude);
            lastLocation.setLongitude(lastPoint.longitude);
            totalDistance += lastLocation.distanceTo(currLocation);

            for (int i = 0; i < chargingStationsSize; i++) {
                ChargerItem found = StMethods.search(currPoint.latitude, allChargingStations, true);
                if (StMethods.distanceBetweenKM(found.getLatLng().latitude,found.getLatLng().longitude,currPoint.latitude,currPoint.longitude) <= 1){
                    validLatStations.add(found);
                    allChargingStations.remove(found);
                }else{
                    break;
                }
            }
            validLatStations.sort(new LongditudeComparator());
            int validLatStationSize = validLatStations.size();
            for (int i = 0; i < validLatStationSize; i++) {
                ChargerItem found = StMethods.search(currPoint.longitude, validLatStations, false);
                if (StMethods.distanceBetweenKM(found.getLatLng().latitude,found.getLatLng().longitude,currPoint.latitude,currPoint.longitude) <= 1){
                    found.setMFromStartLocation(totalDistance);
                    System.out.println("Ladestasjon: " + found.getStreet() + " er" + (totalDistance + 500)/1000 + "km ifra startpunkt");
                    validStations.add(found);
                    validLatStations.remove(found);
                }else{
                    break;
                }
                //TODO: Kanskje gjøre ditanceBetweenKM om til å bruke location.DistanceTo();
            }
        }
        logger.addSplit("B");
        logger.dumpToLog();
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
