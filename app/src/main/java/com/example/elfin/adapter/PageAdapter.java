package com.example.elfin.adapter;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargingStationList;
import com.example.elfin.Activities.Station.StationMap.ChargingStationMap;

public class PageAdapter extends FragmentPagerAdapter {

    private int numberOfTabs;
    private ChargingStationList chargingStationList;
    private ChargingStationMap chargingStationMap;
    private ChargingStations chargingStations;
    public PageAdapter(FragmentManager fm, int numberOfTabs, ChargingStations chargingStations) {
        super(fm, numberOfTabs);
        this.numberOfTabs = numberOfTabs;
        this.chargingStations = chargingStations;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("Pos er " + position);
        switch (position){
            case 0:
                return this.chargingStationList = new ChargingStationList(chargingStations);
            case 1:
                return this.chargingStationMap = new ChargingStationMap(chargingStations);
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }


    public ChargingStationList getChargingStationList() {
        return chargingStationList;
    }

    public ChargingStationMap getChargingStationMap() {
        return chargingStationMap;
    }

}
