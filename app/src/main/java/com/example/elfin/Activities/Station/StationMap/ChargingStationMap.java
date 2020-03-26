package com.example.elfin.Activities.Station.StationMap;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.elfin.API.TaskRequestDirections;
import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChargingStationMap extends Fragment {

    MapView mMapView;
    public GoogleMap gMap;

    private ChargingStationMap chargingStationMap;
    private String destinationID;
    private boolean hasCreatedValidStations = false;
    private boolean hasCreatedPolyline = false;
    private boolean hasCreatedMap = false;
    ChargingStations chargingStations;


    private ArrayList<LatLng> allChargingStations;

    public ChargingStationMap(ChargingStations chargingStations) {
        this.chargingStations = chargingStations;
        chargingStationMap = this;
        destinationID = chargingStations.getBundle().getString("destinationID");
        allChargingStations = chargingStations.getBundle().getParcelableArrayList("chargingStations");
    }

    public GoogleMap getgMap(){
        return gMap;
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
                gMap = googleMap;
                chargingStations.mapCreated = true;
                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);


                ArrayList<LatLng> latLngs = new ArrayList<>();
                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng( 59.964161,15.730915);

                //latLngs.sort(new LongditudeComparator());
                //System.out.println(latLngs + " etter");
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(4).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //TaskRequestDirections taskRequestDirections = new TaskRequestDirections(view.getContext(),chargingStationMap);
                //taskRequestDirections.execute(destinationID);
                //Nobil a = new Nobil(chargingStationMap);
                //a.execute("");
                drawRoute();
                drawValidStations();
            }
        });



    }

    public ArrayList<LatLng> getAllChargingStations() {
        return allChargingStations;
    }



    public void drawPolyLines(PolylineOptions polylineOptions){
        gMap.addPolyline(polylineOptions);
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

    PolylineOptions polylineOptions;
    public void setPolyLineOptions(PolylineOptions polyLineOptions){
        this.polylineOptions = polyLineOptions;
        chargingStations.routeCreated = true;
        drawRoute();
    }

    public void drawRoute(){
        if (chargingStations.mapCreated && chargingStations.routeCreated){
            gMap.addPolyline(polylineOptions);
        }
    }

    ArrayList<LatLng> validStations;
    public void setAllValidStations(ArrayList<LatLng> validStations){
        System.out.println("Har satt valid stations i map");
        this.validStations = validStations;
        chargingStations.validStationsFound = true;
        drawValidStations();
    }


    public void drawValidStations(){
        if (chargingStations.mapCreated && chargingStations.validStationsFound){
            System.out.println("går  gjennom alle valid");
            for (Object latLng : validStations) {
                drawChargingStations((LatLng) latLng);
            }
        }
    }

    public void drawChargingStations(LatLng latLng){
        System.out.println("tegner");
        gMap.addMarker(new MarkerOptions().position(latLng).title("test123").snippet("test9321"));
    }

}
