package com.example.elfin.Activities.Station;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem listTab = findViewById(R.id.listTab);
        TabItem mapTab = findViewById(R.id.mapTab);
        final ViewPager viewPager = findViewById(R.id.viewPager);
        chargingStationContext = this;
        bundle = getIntent().getBundleExtra("bundle");
        allChargingStations = bundle.getParcelableArrayList("chargingStations");


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
        startRequestDirections();
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","Er i onResume Charging Stations");
    }

    public void startRequestDirections(){
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(chargingStationContext,this);
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
    public void setValidStations(ArrayList<LatLng> validStations) {
        this.validStations = validStations;
    }

    public Bundle getBundle(){
        return bundle;
    }
}
