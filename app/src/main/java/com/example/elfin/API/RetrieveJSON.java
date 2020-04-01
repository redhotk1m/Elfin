package com.example.elfin.API;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.AsynchronousCloseException;

public class RetrieveJSON extends AsyncTask<String, Void, String>{
    HttpURLConnection urlConnection;
    Context context;
    public RetrieveJSON(Context context){
        this.context = context;
    }
    @Override
    protected String doInBackground(String... strings){
        String result = "";
        try {
            URL url = new URL(strings[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                result = stringBuilder.toString();
                return result;
        }catch (IOException e){
            //Do something, error message popup
            //TODO: Popup som gir bruker beskjed
            result = "error";
            return result;
        }finally {
            urlConnection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        Intent intent = new Intent("jsonString");
        intent.putExtra("jsonString",jsonString);
        //TODO: Bør kjøres en løkke som sjekker om noen har mottat broadcastet, før asynctask avsluttes
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
