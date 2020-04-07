package com.example.elfin.API;

import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.MainActivity;
import com.example.elfin.comparators.LatitudeComparator;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
public class NobilAPIHandler {
    private ArrayList<ChargerItem> chargingStationCoordinates = new ArrayList<>();
    public NobilAPIHandler(String jsonString){
        try {
            JSONArray latlngJSONArray = new JSONObject(jsonString)
                    .getJSONArray("chargerstations");
            for (int i = 0; i < latlngJSONArray.length(); i++){
                JSONObject object = latlngJSONArray.getJSONObject(i).getJSONObject("csmd");
                //String[] latlng = object
                //        .getString("Position")
                //        .replace("(","")
                //        .replace(")","")
                //        .split(",");
                //street = object.getString("Street");
                // houseNumber = object.getString("House_number");
                //zipCode = object.getString("Zipcode");
                //city = object.getString("City");
                // municipality = object.getString("Municipality");
                //county = object.getString("County");
                //descriptionOfLocation = object.getString("Description_of_location");
                //ownedBy = object.getString("Owned_by");
                // numberChargingPoints = object.getString("Number_charging_points");
                // image = object.getString("Image");
                // availableChargingPoints = object.getString("Available_charging_points");
                // userComment = object.getString("User_comment");
                // contactInfo = object.getString("Contact_info");
                // created = object.getString("Created");
                // updated = object.getString("Updated");
                // stationStatus = object.getString("Station_status");
                chargingStationCoordinates.add(new ChargerItem(
                        object.getString("Street"),
                        object.getString("House_number"),
                        object.getString("Zipcode"),
                        object.getString("City"),
                        object.getString("Municipality"),
                        object.getString("County"),
                        object.getString("Description_of_location"),
                        object.getString("Owned_by"),
                        object.getString("Number_charging_points"),
                        object.getString("Image"),
                        object.getString("Available_charging_points"),
                        object.getString("User_comment"),
                        object.getString("Contact_info"),
                        object.getString("Created"),
                        object.getString("Updated"),
                        object.getString("Station_status"),
                        object.getString("Position")
                                .replace("(","")
                                .replace(")","")
                                .split(",")
                        )
                );
                object = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO: Endre fra CSV til Nobil API, håndter dersom man ikke finner noen stasjoner
        chargingStationCoordinates.sort(new LatitudeComparator());
    }

    public ArrayList<ChargerItem> getChargingStationCoordinates() {
        return chargingStationCoordinates;
    }
}
