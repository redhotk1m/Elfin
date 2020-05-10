package com.elfin.elfin.Activities.Station.StationList;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.elfin.elfin.Activities.Station.ChargingStations;
import com.elfin.elfin.R;
import com.elfin.elfin.adapter.RecyleViewAdapter;
import com.elfin.elfin.comparators.MetersComparator;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChargingStationList extends Fragment {

    private RecyclerView recyclerView3;
    private ChargingStations chargingStations;
    private RecyleViewAdapter recyleViewAdapter;
    private ProgressBar spinner;

    public ChargingStationList(ChargingStations chargingStations) {
        this.chargingStations = chargingStations;
    }

    //TODO: Sjekk hvis chargingStations.routeCreated && routeCreated = true før noe vises, bør ha loading mens disse er false.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_charging_station_list, container, false);
        recyclerView3 = rootView.findViewById(R.id.resyclerViewItems);
        recyclerView3.setNestedScrollingEnabled(true);
        spinner = rootView.findViewById(R.id.progressBar1);
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", "Er i onResume Charging Stations LIST");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private ArrayList<ChargerItem> chargeritems = new ArrayList<>();

    public void setAllValidStations(ArrayList<ChargerItem> validStations) {
        //TODO: Denne blir kalt når alle stasjonene er FUNNET!
        if (spinner != null)
            spinner.setVisibility(View.GONE);
        chargeritems = validStations;
        updateList(validStations);
    }

    private void updateList(ArrayList<ChargerItem> validStations){
        validStations.sort(new MetersComparator());
        recyleViewAdapter = new RecyleViewAdapter(getContext(), validStations);
        recyclerView3.setLayoutManager(new LinearLayoutManager(chargingStations.getContext())); //IKKE BRUK GETCONTEXT
        recyclerView3.setAdapter(recyleViewAdapter);
        recyclerView3.setVisibility(View.VISIBLE);
    }


    public void updateListKM(double drivenMetersSoFar) {
        ArrayList<ChargerItem> listOfChargers = new ArrayList<>();
        if (chargeritems == null || chargeritems.size() <= 0)
            return;
        double metersFromStartLocation = 0;
        for(ChargerItem c : chargeritems){
            if (!(drivenMetersSoFar > (metersFromStartLocation = Double.parseDouble(c.getMFromStartLocation())))){
                listOfChargers.add(c);
                c.setmFromCar(metersFromStartLocation - drivenMetersSoFar);
            }
        }
        updateList(listOfChargers);
    }
}
