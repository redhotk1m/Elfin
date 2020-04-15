package com.example.elfin.Activities.Station.StationMap;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.R;
import com.example.elfin.Utils.App;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class ChargingStationMap extends Fragment {

    private MapView mMapView;
    private GoogleMap gMap;
    private ChargingStations chargingStations;

    public ChargingStationMap(ChargingStations chargingStations) {
        this.chargingStations = chargingStations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,new IntentFilter("polyLineOptions"));

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_charging_station_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//Get map to display instantly
        final FusedLocationProviderClient locationProviderClient
                = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Denne metoden kjører når kartet er klart, og googleMap != null
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                gMap = googleMap;
                chargingStations.mapCreated = true;
                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                Task<Location> locationResult = locationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng,10);
                        googleMap.moveCamera(update);
                    }
                });
                drawRoute();
                drawValidStations();
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //boolean message = intent.getBooleanExtra("polyLineOptions");
            //if ("error".equals(message))
            //    System.out.println("error");
                //TODO: Error message to user
            {
                App applicationContext = (App)getActivity().getApplication();
                setPolyLineOptions(applicationContext.getPolylineOptions());
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
            }
        }
    };

    private PolylineOptions polylineOptions;
    public void setPolyLineOptions(PolylineOptions polyLineOptions){
        this.polylineOptions = polyLineOptions;
        chargingStations.routeCreated = true;
        drawRoute();
    }

    private void drawRoute(){
        if (chargingStations.mapCreated && chargingStations.routeCreated){
            gMap.addPolyline(polylineOptions);
        }
    }

    private ArrayList<ChargerItem> validStations;
    public void setAllValidStations(ArrayList<ChargerItem> validStations){
        this.validStations = validStations;
        chargingStations.validStationsFound = true;
        drawValidStations();
    }

    private void drawValidStations(){
        if (chargingStations.mapCreated && chargingStations.validStationsFound){
            int amountOfChargingStations = 0;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Object chargerItem : validStations) {
                amountOfChargingStations++;
                builder.include(drawChargingStations(((ChargerItem) chargerItem)).getPosition());
            }
            if (amountOfChargingStations <= 1)
                return;
            LatLngBounds bounds = builder.build();
            int padding = 150;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
            gMap.animateCamera(cu);
        }
    }

    private Marker drawChargingStations(ChargerItem chargerItem){
        return gMap.addMarker(new MarkerOptions()
                .position(chargerItem.getLatLng())
                .title(chargerItem.getStreet())
                .snippet(chargerItem.getCity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume","Er i onResume Charging Stations MAP");
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /*
    googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
    For zooming automatically to the location of the marker
    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(4).build();
    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    */

}
