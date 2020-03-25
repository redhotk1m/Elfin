package com.example.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.MainActivity;
import com.example.elfin.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarInfoActivity extends AppCompatActivity {

    private ArrayList<Elbil> mCarList;
    private Button saveCarBtn, loadCarBtn;
    private EditText editTextFastCharge, editTextBattery;
    private TextView editTextBrand, editTextModel, editTextModelYear;
    private Elbil elbil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);

        Intent intent = getIntent();
        elbil = intent.getParcelableExtra("Elbil");

        findViewsById();

        getCarAttributes(elbil);

        mCarList = new ArrayList<>();

        saveCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCarList.add(elbil);
                saveCar();
                clearAttributes();
            }
        });

        loadCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCarList.clear();
                loadCar();
                //if (!mCarList.isEmpty()) getCarAttributes(mCarList.get(0));
            }
        });
    }


    private void saveCar() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.clear(); //clear shared preferences
        Gson gson = new Gson();
        //to contain ArrayList as Json form
        String json = gson.toJson(mCarList);
        editor.putString("car list", json);
        editor.apply();

        Toast.makeText(this, "CAR SAVED!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void loadCar() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("car list", null);
        Type type = new TypeToken<ArrayList<Elbil>>() {}.getType();
        mCarList = gson.fromJson(json, type);

        if (mCarList == null) mCarList = new ArrayList<>();
        else getCarAttributes(mCarList.get(0));
    }


    private void findViewsById() {
        editTextBrand = findViewById(R.id.text_view_brand);
        editTextModel = findViewById(R.id.text_view_model);
        editTextModelYear = findViewById(R.id.text_view_model_year);
        editTextFastCharge = findViewById(R.id.edit_text_fast_charge);
        //editTextBattery = findViewById(R.id.edit_text_battery);

        saveCarBtn = findViewById(R.id.btnSaveCar);
        loadCarBtn = findViewById(R.id.btnLoadCar);
    }

    private void getCarAttributes(Elbil elbil) {
        String documentId = elbil.getDocumentId();
        String brand = "Bilmerke: " + elbil.getBrand();
        String model = "Bilmodell: " + elbil.getModel();
        String modelYear = "Årsmodell: " + elbil.getModelYear();
        Map<String, Double> specs = elbil.getSpecs();
        //String fastCharge = "Ladetype: " + elbil.getFastCharge();
        //+ " " + specs.get("effect") + " kwh";
       // String battery = "Batteri: ";
        //+ specs.get("battery") + " kw";
        //      String effect = specs.get("effect").toString();

        getSpecs(specs);

        editTextBrand.setText(brand);
        editTextModel.setText(model);
        editTextModelYear.setText(modelYear);
        //editTextFastCharge.setText(fastCharge);
        //editTextBattery.setText(battery);
    }


    private void getSpecs(Map<String, Double> specs) {

        if (specs == null) System.out.println("NO SPECS");
        else System.out.println(specs.get("battery"));

//            String spec = specs.keySet().toString();

    //    System.out.println(spec + " ; " + specs.get(spec));



        /*
        for(Map.Entry entry:specs.entrySet()){
            System.out.print(entry.getKey() + " : " + entry.getValue());
            //String s = entry.getKey() + " : " + entry.getValue() + "\n";
            //editTextFastCharge.setText(s);
        }

         */
    }



    private void clearAttributes(){
        String brand = "Bilmerke: ";
        String model = "Bilmodell: ";
        String modelYear = "Årsmodell: ";
        Map<String, Double> specs;
        String fastCharge = "Ladetype: ";
        //+ " " + specs.get("effect") + " kwh";
        //String battery = "Batteri: ";

        editTextBrand.setText(brand);
        editTextModel.setText(model);
        editTextModelYear.setText(modelYear);
        //editTextFastCharge.setText(fastCharge);
        //editTextBattery.setText(battery);
    }

    private void clearSharedPrefferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); //clear shared preferences
        editor.apply();

    }

}
