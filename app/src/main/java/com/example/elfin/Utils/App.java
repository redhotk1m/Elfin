package com.example.elfin.Utils;

import android.app.Application;

import com.example.elfin.Activities.Station.StationList.ChargerItem;

import java.util.ArrayList;

public class App extends Application {

    private ArrayList<ChargerItem> chargerItems;

    public ArrayList<ChargerItem> getChargerItems() {
        if (chargerItems != null)
        return new ArrayList<>(chargerItems);
        else return null;
    }

    public void setChargerItems(ArrayList<ChargerItem> chargerItems) {
        this.chargerItems = chargerItems;
    }
}
