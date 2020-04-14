package com.example.elfin.API;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.elfin.Utils.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.AsynchronousCloseException;

public class RetrieveJSON extends AsyncTask<String, Void, String>{
    private HttpURLConnection urlConnection;
    private Class className;
    private LocalBroadcastManager localBroadcastManager;
    private App applicationContext;
    public RetrieveJSON(Activity context, Class className){
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.applicationContext = (App)context.getApplication();
        this.className = className;
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
        //TODO: Bør ikke kjøre NobilAPIHandler i main Thread, tar 4s
        if (className == NobilAPIHandler.class) {
            NobilAPIHandler a = new NobilAPIHandler(localBroadcastManager,applicationContext);
            a.execute(jsonString);
            //TODO: Bør kjøres en løkke som sjekker om noen har mottat broadcastet, før asynctask avsluttes
            //localBroadcastManager.sendBroadcast(intent);
        }
    }
}
