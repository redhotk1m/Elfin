package com.example.elfin.geo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GeoHandler extends Handler {

    String adress;
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case 1:
                Bundle bundle = msg.getData();
                adress = bundle.getString("adress");
                break;
            default:
                adress =null;
        }

        System.out.println(adress);

    }

    public String returnMessage(){
        return adress;
    }



}
