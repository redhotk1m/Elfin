package com.example.elfin.Activities.Station.StationMap;

import com.google.android.gms.maps.model.LatLng;

public class PolyPoint {
    private double longditude;
    private double latitude;
    private double drivenKM;

    public PolyPoint(LatLng latLng){
        this.longditude = latLng.longitude;
        this.latitude = latLng.latitude;
    }

    public PolyPoint(double latitude, double longditude){
        this.latitude = latitude;
        this.longditude = longditude;
    }

    public void setDrivenKM(double drivenKM) {
        this.drivenKM = drivenKM;
    }

    public double getDrivenKM() {
        return drivenKM;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongditude() {
        return longditude;
    }

    public void setLongditude(double longditude) {
        this.longditude = longditude;
    }
}
