package com.example.elfin;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DisplaySuggestions extends AsyncTask<String, Void, String> {

    Context context;
    AsyncResponse asyncResponse = null;

    ArrayList<String> names = new ArrayList<>();

    public DisplaySuggestions(Context context, AsyncResponse asyncResponse){
        this.asyncResponse =asyncResponse;
        this.context = context;

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

    @Override
    protected String doInBackground(String... strings) {
        String responseString = "";
        responseString = createTextFromCsvTrondheim();
        return responseString;
    }


    @Override
    protected void onPostExecute(String s) {
        JsonToString jsonToString = new JsonToString();
        names = jsonToString.convertJsonToArrayListString(s);
        asyncResponse.processFinish(names.get(0));
    }


    // lageen doinbackground lage en arraylist
}


