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
        try {
            JSONObject biltemaJSON = getJSONFromURL(strings[0]);
            String chassieNR = biltemaJSON.getString("chassieNumber");
            //vegvesen API returns JSONObject within JSONArray within JSONObject.
            JSONObject vegvesenJSON = getJSONFromURL(chassieNR)
                    .getJSONArray("entries")
                    .getJSONObject(0);
            return vegvesenJSON.getString("reg_aar");
        } catch (JSONException | NullPointerException | IOException e){
            e.printStackTrace();
            //Something went wrong, cannot find the correct car
            return "error";
        }
    }

    private JSONObject getJSONFromURL(String identifier) throws JSONException, IOException {
        int minimumChassieNumberLength = 17;
        /*
        Checks if the identifier is a chassienumber or registrationnumber.
        if length >= 17, it's a chassienumber, else it's a regNR
        Creates a corresponding URL to the correct service
         */
        String serviceURL = identifier.length() >= minimumChassieNumberLength ?
                "https://hotell.difi.no/api/json/vegvesen/utek?query=" + identifier
                : "https://reko.biltema.com/v1/Reko/extendedcarinfo/" + identifier + "/2/nb";
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
        } finally {
            urlConnection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String reponse) {
        if (isError(reponse))
            return; //Gi beskjed til bruker om at feil har oppstått
        System.out.println(reponse + " er responsen vi fikk");
    }

    private boolean isError(String s){
        return s.toLowerCase().equals("error");
    }
}
