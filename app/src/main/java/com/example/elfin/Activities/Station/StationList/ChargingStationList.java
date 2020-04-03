package com.example.elfin.Activities.Station.StationList;


import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.R;
import com.example.elfin.adapter.RecyleViewAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChargingStationList extends Fragment {

    RecyclerView recyclerView3;
    List<ChargerItem> chargerItemList;
    ChargingStations chargingStations;
    RecyleViewAdapter recyleViewAdapter;


    public ChargingStationList(ChargingStations chargingStations) {
        this.chargingStations = chargingStations;
        // Required empty public constructor
    }

    //TODO: Sjekk hvis chargingStations.routeCreated && routeCreated = true før noe vises, bør ha loading mens disse er false.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_charging_station_list, container, false);
        recyclerView3 = rootView.findViewById(R.id.resyclerViewItems);
        recyclerView3.setNestedScrollingEnabled(true);
        chargerItemList = new ArrayList<>();
        chargerItemList.add(0,new ChargerItem("lol", "Vandre litt ",
                "40 min", "4", "120 min", "4", "5km",
                R.drawable.baseline_battery_charging_full_black_24dp, R.drawable.baseline_battery_charging_full_black_24dp));
        recyleViewAdapter = new RecyleViewAdapter(getContext(), chargerItemList);
        recyclerView3.setLayoutManager(new LinearLayoutManager(chargingStations.getContext()));
        recyclerView3.setAdapter(recyleViewAdapter);
        recyclerView3.setVisibility(View.INVISIBLE);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume","Er i onResume Charging Stations LIST");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("ON PAUSE LIST");
    }

    public void setAllValidStations(ArrayList<LatLng> validStations) {
        //TODO: Denne blir kalt når alle stasjonene er FUNNET!
        chargerItemList = new ArrayList<>();

        for (int i = 0; i <validStations.size()-1 ; i++) {
            chargerItemList.add(i,new ChargerItem(validStations.get(i).toString(), "Vandre litt ",
                    "40 min", "4", "120 min", "4", "5km",
                    R.drawable.baseline_battery_charging_full_black_24dp, R.drawable.baseline_battery_charging_full_black_24dp));
        }

        recyleViewAdapter = new RecyleViewAdapter(getContext(), chargerItemList);
        recyclerView3.setLayoutManager(new LinearLayoutManager(chargingStations.getContext()));
        recyclerView3.setAdapter(recyleViewAdapter);
        recyclerView3.setVisibility(View.VISIBLE);

    }
}
