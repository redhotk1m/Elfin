package com.example.elfin.staticMethods;

public class Distance {
    public static long distanceBetweenKM(double lat1, double lon1, double lat2, double lon2){
        double startTime = System.currentTimeMillis();
        double p = 0.017453292519943295;
        double a = 0.5 - Math.cos((lat2 - lat1) * p)/2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p))/2;
        long result = Math.round(12742 * Math.asin(Math.sqrt(a)));
        double endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime/1000);
        return result;
    }

    public static double distanceBetweenFlat(double x1, double y1, double x2, double y2){
        double startTime = System.currentTimeMillis();
        double a = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime/1000 + " FLAT");
        return a;
    }
}
