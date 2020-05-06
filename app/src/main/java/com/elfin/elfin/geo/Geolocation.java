package com.elfin.elfin.geo;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Geolocation {

    public void getAdress(final String locationAdress, final Context context, final Handler handler){

        Thread thread = new Thread(){
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List addressList = geocoder.getFromLocationName(locationAdress,1);
                    System.out.println("---------------------------------------------");
                    System.out.println(addressList.toString());
                    if(addressList != null && addressList.size() > 0){
                        Address address = (Address) addressList.get(0);
                        System.out.println(address);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(address.getLatitude()).append("\n");
                        stringBuilder.append(address.getLongitude()).append("\n");
                        result = stringBuilder.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if(result != null){
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Adress :   " + locationAdress +" \n\n\n Latitude  AND Longtitude\n" + result;
                        bundle.putString("adress",result);
                        message.setData(bundle);

                    }

                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }



}
