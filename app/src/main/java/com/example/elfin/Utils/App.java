package com.example.elfin.Utils;

import android.app.Application;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.Activities.Station.StationMap.PolyPoint;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class App extends Application {

    private ArrayList<ChargerItem> chargerItems;
    private PolylineOptions polylineOptions;
    private ArrayList<ChargerItem> allValidChargingStations;
    private ArrayList<PolyPoint> polypoints;

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


}
