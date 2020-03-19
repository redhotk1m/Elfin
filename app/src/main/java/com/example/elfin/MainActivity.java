package com.example.elfin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.API.Nobil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import rebus.permissionutils.AskAgainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;
import rebus.permissionutils.PermissionUtils;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public EditText editText;
    DisplaySuggestions displaySuggestions;
    //TextView textView;
    ListView listViewSuggest;
    ArrayAdapter<String> arrayAdapterSuggestions;
    ArrayList<String> placeIdList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //textView = findViewById(R.id.textViewSuggest);
        listViewSuggest=findViewById(R.id.listViewSuggest);
        listViewSuggest.setVisibility(View.INVISIBLE);


        Spinner dropdown = findViewById(R.id.chooseCar);
        ImageButton imageButton = findViewById(R.id.imageButtonDriveNow);
        editText = findViewById(R.id.editTextToAPlace);
        //dropdown.setPrompt("EB12342 VW e-Golf");
        String[] items = new String[]{"EB 12342 VW e-Golf", "Legg til bil"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setPrompt("EB12342 VW e-Golf");
        //Intent intent = new Intent(this,AboutCharger.class);
        //startActivity(intent);

        /*
        //Hvis den skal funke
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Geolocation geolocation = new Geolocation();
                geolocation.getAdress(editText.getText().toString(), getApplicationContext(), new GeoHandler());

            }
        });
        */
        //swapview


    }

    public void displaySuggestions(View view){
        displaySuggestions = new DisplaySuggestions(getBaseContext(), new AsyncResponse() {
            ArrayList<String> list = new ArrayList<>();

            @Override
            public void processFinish(ArrayList<ArrayList<String>> lists) {

                for (int i = 0; i <lists.size() ; i++) {
                    for (int j = 0; j <lists.get(i).size() ; j++) {
                        if(i == 0){
                            list.add(lists.get(i).get(j));
                        }else {
                            placeIdList.add(lists.get(i).get(j));
                        }
                    }
                }
                listViewSuggest.setVisibility(View.VISIBLE);
                arrayAdapterSuggestions = new ArrayAdapter<>(getApplication().getBaseContext(), android.R.layout.simple_list_item_1,list);
                listViewSuggest.setAdapter(arrayAdapterSuggestions);
            }
        });
        displaySuggestions.execute("");
        listViewSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                editText.setText(listViewSuggest.getItemAtPosition(position).toString());
                sendPlaceId(placeIdList.get(position));
                listViewSuggest.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void sendPlaceId(String placeId){
        System.out.println(placeId);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        //This is the result when user accepts / declines GPS location.
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            startChargingStationActivity();//Access was granted to GPS, change activity
        else{
            //Permission was not granted, should inform user that it's required to use the app.
            //Gi beskjed til bruker at appen ikke kan brukes uten tilgang til GPS
        }
    }

    private void startChargingStationActivity() {
        Intent intent = new Intent(this,ChargingStations.class);
        startActivity(intent);
    }

    public void nextActivity(View view) {
        //Attempts to go to chargingStation activity, checks GPS permission first
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //If permission isn't already granted, ask for permission
            ActivityCompat.requestPermissions(
                    this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    ,0);
        }else{
            //If permission is already granted, change activity
            startChargingStationActivity();
        }
    }
}
