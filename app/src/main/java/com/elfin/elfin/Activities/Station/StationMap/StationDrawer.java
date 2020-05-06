package com.elfin.elfin.Activities.Station.StationMap;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.TimingLogger;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elfin.elfin.Activities.Station.StationList.ChargerItem;
import com.elfin.elfin.Utils.App;
import com.elfin.elfin.comparators.LongditudeComparator;
import com.elfin.elfin.Utils.StMethods;

import java.util.ArrayList;

public class StationDrawer extends AsyncTask<ArrayList<ChargerItem>, Void, ArrayList<ChargerItem>> {
    private ArrayList<PolyPoint> points;

    private App applicationContext;
    private LocalBroadcastManager localBroadcastManager;
    public StationDrawer(LocalBroadcastManager localBroadcastManager, App applicationContext, ArrayList<PolyPoint> points){
        this.localBroadcastManager = localBroadcastManager;
        this.applicationContext = applicationContext;
        this.points = points;
    }

    @Override
    protected ArrayList<ChargerItem> doInBackground(ArrayList<ChargerItem>... allChargingStationsArr) {
        TimingLogger logger = new TimingLogger("MyTag", "MethodAAA");
        logger.addSplit("Start");
        ArrayList<ChargerItem>
                allChargingStations = allChargingStationsArr[0],
                validLatStations = new ArrayList<>(),
                validStations = new ArrayList<>();
        //Iterate over all points, draw all stations
        int chargingStationsSize = allChargingStations.size();

        float totalDistance = 0;
        PolyPoint currPoint;
        Location currLocation = new Location("this");
        PolyPoint lastPoint;
        Location lastLocation = new Location("this");
        points.get(0).setDrivenKM(totalDistance);

        for (int k = 1; k < points.size(); k++){

            currPoint = points.get(k);
            currLocation.setLatitude(currPoint.getLatitude());
            currLocation.setLongitude(currPoint.getLongditude());
            lastPoint = points.get(k - 1);
            lastLocation.setLatitude(lastPoint.getLatitude());
            lastLocation.setLongitude(lastPoint.getLongditude());
            totalDistance += lastLocation.distanceTo(currLocation);
            points.get(k).setDrivenKM(totalDistance);


            for (int i = 0; i < chargingStationsSize; i++) {
                ChargerItem found = StMethods.search(currPoint.getLatitude(), allChargingStations, true);
                if (StMethods.distanceBetweenKM(found.getLatLng().latitude,found.getLatLng().longitude,currPoint.getLatitude(),currPoint.getLongditude()) <= 1){
                    validLatStations.add(found);
                    allChargingStations.remove(found);
                }else{
                    break;
                }
            }
            validLatStations.sort(new LongditudeComparator());
            int validLatStationSize = validLatStations.size();
            float distance;
            for (int i = 0; i < validLatStationSize; i++) {
                ChargerItem found = StMethods.search(currPoint.getLongditude(), validLatStations, false);
                if ((distance = StMethods.distanceBetweenKM(found.getLatLng().latitude,found.getLatLng().longitude,currPoint.getLatitude(),currPoint.getLongditude())) <= 1){
                    found.setMFromStartLocation(totalDistance + (distance * 1000));
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
