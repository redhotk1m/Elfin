package com.example.elfin.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elfin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.grpc.internal.SharedResourceHolder;

public class AboutCharger extends AppCompatActivity implements OnMapReadyCallback {
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
    TextView payMethodTitel;
    View linePayMethod;
    View lineDescription;
    View lineInfo;

    TextView textViewDescription;
    TextView textViewHowToPay;
    TextView textViewHowToPayText;
    View lineHowToPay;


    TextView textViewPayMethod;

    TextView textViewHelp;
    TextView textViewPayTittel;

    LatLng latLng;
    String latLngFromList;
    String ownedby;

    ArrayList<String> infoFromList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_charger);

        latLngFromList = getIntent().getStringExtra("latlng");
        ownedby = getIntent().getStringExtra("owned");
        infoFromList = getIntent().getStringArrayListExtra("infoFromList");

        //ChargerItem chargerItem = (ChargerItem) getIntent().getParcelableExtra("charger");

        String[] latlong =  latLngFromList.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        latLng = new LatLng(latitude, longitude);


        Button button = findViewById(R.id.button2);
        textViewTitel= findViewById(R.id.textViewTitel);
        textViewAdress = findViewById(R.id.textViewAdress);
//        textViewCompany = findViewById(R.id.textViewCompany);
        textViewAvaiable = findViewById(R.id.textViewChargersAvaiable);
        textViewAvaiable2 = findViewById(R.id.textViewChargersAvaiable2);
        textViewAvaiable3 = findViewById(R.id.textViewChargersAvaiable3);
        textViewDescriptionText = findViewById(R.id.textViewDescriptionText);
        textViewInfoText = findViewById(R.id.textViewInfoText);
        textViewChargerType = findViewById(R.id.textViewChargerType);
        textViewChargerType2 = findViewById(R.id.textViewChargerType2);
        textViewChargerType3 = findViewById(R.id.textViewChargerType3);
        textViewPayMethod = findViewById(R.id.textViewPayMethod);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewHelp = findViewById(R.id.textViewHelp);

        payMethodTitel = findViewById(R.id.textViewPayTittel);

        lineDescription = findViewById(R.id.line3_about);
        linePayMethod = findViewById(R.id.line2_about);
        lineInfo = findViewById(R.id.line3);

        textViewHowToPay = findViewById(R.id.textViewHowToPay);
        textViewHowToPayText = findViewById(R.id.textViewHowToPayText);
        lineHowToPay = findViewById(R.id.lineHowToPay);




        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setInfo();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringLatlng = latLng.latitude +"," +latLng.longitude;
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLngFromList + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Lader"));
    }


