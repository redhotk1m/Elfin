package com.elfin.elfin.Parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class for converting Json to String and putting them intp a Arraylist of Arraylist. One for
 * the adress and one for placesId(Coordinates)
 */

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
                names.add(name);
            }
            for (int i = 0; i <jsonArrayPredictions.length() ; i++) {
                JSONObject jsonObject1 = jsonArrayPredictions.getJSONObject(i);
                String placeID = jsonObject1.getString("place_id");
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
