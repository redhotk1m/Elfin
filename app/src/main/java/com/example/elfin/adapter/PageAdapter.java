package com.example.elfin.adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.elfin.ChargingStationList;
import com.example.elfin.ChargingStationMap;

public class PageAdapter extends FragmentPagerAdapter {

    private int numberOfTabs;
    FragmentManager fragmentManager;
    private Bundle bundle;
    public PageAdapter(FragmentManager fm, int numberOfTabs, Bundle bundle) {
        super(fm, numberOfTabs);
        this.numberOfTabs = numberOfTabs;
        this.fragmentManager = fm;
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("Pos er " + position);
        switch (position){
            case 0:
                return new ChargingStationList();
            case 1:
                return new ChargingStationMap(bundle);
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
        /*if (position == 0){
            container.getChildAt(0).setTag(0,"ChargingStationList");
            System.out.println("LIST");
        } else if (position == 1){
            container.getChildAt(1).setTag(1,"ChargingStationMap");
            System.out.println("MAP");
        }*/
        return super.instantiateItem(container, position);
    }

}
