package com.elfin.elfin.Activities.Station.StationMap;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elfin.elfin.Activities.Station.StationList.ChargerItem;
import com.elfin.elfin.Utils.App;
import com.elfin.elfin.comparators.LongditudeComparator;
import com.elfin.elfin.Utils.StMethods;

import java.util.ArrayList;

public class StationDrawer extends AsyncTask<ArrayList<ChargerItem>, Void, ArrayList<ChargerItem>> {
    private ArrayList<PolyPoint> points;
    /**
     * This class finds all the charging stations that are in 1km proximity radius of our route,
     * and then draws them to the map / updates the list.
     * It also saves information about every point of our route, and how far we've driven
     * so we get accurate information on where a charger is compared to the users position.
     * It works with "teleportation" and driving back and forth aswell. Error of margin should be roughly 20meters.
     */

    private App applicationContext;
    private LocalBroadcastManager localBroadcastManager;
    public StationDrawer(LocalBroadcastManager localBroadcastManager, App applicationContext, ArrayList<PolyPoint> points){
        this.localBroadcastManager = localBroadcastManager;
        this.applicationContext = applicationContext;
        this.points = points;
    }


    /*
    This method takes in the array of all the chargingStations we've found from the NobilAPi which matches our car
    This array is already sorted by latitude.
    Then we iterate over every single point on our route (can be several thousand), and check if there's a charging station within 1km of that point.
    To make this fast, since there's at max 3000 charging stations, we do a binary search. wchi is O(log(n))
    Once we find one, we remove it from the array, and add it to a new array (foundLatArray), and save how long we've driven so far.
    When we don't one, we know there's none left, since it's sorted.
    Now we've found all the charging stations in proximity by latitude, so we do the exact same again,
    but only for longditude.
    Sort by long, search, remove from array, add to new array (validStation).
    In the end, validStation array is filled with all the charging stations along our route.

     */
    @Override
    protected ArrayList<ChargerItem> doInBackground(ArrayList<ChargerItem>... allChargingStationsArr) {
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
        return validStations;
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
