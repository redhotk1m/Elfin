package com.elfin.elfin.API;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elfin.elfin.R;
import com.elfin.elfin.Parsers.TaskParser;
import com.elfin.elfin.Utils.App;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TaskRequestDirections extends AsyncTask<String, Void, String> {

    private LocalBroadcastManager localBroadcastManager;
    private App applicationContext;
    public TaskRequestDirections(Activity activity){
        this.localBroadcastManager = LocalBroadcastManager.getInstance(activity);
        this.applicationContext = (App)activity.getApplication();
    }


    @Override
    protected String doInBackground(String... strings) {
        return strings[0];
    }

    @Override
    protected void onPostExecute(String s) {
        TaskParser taskParser = new TaskParser(localBroadcastManager,applicationContext);
        taskParser.execute(s);
    }
}


