package com.example.elfin.Utils;

import com.google.android.gms.maps.model.LatLng;

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

    public static double distanceBetweenFlat(double x1, double y1, double x2, double y2){
        double startTime = System.currentTimeMillis();
        double a = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double endTime = System.currentTimeMillis();
        //System.out.println(endTime-startTime/1000 + " FLAT");
        return a;
    }

    public static LatLng search(double value, ArrayList<LatLng> a, boolean isLatitude) {
        if (isLatitude) {
            if (value < a.get(0).latitude) {
                return a.get(0);
            }
            if (value > a.get(a.size() - 1).latitude) {
                return a.get(a.size() - 1);
            }

            int lo = 0;
            int hi = a.size() - 1;

            while (lo <= hi) {
                int mid = (hi + lo) / 2;

                if (value < a.get(mid).latitude) {
                    hi = mid - 1;
                } else if (value > a.get(mid).latitude) {
                    lo = mid + 1;
                } else {
                    return a.get(mid);
                }
            }
            // lo == hi + 1
            return (a.get(lo).latitude - value) < (value - a.get(hi).latitude) ? a.get(lo) : a.get(hi);
        } else {
            if (value < a.get(0).longitude) {
                return a.get(0);
            }
            if (value > a.get(a.size() - 1).longitude) {
                return a.get(a.size() - 1);
            }

            int lo = 0;
            int hi = a.size() - 1;

            while (lo <= hi) {
                int mid = (hi + lo) / 2;

                if (value < a.get(mid).longitude) {
                    hi = mid - 1;
                } else if (value > a.get(mid).longitude) {
                    lo = mid + 1;
                } else {
                    return a.get(mid);
                }
            }
            // lo == hi + 1
            return (a.get(lo).longitude - value) < (value - a.get(hi).longitude) ? a.get(lo) : a.get(hi);
        }
    }

}
