package com.example.elfin.Activities.Station.StationMap;

import android.os.AsyncTask;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.comparators.LongditudeComparator;
import com.example.elfin.Utils.StMethods;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class StationDrawer extends AsyncTask<ArrayList<LatLng>, Void, String> {
    private ArrayList<LatLng> points,
            chargingStations,
            validLatStations = new ArrayList<>(),
            validStations = new ArrayList<>();

    final double LONG_DIFF = 0.0015, LAT_DIFF = 0.0015;
    ChargingStations chargingStation;

    public StationDrawer(ChargingStations chargingStations){
        this.chargingStation = chargingStations;
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + chargingStations + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        //this.chargingStations = new ArrayList<>(chargingStationMap.getAllChargingStations());
        this.chargingStations = new ArrayList<>(chargingStations.getAllChargingStations());
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@2" + this.chargingStations.toString() + " @@@@@@@@@@@@@@@@@@@@@");
    }


    private void findAllValidStations(){
        //Iterate over all points, draw all stations
        int chargingStationsSize = chargingStations.size();
        for (Object point : points) {
            //validStations.add(new LatLng(((LatLng)point).latitude + LAT_DIFF,((LatLng)point).longitude + LONG_DIFF));
            //validStations.add(new LatLng(((LatLng)point).latitude - LAT_DIFF,((LatLng)point).longitude - LONG_DIFF));
            //validStations.add(new LatLng(((LatLng)point).latitude + LAT_DIFF,((LatLng)point).longitude - LONG_DIFF));
            //validStations.add(new LatLng(((LatLng)point).latitude - LAT_DIFF,((LatLng)point).longitude + LONG_DIFF));

             for (int i = 0; i < chargingStationsSize; i++) {
                LatLng found = StMethods.search(((LatLng) point).latitude, chargingStations, true);
                if (StMethods.distanceBetweenKM(found.latitude,found.longitude,((LatLng) point).latitude,((LatLng) point).longitude) < 1){
                //if ((found.latitude >= ((LatLng) point).latitude - LAT_DIFF)
                //        && (found.latitude <= ((LatLng) point).latitude + LAT_DIFF)) {
                    validLatStations.add(found);
                    chargingStations.remove(found);
                }else{
                    break;
                }
            }
            validLatStations.sort(new LongditudeComparator());
            int validLatStationSize = validLatStations.size();
            for (int i = 0; i < validLatStationSize; i++) {
                LatLng found = StMethods.search(((LatLng) point).longitude, validLatStations, false);
                if (StMethods.distanceBetweenKM(found.latitude,found.longitude,((LatLng) point).latitude,((LatLng) point).longitude) < 1){
                    //if ((found.longitude >= ((LatLng) point).longitude - LONG_DIFF)
                //        && (found.longitude <= ((LatLng) point).longitude + LONG_DIFF)){
                    validStations.add(found);
                    validLatStations.remove(found);
                }else{
                    break;
                }
                //validStations.add();
                //validLatStations.remove();
            }

        }
        //TODO: Bruke sett, feil høyde/lengde dersom punkt er innenfor i høyde,
        // men ikke i bredde vil det fjernes i fra array,
        // selv om punktet kanskje er valid i et nytt punkt senere i ruten
        //chargingStationMap.addAllChargingStations();
    }


    @Override
    protected String doInBackground(ArrayList<LatLng>... points) {
        this.points = points[0];
        findAllValidStations();
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        System.out.println("VALID STATIONS: " + validStations.toString());
        chargingStation.setValidStations(validStations);
        chargingStation.getPagerAdapter().getChargingStationMap().setAllValidStations(validStations);
        //drawAllValidStations();
    }

    private void drawAllValidStations(){
        for (Object latLng : validStations) {
            chargingStation.getPagerAdapter().getChargingStationMap().drawChargingStations((LatLng) latLng);
        }
    }
}
