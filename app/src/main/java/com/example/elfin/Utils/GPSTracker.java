package com.example.elfin.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.Activities.Station.StationMap.PolyPoint;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GPSTracker extends Service implements LocationListener {

    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private LocalBroadcastManager localBroadcastManager;

    Location location;

    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;

    protected LocationManager locationManager;

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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("Turn on your GPS to find nearby helpers");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    float[] result = new float[1];
    float[] resultNext = new float[1];
    @Override
    public void onLocationChanged(Location arg0) {
        if (arg0.distanceTo(location) > 1000) {
            location = arg0;
            App app = (App) context.getApplicationContext();
            ArrayList<PolyPoint> pointz = null;
            if (app != null)
                pointz = app.getPolypoints();
            if (pointz != null) {
                // TODO Auto-generated method stub
                // TODO: Kanskje legge inn permission sjekk hver gang her, istedenfor
                System.out.println("loc changed " + arg0.getLatitude() + " " + arg0.getLongitude());
                int idx = 0;
                Location.distanceBetween(pointz.get(0).getLatitude(), pointz.get(0).getLongditude(), arg0.getLatitude(), arg0.getLongitude(), result);
                System.out.println(result[0] + " er distanse mellom punkt 0 og oss");
                int counter = 0;
                for (int i = 1; i < pointz.size(); i++) {
                    Location.distanceBetween(pointz.get(i).getLatitude(), pointz.get(i).getLongditude(), arg0.getLatitude(), arg0.getLongitude(), resultNext);
                    System.out.println("resN = " + resultNext[0] + "res = " + result[0]);
                    if (resultNext[0] < result[0]) {
                        idx = i;
                        result[0] = resultNext[0];
                        System.out.println("Nå har vi kjørt lenger enn før");
                    }else{
                        counter ++;
                        if (counter > 20) {
                            double currentDrivenKM = pointz.get(idx).getDrivenKM();
                            System.out.println("Return med ca " + currentDrivenKM);
                            sendKMDrivenSoFar(currentDrivenKM);
                            return;
                        }
                    }
                }
                double currentDrivenKM = pointz.get(idx).getDrivenKM();
                sendKMDrivenSoFar(currentDrivenKM);
                System.out.println("Nå er vi kjørt ca: " + currentDrivenKM);
            }
        }

    }

    private void sendKMDrivenSoFar(double currentDrivenKM){
        Intent intent = new Intent("updateKMList");
        intent.putExtra("case","updateKMList");
        intent.putExtra("drivenMetersSoFar",currentDrivenKM);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void initializeDriving(){

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
}