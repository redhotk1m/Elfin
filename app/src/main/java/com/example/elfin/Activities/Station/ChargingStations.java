package com.example.elfin.Activities.Station;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.elfin.API.NobilAPIHandler;
import com.example.elfin.API.TaskRequestDirections;
import com.example.elfin.R;
import com.example.elfin.adapter.PageAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ChargingStations extends AppCompatActivity {

    public static Context chargingStationContext;
    Bundle bundle;

    ArrayList<LatLng> allChargingStations;

    PageAdapter pagerAdapter;
    public boolean
            mapCreated = false,
            routeCreated = false,
            validStationsFound = false;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem listTab = findViewById(R.id.listTab);
        TabItem mapTab = findViewById(R.id.mapTab);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        bundle = getIntent().getBundleExtra("bundle");
        allChargingStations = bundle.getParcelableArrayList("chargingStations");
        setContext(this);

        final PageAdapter pagerAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),this);
        setPagerAdapter(pagerAdapter);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0){
                    pagerAdapter.notifyDataSetChanged();
                    System.out.println("tab Pos = 0");
                    pagerAdapter.getItem(0).onResume();//Kjører onresume i Listen,
                    // dersom listen er valgt fra Map.
                    // Lager ikke nytt objekt siden det allerede eksisterer
                    // Bør skje dersom noe er blitt endret i kriterier, sharedPrefs?
                }
                else if (tab.getPosition() == 1){
                    pagerAdapter.notifyDataSetChanged();
                    getSupportFragmentManager().findFragmentById(0);
                    System.out.println("tab Pos = 1");
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

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverAllStations,new IntentFilter("jsonString"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverAllValidStations,new IntentFilter("allValidChargingStations"));

        if (allChargingStations != null && allChargingStations.size() > 0)
            startRequestDirections();
    }

    private BroadcastReceiver mMessageReceiverAllValidStations = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("BLIR MOTTAT!");
            ArrayList<LatLng> allValidStations = intent.getParcelableArrayListExtra("allValidChargingStations");
            if (allValidStations == null) //Bør være allValidStations.get(0).getError (ikke helt sånn)
                System.out.println("error"); //Bør aldri skje
                //TODO: Error message to user
            else {
                pagerAdapter.getChargingStationList().setAllValidStations(allValidStations);
                pagerAdapter.getChargingStationMap().setAllValidStations(allValidStations);
                //TODO: Gi beskjed til map og list om at alle ladestasjoner er funnet
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiverAllValidStations);
            }
        }
    };


    private BroadcastReceiver mMessageReceiverAllStations = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("BLIR MOTTAT!");
            String message = intent.getStringExtra("jsonString");
            if ("error".equals(message))
                System.out.println("error");
                //TODO: Error message to user
            else {
                NobilAPIHandler nobilAPIHandler = new NobilAPIHandler(message);
                setAllChargingStations(nobilAPIHandler.getChargingStationCoordinates());
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiverAllStations);
            }
        }
    };

    public void setAllChargingStations(ArrayList<LatLng> allChargingStations) {
        this.allChargingStations = allChargingStations;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","Er i onResume Charging Stations");
    }

    public void startRequestDirections(){
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(getContext(),this);
        taskRequestDirections.execute(bundle.getString("destinationID"));
    }

    public ArrayList<LatLng> getAllChargingStations() {
        return allChargingStations;
    }

    public PageAdapter getPagerAdapter(){
        return pagerAdapter;
    }

    private void setPagerAdapter(PageAdapter pagerAdapter){
        this.pagerAdapter = pagerAdapter;
    }

    ArrayList<LatLng> validStations;

    public Bundle getBundle(){
        return bundle;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
