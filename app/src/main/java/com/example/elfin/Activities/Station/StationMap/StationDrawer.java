package com.example.elfin.Activities.Station.StationMap;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.comparators.LongditudeComparator;
import com.example.elfin.Utils.StMethods;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class StationDrawer extends AsyncTask<ArrayList<LatLng>, Void, ArrayList<LatLng>> {
    private ArrayList<LatLng> points;

    final double LONG_DIFF = 0.0015, LAT_DIFF = 0.0015;
    private ChargingStations chargingStation;

    public StationDrawer(ChargingStations chargingStations, ArrayList<LatLng> points){
        this.chargingStation = chargingStations;
        this.points = points;
    }



    private ArrayList<LatLng> findAllValidStations(ArrayList<LatLng> allChargingStations){
        //ArrayList<LatLng>
        //                validLatStations = new ArrayList<>(),
        //                validStations = new ArrayList<>();
        //        //Iterate over all points, draw all stations
        //        int chargingStationsSize = allChargingStations.size();
        //        for (Object point : points) {
        //             for (int i = 0; i < chargingStationsSize; i++) {
        //                LatLng found = StMethods.search(((LatLng) point).latitude, allChargingStations, true);
        //                if (StMethods.distanceBetweenKM(found.latitude,found.longitude,((LatLng) point).latitude,((LatLng) point).longitude) <= 1){
        //                    validLatStations.add(found);
        //                    allChargingStations.remove(found);
        //                }else{
        //                    break;
        //                }
        //            }
        //            validLatStations.sort(new LongditudeComparator());
        //            int validLatStationSize = validLatStations.size();
        //            for (int i = 0; i < validLatStationSize; i++) {
        //                LatLng found = StMethods.search(((LatLng) point).longitude, validLatStations, false);
        //                if (StMethods.distanceBetweenKM(found.latitude,found.longitude,((LatLng) point).latitude,((LatLng) point).longitude) <= 1){
        //                    validStations.add(found);
        //                    validLatStations.remove(found);
        //                }else{
        //                    break;
        //                }
        //            }
        //
        //        }
        //        return validStations;
        //        //TODO: Bruke sett, feil høyde/lengde dersom punkt er innenfor i høyde,
        //        // men ikke i bredde vil det fjernes i fra array,
        //        // selv om punktet kanskje er valid i et nytt punkt senere i ruten
        //        //chargingStationMap.addAllChargingStations();
        return null;
    }


    @Override
    protected ArrayList<LatLng> doInBackground(ArrayList<LatLng>... allChargingStationsArr) {
        ArrayList<LatLng>
                allChargingStations = allChargingStationsArr[0],
                validLatStations = new ArrayList<>(),
                validStations = new ArrayList<>();
        //Iterate over all points, draw all stations
        int chargingStationsSize = allChargingStations.size();
        for (Object point : points) {
            for (int i = 0; i < chargingStationsSize; i++) {
                LatLng found = StMethods.search(((LatLng) point).latitude, allChargingStations, true);
                if (StMethods.distanceBetweenKM(found.latitude,found.longitude,((LatLng) point).latitude,((LatLng) point).longitude) <= 1){
                    validLatStations.add(found);
                    allChargingStations.remove(found);
                }else{
                    break;
                }
            }
            validLatStations.sort(new LongditudeComparator());
            int validLatStationSize = validLatStations.size();
            for (int i = 0; i < validLatStationSize; i++) {
                LatLng found = StMethods.search(((LatLng) point).longitude, validLatStations, false);
                if (StMethods.distanceBetweenKM(found.latitude,found.longitude,((LatLng) point).latitude,((LatLng) point).longitude) <= 1){
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
        //chargingStationMap.addAllChargingStations();
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> allValidChargingStations) {
        System.out.println("ONPOST_EXECUTE_STATION_DRAWER"
        + allValidChargingStations.toString());
        Intent intent = new Intent("allValidChargingStations");
        intent.putParcelableArrayListExtra("allValidChargingStations",allValidChargingStations);
        //TODO: Bør kjøres en løkke som sjekker om noen har mottat broadcastet, før asynctask avsluttes
        LocalBroadcastManager.getInstance(chargingStation.getContext()).sendBroadcast(intent);
        //chargingStation.setValidStations(allValidChargingStations);
        //chargingStation.getPagerAdapter().getChargingStationMap().setAllValidStations(allValidChargingStations);
        //drawAllValidStations();
    }
}
