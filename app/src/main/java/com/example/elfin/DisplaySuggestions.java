package com.example.elfin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.elfin.API.RetrieveJSON;
import com.example.elfin.Parsers.JsonToString;
import com.example.elfin.Utils.AsyncResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DisplaySuggestions extends AsyncTask<String, Void, String> {

    Context context;
    AsyncResponse asyncResponse = null;
    String adress;

    //ArrayList<> names = new ArrayList<>();
    ArrayList<ArrayList<String>> lists= new ArrayList<>();
    public DisplaySuggestions(Context context, String adress, AsyncResponse asyncResponse){
        this.asyncResponse = asyncResponse;
        this.context = context;
        this.adress=adress;

    }
    private String createTextFromCsvTrondheim() {
        String responseString = "";
        InputStream inputStream = context.getResources().openRawResource(R.raw.trondheim);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String responseText = "";
        StringBuffer stringBuffer = new StringBuffer();

        try {
            while ((responseText = bufferedReader.readLine()) != null) {
                stringBuffer.append(responseText);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            Log.e("error", "Error" + responseText, e);
            e.printStackTrace();
        }
        return responseString;
    }


    private String createTextFromWebJson() {
        //String adress = "Trondheim";
        String data ="";
        String responseString = "";
        String jsonUrl= "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
        jsonUrl+= adress;
        jsonUrl+="&types=geocode&components=country:no&language=no&key=";
        jsonUrl+= context.getString(R.string.google_map_api_key);
        try{
            URL url = new URL(jsonUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                responseString = responseString + line;
            }
        }

        catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected String doInBackground(String... strings) {
        String responseString = "";
        //responseString = createTextFromCsvTrondheim();
        responseString= createTextFromWebJson();
        return responseString;
    }


    @Override
    protected void onPostExecute(String s) {
        JsonToString jsonToString = new JsonToString();
        lists = jsonToString.convertJsonToArrayListString(s);
        asyncResponse.processFinish(lists);
    }


}
