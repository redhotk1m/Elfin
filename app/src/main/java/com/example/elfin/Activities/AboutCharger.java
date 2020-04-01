package com.example.elfin.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elfin.API.NobilInfo;
import com.example.elfin.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AboutCharger extends AppCompatActivity {
    TextView textViewTitel;
    TextView textViewAdress;
    TextView textViewCompany;
    TextView textViewAvaiable;
    TextView textViewAvaiable2;
    TextView textViewAvaiable3;
    TextView textViewDescriptionText;
    TextView textViewInfoText;
    TextView textViewChargerType;
    TextView textViewChargerType2;
    TextView textViewChargerType3;

    TextView textViewPayMethod;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_charger);
        Button button = findViewById(R.id.button2);
        textViewTitel= findViewById(R.id.textViewTitel);
        textViewAdress = findViewById(R.id.textViewAdress);
        textViewCompany = findViewById(R.id.textViewCompany);
        textViewAvaiable = findViewById(R.id.textViewChargersAvaiable);
        textViewAvaiable2 = findViewById(R.id.textViewChargersAvaiable2);
        textViewAvaiable3 = findViewById(R.id.textViewChargersAvaiable3);
        textViewDescriptionText = findViewById(R.id.textViewDescriptionText);
        textViewInfoText = findViewById(R.id.textViewInfoText);
        textViewChargerType = findViewById(R.id.textViewChargerType);
        textViewChargerType2 = findViewById(R.id.textViewChargerType2);
        textViewChargerType3 = findViewById(R.id.textViewChargerType3);
        textViewPayMethod = findViewById(R.id.textViewPayMethod);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=59.909266,10.742538&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        getInfo();

    }



    public void getInfo(){
        NobilInfo nobilInfo = new NobilInfo(this);
        //tjeldstø --> 2 chademo, 2 ccs, 2 trege
        //LatLng latLng = new LatLng(60.58991, 4.84047);
        //en plass cirkle k ---> 63.32481,10.30589 ... 2 hurtig -- 2 chademo
        // Esso loddefjord 60.58991, 4.84047 --> 1 lyn, 2 hurti -- 2 chademo
        LatLng latLng = new LatLng(60.36144, 5.23441);
        nobilInfo.execute(latLng);
    }


    public void setInfo(ArrayList<String> info){

        /**
         * Sette alle verdiene som ikke er i den neste forloopen først, frodi nesten fosvinner når den breakes.
         */
        textViewTitel.setText(info.get(0));
        textViewAdress.setText(info.get(1));
        textViewCompany.setText(info.get(2));
        textViewPayMethod.setText(info.get(3));
        textViewDescriptionText.setText(info.get(4));
        textViewInfoText.setText(info.get(5));



        textViewChargerType.setText(info.get(6));
        textViewAvaiable.setText(info.get(7));
        textViewChargerType2.setText(info.get(8));
        textViewAvaiable2.setText(info.get(9));
        textViewChargerType3.setText(info.get(10));
        textViewAvaiable3.setText(info.get(11));
//        textViewPayMethod.setText(info.get(9));
  /*     textViewDescriptionText.setText("yyyyyyyyyyyyyyyyyrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                        "ryeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +"yyyyyyyyyyyyyyyyyrrrrrrrrrrrrrrrrrrrrrrrrrrrrr" +
                        "ryeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
                );
        //textViewDescriptionText.setText(info.get(10));



        /*

        textViewCompany.setText(info.get(4));
        textViewAvaiable.setText(info.get(5) + " ledig");
        textViewAvaiable.setText(info.get(6) + " ledig");
        textViewPayInfo.setText(info.get(7));
        textViewInfoText.setText(info.get(8));
        //åpningstider er 8
        textViewChargerType.setText(info.get(10));
        textViewChargerType2.setText(info.get(11));
        textViewPayMethod.setText(info.get(12));

         */
    }

}
