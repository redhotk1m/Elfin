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

    /**
     * Singleton class from Android, which we use to transfer large amounts of data between activities.
     * This is because android doesn't support sending large data between two activities using intents.
     * Primarily created for ChargerItems and allValidChargingStations, as these are very large.
     * It's also not possible to send classes between two activities, as far as we could see, with the exception of this singleton class.
     * Rest is used because we got lazy, and didnt refactor to use dependency injection, and instead
     * used broadcasts (Which isn't ideal).
     * Broadcasts is like a subscription based listener, which only sends to those interested in that *particular* event.
     * (Only those interested in that specific intent, gets it).
     **/

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
        return new ArrayList<>(allValidChargingStations);
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
        App.elbil = elbil;
    }
}
