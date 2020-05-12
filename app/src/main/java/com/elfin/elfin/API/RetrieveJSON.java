package com.elfin.elfin.API;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elfin.elfin.Parsers.TaskParser;
import com.elfin.elfin.Utils.App;
import com.elfin.elfin.Utils.DialogBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrieveJSON extends AsyncTask<String, Void, String>{
    private HttpURLConnection urlConnection;
    private Class className;
    private LocalBroadcastManager localBroadcastManager;
    private App applicationContext;

    /**
     * This class retrieves all the JSON information, from the provided URL.
     * The handler you want to use, to handle the output of the API, should be used as input to this class (Constructor)
     * We use this to differentiate between which handlers gets created.
     * E.g TaskRequestDirections.class as input.
     * @param context
     * @param className
     */

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
        } catch (IOException e) {
            //Do something, error message popup
            DialogBox dialogBox = new DialogBox(applicationContext, "Server feil", "Klarte ikke å hente data",
                    "OK", "", 2);
            dialogBox.simpleDialogBox();
            result = "error";
            return result;
        } finally {
            urlConnection.disconnect();
        }
    }



    @Override
    protected void onPostExecute(String jsonString) {
        if (className == NobilAPIHandler.class) {
            NobilAPIHandler a = new NobilAPIHandler(localBroadcastManager,applicationContext);
            a.execute(jsonString);
            //TODO: Bør kjøres en løkke som sjekker om noen har mottat broadcastet, før asynctask avsluttes
            }
        if (className == TaskRequestDirections.class){
            TaskParser taskParser = new TaskParser(localBroadcastManager,applicationContext);
            taskParser.execute(jsonString);
        }
    }
}
