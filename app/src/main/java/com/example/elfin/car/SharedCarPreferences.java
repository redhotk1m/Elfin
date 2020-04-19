package com.example.elfin.car;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedCarPreferences {

    private ArrayList<Elbil> mCarList;

    public ArrayList<Elbil> getSavedCars(SharedPreferences sharedPreferences) {
       // SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("car list", null);
        Type type = new TypeToken<ArrayList<Elbil>>() {
        }.getType();
        mCarList = gson.fromJson(json, type);

        if (mCarList == null) mCarList = new ArrayList<>();
       // mCarList.add(new Elbil("Legg til bil", null, null, null, null, null));
        mCarList.add(new Elbil("Legg til bil"));
        return mCarList;
    }


}
