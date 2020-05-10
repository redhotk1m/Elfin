package com.elfin.elfin.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.elfin.elfin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Class for displaying the information on the about charger site,
 * also opens google maps navigate if the button is pressed
 */

public class AboutCharger extends AppCompatActivity implements OnMapReadyCallback {
    TextView textViewTitel;
    TextView textViewAdress;
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

        String[] latlong =  latLngFromList.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        latLng = new LatLng(latitude, longitude);


        Button button = findViewById(R.id.button2);
        textViewTitel= findViewById(R.id.textViewTitel);
        textViewAdress = findViewById(R.id.textViewAdress);
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




    public void setInfo(){


        /**
         * Settes the needed information for each charger station
         *
         */


        String fastCharger = "50 kW - 500VDC max 100A";

        String paymentMethodFortum = "Hurtig lading 4 kr per minutt \n\nLyn lading 4 kr per minutt + 2,50 kr per kWh";
        String paymentMethodGreenContact = "Registrert kunde:   NOK 1,25 per minutt + NOK 2,90 per kWh \n \n"
                + "Drop inn kunde:     NOK 2,50 per minutt + NOK 2,90 per kWh";
        String paymentMethodCirkleK = "Hurtiglading med ladebrikke eller mobilapp: 4,49 kr per kWh\n\n" +
                "Lynlading med ladebrikke eller mobilapp: 4,99 kr per kWh";
        String paymentBKK = "Hurtiglading 1,25 kr per minutt + 2,90 kr per kWh\n\n"+
                "Lynlading 1,25 kr per minutt + 2,90 kr per kWh";

        String paymentIonity = "Lynlading 8,40 kr per kWh (drop-in)";


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

        String howtoCHargeBkk = "Last ned appen fra BKK eller ladebrikk\n\n" + "Stop og start betaling via app";

        String howtoChargeIonity = "Skann strekkode på ladested legg inn nødvendig informasjon og bekreft betaling eller last ned IONITY appen";


        System.out.println(infoFromList.get(5));

        textViewTitel.setText(infoFromList.get(0));
        textViewAdress.setText(infoFromList.get(1));
        textViewChargerType.setText(infoFromList.get(2) + "\n" +  fastCharger);
        textViewAvaiable.setText(infoFromList.get(3));
        textViewChargerType2.setText(infoFromList.get(4) + "\n" +  fastCharger);
        textViewAvaiable2.setText(infoFromList.get(5));
        textViewChargerType3.setText(infoFromList.get(6));
        textViewAvaiable3.setText(infoFromList.get(7));
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

        else if(infoFromList.get(0).toUpperCase().equals("BKK")) {
            textViewHowToPayText.setTextColor(Color.BLACK);
            textViewPayMethod.setTextColor(Color.BLACK);
            textViewHowToPayText.setText(howtoCHargeBkk);
            payMethodTitel.setText("Priser for BKK");
            textViewPayMethod.setText(paymentBKK);
        }
        else if(infoFromList.get(0).toUpperCase().equals("IONITY")) {
            textViewHowToPayText.setTextColor(Color.BLACK);
            textViewPayMethod.setTextColor(Color.BLACK);
            textViewHowToPayText.setText(howtoChargeIonity);
            payMethodTitel.setText("Priser for IONITY");
            textViewPayMethod.setText(paymentIonity);
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
    }





}
