package com.elfin.elfin.comparators;

import com.elfin.elfin.Activities.Station.StationList.ChargerItem;

import java.util.Comparator;

public class LatitudeComparator implements Comparator<ChargerItem> {

    @Override
    public int compare(ChargerItem c1, ChargerItem c2) {
        return Double.compare(c1.getLatLng().latitude, c2.getLatLng().latitude);
    }

}
