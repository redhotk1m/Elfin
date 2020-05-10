package com.elfin.elfin.Utils;

import com.elfin.elfin.Activities.Station.StationList.ChargerItem;

import java.util.ArrayList;

public class StMethods {
    public static long distanceBetweenKM(double lat1, double lon1, double lat2, double lon2){
        double startTime = System.currentTimeMillis();
        double p = 0.017453292519943295;
        double a = 0.5 - Math.cos((lat2 - lat1) * p)/2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p))/2;
        long result = Math.round(12742 * Math.asin(Math.sqrt(a)));
        double endTime = System.currentTimeMillis();
        //System.out.println(endTime-startTime/1000);
        return result;
    }

    public static ChargerItem search(double value, ArrayList<ChargerItem> a, boolean isLatitude) {
        if (isLatitude) {
            if (value < a.get(0).getLatLng().latitude) {
                return a.get(0);
            }
            if (value > a.get(a.size() - 1).getLatLng().latitude) {
                return a.get(a.size() - 1);
            }

            int lo = 0;
            int hi = a.size() - 1;

            while (lo <= hi) {
                int mid = (hi + lo) / 2;

                if (value < a.get(mid).getLatLng().latitude) {
                    hi = mid - 1;
                } else if (value > a.get(mid).getLatLng().latitude) {
                    lo = mid + 1;
                } else {
                    return a.get(mid);
                }
            }
            // lo == hi + 1
            return (a.get(lo).getLatLng().latitude - value) < (value - a.get(hi).getLatLng().latitude) ? a.get(lo) : a.get(hi);
        } else {
            if (value < a.get(0).getLatLng().longitude) {
                return a.get(0);
            }
            if (value > a.get(a.size() - 1).getLatLng().longitude) {
                return a.get(a.size() - 1);
            }

            int lo = 0;
            int hi = a.size() - 1;

            while (lo <= hi) {
                int mid = (hi + lo) / 2;

                if (value < a.get(mid).getLatLng().longitude) {
                    hi = mid - 1;
                } else if (value > a.get(mid).getLatLng().longitude) {
                    lo = mid + 1;
                } else {
                    return a.get(mid);
                }
            }
            // lo == hi + 1
            return (a.get(lo).getLatLng().longitude - value) < (value - a.get(hi).getLatLng().longitude) ? a.get(lo) : a.get(hi);
        }
    }

}
