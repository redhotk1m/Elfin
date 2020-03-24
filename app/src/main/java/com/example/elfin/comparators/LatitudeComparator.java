package com.example.elfin.comparators;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;

public class LatitudeComparator implements Comparator<LatLng> {

    @Override
    public int compare(LatLng c1, LatLng c2) {
        return Double.compare(c1.latitude, c2.latitude);
    }
}
