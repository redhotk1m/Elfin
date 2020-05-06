package com.elfin.elfin.API;

import android.os.AsyncTask;
import android.util.Log;

import com.elfin.elfin.MainActivity;
import com.elfin.elfin.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Nobil extends AsyncTask<Void, Void, ArrayList<LatLng>>{
    InputStreamReader in;
    BufferedReader reader;
    InputStream is;
    MainActivity mainActivity;

    public Nobil(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        // Create an InputStream object. From API
        is = mainActivity.getResources().openRawResource(R.raw.nobil);
        // Create a BufferedReader object to read values from CSV file.
        in = new InputStreamReader(is);
        reader = new BufferedReader(in);
    }

    private String requestDirection(){
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
    protected ArrayList<LatLng> doInBackground(Void... arrayLists) {
        ArrayList<LatLng> chargingStationCoordinates = new ArrayList<>();
        try {
            JSONArray latlngJSONArray = new JSONObject(
                    requestDirection())
                    .getJSONArray("chargerstations");
            for (int i = 0; i < latlngJSONArray.length(); i++){
                String[] latlng = latlngJSONArray.getJSONObject(i)
                        .getJSONObject("csmd")
                        .getString("Position")
                        .replace("(","")
                        .replace(")","")
                        .trim().split(",");
                chargingStationCoordinates.add(
                        new LatLng(
                                Double.valueOf(latlng[0]),
                                Double.valueOf(latlng[1])
                        ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO: Endre fra CSV til Nobil API, håndter dersom man ikke finner noen stasjoner
        //chargingStationCoordinates.sort(new LatitudeComparator());
        //mainActivity.setAllChargingStations(chargingStationCoordinates);
        mainActivity.setChargingStationsFound(true);
        return chargingStationCoordinates;
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> ladestasjoner) {
        //TODO Iterere gjennom dobbelt, sånn at den ikke viser antall i forhold til den minste listen
        //double startTime = System.currentTimeMillis();
        //System.out.println(startTime);
        /*TimingLogger timings = new TimingLogger("TimingLogger","MethodA");
        Collections.sort(ladestasjoner,new LongditudeComparator());
        timings.addSplit("Work A");
        Collections.sort(ladestasjoner,new LatitudeComparator());
        timings.addSplit("Work B");
        timings.dumpToLog();
        */
        //System.out.println(System.currentTimeMillis() - startTime);
        int i = 0;
        double lat1, lon1;
        String[] a,b;
        double g = 0;
        int arrSize = ladestasjoner.size();
        int sameLon = 0;
        int sameLat = 0;
        /*for (int j = 0; j < ladestasjoner.size(); j++){
            a = ladestasjoner.get(j).split(",");
            lat1 = Double.parseDouble(a[0]);
            lon1 = Double.parseDouble(a[1]);
            for (int k = j+1; k < ladestasjoner.size(); k++){
                b = ladestasjoner.get(k).split(",");
                lat1 = Double.parseDouble(a[0]);
                lon1 = Double.parseDouble(a[1]);
                if (a[0].equals(b[0]))
                    sameLat++;
                if (a[1].equals(b[1]))
                    sameLon++;
            }
        }*/
        System.out.println("same er : " + sameLat + " " + sameLon);
        //LatLng currentPoint = (LatLng)points.get(0);
        //chargingStationsEnroute.add(currentPoint);
        //if (points.size() <= arrayList.size()) {
            /*for (Object point : points) {
                //if (StMethods.distanceBetweenKM(currentPoint.latitude,currentPoint.longitude,((LatLng) point).latitude, ((LatLng) point).longitude) > 0.5){
                    currentPoint = (LatLng) point;
                    chargingStationsEnroute.add(currentPoint);
                    System.out.println("added");
                //}
                //    for (i = 0; i < arrSize;) {//TODO Inner for-loop disabled for testing, not showing all charging stations
                //if (i % 10 == 0) {//TODO check for every 10th point?
                //a = arrayList.get(i++).split(",");
                //lat1 = Double.parseDouble(a[0]);
                //lon1 = Double.parseDouble(a[1]);
                //googleMap.addCircle(new CircleOptions().center(new LatLng(((LatLng) point).latitude,((LatLng) point).longitude)).radius(500));
                //if ((g = StMethods.distanceBetweenFlat(lat1, lon1, ((LatLng) point).latitude, ((LatLng) point).longitude)) < 1.1) {
                //    System.out.println(g);
                //    chargingStationsEnroute.add(new LatLng(lat1, lon1));
                //}
                //      }
                //System.out.println("running: " + i);
                //}
                //googleMap.addMarker(new MarkerOptions().position(new LatLng(((LatLng) point).latitude,((LatLng) point).longitude)).title("point"));
                //chargingStationsEnroute.add((LatLng) point);
           // }
        }

        for (LatLng latLng : chargingStationsEnroute) {
            chargingStationMap.addAllChargingStations(latLng);
            //googleMap.addMarker(new MarkerOptions().position(latLng).title("test123").snippet("test9321"));
        }
        System.out.println("FERDIG MED ALLE PUNKTER PÅ KART!@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + chargingStationsEnroute.size());
        //asyncResponse.processFinishSet(set);*/
    }


}
