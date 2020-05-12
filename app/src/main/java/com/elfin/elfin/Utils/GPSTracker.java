package com.elfin.elfin.Utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elfin.elfin.Activities.Station.StationMap.PolyPoint;

import java.util.ArrayList;

public class GPSTracker extends Service implements LocationListener {

    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private LocalBroadcastManager localBroadcastManager;

    static Location location;

    static double latitude;
    static double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;

    protected LocationManager locationManager;

    /**
     * Class to handle all GPS events.
     * Used to get position, check how far we've driven, etc.
     * This is then used to E.g update the list on which charging stations we show in the list,
     * and how far they're from the user's current position.
     * @param context
     */

    public GPSTracker(Context context) {
        this.context = context;
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert();
            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    Log.d("network", "using network");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }

                if (isGPSEnabled) {
                    Log.d("GPS", "Using GPS");
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void popupMessageNeedPermission() {
        System.out.println("No permission from popup method");
        //TODO: Lage popup som sier ifra at vi er nødt til å ha permission, for at appen skal fungere
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {

        DialogBox dialogBox = new DialogBox(context, "GPS is settings","Turn on your GPS to find nearby helpers",
                "Settings", "Cancel",1);
        dialogBox.createDialogBox();


    }

    float[] result = new float[1];
    float[] resultNext = new float[1];
    @Override
    public void onLocationChanged(Location arg0) {
        if (location != null && arg0.distanceTo(location) > 200) {
            location = arg0;
            App app = (App) context.getApplicationContext();
            ArrayList<PolyPoint> pointz = null;
            if (app != null) pointz = app.getPolypoints();
            if (pointz != null) {
                // TODO: Kanskje legge inn permission sjekk hver gang her, istedenfor
                int idx = 0;
                Location.distanceBetween(pointz.get(0).getLatitude(),
                        pointz.get(0).getLongditude(),
                        arg0.getLatitude(),
                        arg0.getLongitude(),
                        result);
                int counter = 0;
                for (int i = 1; i < pointz.size(); i++) {
                    Location.distanceBetween(pointz.get(i).getLatitude(),
                            pointz.get(i).getLongditude(),
                            arg0.getLatitude(),
                            arg0.getLongitude(),
                            resultNext);
                    if (resultNext[0] < result[0]) {
                        idx = i;
                        result[0] = resultNext[0];
                    }
                }
                //getDrivenKM is a very accurate value on the distance between startPoint and currentPoint.
                //We take in account all the curves of the route.
                //This means we do point.setDrivenKM(distanceBetween(lastPoint.LatLng,currentPoint.LatLng) + lastPoint.getDrivenKM)
                double currentDrivenKM = pointz.get(idx).getDrivenKM();
                sendKMDrivenSoFar(currentDrivenKM);
                Location closestPolyPoint = new Location("this");
                closestPolyPoint.setLongitude(pointz.get(idx).getLongditude());
                closestPolyPoint.setLatitude(pointz.get(idx).getLatitude());
                if (arg0.distanceTo(closestPolyPoint) > 2000)
                    sendDrivenTooFarOffRoute();
            }
        }

    }


    private void sendKMDrivenSoFar(double currentDrivenKM){
        //Notify the correct subscriber how far we've progressed on our route.
        //This is accurate, not just currentDrivenKM + LastDrivenKM +... etc
        //It depends on which point on the route we're closest to.
        Intent intent = new Intent("gpsTracker");
        intent.putExtra("case","updateKMList");
        intent.putExtra("drivenMetersSoFar",currentDrivenKM);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void sendDrivenTooFarOffRoute(){
        //Notify the correct subscriber that we've driven too far off the route, so it can handle it.
        Intent intent = new Intent("gpsTracker");
        intent.putExtra("case","drivenTooFarOffRoute");
        localBroadcastManager.sendBroadcast(intent);
    }


    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
        System.out.println("Slått av");
    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
        System.out.println("slått på");
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static Location getLastKnownLocation(){
        return location;
    }
}