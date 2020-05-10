package com.elfin.elfin.car;

import android.content.SharedPreferences;

import com.elfin.elfin.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedCarPreferences {

    private ArrayList<Elbil> mCarList;


    public ArrayList<Elbil> loadCars(SharedPreferences sharedPreferences) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("car list", null);
        Type type = new TypeToken<ArrayList<Elbil>>() {
        }.getType();
        mCarList = gson.fromJson(json, type);

        if (mCarList == null) mCarList = new ArrayList<>();

        return mCarList;
    }


    public ArrayList<Elbil> getSavedCars(SharedPreferences sharedPreferences) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("car list", null);
        Type type = new TypeToken<ArrayList<Elbil>>() {

        }.getType();
        mCarList = gson.fromJson(json, type);

        if (mCarList == null) mCarList = new ArrayList<>();

        for (Elbil elbil : mCarList) {
            elbil.setIconImage(R.drawable.ic_car_black_24dp);
            //todo: fix elbil.toString() or make elbil.display()
            String display = elbil.getBrand() + " " + elbil.getModel() + " (" + elbil.getModelYear() + ")";
            elbil.setSpinnerDisplay(display);
        }

        String addCar = "Legg til bil";
        mCarList.add(new Elbil(R.drawable.ic_add_box_black_24dp, addCar));

        return mCarList;
    }

    public List<Elbil> updateSavedCars(SharedPreferences sharedPreferences, ArrayList<Elbil> elbils) {
        mCarList = new ArrayList<>();
        for (Elbil elbil : elbils) {
            if (elbil != null) {
                mCarList.add(elbil);
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        //to contain ArrayList as Json form
        String json = gson.toJson(elbils);
        editor.putString("car list", json);
        editor.apply();

        return elbils;
    }

    public void clearSharedPreferences(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); //clear shared preferences
        editor.apply();
    }

}
