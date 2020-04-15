package com.example.elfin.API;

import android.os.AsyncTask;
import android.util.Log;

import com.example.elfin.Activities.AboutCharger;
import com.example.elfin.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NobilInfo extends AsyncTask<LatLng, Void, ArrayList<String>>{
    InputStreamReader in;
    BufferedReader reader;
    InputStream is;
    AboutCharger aboutCharger;


    public NobilInfo(AboutCharger aboutCharger) {
        this.aboutCharger=aboutCharger;
        is=aboutCharger.getResources().openRawResource(R.raw.nobil);
        in = new InputStreamReader(is);
        reader = new BufferedReader(in);
    }




    private String getStringNobil(){
        String responseString="";
        // Create an InputStream object. From API
        // Create a BufferedReader object to read values from CSV file.
        String line = "";
        // Create a list of LatLng objects.
        StringBuffer stringBuffer = new StringBuffer();
        try {
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            reader.close();
            in.close();
        } catch (IOException e1) {
            Log.e("error", "Error" + line, e1);
            e1.printStackTrace();
        }
        return responseString;
    }




    @Override
    protected ArrayList<String> doInBackground(LatLng... latlng) {
        double latitude = latlng[0].latitude;
        double longitude = latlng[0].longitude;
        String stringLatLng = "("+latitude + ","+ longitude+")";
        ArrayList<String> info = new ArrayList<>();

        /**
         * Takes a coordinate (latLng) from execute, converts it into a string(stringLatLng)
         * where the string goes through a for loop to check for matching coordinates in the nobil api.
         * When it finds the coordinate it puts the information that is needed for the user into a
         * String arraylist --> info. When all the info has been added it returns the arraylist from onpost and
         * sends it to the aboutcharger class -->aboutcharger.setInfo()
         */

        try {
            JSONArray jsonArrayInfo = null;
            JSONObject jsonObject = new JSONObject(getStringNobil());
            jsonArrayInfo = jsonObject.getJSONArray("chargerstations");


            for (int i = 0; i < jsonArrayInfo.length(); i++){
                if(jsonArrayInfo.getJSONObject(i).getJSONObject("csmd").getString("Position").equals(stringLatLng)){
                    info.add(jsonArrayInfo.getJSONObject(i).getJSONObject("csmd").getString("name"));
                    String place= "";
                    place += jsonArrayInfo.getJSONObject(i).getJSONObject("csmd").getString("Street");
                    place += " ";
                    place += jsonArrayInfo.getJSONObject(i).getJSONObject("csmd").getString("House_number");
                    // innholder adressen og tallet (Loodfjordveien2)
                    info.add(place);
                    info.add(jsonArrayInfo.getJSONObject(i).getJSONObject("csmd").getString("Owned_by"));
                    info.add(jsonArrayInfo.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("2").
                            getJSONObject("19").getString("trans"));
                    info.add(jsonArrayInfo.getJSONObject(i).getJSONObject("csmd").getString("Description_of_location"));


                    info.add(jsonArrayInfo.getJSONObject(i).getJSONObject("attr").getJSONObject("st").
                            getJSONObject("24").getString("attrname")+
                            "\n\n" +jsonArrayInfo.getJSONObject(i).getJSONObject("csmd").getString("User_comment") + " \n\n" +
                            jsonArrayInfo.getJSONObject(i).getJSONObject("csmd").getString("Contact_info") + "\n");

                    String lightning = "CCS/Combo";
                    String chademo = "CHAdeMO";
                    String lightCharger = "150 kW DC";
                    String fastCharger = "50 kW - 500VDC max 100A";

                    Boolean boolLighting = false;
                    Boolean boolFast = false;
                    Boolean boolCha = false;


                    int counterlight = 0;
                    int counterCHD = 0;
                    int counterFast = 0;
                    //denne for løkken må være like stor som antall ladere

                    for (int j = 1; j < 10 ; j++) {


                        try {
                            if (jsonArrayInfo.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                    getJSONObject("4").getString("trans").equals(lightning) &&
                                    jsonArrayInfo.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                            getJSONObject("5").getString("trans").equals(lightCharger)) {
                                counterlight++;
                                boolLighting = true;
                            }

                            if (jsonArrayInfo.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                    getJSONObject("4").getString("trans").equals(lightning) &&
                                    jsonArrayInfo.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                            getJSONObject("5").getString("trans").equals(fastCharger)) {
                                counterFast++;
                                boolFast = true;
                            }

                            if (jsonArrayInfo.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                    getJSONObject("4").getString("trans").equals(chademo) &&
                                    jsonArrayInfo.getJSONObject(i).getJSONObject("attr").getJSONObject("conn").getJSONObject("" + j).
                                            getJSONObject("5").getString("trans").equals(fastCharger)) {
                                counterCHD++;
                                boolCha = true;
                            }
                        }catch (JSONException e){
                            break;

                        }
                    }
                    if(boolLighting){
                        info.add("" + counterlight + " " + lightning + "\n" + lightCharger);
                        info.add(""+counterlight + " ledig");
                    }else {
                        info.add("");
                        info.add("");
                    }
                    if(boolFast){
                        info.add("" + counterFast + " " + lightning + "\n" + fastCharger);
                        info.add(""+ counterFast + " ledig");
                    }
                    else {
                        info.add("");
                        info.add("");
                    }
                    if(boolCha){
                        info.add("" + counterCHD + " " + chademo + "\n" + fastCharger);
                        info.add("" + counterCHD+ " ledig");
                    }
                    else {
                        info.add("");
                        info.add("");
                    }
                    return  info;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        info.add("");
        return info;
    }

    @Override
    protected void onPostExecute(ArrayList<String> ladestasjoninfo) {
        //aboutCharger.infoTextView.setText(ladestasjoninfo.get(0));
        aboutCharger.setInfo(ladestasjoninfo);

    }


}
