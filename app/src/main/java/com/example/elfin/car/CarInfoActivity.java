package com.example.elfin.car;

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
import android.widget.Toast;

import com.example.elfin.MainActivity;
import com.example.elfin.R;
import com.example.elfin.Utils.DialogBox;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
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
    // private EditText editTextFastCharge, editTextBattery;
    private TextView textView, textView2, textViewAddCar;
    private TextView editTextBrand, editTextModel, editTextModelYear, editTextFastCharge, editTextBattery;
    private TextView editTextBrand2, editTextModel2, editTextModelYear2, editTextFastCharge2, editTextBattery2;
    private Spinner spinner;


    private ArrayAdapter<String> adapterBrand, adapterModel, adapterModelYear, adapterFastCharge, adapterBattery;
    private List<String> brands, models, modelYears, fastCharges, batteries;
    private Spinner spinnerBrand, spinnerModel, spinnerModelYear, spinnerFastCharge, spinnerBattery;
    private String selectedBrand, selectedModel, selectedModelYear, selectedFastCharge, selectedBattery,
            regNr, exactModelYear;

    private ArrayAdapter adapter;
    private Elbil elbil;

    private boolean elbilFound, carInfo;

    private SharedPreferences sharedPreferences;
    private SharedCarPreferences sharedCarPreferences;

    private CarFilteredList carFilteredList;
    private List<Elbil> filteredElbils;

    private DialogBox dialogBox, dialogBox1, dialogBox2, dialogBox3, dialogBox4;

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
        } else if (!carInfo) {
            setSpinnerSelection(spinnerBrand, BRAND, found[0]);
            setSpinnerSelection(spinnerModel, MODEL, found[1]);
            setSpinnerSelection(spinnerModelYear, MODELYEAR, found[2]);
            setSpinnerSelection(spinnerBattery, BATTERY, found[3]);
            setSpinnerSelection(spinnerFastCharge, FASTCHARGE, false);
        }


        saveCarBtn.setOnClickListener(myOnClickListener);
        loadCarBtn.setOnClickListener(myOnClickListener);
    }


    private void findViewsById() {
        editTextBrand = findViewById(R.id.text_view_brand);
        editTextModel = findViewById(R.id.text_view_model);
        editTextModelYear = findViewById(R.id.text_view_model_year);
        editTextFastCharge = findViewById(R.id.text_view_fast_charge);
        editTextBattery = findViewById(R.id.text_view_battery);

        // editTextBrand2 = findViewById(R.id.text_view_brand2);
        // editTextModel2 = findViewById(R.id.text_view_model2);
        // editTextModelYear2 = findViewById(R.id.text_view_model_year2);
        // editTextFastCharge2 = findViewById(R.id.text_view_fast_charge2);
        // editTextBattery2 = findViewById(R.id.text_view_battery2);

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
        //AllCarsList
        allElbils = intent.getParcelableArrayListExtra("AllCarsList");
        System.out.println("#####################################################################");
        if (allElbils != null)
            System.out.println("(CAR INFO ACTIVITY) ALL CAR LIST SIZE: " + allElbils.size());
        else System.out.println("(CAR INFO ACTIVITY) ALL CAR LIST SIZE: NULL");
        System.out.println("#####################################################################");


        //Exact elbil found
        elbil = intent.getParcelableExtra("Elbil");
        if (elbil != null) {
            elbilFound = true;
            System.out.println("getParcelable Elbil: " + elbil.toString() + " ; " + elbilFound);
            // getCarAttributes(elbil, elbilFound);
        } else System.out.println("getParcelable Elbil not received");
        System.out.println("ELBIL FOUND BOOLEAN: " + elbilFound);


        //Missing Elbil fields
        found = intent.getBooleanArrayExtra("Missing");
        if (found != null) {
            // Toast.makeText(this, "INTEN MISSING[]: " + Arrays.toString(found), Toast.LENGTH_LONG).show();
            System.out.println("INTEN MISSING[]: " + Arrays.toString(found));
        } else System.out.println("FOUND FIELDS[] NOT RECEIVED");


        //Found Elbil fields
        fieldMap = (HashMap<String, String>) intent.getSerializableExtra("FieldMap");
        if (fieldMap != null) {
            System.out.println("INTENT HASH MAP: " + fieldMap);
            System.out.println("INTENT EXACT FIELDS: \n\n"
                    + BRAND + " ; " + fieldMap.get(BRAND) + "\n\n"
                    + MODEL + " ; " + fieldMap.get(MODEL) + "\n\n"
                    + MODELYEAR + " ; " + fieldMap.get(MODELYEAR) + "\n\n"
                    + BATTERY + " ; " + fieldMap.get(BATTERY) + "\n\n"
            );
            regNr = fieldMap.get("regNr");
            if (regNr != null) textViewAddCar.setText(regNr);
            exactModelYear = fieldMap.get("exactModelYear");
            if (exactModelYear == null) exactModelYear = "";
        } else System.out.println("getParcelable fieldMap not received");

        //Matching elbils found
        elbils = getIntent().getParcelableArrayListExtra("CarList");
        if (elbils != null && elbils.size() > 0) {
            System.out.println("ELBILS RECEIVED: " + elbils.get(0).toString());
            // for (Elbil elbil : elbils) System.out.println(elbil.getModel());
        } else System.out.println("NO ELBILS RECEIVED!");


        //If "Car Info" selected from Main Activity
        carInfo = intent.getBooleanExtra("CarInfo", false);
        if (carInfo) {
            saveCarBtn.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView2.setText("Tilbake");
            imageView2.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        }
    }


    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSaveCar:
                    // Toast.makeText(CarInfoActivity.this, "LEGG TIL BIL!", Toast.LENGTH_SHORT).show();

                    if (elbilFound) {
                        mCarList.add(elbil);

                        //  dialogBox1.createDialogBox();

                        saveCar();
                        // clearAttributes();
                    } else {

                        // dialogBox3.createDialogBox();

                        System.out.println("SELECTED ELBIL: \n" +
                                "\nSELECTED BRAND: " + selectedBrand +
                                "\nSELECTED MODEL: " + selectedModel +
                                "\nSELECTED MODEL YEAR: " + selectedModelYear +
                                "\nSELECTED FAST CHARGE: " + selectedFastCharge +
                                "\nSELECTED BATTERY: " + selectedBattery);

                        //TODO: SAVE CAR WITH SELECTED SPINNER VALUES
                        // validate if "UKJENT" & inform user

                        //  Elbil selectedElbil;
                        mCarList = searchCar();
                        System.out.println("SEARCHED CAR LIST SIZE: " + mCarList.size());
                        if (mCarList.size() == 1) saveCar();

                        if (selectedFastCharge.equals(getString(R.string.unknown))) {
                            editTextFastCharge.setTextColor(Color.RED);

                            ((TextView) spinnerFastCharge.getChildAt(0)).setError("Message");
                            ((TextView) spinnerFastCharge.getChildAt(0)).setText("");
                            // ContextCompat.getColor(CarInfoActivity.this, R.color.backGroundColor); //make orange error color
                        }

                        if (selectedBattery.equals(getString(R.string.unknown))) {
                            editTextBattery.setTextColor(Color.RED);

                            ((TextView) spinnerBattery.getChildAt(0)).setError("Message");
                            ((TextView) spinnerBattery.getChildAt(0)).setText("");
                            // ContextCompat.getColor(CarInfoActivity.this, R.color.backGroundColor); //make orange error color
                        }

                        //errorText.setError("anything here, just to add the icon");
                        //  errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        //  errorText.setText("my actual error text");//changes the selected item text to this
                    }
                    break;
                case R.id.btnLoadCar:
                    if (!carInfo) {
                        Intent intent = new Intent(CarInfoActivity.this, CarSelectionActivity.class);
                        intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allElbils));
                        startActivityDialogBox(0, intent);
                    } else finish();
                    break;
                default:
                    Toast.makeText(CarInfoActivity.this, "CLICKABLE ID NOT FOUND..", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private AdapterView.OnItemSelectedListener myOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            // ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(CarInfoActivity.this, R.color.backGroundColor));
            switch (adapterView.getId()) {
                case R.id.spinner_brand:
                    selectedBrand = spinnerBrand.getSelectedItem().toString();
                    System.out.println("SELECTED BRAND: " + selectedBrand);
                    // clickableSelection(spinnerBrand, found[0]);
                    if (!selectedBrand.equals(getString(R.string.unknown)))
                        editTextBrand.setTextColor(Color.BLACK);
                    break;
                case R.id.spinner_model:
                    selectedModel = spinnerModel.getSelectedItem().toString();
                    System.out.println("SELECTED MODEL: " + selectedModel);
                    if (!selectedModel.equals(getString(R.string.unknown)))
                        editTextModel.setTextColor(Color.BLACK);
                    break;
                case R.id.spinner_model_year:
                    selectedModelYear = spinnerModelYear.getSelectedItem().toString();
                    System.out.println("SELECTED MODEL YEAR: " + selectedModelYear);
                    if (!selectedModelYear.equals(getString(R.string.unknown)))
                        editTextModelYear.setTextColor(Color.BLACK);
                    break;
                case R.id.spinner_fast_charge:
                    selectedFastCharge = spinnerFastCharge.getSelectedItem().toString();
                    System.out.println("SELECTED FAST CHARGE: " + selectedFastCharge);
                    if (!selectedFastCharge.equals(getString(R.string.unknown)))
                        editTextFastCharge.setTextColor(Color.BLACK);

                    /*
                    if (selectedFastCharge.equals("UKJENT")) {
                        ((TextView) adapterView.getChildAt(0)).setError("ERROR MESSAGE...");
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                      //  ((TextView) adapterView.getChildAt(0)).setText("VELG LADE TYPE!");
                      //  errorText.setError("anything here, just to add the icon");
                      //  errorText.setTextColor(Color.RED);//just to highlight that this is an error
                      //  errorText.setText("my actual error text");//changes the selected item text to this
                    }
                    */

                    // filteredSelection(spinnerBattery);
                    break;
                case R.id.spinner_battery:
                    selectedBattery = spinnerBattery.getSelectedItem().toString();
                    System.out.println("SELECTED BATTERY: " + selectedBattery);
                    if (!selectedBattery.equals(getString(R.string.unknown)))
                        editTextBattery.setTextColor(Color.BLACK);
                    /*
                    if (selectedBattery.equals("UKJENT")) {
                        ((TextView) adapterView.getChildAt(0)).setError("ERROR MESSAGE...");
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.RED);
                        ((TextView) adapterView.getChildAt(0)).setText("");
                    }
                     */
                    break;
                default:
                    Toast.makeText(adapterView.getContext(), "NO SUCH SPINNER LISTENER..", Toast.LENGTH_SHORT).show();
            }

            ((TextView) adapterView.getChildAt(0)).setTypeface(null, Typeface.BOLD);
            ((TextView) adapterView.getChildAt(0)).setTextSize(18);
            ((TextView) adapterView.getChildAt(0)).setEms(10);
            // ((TextView) adapterView.getChildAt(0)).
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
                // if (found) {
                // modelYears.add(fieldMap.get(MODELYEAR));
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
                System.out.println("NO SUCH SPINNER..");
        }


        // clickableSelection(spinner, found);
    }

    private void setSpinnerSelection(Spinner spinner, String dataField, boolean foundField) {
        switch (dataField) {
            case BRAND:
                if (elbilFound) brands.add(elbil.getBrand());
                else if (foundField) brands.add(fieldMap.get(BRAND));
                // clickableSelection(spinnerBrand, true);
                adapterBrand.notifyDataSetChanged();
                break;
            case MODEL:
                if (elbilFound) models.add(elbil.getModel());
                else if (foundField) models.add(fieldMap.get(MODEL));
                // clickableSelection(spinnerModel, true);
                adapterModel.notifyDataSetChanged();
                break;
            case MODELYEAR:
                if (elbilFound) modelYears.add(elbil.getModelYear());
                else if (!exactModelYear.isEmpty()) {
                    modelYears.add(fieldMap.get("exactModelYear"));
                    // clickableSelection(spinnerModelYear, true);
                    foundField = true;
                }
                adapterModelYear.notifyDataSetChanged();
                break;
            case FASTCHARGE:
                if (elbilFound) fastCharges.add(elbil.getFastCharge());
                else if (foundField) fastCharges.add(fieldMap.get(FASTCHARGE));
                // clickableSelection(spinnerFastCharge, true);
                adapterFastCharge.notifyDataSetChanged();
                break;
            case BATTERY:
                if (elbilFound) batteries.add(elbil.getBattery());
                else if (foundField) batteries.add(fieldMap.get(BATTERY));
                // clickableSelection(spinnerBattery, true);
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
        //  carFilteredList.filteredCars(filteredElbils);
        filteredElbils = new ArrayList<>();
        List<String> fields = new ArrayList<>();

        String modelYear = fieldMap.get(MODELYEAR);
        if (modelYear == null) modelYear = "";

        //todo: handle if model year not in database

        switch (spinner.getId()) {
            case R.id.spinner_brand:
                // String selectedBrand = spinnerBrand.getSelectedItem().toString();
                carFilteredList.filterFields(BRAND, selectedBrand, brands);
                break;
            case R.id.spinner_model:
                //selectedBrand
                // String selectedModel = spinnerModel.getSelectedItem().toString();
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
                        // && selectedModelYear.equals(elbil.getModelYear())
                        // && selectedFastCharge.equals(elbil.getFastCharge())
                        && selectedBattery.equals(elbil.getBattery())) {
                    mCarList.add(elbil);
                }
            } else {
                if (selectedBrand.equals(elbil.getBrand())
                        && selectedModel.equals(elbil.getModel())
                        // && modelYear.equals(elbil.getModelYear())
                        // && selectedModelYear.equals(elbil.getModelYear())
                        // && selectedFastCharge.equals(elbil.getFastCharge())
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
        //editor.clear(); //clear shared preferences
        Gson gson = new Gson();
        //to contain ArrayList as Json form
        mCarList.addAll(mCarListAll);
        String json = gson.toJson(mCarList);
        editor.putString("car list", json);
        editor.apply();

        // Toast.makeText(this, "CAR SAVED!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void loadCar() {
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        mCarListAll = sharedCarPreferences.loadCars(sharedPreferences);
        System.out.println("SHARED CAR PREFERENCES LOAD SIZE: " + mCarListAll.size());
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
            else {
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



        /*
        editTextBrand2.setText(brand2);
        editTextModel2.setText(model2);
        editTextModelYear2.setText(modelYear2);
        editTextFastCharge2.setText(fastCharge2);
        editTextBattery2.setText(battery2);
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

        System.out.println("<SHOWING DIALOG BOX>");
        dialogBox.setIntent(intent);
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
