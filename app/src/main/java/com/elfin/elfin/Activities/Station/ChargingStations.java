package com.elfin.elfin.Activities.Station;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.elfin.elfin.API.RetrieveJSON;
import com.elfin.elfin.API.TaskRequestDirections;
import com.elfin.elfin.Activities.Station.StationList.ChargerItem;
import com.elfin.elfin.R;
import com.elfin.elfin.Utils.App;
import com.elfin.elfin.Utils.DialogBox;
import com.elfin.elfin.Utils.GPSTracker;
import com.elfin.elfin.adapter.PageAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ChargingStations extends AppCompatActivity {

    Bundle bundle;
    TextView toTextView;

    ArrayList<ChargerItem> allChargingStations;
    App applicationContext;
    PageAdapter pagerAdapter;
    String toText ;
    public boolean
            mapCreated = false,
            routeCreated = false,
            validStationsFound = false;

    private Activity activity;
    private double longditude, latitude;

    /**
     * This is the container of List/Map fragments.
     * It's main job is to create the adapter to change between those fragments,
     * and accept and handle all the broadcasts that get sent by other classes.
     * These broadcasts are used by both the List and Map, therefore it belongs in this class.
     * The reason we use Broadcasts is to notify all listening classes that an action is done.
     * It's basically a global listener. We used dependency injection before, but the information
     * we had to inject between different activities was too large for android to support, so we had
     * to use a singleton class to "transfer" all the information. So we use broadcasts to notify
     * when this information is ready.
     * @param savedInstanceState
     */

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        toTextView = findViewById(R.id.tilTextView);
        bundle = getIntent().getBundleExtra("bundle");
        longditude = bundle.getDouble("longditude");
        latitude = bundle.getDouble("latitude");
        toText = bundle.getString("destinatinasjon");
        applicationContext = (App)getApplication();
        setAllChargingItems();
        toTextView.setText("Til: " + toText);
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
                }
                else if (tab.getPosition() == 1){
                    pagerAdapter.notifyDataSetChanged();
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
            startRequestDirections(latitude,longditude);
    }

    private void registerBroadcastReceivers(){
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        receiver,new IntentFilter("allStations"));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        receiver,new IntentFilter("allValidStations"));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        receiver,new IntentFilter("gpsTracker"));
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
                case "updateKMList":
                    handleUpdateKMList(intent.getDoubleExtra("drivenMetersSoFar",0));
                    break;
                case "drivenTooFarOffRoute":
                    handleDrivenTooFarOffRoute();
                    break;
                default:
                        System.out.println("error i ChargingStations default switch case");
                        handleError();
                        break;
            }
        }
    };

    boolean hasCheckedIfCreateNewRoute = false;

    private void handleDrivenTooFarOffRoute() {
        if (!hasCheckedIfCreateNewRoute){
            hasCheckedIfCreateNewRoute = true;
            DialogBox dialogBox = new DialogBox(this,"Ny rute?","Vi ser at du har kjørt en annen vei enn hva vi har planlagt for deg. Vil du at vi skal lage en ny rute for deg, der du kjører nå?","Ja","Nei",2);
            dialogBox.setChargingStations(this);
            dialogBox.createDialogBox();
        }
    }

    public void setHasCheckedIfCreateNewRoute(boolean hasCheckedIfCreateNewRoute) {
        this.hasCheckedIfCreateNewRoute = hasCheckedIfCreateNewRoute;
    }

    public void createNewRoute(){
        Location lastKnownLocation = GPSTracker.getLastKnownLocation();
        startRequestDirections(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        hasCheckedIfCreateNewRoute = false;
    }

    public void handleUpdateKMList(double drivenMetersSoFar){
        pagerAdapter.getChargingStationList().updateListKM(drivenMetersSoFar);
    }

    private void handleAllStations(){
        setAllChargingItems();
        startRequestDirections(latitude,longditude);
    }

    private void handleError(){
        //TODO: Handle error
        System.out.println("Error must be handled in ChargingStations");
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

    public void startRequestDirections(double latitude, double longditude){
        String ID = bundle.getString("destinationID");
        String googleURLDirection = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin = latitude + "," + longditude;
        String destination = ID;
        String key = getString(R.string.google_map_api_key);
        String mode = "driving";
        String region = "no";
        String parameters = googleURLDirection + "origin=" + origin + "&destination=place_id:" + destination + "&mode=" + mode + "&region=" + region + "&key=" + key;
        System.out.println(parameters);
        RetrieveJSON directionFromAPI = new RetrieveJSON(activity,TaskRequestDirections.class);
        directionFromAPI.execute(parameters);
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
