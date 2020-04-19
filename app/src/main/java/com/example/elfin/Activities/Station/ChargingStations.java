package com.example.elfin.Activities.Station;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.elfin.API.RetrieveJSON;
import com.example.elfin.API.TaskRequestDirections;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.Activities.Station.StationList.ChargingStationList;
import com.example.elfin.R;
import com.example.elfin.Utils.App;
import com.example.elfin.adapter.PageAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ChargingStations extends AppCompatActivity {

    Bundle bundle;
    TextView toTextView;

    ArrayList<ChargerItem> allChargingStations;
    App applicationContext;
    PageAdapter pagerAdapter;
    String toText ;
    TextView textViewTo;
    public boolean
            mapCreated = false,
            routeCreated = false,
            validStationsFound = false;

    private Activity activity;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        toTextView = findViewById(R.id.tilTextView);
        bundle = getIntent().getBundleExtra("bundle");
        toText = bundle.getString("destination");
        applicationContext = (App)getApplication();
        setAllChargingItems();
        toTextView.setText("Til: " +toText);
        activity = this;
        final PageAdapter pagerAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),this);
        setPagerAdapter(pagerAdapter);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0){
                    pagerAdapter.notifyDataSetChanged();
                    //ChargingStationList c = (ChargingStationList)pagerAdapter.getItem(0);
                    //getSupportFragmentManager().findFragmentById(1);
                    //Gjør ingenting, kjører bare onResume for testing
                    //pagerAdapter.getItem(0).onResume();//Kjører onresume i Listen,
                    // dersom listen er valgt fra Map.
                    // Lager ikke nytt objekt siden det allerede eksisterer
                    // Bør skje dersom noe er blitt endret i kriterier, sharedPrefs?
                }
                else if (tab.getPosition() == 1){
                    pagerAdapter.notifyDataSetChanged();
                    //Gjør ingenting, bare testing
                   // getSupportFragmentManager().findFragmentById(0);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        registerBroadcastReceivers();

        if (allChargingStations != null && allChargingStations.size() > 0)
            startRequestDirections();
    }

    private void registerBroadcastReceivers(){
        System.out.println("Kjører register broadcasts");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        receiver,new IntentFilter("allStations"));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        receiver,new IntentFilter("allValidStations"));
    }

    private void setAllChargingItems(){
        allChargingStations = applicationContext.getChargerItems();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("case");
            if (message == null)
                message = "error";
            switch (message) {
                case "error":
                    //TODO: Error to user
                    handleError(); //Does nothing at the moment.
                    System.out.println("Error, should not happen");
                    break;
                case "allStations":
                    System.out.println("All Stations is handled");
                    handleAllStations();
                    break;
                case "allValidStations":
                    System.out.println("All Valid Stations is handled");
                    handleAllValidStations();
                    break;
                case "unregisterBroadcastReceivers":
                    unregisterBroadcastReceivers();
                    break;
                default:
                        System.out.println("error");
                        handleError();
                        break;
            }
        }
    };

    private void handleAllStations(){
        setAllChargingItems();
        startRequestDirections();
    }

    private void handleError(){

    }

    private void handleAllValidStations(){
        ArrayList<ChargerItem> allValidStations = applicationContext.getAllValidChargingStations();
        pagerAdapter.getChargingStationList().setAllValidStations(allValidStations);
        pagerAdapter.getChargingStationMap().setAllValidStations(allValidStations);
    }

    private void unregisterBroadcastReceivers(){
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","Er i onResume Charging Stations");
    }

    public void startRequestDirections(){
        String ID = bundle.getString("destinationID");
        String googleURLDirection = "https://maps.googleapis.com/maps/api/directions/json?";
        String longditude = String.valueOf(bundle.getDouble("longditude"));
        String latitude = String.valueOf(bundle.getDouble("latitude"));
        String origin = latitude + "," + longditude;
        String destination = ID;
        String key = "AIzaSyDskTx9G4bXFvfz2T2jMiBtG8UWa5KX3KU";
        String mode = "driving";
        String departureTime = "now";
        String region = "no";
        String parameters = googleURLDirection + "origin=" + origin + "&destination=place_id:" + destination + "&mode=" + mode + "&region=" + region + "&key=" + key;
        System.out.println(parameters);
        RetrieveJSON directionFromAPI = new RetrieveJSON(activity,TaskRequestDirections.class);
        directionFromAPI.execute(parameters);
        //TaskRequestDirections taskRequestDirections = new TaskRequestDirections(activity);
        //taskRequestDirections.execute(bundle.getString("destinationID"));
    }

    private void setPagerAdapter(PageAdapter pagerAdapter){
        this.pagerAdapter = pagerAdapter;
    }

    public Bundle getBundle(){
        return bundle;
    }

    public Activity getContext(){
        return activity; //Skal slettes, må refaktoreres i ChargingStationList
    }

    @Override
    protected void onDestroy() {
        unregisterBroadcastReceivers();
        super.onDestroy();
    }
}
