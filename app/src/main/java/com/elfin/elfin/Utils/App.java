package com.elfin.elfin.Utils;

import android.app.Application;

import com.elfin.elfin.Activities.Station.StationList.ChargerItem;
import com.elfin.elfin.Activities.Station.StationMap.PolyPoint;
import com.elfin.elfin.car.Elbil;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class App extends Application {

    private ArrayList<ChargerItem> chargerItems;
    private PolylineOptions polylineOptions;
    private ArrayList<ChargerItem> allValidChargingStations;
    private ArrayList<PolyPoint> polypoints;
    private static Elbil elbil;

    public ArrayList<ChargerItem> getChargerItems() {
        if (chargerItems != null)
        return new ArrayList<>(chargerItems);
        else return null;

    }

    public void setChargerItems(ArrayList<ChargerItem> chargerItems) {
        this.chargerItems = chargerItems;
    }

    public void setPolyLineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setAllValidChargingStations(ArrayList<ChargerItem> allValidChargingStations) {
        this.allValidChargingStations = allValidChargingStations;
    }

    public ArrayList<ChargerItem> getAllValidChargingStations() {
        if (allValidChargingStations != null)
        return new ArrayList<ChargerItem>(allValidChargingStations);
        else
            return null;
    }

    public void setPolyPoints(ArrayList<PolyPoint> polyPoints) {
        this.polypoints = polyPoints;
    }

    public ArrayList<PolyPoint> getPolypoints() {
        return polypoints;
    }


    public Elbil getElbil() {
        return elbil;
    }

    public void setElbil(Elbil elbil) {
        this.elbil = elbil;
    }
}
