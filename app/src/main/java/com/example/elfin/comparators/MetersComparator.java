package com.example.elfin.comparators;

import com.example.elfin.Activities.Station.StationList.ChargerItem;

import java.util.Comparator;

public class MetersComparator implements Comparator<ChargerItem> {
    @Override
    public int compare(ChargerItem chargerItem, ChargerItem t1) {
        return Double.compare(Double.parseDouble(chargerItem.getMFromStartLocation()),
                Double.parseDouble(t1.getMFromStartLocation()));
    }
}
