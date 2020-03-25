package com.example.elfin.Parsers;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class JsonToString {
    public ArrayList<ArrayList<String>> convertJsonToArrayListString(String s) {

        //arraylist inni arraylist
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> placesID = new ArrayList<>();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        try {
            JSONArray jsonArrayPredictions = null;
            JSONObject jsonObject = new JSONObject(s);
            jsonArrayPredictions = jsonObject.getJSONArray("predictions");

            for (int i = 0; i <jsonArrayPredictions.length() ; i++) {
                JSONObject jsonObject1 = jsonArrayPredictions.getJSONObject(i);
                String name = jsonObject1.getString("description");
                //System.out.println(name);
                names.add(name);
            }
            for (int i = 0; i <jsonArrayPredictions.length() ; i++) {
                JSONObject jsonObject1 = jsonArrayPredictions.getJSONObject(i);
                String placeID = jsonObject1.getString("place_id");
                //System.out.println(placeID);
                placesID.add(placeID);
            }

            arrayLists.add(names);
            arrayLists.add(placesID);

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return arrayLists;
    }


}
