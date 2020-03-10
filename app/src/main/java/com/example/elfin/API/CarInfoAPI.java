package com.example.elfin.API;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CarInfoAPI extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {

        String biltemaURL_End = "/2/nb",
                biltemaURL_Start = "https://reko.biltema.com/v1/Reko/extendedcarinfo/",
                regNR = strings[0],
                 jsonFromAPI = "", //getJSONFromURL(biltemaURL_Start,regNR, biltemaURL_End)
                chassieNR = null;
        try {
            JSONObject biltemaJSON = new JSONObject(jsonFromAPI);
            chassieNR = biltemaJSON.getString("chassieNumber");
        } catch (JSONException e){
            return "Error";
        }



        return null;
    }

    JSONObject getJSONFromURL(String identifier){
        int minimumChassieNumberLength = 17;
        //Checks if the identifier is a chassienumber or registrationnumber.
        //It's chassieNR if length >= 17, else it's a regNR
        //Creates a corresponding URL to the correct service
        String serviceURL = identifier.length() >= minimumChassieNumberLength ?
                "https://reko.biltema.com/v1/Reko/extendedcarinfo/" + identifier + "/2/nb"
                : "https://hotell.difi.no/api/json/vegvesen/utek?query=" + identifier;
        try {
            URL url = new URL(serviceURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
