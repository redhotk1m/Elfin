package com.example.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.MainActivity;
import com.example.elfin.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class CarInfoActivity extends AppCompatActivity {

    private ArrayList<Elbil> mCarList, mCarListAll;
    private Button saveCarBtn, loadCarBtn;
   // private EditText editTextFastCharge, editTextBattery;
    private TextView editTextBrand, editTextModel, editTextModelYear, editTextFastCharge, editTextBattery;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private Elbil elbil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);

        Intent intent = getIntent();
        elbil = intent.getParcelableExtra("Elbil");

        findViewsById();

       // clearSharedPrefferences();

        loadCar();

        getCarAttributes(elbil);

        mCarList = new ArrayList<>();
        /*
        if (mCarList.size() == 0) Toast.makeText(this, "EMPTY CAR LIST!", Toast.LENGTH_SHORT).show();
        else {
            for (Elbil elbil : mCarList) {
                Toast.makeText(this, "BRAND: " + elbil.getBrand(), Toast.LENGTH_SHORT).show();
            }
        }
        */

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
                // mCarList.clear();

                initSpinner();

                Toast.makeText(CarInfoActivity.this, "SIZE: " + mCarListAll.size(), Toast.LENGTH_LONG).show();
                for (Elbil elbil : mCarListAll)
                    System.out.println("\nINFO MERKE : " + elbil.getBrand());

                //display spinner item
                // getSelectedCar(view);
            }
        });
    }

    private void initSpinner() {
        loadCar();
        mCarListAll.add(new Elbil("LEGG TIL BIL", null, null, null, null, null));

        adapter = new ArrayAdapter<>(CarInfoActivity.this,
                android.R.layout.simple_spinner_item, mCarListAll);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(myOnItemSelectedListener);
    }

    private void saveCar() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.clear(); //clear shared preferences
        Gson gson = new Gson();
        //to contain ArrayList as Json form
        mCarList.addAll(mCarListAll);
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
        Type type = new TypeToken<ArrayList<Elbil>>() {
        }.getType();
        mCarListAll = gson.fromJson(json, type);

        if (mCarListAll == null) mCarListAll = new ArrayList<>();
    }


    private void findViewsById() {
        editTextBrand = findViewById(R.id.text_view_brand);
        editTextModel = findViewById(R.id.text_view_model);
        editTextModelYear = findViewById(R.id.text_view_model_year);
        editTextFastCharge = findViewById(R.id.text_view_fast_charge);
        editTextBattery = findViewById(R.id.text_view_battery);

        spinner = findViewById(R.id.spinner_all_cars);

        saveCarBtn = findViewById(R.id.btnSaveCar);
        loadCarBtn = findViewById(R.id.btnLoadCar);
    }

    private AdapterView.OnItemSelectedListener myOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            Toast.makeText(adapterView.getContext(),
                    "OnItemSelectedListener : " + adapterView.getItemAtPosition(position).toString(),
                    Toast.LENGTH_SHORT).show();

            getSelectedCar();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void getSelectedCar() {
        Elbil elbil = (Elbil) spinner.getSelectedItem();

        if (elbil.getBrand().equals("LEGG TIL BIL"))
            startActivity(new Intent(this, AddCarActivity.class));
            //Toast.makeText(this, "NO CAR SELECTED!\n" + elbil.getBrand(), Toast.LENGTH_SHORT).show();
        else getCarAttributes(elbil);
    }


    private void getCarAttributes(Elbil elbil) {
        String documentId = elbil.getDocumentId();
        String brand = "Bilmerke: " + elbil.getBrand();
        String model = "Bilmodell: " + elbil.getModel();
        String modelYear = "Årsmodell: " + elbil.getModelYear();
        String battery = "Batterikappasitet: " + elbil.getBattery();
        String fastCharge = "Hurtiglader: " + elbil.getFastCharge() + " ; " + elbil.getEffect();
        Map<String, Double> specs = elbil.getSpecs();
        //String fastCharge = "Ladetype: " + elbil.getFastCharge();
        //+ " " + specs.get("effect") + " kwh";
        // String battery = "Batteri: ";
        //+ specs.get("battery") + " kw";
        //      String effect = specs.get("effect").toString();

       // getSpecs(specs);

        editTextBrand.setText(brand);
        editTextModel.setText(model);
        editTextModelYear.setText(modelYear);
        editTextFastCharge.setText(fastCharge);
        editTextBattery.setText(battery);
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


    private void clearAttributes() {
        String brand = "Bilmerke: ";
        String model = "Bilmodell: ";
        String modelYear = "Årsmodell: ";
        String battery = "Batterikapasitet: ";
        Map<String, Double> specs;
        String fastCharge = "Ladetype: ";
        //+ " " + specs.get("effect") + " kwh";
        //String battery = "Batteri: ";

        editTextBrand.setText(brand);
        editTextModel.setText(model);
        editTextModelYear.setText(modelYear);
        editTextFastCharge.setText(fastCharge);
        //editTextBattery.setText(battery);
    }

    private void clearSharedPrefferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); //clear shared preferences
        editor.apply();

    }

}
