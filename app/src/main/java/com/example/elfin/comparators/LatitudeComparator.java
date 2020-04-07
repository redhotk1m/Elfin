package com.example.elfin.comparators;

import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

public class LatitudeComparator implements Comparator<ChargerItem> {

    @Override
    public int compare(ChargerItem c1, ChargerItem c2) {
        return Double.compare(c1.getLatLng().latitude, c2.getLatLng().latitude);
    }

}
