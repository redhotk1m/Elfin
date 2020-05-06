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

    RecyclerView recyclerView3;
    List<ChargerItem> chargerItemList;
    private ChargingStations chargingStations;
    RecyleViewAdapter recyleViewAdapter;
    private ProgressBar spinner;

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
        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        System.out.println("ON CREATE BLIR KJØRT");
        /*
        chargerItemList = new ArrayList<>();
        chargerItemList.add(0,new ChargerItem("lol", "Vandre litt ",
                "40 min", "4", "120 min", "4", "5km",
                R.drawable.baseline_battery_charging_full_black_24dp, R.drawable.baseline_battery_charging_full_black_24dp));
        recyleViewAdapter = new RecyleViewAdapter(getContext(), chargerItemList);
        recyclerView3.setLayoutManager(new LinearLayoutManager(chargingStations.getContext()));
        recyclerView3.setAdapter(recyleViewAdapter);
        recyclerView3.setVisibility(View.INVISIBLE);
         */
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
        System.out.println("ON PAUSE LIST");
    }

    ArrayList<ChargerItem> chargeritems = new ArrayList<>();

    ChargerItem chargerItem;
    public void setAllValidStations(ArrayList<ChargerItem> validStations) {
        //TODO: Denne blir kalt når alle stasjonene er FUNNET!
        if (spinner != null)
            spinner.setVisibility(View.GONE);
        chargeritems = validStations;

        /*Location selected_location=new Location("locationA");
        selected_location.setLatitude(59.9139);
        selected_location.setLongitude(10.7522);

        chargerItemList = new ArrayList<>();
        ArrayList<String> chargerListDistance = new ArrayList<>();

        double km = 1000;
        Location near_locations = new Location("locationB");


        for (ChargerItem chargerItem : validStations){

                near_locations.setLatitude(chargerItem.getLatLng().latitude);
                near_locations.setLongitude(chargerItem.getLatLng().longitude);
                double distance = selected_location.distanceTo(near_locations);
                if(distance > km){
                    distance/=km;
                }
                DecimalFormat df2 = new DecimalFormat("#.#");
                df2.setRoundingMode(RoundingMode.UP);
                chargerListDistance.add(""+df2.format(distance) + " km");
        }*/

        updateList(validStations);
    }

    private void updateList(ArrayList<ChargerItem> validStations){
        validStations.sort(new MetersComparator());
        recyleViewAdapter = new RecyleViewAdapter(getContext(), validStations);
        recyclerView3.setLayoutManager(new LinearLayoutManager(chargingStations.getContext())); //IKKE BRUK GETCONTEXT
        recyclerView3.setAdapter(recyleViewAdapter);
        recyclerView3.setVisibility(View.VISIBLE);
    }


    double drivenMetersFromLast = 0;
    ArrayList<ChargerItem> drivenPastCHargerItems = new ArrayList<>();
    double metersFromLastChargerRemove = 0;
    public void updateListKM(double drivenMetersSoFar) {
        ArrayList<ChargerItem> listOfChargers = new ArrayList<>();
        if (chargeritems == null || chargeritems.size() <= 0)
            return;

        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        System.out.println(drivenMetersSoFar);
        double metersFromStartLocation = 0;
        for(ChargerItem c : chargeritems){
            if (!(drivenMetersSoFar > (metersFromStartLocation = Double.parseDouble(c.getMFromStartLocation())))){
                System.out.println("m from start: " + metersFromStartLocation);
                listOfChargers.add(c);
                c.setmFromCar(metersFromStartLocation - drivenMetersSoFar);
            }
        }
        updateList(listOfChargers);

        /*for (Integer integer : integerArrayList){
            System.out.println("Skal slette element nr: " + integer + " " + integer.intValue());
            chargeritems.remove(integer.intValue());
        }
        if (drivenMetersSoFar > drivenMetersFromLast && !chargeritems.isEmpty()) {
            int i = 0;
            for (ChargerItem oneChargerItem : chargeritems ){
                double distance = Double.parseDouble(oneChargerItem.getmFromCar());
                double meterFromPoint = Double.parseDouble(oneChargerItem.getMFromStartLocation()) - (drivenMetersSoFar-drivenMetersFromLast);
                oneChargerItem.setmFromCar((float) meterFromPoint);
                if(distance + 2000 < drivenMetersSoFar){
                    drivenPastCHargerItems.add(oneChargerItem);
                    //chargeritems.remove(oneChargerItem);
                    integerArrayList.add(i);
                    System.out.println("Her skal vi snart slette no jaaa");
                }
                i++;
            }
            for (Integer integer : integerArrayList){
                System.out.println("Skal slette element nr: " + integer + " " + integer.intValue());
                chargeritems.remove(integer.intValue());
            }

            drivenMetersFromLast = drivenMetersSoFar;



        }
        /*
        else {
            System.out.println("Size  -->  " + chargeritems.size());
            double drivingBack = drivenMetersFromLast-drivenMetersSoFar;

            for (ChargerItem chargerItems : chargeritems) {
                double meterFromPoint = Double.parseDouble(chargerItems.getMFromStartLocation()) + drivingBack;
                chargerItems.setMFromStartLocation((float) meterFromPoint);
            }



            if(!drivenPastCHargerItems.isEmpty()){
                if(drivenMetersSoFar < metersFromLastChargerRemove){
                    int lastElement =drivenPastCHargerItems.size() -1;
                    chargeritems.add(drivenPastCHargerItems.get(lastElement ));
                    drivenPastCHargerItems.remove(lastElement);
                }
            }


        }

         */

        //setAllValidStations(listOfChargers);

    }
}
