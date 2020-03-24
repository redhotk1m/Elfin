package com.example.elfin;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChargingStationList extends Fragment {

    View v;

    private  RecyclerView recyclerView3;
    private List<ChargerItem> chargerItemList;






    public ChargingStationList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_charging_station_list, container, false);
        recyclerView3 = rootView.findViewById(R.id.resyclerViewItems);
        recyclerView3.setNestedScrollingEnabled(true);

        recyclerView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("123");
            }
        });



        // Inflate the layout for this fragment

        chargerItemList = new ArrayList<>();
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));
        chargerItemList.add(new ChargerItem("Cirkle K", "Veikro", "Ladetid 40 min", "4 ledige",
                "Ladetid 75 min", "1 ledig", "5km", R.drawable.baseline_battery_charging_full_black_24dp,
                R.drawable.baseline_battery_charging_full_black_24dp));


        RecyleViewAdapter recyleViewAdapter = new RecyleViewAdapter(getContext(), chargerItemList);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView3.setAdapter(recyleViewAdapter);
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
}