/*
    public void getInfo(){

        NobilInfo nobilInfo = new NobilInfo(this);
        //tjeldstø --> 2 chademo, 2 ccs, 2 trege
        //LatLng latLng = new LatLng(60.58991, 4.84047);
        //en plass cirkle k ---> 63.32481,10.30589 ... 2 hurtig -- 2 chademo
        // Esso loddefjord 60.58991, 4.84047 --> 1 lyn, 2 hurti -- 2 chademo
        LatLng latLng2 = new LatLng(60.36144, 5.23441);
        nobilInfo.execute(latLng2);

    }

 */


    public void setInfo(){

        /**
         * Sette alle verdiene som ikke er i den neste forloopen først, frodi resten fosvinner når den breakes.
         *
         */


        String lightCharger = "150 kW DC";
        String fastCharger = "50 kW - 500VDC max 100A";
        String ccs = "CCS/COMBO" + "\n" + fastCharger;

        String paymentMethodFortum = "Hurtig lading 4 kr per minutt \n\nLyn lading 4 kr per minutt + 2,50 kr per kWh";
        String paymentMethodGreenContact = "Registrert kunde:   NOK 1,25 per minutt + NOK 2,90 per kWh \n \n"
                + "Drop inn kunde:     NOK 2,50 per minutt + NOK 2,90 per kWh";
        String paymentMethodCirkleK = "Hurtiglading med ladebrikke eller mobilapp: 4,49 kr per kWh\n\n" +
                "Lynlading med ladebrikke eller mobilapp: 4,99 kr per kWh";


        String paymentOtherStations = "Se informasjon på ladepunktet";


        String howToChargeFortum = "1) Koble ladekabel til bil (og ladestasjon ved mellomrask lading)\n\n" +
                "2) Send SMS med teksten «Start ladenr» til 2430. (Eks. Start 123a) " +
                " Du finner riktig ladenummer ved ladeuttakene/ladekablene.\n\n" +
                "3) Følg eventuelle instrukser i displayet For å avslutte lading, send SMS «avslutt ladenr» +" +
                " til 2430 (Eks. Avslutt 123a).\n\n" +
                "Du finner riktig ladenummer ved ladeuttakene/ladekablene. " +
                "Når du lader med SMS betaler du litt mer enn når du lader med app eller ladebrikke.";

        String howToChargeGreenContact = "Send sms for å start lading og for å slutte lading"
                + " (se instruksjon på ladepunkt). \n\n Ved spørsmål om lading tlf 47 67 08 00.";

        String howtoChargeCirkleK = "Husk først å registrere deg som ladekunde eller bruk drop-in-lading. \n\n" +
                "Kort fortalt så gjennomfører du en lading slik:\n" +
                "(1) Sett ladekabel i bilen, \n" +
                "(2) Velg betalingsmåte \n" +
                "(3) Start lading (ved hjelp av app/ladebrikke og evt. start-knapp). \n\n" +
                "Når du skal avslutte så gjøres dette ved å stoppe ladingen " +
                "(ved hjelp av samme app/ladebrikke og evt. stopp-knapp) og sette tilbake ladepluggen. ";


        System.out.println(infoFromList.get(5));

        textViewTitel.setText(infoFromList.get(0));
        textViewAdress.setText(infoFromList.get(1));
        textViewChargerType.setText(infoFromList.get(2) + "\n" +  fastCharger);
        textViewAvaiable.setText(infoFromList.get(3) + " stk" );
        textViewChargerType2.setText(infoFromList.get(4) + "\n" +  fastCharger);
        textViewAvaiable2.setText(infoFromList.get(5)+ " stk");
        textViewChargerType3.setText(infoFromList.get(6) + "\n" + lightCharger);
        textViewAvaiable3.setText(infoFromList.get(7) + " stk");
        textViewInfoText.setText(infoFromList.get(8));
        textViewDescriptionText.setText(infoFromList.get(9));


        //Dette er beskrivelse delen
        if(infoFromList.get(8).equals("")){
            textViewHelp.setVisibility(View.GONE);
            textViewInfoText.setVisibility(View.GONE);
            lineInfo.setVisibility(View.GONE);

        }
        //Dette er info delen
        if(infoFromList.get(9).equals("")){
            textViewDescription.setVisibility(View.GONE);
            textViewDescriptionText.setVisibility(View.GONE);
            lineDescription.setVisibility(View.GONE);
        }

        if(infoFromList.get(3).equals("")){
            textViewAvaiable.setVisibility(View.GONE);
            textViewChargerType.setVisibility(View.GONE);
        }



        // Lyn lading
        if(infoFromList.get(7).equals("")){
            textViewAvaiable3.setVisibility(View.GONE);
            textViewChargerType3.setVisibility(View.GONE);
        }
        //hurtig lading
        if(infoFromList.get(5).equals("")){
            textViewAvaiable2.setVisibility(View.GONE);
            textViewChargerType2.setVisibility(View.GONE);
        }

        if(infoFromList.get(0).toUpperCase().equals("FORTUM")){
            textViewHowToPayText.setText(howToChargeFortum);
            textViewHowToPayText.setTextColor(Color.BLACK);
            textViewPayMethod.setTextColor(Color.BLACK);
            textViewPayMethod.setText(paymentMethodFortum);

        } else if(infoFromList.get(0).toUpperCase().equals("GRØNN KONTAKT")) {
            textViewHowToPayText.setTextColor(Color.BLACK);
            textViewPayMethod.setTextColor(Color.BLACK);
            textViewHowToPayText.setText(howToChargeGreenContact);
            textViewPayMethod.setText(paymentMethodGreenContact);
        }

        else if(infoFromList.get(0).toUpperCase().equals("CIRCLE K")){
            textViewHowToPayText.setTextColor(Color.BLACK);
            textViewPayMethod.setTextColor(Color.BLACK);
            textViewPayMethod.setText(paymentMethodCirkleK);
            payMethodTitel.setText("Priser for CIrkle K");
            textViewHowToPayText.setText(howtoChargeCirkleK);

        }

        else {
            textViewPayMethod.setText(paymentOtherStations);
            textViewHowToPay.setVisibility(View.GONE);
            textViewHowToPayText.setVisibility(View.GONE);
            lineHowToPay.setVisibility(View.GONE);
            payMethodTitel.setText("Priser");
        }




        /*
        1) Koble ladekabel til bil (og ladestasjon ved mellomrask lading)
        2) Send SMS med teksten «Start ladenr» til 2430. (Eks. Start 123a)
        Du finner riktig ladenummer ved ladeuttakene/ladekablene.
        3) Følg eventuelle instrukser i displayet For å avslutte lading, send SMS «avslutt ladenr» til 2430 (Eks. Avslutt 123a).
        Du finner riktig ladenummer ved ladeuttakene/ladekablene. Når du lader med SMS betaler du litt mer enn når du lader med app eller ladebrikke.
         */



        // Grønn kontakt





        /*

        textViewAvaiable.setText(info.get(7));
        textViewChargerType2.setText(info.get(8));
        textViewAvaiable2.setText(info.get(9));
        textViewChargerType3.setText(info.get(10));
        textViewAvaiable3.setText(info.get(11));

         */
        /*

//        textViewCompany.setText(ownedby);
        textViewPayMethod.setText(info.get(3));
        textViewDescriptionText.setText(info.get(4));
        textViewInfoText.setText(info.get(5));
        textViewPayMethod.setText("Hurtig --> 4 kr per minutt" + "\n" + "\n"  + "\n"+ "Lyn ---->4 kr per minutt + 2,50 kr per kWh");

         */





        /*
        if(textViewCompany.getText().toString().equals("Fortum")&& textViewPayMethod.getText().toString().
                equals("Cellular phone and Charging card")){
            textViewPayMethod.setText("Hurtig --> 4 kr per minutt" + "\n" + "\n"  + "\n"+ "Lyn ---->4 kr per minutt + 2,50 kr per kWh");
        }

        /*
        // Bare testing av det funeket for betalling
        textViewPayMethod.setText("");
        if(textViewPayMethod.getText().toString().equals("")){
            payMethodTitel.setVisibility(View.GONE);
            textViewPayMethod.setVisibility(View.GONE);
            linePayMethod.setVisibility(View.GONE);
        }

         */

        /*
        if(info.get(3).equals("")){
            payMethodTitel.setVisibility(View.GONE);
            textViewPayMethod.setVisibility(View.GONE);
            linePayMethod.setVisibility(View.GONE);
        }

         */
        /*

        if(info.get(4).equals("")){
            textViewDescription.setVisibility(View.GONE);
            textViewDescriptionText.setVisibility(View.GONE);
            lineDescription.setVisibility(View.GONE);
        }


        /**
         * Verdier inni forloopen
         */

        /*
        textViewChargerType.setText(info.get(6));
        textViewAvaiable.setText(info.get(7));
        textViewChargerType2.setText(info.get(8));
        textViewAvaiable2.setText(info.get(9));
        textViewChargerType3.setText(info.get(10));
        textViewAvaiable3.setText(info.get(11));

         */

    }





}
