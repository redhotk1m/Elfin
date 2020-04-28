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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.MainActivity;
import com.example.elfin.R;
import com.example.elfin.Utils.DialogBox;
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

    private ArrayList<Elbil> elbils, allElbils;
    private boolean[] found;
    private HashMap<String, String> fieldMap;
    private String brand, model, modelYear, battery, fastCharge;
    private String brand2, model2, modelYear2, battery2, fastCharge2;

    private ArrayList<Elbil> mCarList, mCarListAll;
    private ImageButton saveCarBtn, loadCarBtn;
    // private EditText editTextFastCharge, editTextBattery;
    private TextView editTextBrand, editTextModel, editTextModelYear, editTextFastCharge, editTextBattery;
    private TextView editTextBrand2, editTextModel2, editTextModelYear2, editTextFastCharge2, editTextBattery2;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private Elbil elbil;

    private boolean elbilFound;

    private SharedCarPreferences sharedCarPreferences;

    private DialogBox dialogBox, dialogBox1, dialogBox2, dialogBox3, dialogBox4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);

        findViewsById();

        initDisplayFields();

        System.out.println("ELBIL FOUND BOOLEAN: " + elbilFound);

        Intent intent = getIntent();

        //AllCarsList
        allElbils = intent.getParcelableArrayListExtra("AllCarsList");
        System.out.println("#####################################################################");
        if (allElbils != null)
            System.out.println("(CAR INFO ACTIVITY) ALL CAR LIST SIZE: " + allElbils.size());
        else System.out.println("(CAR INFO ACTIVITY) ALL CAR LIST SIZE: NULL");
        System.out.println("#####################################################################");


        elbil = intent.getParcelableExtra("Elbil");
        if (elbil != null) {
            elbilFound = true;
            System.out.println("getParcelable Elbil: " + elbil.toString() + " ; " + elbilFound);
            // getCarAttributes(elbil, elbilFound);
        } else System.out.println("getParcelable Elbil not received");


        found = intent.getBooleanArrayExtra("Missing");
        if (found != null) {
            // Toast.makeText(this, "INTEN MISSING[]: " + Arrays.toString(found), Toast.LENGTH_LONG).show();
            System.out.println("INTEN MISSING[]: " + Arrays.toString(found));
        } else System.out.println("FOUND FIELDS[] NOT RECEIVED");


        fieldMap = (HashMap<String, String>) intent.getSerializableExtra("FieldMap");
        if (fieldMap != null) {
            System.out.println("INTENT HASH MAP: " + fieldMap);
            //  Toast.makeText(this, "INTENT HASH MAP: " + fieldMap, Toast.LENGTH_LONG).show();
            /*
            Toast.makeText(this, "EXACT FIELDS FOUND:\n\n"
                            + BRAND + " ; " + fieldMap.get(BRAND) + "\n\n"
                            + MODEL + " ; " + fieldMap.get(MODEL) + "\n\n"
                            + MODELYEAR + " ; " + fieldMap.get(MODELYEAR) + "\n\n"
                            + BATTERY + " ; " + fieldMap.get(BATTERY) + "\n\n"
                    , Toast.LENGTH_LONG).show();
             */
            System.out.println("INTENT EXACT FIELDS: \n\n"
                    + BRAND + " ; " + fieldMap.get(BRAND) + "\n\n"
                    + MODEL + " ; " + fieldMap.get(MODEL) + "\n\n"
                    + MODELYEAR + " ; " + fieldMap.get(MODELYEAR) + "\n\n"
                    + BATTERY + " ; " + fieldMap.get(BATTERY) + "\n\n"
            );
        } else System.out.println("getParcelable fieldMap not received");
        /*

        foundFieldsMap = (HashMap<String, List<String>>) intent.getSerializableExtra("FoundFieldsMap");
        System.out.println("INTENT HASH MAP 2: " + foundFieldsMap);
        Toast.makeText(this, "INTENT HASH MAP: " + foundFieldsMap, Toast.LENGTH_LONG).show();

         */


        elbils = getIntent().getParcelableArrayListExtra("CarList");
        if (elbils != null && elbils.size() > 0) {
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


        initDialog();
        /*
        if (elbilFound) {
            dialogBox1.createDialogBox();
        } else {
            dialogBox3.createDialogBox();
        }
        */


        saveCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(CarInfoActivity.this, "LEGG TIL BIL!", Toast.LENGTH_SHORT).show();

                if (elbilFound) {
                    mCarList.add(elbil);

                    //  dialogBox1.createDialogBox();

                    saveCar();
                    // clearAttributes();
                } else {

                    // dialogBox3.createDialogBox();

                    Intent intent = new Intent(CarInfoActivity.this, CarSelectionActivity.class);
                    intent.putParcelableArrayListExtra("CarList", new ArrayList<>(elbils));
                    intent.putExtra("Missing", found);
                    intent.putExtra("FieldMap", fieldMap);

                    startActivityDialogBox(1, intent);

                    // startActivity(intent);
                   // finish();
                }
            }
        });

        loadCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Toast.makeText(CarInfoActivity.this, "IKKE LEGG TIL BIL", Toast.LENGTH_SHORT).show();
                finish();
                /*
                if (elbilFound) {
                    dialogBox2.createDialogBox();
                    finish();
                } else {
                    dialogBox4.createDialogBox();
                    finish();
                }




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
        loadCar();

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
        editTextBrand2 = findViewById(R.id.text_view_brand2);
        editTextModel2 = findViewById(R.id.text_view_model2);
        editTextModelYear2 = findViewById(R.id.text_view_model_year2);
        editTextFastCharge2 = findViewById(R.id.text_view_fast_charge2);
        editTextBattery2 = findViewById(R.id.text_view_battery2);




        spinner = findViewById(R.id.spinner_all_cars);

        saveCarBtn = findViewById(R.id.btnSaveCar);
        loadCarBtn = findViewById(R.id.btnLoadCar);
    }

    private AdapterView.OnItemSelectedListener myOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            /*
            Toast.makeText(adapterView.getContext(),
                    "OnItemSelectedListener : " + adapterView.getItemAtPosition(position).toString(),
                    Toast.LENGTH_SHORT).show();
            */

            getSelectedCar();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void getSelectedCar() {
        Elbil elbil = (Elbil) spinner.getSelectedItem();

        if (elbil.getBrand().equals("LEGG TIL BIL"))
            Toast.makeText(this, "NO CAR SELECTED!\n" + elbil.getBrand(), Toast.LENGTH_SHORT).show();
        else getCarAttributes(elbil, elbilFound);
    }


    private void getCarAttributes(Elbil elbil, boolean elbilFound) {

        if (elbilFound) {
//            String documentId = elbil.getDocumentId();
            brand2 = elbil.getBrand();
            model2 = elbil.getModel();
            modelYear2 = elbil.getModelYear();
            battery2 = elbil.getBattery();
            fastCharge2 = elbil.getFastCharge();

            //todo: lag popup dialog ==> "VIL DU PRØVE MED ET ANNET REG NR ELLER VELGE BILEN DIN MANUELT? "
           // loadCarBtn.setText("IKKE MIN BIL");
        } else if (fieldMap != null) {

            if (found[0]) brand2 = fieldMap.get(BRAND);
            else brand2 = "[IKKE FUNNET]";

            if (found[1]) model2 = fieldMap.get(MODEL);
            else model2 = "[IKKE FUNNET]";

            if (found[2]) modelYear2 = fieldMap.get(MODELYEAR);
            else modelYear2 = "[IKKE FUNNET]";

            if (found[3]) battery2 = fieldMap.get(BATTERY);
            else{
                battery2 = "UKJENT";
                fastCharge2 = "UKJENT";
            }


            // fastCharge += fieldMap.get(FASTCHARGE);
            fastCharge = "[IKKE FUNNET]";


            //todo: lag popup dialog ==> "Vi mangler noen opplysninger om bilen din, vennligst velg blant de opplysningen som passer din bil.." ==> "OK?"
            //saveCarBtn.setText("FYLL INN MANGLER");
            //todo: lag popup dialog ==> "VIL DU PRØVE MED ET ANNET REG NR ELLER VELGE BILEN DIN MANUELT? "
            //loadCarBtn.setText("IKKE MIN BIL");
        }


        editTextBrand2.setText(brand2);
        editTextModel2.setText(model2);
        editTextModelYear2.setText(modelYear2);
        editTextFastCharge2.setText(fastCharge2);
        editTextBattery2.setText(battery2);
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
        editTextBattery.setText(battery);
    }

    private void initDialog() {
        dialogBox1 = new DialogBox(this, "ER DETTE DIN BIL?",
                "Vil du legge til denne bilen i appen?",
                "LEGG TIL", "SØK IGJEN", 2);

        dialogBox2 = new DialogBox(this, "ER DETTE IKKE DIN BIL?",
                "VIL DU PRØVE MED ET ANNET REG NR ELLER VELGE BILEN DIN MANUELT?",
                "VELG MANUELT", "SØK IGJEN", 2);

        dialogBox3 = new DialogBox(this, "ER DETTE DIN BIL?",
                "Vi mangler noen opplysninger om bilen din, vennligst velg blant de opplysningen som passer din bil",
                "FYLL IN MANGLER", "SØK IGJEN", 2);

        dialogBox4 = new DialogBox(this, "ER DETTE IKKE DIN BIL?",
                "VIL DU PRØVE MED ET ANNET REG NR ELLER VELGE BILEN DIN MANUELT?",
                "VELG MANUELT", "SØK IGJEN", 2);
    }

    private void startActivityDialogBox(int identifier, Intent intent) {
        String title, msg, yesBtn, noBtn;

        switch (identifier) {
            case 0:
                title = "Er dette din bil?";
                msg = "Vil du legge til denne bilen i appen?";
                yesBtn = "Legg til bil";
                noBtn = "Ikke min bil";
                dialogBox = new DialogBox(this, title, msg, yesBtn, noBtn, 3);
                break;
            case 1:
                title = "Er dette din bil?";
                msg = "Vi mangler noen opplysninger om bilen din, vennligst velg blant de opplysningen som passer din bil best";
                yesBtn = "Fyll mangler";
                noBtn = "Nei";
                dialogBox = new DialogBox(this, title, msg, yesBtn, noBtn, 3);
                break;
            case 2:
                title = "Er dette din bil?";
                msg = "Vil du prøve med et annet reg nr, eller velge bilen din manuelt?";
                yesBtn = "Velg bil manuelt";
                noBtn = "Søk igjen";
                dialogBox = new DialogBox(this, title, msg, yesBtn, noBtn, 99);
                break;
        }

        System.out.println("<SHOWING DIALOG BOX>");
        dialogBox.setIntent(intent);
        // dialogBox.simpleDialogBox();
        dialogBox.createDialogBox();
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
