package com.example.elfin.Utils;

import android.app.Application;

import com.example.elfin.Activities.Station.StationList.ChargerItem;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class App extends Application {

    private ArrayList<ChargerItem> chargerItems;

    public ArrayList<ChargerItem> getChargerItems() {
        return chargerItems;
    }

    public void setChargerItems(ArrayList<ChargerItem> chargerItems) {
        this.chargerItems = chargerItems;
    }
}
