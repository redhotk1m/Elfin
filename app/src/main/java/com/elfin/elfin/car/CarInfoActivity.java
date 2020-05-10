package com.elfin.elfin.car;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.elfin.elfin.MainActivity;
import com.elfin.elfin.R;
import com.elfin.elfin.Utils.DialogBox;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private ImageView imageView, imageView2;
    private TextView textView, textView2, textViewAddCar;
    private TextView editTextBrand, editTextModel, editTextModelYear, editTextFastCharge, editTextBattery;
    private Spinner spinner;


    private ArrayAdapter<String> adapterBrand, adapterModel, adapterModelYear, adapterFastCharge, adapterBattery;
    private List<String> brands, models, modelYears, fastCharges, batteries;
    private Spinner spinnerBrand, spinnerModel, spinnerModelYear, spinnerFastCharge, spinnerBattery;
    private String selectedBrand, selectedModel, selectedModelYear, selectedFastCharge, selectedBattery,
            regNr, exactModelYear;

    private ArrayAdapter adapter;
    private Elbil elbil;

    private boolean elbilFound, carInfo, manualSelection;

    private SharedPreferences sharedPreferences;
    private SharedCarPreferences sharedCarPreferences;

    private CarFilteredList carFilteredList;

    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info);

        findViewsById();



        initDisplayFields();

        sharedCarPreferences = new SharedCarPreferences();

        Intent intent = getIntent();


        getAllIntent(intent);

        getCarAttributes(elbil, elbilFound);

        mCarList = new ArrayList<>();

        carFilteredList = new CarFilteredList();

        initCarSpinnerSelection(spinnerBrand, BRAND);
        initCarSpinnerSelection(spinnerModel, MODEL);
        initCarSpinnerSelection(spinnerModelYear, MODELYEAR);
        initCarSpinnerSelection(spinnerBattery, BATTERY);
        initCarSpinnerSelection(spinnerFastCharge, FASTCHARGE);

        //todo: midlertidig; må håndteres riktig

        if (elbilFound) {
            setSpinnerSelection(spinnerBrand, BRAND, elbilFound);
            setSpinnerSelection(spinnerModel, MODEL, elbilFound);
            setSpinnerSelection(spinnerModelYear, MODELYEAR, elbilFound);
            setSpinnerSelection(spinnerBattery, BATTERY, elbilFound);
            setSpinnerSelection(spinnerFastCharge, FASTCHARGE, elbilFound);
        } else if (!carInfo && !manualSelection) {
            setSpinnerSelection(spinnerBrand, BRAND, found[0]);
            setSpinnerSelection(spinnerModel, MODEL, found[1]);
            setSpinnerSelection(spinnerModelYear, MODELYEAR, found[2]);
            setSpinnerSelection(spinnerBattery, BATTERY, found[3]);
            setSpinnerSelection(spinnerFastCharge, FASTCHARGE, false);
        }

        //todo: handle if multiple car from manual selection received


        saveCarBtn.setOnClickListener(myOnClickListener);
        loadCarBtn.setOnClickListener(myOnClickListener);
    }

    private void findViewsById() {
        editTextBrand = findViewById(R.id.text_view_brand);
        editTextModel = findViewById(R.id.text_view_model);
        editTextModelYear = findViewById(R.id.text_view_model_year);
        editTextFastCharge = findViewById(R.id.text_view_fast_charge);
        editTextBattery = findViewById(R.id.text_view_battery);

        textViewAddCar = findViewById(R.id.text_view_add_car);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);

        spinner = findViewById(R.id.spinner_all_cars);

        spinnerBrand = findViewById(R.id.spinner_brand);
        spinnerModel = findViewById(R.id.spinner_model);
        spinnerModelYear = findViewById(R.id.spinner_model_year);
        spinnerFastCharge = findViewById(R.id.spinner_fast_charge);
        spinnerBattery = findViewById(R.id.spinner_battery);

        saveCarBtn = findViewById(R.id.btnSaveCar);
        loadCarBtn = findViewById(R.id.btnLoadCar);
    }

    private void getAllIntent(Intent intent) {
        allElbils = intent.getParcelableArrayListExtra("AllCarsList");
        textViewAddCar.setText(intent.getStringExtra("regNr"));


        //Exact elbil found
        elbil = intent.getParcelableExtra("Elbil");
        if (elbil != null) {
            elbilFound = true;
        } else System.out.println("getParcelable Elbil not received");


        //Missing Elbil fields
        found = intent.getBooleanArrayExtra("Missing");

        //Found Elbil fields
        fieldMap = (HashMap<String, String>) intent.getSerializableExtra("FieldMap");
        if (fieldMap != null) {
            regNr = fieldMap.get("regNr");
            if (regNr != null) textViewAddCar.setText(regNr);
            exactModelYear = fieldMap.get("exactModelYear");
            if (exactModelYear == null) exactModelYear = "";
        }
        elbils = getIntent().getParcelableArrayListExtra("CarList");

        //If "Car Info" selected from Main Activity
        carInfo = intent.getBooleanExtra("CarInfo", false);
        if (carInfo) {
            saveCarBtn.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView2.setText("Tilbake");
            imageView2.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        }

        manualSelection = intent.getBooleanExtra("manualSelection", false);
    }


    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSaveCar:
                    if (elbilFound) {
                        mCarList.add(elbil);

                        saveCar();
                        } else {

                        //TODO: SAVE CAR WITH SELECTED SPINNER VALUES
                        // validate if "UKJENT" & inform user

                        mCarList = searchCar();
                        if (mCarList.size() == 1) saveCar();

                        if (selectedFastCharge.equals(getString(R.string.unknown))) {
                            editTextFastCharge.setTextColor(Color.RED);

                            ((TextView) spinnerFastCharge.getChildAt(0)).setError("Message");
                            ((TextView) spinnerFastCharge.getChildAt(0)).setText("");
                        }

                        if (selectedBattery.equals(getString(R.string.unknown))) {
                            editTextBattery.setTextColor(Color.RED);

                            ((TextView) spinnerBattery.getChildAt(0)).setError("Message");
                            ((TextView) spinnerBattery.getChildAt(0)).setText("");
                        }

                    }
                    break;
                case R.id.btnLoadCar:
                    if (!carInfo && !manualSelection) {
                        Intent intent = new Intent(CarInfoActivity.this, CarSelectionActivity.class);
                        intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allElbils));
                        startActivityDialogBox(0, intent);
                    } else finish();
                    break;
                default:
            }
        }
    };

    private AdapterView.OnItemSelectedListener myOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            switch (adapterView.getId()) {
                case R.id.spinner_brand:
                    selectedBrand = spinnerBrand.getSelectedItem().toString();
                    if (!selectedBrand.equals(getString(R.string.unknown)))
                        editTextBrand.setTextColor(Color.BLACK);
                    break;
                case R.id.spinner_model:
                    selectedModel = spinnerModel.getSelectedItem().toString();
                    if (!selectedModel.equals(getString(R.string.unknown)))
                        editTextModel.setTextColor(Color.BLACK);
                    break;
                case R.id.spinner_model_year:
                    selectedModelYear = spinnerModelYear.getSelectedItem().toString();
                    if (!selectedModelYear.equals(getString(R.string.unknown)))
                        editTextModelYear.setTextColor(Color.BLACK);
                    break;
                case R.id.spinner_fast_charge:
                    selectedFastCharge = spinnerFastCharge.getSelectedItem().toString();
                    editTextFastCharge.setTextColor(Color.BLACK);
                    if (selectedFastCharge.equals(getString(R.string.unknown))) {
                        editTextFastCharge.setTextColor(Color.RED);
                        ((TextView) spinnerBattery.getChildAt(0)).setTextColor(Color.RED);
                    }

                    break;
                case R.id.spinner_battery:
                    selectedBattery = spinnerBattery.getSelectedItem().toString();
                    editTextBattery.setTextColor(Color.BLACK);
                    if (selectedBattery.equals(getString(R.string.unknown))) {
                        editTextBattery.setTextColor(Color.RED);
                        ((TextView) spinnerBattery.getChildAt(0)).setTextColor(Color.RED);
                    }
                    break;
                default:
            }

            ((TextView) adapterView.getChildAt(0)).setTypeface(null, Typeface.BOLD);
            ((TextView) adapterView.getChildAt(0)).setTextSize(18);
            ((TextView) adapterView.getChildAt(0)).setEms(10);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void initCarSpinnerSelection(Spinner spinner, String dataField) {
        spinner.setOnItemSelectedListener(myOnItemSelectedListener);
        switch (dataField) {
            case BRAND:
                brands = new ArrayList<>();
                adapterBrand = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
                adapterBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterBrand);
                break;
            case MODEL:
                models = new ArrayList<>();
                adapterModel = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
                adapterModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterModel);
                break;
            case MODELYEAR:
                modelYears = new ArrayList<>();
                adapterModelYear = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modelYears);
                adapterModelYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterModelYear);
                break;
            case FASTCHARGE:
                fastCharges = new ArrayList<>();
                // spinnerSelection.initSpinnerList(spinnerCharges, fastCharges, FASTCHARGE);
                adapterFastCharge = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fastCharges);
                adapterFastCharge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterFastCharge);
                break;
            case BATTERY:
                batteries = new ArrayList<>();
                adapterBattery = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, batteries);
                adapterBattery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterBattery);
                break;
            default:
        }
    }

    private void setSpinnerSelection(Spinner spinner, String dataField, boolean foundField) {
        switch (dataField) {
            case BRAND:
                if (elbilFound) brands.add(elbil.getBrand());
                else if (foundField) brands.add(fieldMap.get(BRAND));
                adapterBrand.notifyDataSetChanged();
                break;
            case MODEL:
                if (elbilFound) models.add(elbil.getModel());
                else if (foundField) models.add(fieldMap.get(MODEL));
                adapterModel.notifyDataSetChanged();
                break;
            case MODELYEAR:
                if (elbilFound) modelYears.add(elbil.getModelYear());
                else if (!exactModelYear.isEmpty()) {
                    modelYears.add(fieldMap.get("exactModelYear"));
                    foundField = true;
                }
                adapterModelYear.notifyDataSetChanged();
                break;
            case FASTCHARGE:
                if (elbilFound) fastCharges.add(elbil.getFastCharge());
                else if (foundField) fastCharges.add(fieldMap.get(FASTCHARGE));
                adapterFastCharge.notifyDataSetChanged();
                break;
            case BATTERY:
                if (elbilFound) batteries.add(elbil.getBattery());
                else if (foundField) batteries.add(fieldMap.get(BATTERY));
                adapterBattery.notifyDataSetChanged();
                break;
        }

        clickableSelection(spinner, foundField);
    }

    private void clickableSelection(Spinner spinner, boolean foundField) {
        if (foundField) {
            spinner.setSelection(0, false);
            spinner.setBackgroundColor(ContextCompat.getColor(this, R.color.backGroundColor));
            spinner.setClickable(false);
        } else {
            filteredSelection(spinner);
        }
    }


    private void filteredSelection(Spinner spinner) {
        getSelectedSpinnerFiled();

        String modelYear = fieldMap.get(MODELYEAR);
        if (modelYear == null) modelYear = "";

        //todo: handle if model year not in database

        switch (spinner.getId()) {
            case R.id.spinner_brand:
                carFilteredList.filterFields(BRAND, selectedBrand, brands);
                break;
            case R.id.spinner_model:
                break;
            case R.id.spinner_fast_charge:
                fastCharges.clear();
                for (Elbil elbil : elbils) {
                    if (!modelYear.isEmpty()) {
                        if (selectedBrand.equals(elbil.getBrand())
                                && selectedModel.equals(elbil.getModel())
                                && modelYear.equals(elbil.getModelYear())
                                && !fastCharges.contains(elbil.getFastCharge())) {
                            fastCharges.add(elbil.getFastCharge());
                        }
                    } else {
                        if (selectedBrand.equals(elbil.getBrand())
                                && selectedModel.equals(elbil.getModel())
                                && !fastCharges.contains(elbil.getFastCharge())) {
                            fastCharges.add(elbil.getFastCharge());
                        }
                    }
                }
                if (fastCharges.size() == 1) clickableSelection(spinnerFastCharge, true);
                else {
                    fastCharges.add("UKJENT");
                    spinnerFastCharge.setSelection(fastCharges.size() - 1);
                }
                adapterFastCharge.notifyDataSetChanged();
                break;
            case R.id.spinner_battery:
                batteries.clear();
                for (Elbil elbil : elbils) {
                    if (!modelYear.isEmpty()) {
                        if (selectedBrand.equals(elbil.getBrand())
                                && selectedModel.equals(elbil.getModel())
                                && modelYear.equals(elbil.getModelYear())) {
                            batteries.add(elbil.getBattery());
                        }
                    } else {
                        if (selectedBrand.equals(elbil.getBrand())
                                && selectedModel.equals(elbil.getModel())
                                && !batteries.contains(elbil.getBattery())) {
                            batteries.add(elbil.getBattery());
                        }
                    }
                }
                if (batteries.size() == 1) clickableSelection(spinnerBattery, true);
                else {
                    batteries.add("UKJENT");
                    spinnerBattery.setSelection(batteries.size() - 1);
                }
                adapterBattery.notifyDataSetChanged();
                break;
        }
    }


    private void getSelectedSpinnerFiled() {
        if (spinnerBrand.getSelectedItem() != null)
            selectedBrand = spinnerBrand.getSelectedItem().toString();
        else selectedBrand = "";

        if (spinnerModel.getSelectedItem() != null)
            selectedModel = spinnerModel.getSelectedItem().toString();
        else selectedModel = "";

        if (spinnerModelYear.getSelectedItem() != null)
            selectedModelYear = spinnerModelYear.getSelectedItem().toString();
        else selectedModelYear = "";

        if (spinnerBattery.getSelectedItem() != null)
            selectedBattery = spinnerBattery.getSelectedItem().toString();
        else selectedBattery = "";

        if (spinnerFastCharge.getSelectedItem() != null)
            selectedFastCharge = spinnerFastCharge.getSelectedItem().toString();
        else selectedFastCharge = "";
    }


    private ArrayList<Elbil> searchCar() {
        mCarList = new ArrayList<>();
        //todo: get allCarsList from CAR INFO!!!
        String modelYear = fieldMap.get(MODELYEAR);
        if (modelYear == null) modelYear = "";
        for (Elbil elbil : elbils) {
            if (!modelYear.isEmpty()) {
                if (selectedBrand.equals(elbil.getBrand())
                        && selectedModel.equals(elbil.getModel())
                        && modelYear.equals(elbil.getModelYear())
                        && selectedBattery.equals(elbil.getBattery())) {
                    mCarList.add(elbil);
                }
            } else {
                if (selectedBrand.equals(elbil.getBrand())
                        && selectedModel.equals(elbil.getModel())
                        && selectedBattery.equals(elbil.getBattery())) {
                    mCarList.add(elbil);
                }
            }
        }
        return mCarList;
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
        Gson gson = new Gson();
        //to contain ArrayList as Json form
        mCarList.addAll(mCarListAll);
        String json = gson.toJson(mCarList);
        editor.putString("car list", json);
        editor.apply();

        startActivity(new Intent(this, MainActivity.class));
    }

    private void loadCar() {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        mCarListAll = sharedCarPreferences.loadCars(sharedPreferences);
      }


    private void getCarAttributes(Elbil elbil, boolean elbilFound) {

        if (elbilFound) {
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
            else {
                battery2 = "UKJENT";
                fastCharge2 = "UKJENT";
            }


            fastCharge = "[IKKE FUNNET]";


            //todo: lag popup dialog ==> "Vi mangler noen opplysninger om bilen din, vennligst velg blant de opplysningen som passer din bil.." ==> "OK?"
            //todo: lag popup dialog ==> "VIL DU PRØVE MED ET ANNET REG NR ELLER VELGE BILEN DIN MANUELT? "
        }

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


    private void startActivityDialogBox(int identifier, Intent intent) {
        String title, msg, yesBtn, noBtn;

        switch (identifier) {
            case 0:
                title = "Ikke din bil?";
                msg = "Vil du søke etter et annet registreringsnummer, eller velge bilen din manuelt?";
                yesBtn = "Velg manulet";
                noBtn = "Søk igjen";
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

        dialogBox.setIntent(intent);
        dialogBox.createDialogBox();
    }

}
