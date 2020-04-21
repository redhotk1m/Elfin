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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CarInfoActivity extends AppCompatActivity {

    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private ArrayList<Elbil> elbils;
    private boolean[] found;
    private HashMap<String, String> fieldMap;
    private String brand, model, modelYear, battery, fastCharge;

    private ArrayList<Elbil> mCarList, mCarListAll;
    private Button saveCarBtn, loadCarBtn;
    // private EditText editTextFastCharge, editTextBattery;
    private TextView editTextBrand, editTextModel, editTextModelYear, editTextFastCharge, editTextBattery;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private Elbil elbil;

    private boolean elbilFound;

    private SharedCarPreferences sharedCarPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);

        findViewsById();

        initDisplayFields();

        System.out.println("ELBIL FOUND BOOLEAN: " + elbilFound);

        Intent intent = getIntent();
        elbil = intent.getParcelableExtra("Elbil");
        if (elbil != null) {
            elbilFound = true;
            System.out.println("getParcelable Elbil: " + elbil.toString() + " ; " + elbilFound);
            // getCarAttributes(elbil, elbilFound);
        } else System.out.println("getParcelable Elbil not received");


        found = intent.getBooleanArrayExtra("Missing");
        if (found != null) {
            Toast.makeText(this, "INTEN MISSING[]: " + Arrays.toString(found), Toast.LENGTH_LONG).show();
            System.out.println("INTEN MISSING[]: " + Arrays.toString(found));
        } else System.out.println("FOUND FIELDS[] NOT RECEIVED");


        fieldMap = (HashMap<String, String>) intent.getSerializableExtra("FieldMap");
        if (fieldMap != null) {
            System.out.println("INTENT HASH MAP: " + fieldMap);
            //  Toast.makeText(this, "INTENT HASH MAP: " + fieldMap, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "EXACT FIELDS FOUND:\n\n"
                            + BRAND + " ; " + fieldMap.get(BRAND) + "\n\n"
                            + MODEL + " ; " + fieldMap.get(MODEL) + "\n\n"
                            + MODELYEAR + " ; " + fieldMap.get(MODELYEAR) + "\n\n"
                            + BATTERY + " ; " + fieldMap.get(BATTERY) + "\n\n"
                    , Toast.LENGTH_LONG).show();
        } else System.out.println("getParcelable fieldMap not received");
        /*

        foundFieldsMap = (HashMap<String, List<String>>) intent.getSerializableExtra("FoundFieldsMap");
        System.out.println("INTENT HASH MAP 2: " + foundFieldsMap);
        Toast.makeText(this, "INTENT HASH MAP: " + foundFieldsMap, Toast.LENGTH_LONG).show();

         */


        elbils = getIntent().getParcelableArrayListExtra("CarList");
        if (elbils != null) {
            System.out.println("ELBILS RECEIVED: " + elbils.get(0).toString());
            // for (Elbil elbil : elbils) System.out.println(elbil.getModel());
        } else System.out.println("NO ELBILS RECEIVED!");


        sharedCarPreferences = new SharedCarPreferences();


        // clearSharedPrefferences();

        // loadCar();


        getCarAttributes(elbil, elbilFound);


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

                Toast.makeText(CarInfoActivity.this, "LEGG TIL BIL!", Toast.LENGTH_SHORT).show();

                if (elbilFound) {
                    mCarList.add(elbil);
                    saveCar();
                   // clearAttributes();
                } else {
                    Intent intent = new Intent(CarInfoActivity.this, CarSelectionActivity.class);
                   // intent.putParcelableArrayListExtra("CarList", new ArrayList<>(mElbilList));
                    intent.putExtra("Missing", found);
                    intent.putExtra("FieldMap", fieldMap);
                    startActivity(intent);
                }
            }
        });

        loadCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(CarInfoActivity.this, "IKKE LEGG TIL BIL", Toast.LENGTH_SHORT).show();

                finish();

                // mCarList.clear();

               // initSpinner();

                /*
                Toast.makeText(CarInfoActivity.this, "SIZE: " + mCarListAll.size(), Toast.LENGTH_LONG).show();
                for (Elbil elbil : mCarListAll)
                    System.out.println("\nINFO MERKE : " + elbil.getBrand());

                 */

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
        else getCarAttributes(elbil, elbilFound);
    }


    private void getCarAttributes(Elbil elbil, boolean elbilFound) {

        if (elbilFound) {
            String documentId = elbil.getDocumentId();
            brand += elbil.getBrand();
            model += elbil.getModel();
            modelYear += elbil.getModelYear();
            battery += elbil.getBattery();
            fastCharge += elbil.getFastCharge();

            //todo: lag popup dialog ==> "VIL DU PRØVE MED ET ANNET REG NR ELLER VELGE BILEN DIN MANUELT? "
            loadCarBtn.setText("IKKE MIN BIL");
        } else {

            if (found[0]) brand += fieldMap.get(BRAND);
            else brand += "[IKKE FUNNET]";

            if (found[1]) model += fieldMap.get(MODEL);
            else model += "[IKKE FUNNET]";

            if (found[2]) modelYear += fieldMap.get(MODELYEAR);
            else modelYear += "[IKKE FUNNET]";

            if (found[3]) battery += fieldMap.get(BATTERY);
            else battery += "[IKKE FUNNET]";


            // fastCharge += fieldMap.get(FASTCHARGE);
            fastCharge += "[IKKE FUNNET]";


            //todo: lag popup dialog ==> "Vi mangler noen opplysninger om bilen din, vennligst velg blant de opplysningen som passer din bil.." ==> "OK?"
            saveCarBtn.setText("FYLL INN MANGLER");
            //todo: lag popup dialog ==> "VIL DU PRØVE MED ET ANNET REG NR ELLER VELGE BILEN DIN MANUELT? "
            loadCarBtn.setText("IKKE MIN BIL");
        }

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

    private void initDisplayFields() {
        brand = "Bilmerke: ";
        model = "Bilmodell: ";
        modelYear = "Årsmodell: ";
        battery = "Batterikapasitet: ";
        fastCharge = "Ladetype: ";

        editTextBrand.setText(brand);
        editTextModel.setText(model);
        editTextModelYear.setText(modelYear);
        editTextFastCharge.setText(fastCharge);
    }


    /*
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

     */

    private void clearSharedPrefferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); //clear shared preferences
        editor.apply();
    }

}
