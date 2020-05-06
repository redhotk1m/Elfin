package com.elfin.elfin.Activities.Station.StationMap;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elfin.elfin.Activities.Station.ChargingStations;
import com.elfin.elfin.Activities.Station.StationList.ChargerItem;
import com.elfin.elfin.R;
import com.elfin.elfin.Utils.App;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

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
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter("polyLineOptions"));

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

                double longd = chargingStations.getBundle().getDouble("longditude");
                double lati = chargingStations.getBundle().getDouble("latitude");
                LatLng currentLatLng = new LatLng(lati, longd);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 10);
                googleMap.moveCamera(update);

                drawRoute();
                drawValidStations();
            }
        });
    }


    @SuppressLint("MissingPermission")
    public static Location getLastKnownLoaction(boolean enabledProvidersOnly, Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location utilLocation = null;
        List<String> providers = manager.getProviders(enabledProvidersOnly);
        for (String provider : providers) {

            utilLocation = manager.getLastKnownLocation(provider);
            if(utilLocation != null) return utilLocation;
        }
        return null;
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //boolean message = intent.getBooleanExtra("polyLineOptions");
            //if ("error".equals(message))
            //    System.out.println("error");
                //TODO: Error message to user
            {
                App applicationContext = (App)chargingStations.getContext().getApplication();
                setPolyLineOptions(applicationContext.getPolylineOptions());
            }
        }
    };

    private PolylineOptions polylineOptions;
    public void setPolyLineOptions(PolylineOptions polyLineOptions){
        this.polylineOptions = polyLineOptions;
        //TODO: Kanskje sette til false etterpå?
        chargingStations.routeCreated = true;
        drawRoute();
    }

    private void drawRoute(){
        if (chargingStations.mapCreated && chargingStations.routeCreated){
            gMap.clear();
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
                ChargerItem currentChargerItem = (ChargerItem) chargerItem;
                Marker currentMarker = drawChargingStations(currentChargerItem);
                if (currentChargerItem.getLightningText().equals("Lynlading"))
                    currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.ic_lightning)));
                else
                    currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitmap(R.drawable.ic_charger)));
                builder.include(currentMarker.getPosition());
            }
            if (amountOfChargingStations <= 1)
                return;
            LatLngBounds bounds = builder.build();
            int padding = 150;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
            gMap.animateCamera(cu);
        }
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),drawableRes,null);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
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
