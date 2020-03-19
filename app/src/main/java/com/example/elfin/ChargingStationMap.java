package com.example.elfin;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.elfin.API.Nobil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChargingStationMap extends Fragment {

    MapView mMapView;
    public static GoogleMap gMapStatic;
    static ChargingStationMap chargingStationMap;

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    PolylineOptions polylineOptions;

    public ChargingStationMap() {
        chargingStationMap = this;
    }
    public GoogleMap getgMapStatic(){
        return gMapStatic;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_charging_station_map, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        System.out.println("Kjører onCreateView");
        mMapView.onResume();//Get map to display instantly

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Denne metoden kjører når kartet er klart, og googleMap != null
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                System.out.println("Kjører onMapReady ASYNC i onCreateView");
                gMapStatic = googleMap;
                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng( 59.964161,10.730915);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(4).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                String googleURLDirection = "https://maps.googleapis.com/maps/api/directions/json?";
                String origin = "59.967771,10.731879";
                String destination = "59.913622,10.753237";
                String key = "AIzaSyDskTx9G4bXFvfz2T2jMiBtG8UWa5KX3KU";
                String mode = "driving";
                String depertureTime = "now";
                String parameters = googleURLDirection + "origin=" + origin + "&destination=" + destination + "&mode=" + mode + "&departure_time=" + depertureTime + "&key=" + key;
                System.out.println(parameters);
                TaskRequestDirections taskRequestDirections = new TaskRequestDirections(view.getContext());
                taskRequestDirections.execute("");
                //Nobil a = new Nobil(chargingStationMap);
                //a.execute("");
            }
        });



    }

    public void addAllChargingStations(LatLng latLng){
        gMapStatic.addMarker(new MarkerOptions().position(latLng).title("test123").snippet("test9321"));
    }


    public void drawAllPolyLines(){
        gMapStatic.addPolyline(polylineOptions);
        //googleMap.addPolyline(new PolylineOptions().color(getResources().getColor(R.color.blackColor)).width(5f).clickable(false).addAll(Utils.readEncodedPolyLinePointsFromCSV(this,)));
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
}
