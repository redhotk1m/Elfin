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
        // SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("car list", null);
        Type type = new TypeToken<ArrayList<Elbil>>() {
        }.getType();
        mCarList = gson.fromJson(json, type);

        if (mCarList == null) mCarList = new ArrayList<>();

        return mCarList;
    }


    public ArrayList<Elbil> getSavedCars(SharedPreferences sharedPreferences) {
        // SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("car list", null);
        Type type = new TypeToken<ArrayList<Elbil>>() {
        }.getType();
        mCarList = gson.fromJson(json, type);

        if (mCarList == null) mCarList = new ArrayList<>();
        // mCarList.add(new Elbil("Legg til bil", null, null, null, null, null));
        // mCarList.add(new Elbil("Legg til bil"));

        for (Elbil elbil : mCarList) {
            elbil.setIconImage(R.drawable.ic_car_black_24dp);
            //todo: fix elbil.toString() or make elbil.display()
            String display = elbil.getBrand() + " " + elbil.getModel() + " (" + elbil.getModelYear() + ")";
            elbil.setSpinnerDisplay(display);
        }



        String addCar = "Legg til bil"; //getString(R.string.add_car)
        mCarList.add(new Elbil(R.drawable.ic_add_box_black_24dp, addCar));

        return mCarList;
    }

    public List<Elbil> updateSavedCars(SharedPreferences sharedPreferences, ArrayList<Elbil> elbils) {
        // SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // ArrayList<Elbil> elbils = getSavedCars(sharedPreferences);
        // ArrayList<Elbil> elbils = new ArrayList<>();
        // elbils = getSavedCars(sharedPreferences);
        // elbils.add(elbil);
      //  System.out.println("mCarList: " + elbils);
        // elbils.remove(elbil);
        // System.out.println("REMOVED LIST: " + elbils);
        mCarList = new ArrayList<>();
        for (Elbil elbil : elbils) {
            if (elbil != null) {
                mCarList.add(elbil);
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.clear(); //clear shared preferences
        Gson gson = new Gson();
        //to contain ArrayList as Json form
        // mCarList.addAll(mCarListAll);
        String json = gson.toJson(elbils);
        editor.putString("car list", json);
        editor.apply();

        return elbils;
    }

    public void clearSharedPreferences(SharedPreferences sharedPreferences) {
        // SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); //clear shared preferences
        editor.apply();
    }

}
