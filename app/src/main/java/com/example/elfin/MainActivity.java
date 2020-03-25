package com.example.elfin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.elfin.API.Nobil;
import com.example.elfin.Activities.Station.ChargingStations;
import com.example.elfin.Utils.AsyncResponse;
import com.example.elfin.Utils.EditTextFunctions;
import com.example.elfin.car.AddCarActivity;
import com.example.elfin.car.Elbil;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public EditText editText;
    DisplaySuggestions displaySuggestions;
    //TextView textView;
    public ListView listViewSuggest;
    ArrayAdapter<String> arrayAdapterSuggestions;
    ArrayList<String> placeIdList = new ArrayList<>();
    String destinationID;
    public static ArrayList<LatLng> allChargingStations;
    private boolean chargingStationsFound = false;

    private TextView tvMyCar;
    private Spinner dropdown;
    private ArrayList<Elbil> mCarList;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //textView = findViewById(R.id.textViewSuggest);
        listViewSuggest=findViewById(R.id.listViewSuggest);
        listViewSuggest.setVisibility(View.INVISIBLE);


        dropdown = findViewById(R.id.chooseCar);
        ImageButton imageButton = findViewById(R.id.imageButtonDriveNow);
        editText = findViewById(R.id.editTextToAPlace);
        editText.setCursorVisible(false);

        //dropdown.setPrompt("EB12342 VW e-Golf");
        String[] items = new String[]{"EB 12342 VW e-Golf", "Legg til bil"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setPrompt("EB12342 VW e-Golf");



        //Intent intent = new Intent(this,AboutCharger.class);
        //startActivity(intent);
        EditTextFunctions editTextFunctions = new EditTextFunctions(this);
        editTextFunctions.setText();


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


        Nobil nobil = new Nobil(this);
        nobil.execute();

        tvMyCar = findViewById(R.id.tv_my_car);
        getSavedCar();
        tvMyCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddCarActivity.class));
            }
        });
    }

    public void closeKeyboard(View view){
        InputMethodManager keyboardManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboardManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }



    public void displaySuggestions(String adress){
        displaySuggestions = new DisplaySuggestions(getBaseContext(), adress, new AsyncResponse() {
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
                setDestinationID(placeIdList.get(position));
                listViewSuggest.setVisibility(View.INVISIBLE);
                arrayAdapterSuggestions.clear();
                closeKeyboard(view);
            }
        });
    }




    public void setDestinationID(String destinationID){
        this.destinationID = destinationID;
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
        checkIfChargingStationsAreFound();
        Intent intent = new Intent(this, ChargingStations.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("chargingStations",getAllChargingStations());
        bundle.putString("destinationID",destinationID);
        intent.putExtra("bundle",bundle);
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
        } else {
            //If permission is already granted, change activity
            startChargingStationActivity();
        }
    }


    public ArrayList<LatLng> getAllChargingStations() {
        return allChargingStations;
    }

    public void setAllChargingStations(ArrayList<LatLng> allChargingStations) {
        this.allChargingStations = allChargingStations;
    }

    private void checkIfChargingStationsAreFound(){
        while (!chargingStationsFound){
            //TODO: Venter på alle ladestasjoner før noe mer skjer,
            // bør håndtere hvis den ikke finner ladestajoner, eller det går en viss tid.
        }
    }

    public void setChargingStationsFound(boolean found){
        this.chargingStationsFound = found;
    }

    private void getSavedCar() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("car list", null);
        Type type = new TypeToken<ArrayList<Elbil>>() {}.getType();
        mCarList = gson.fromJson(json, type);

        if (mCarList == null) mCarList = new ArrayList<>();
        else {
            String myCar = mCarList.get(0).getBrand() + " " + mCarList.get(0).getModel();
            tvMyCar.setText(myCar);
            //dropdown.setPrompt(myCar);
        }
    }
}
