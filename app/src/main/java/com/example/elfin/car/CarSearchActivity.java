package com.example.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarSearchActivity extends AppCompatActivity {

    private static final String TAG = "CarSearchActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private String brand, model, modelYear, battery;

    private Elbil elbil;
    private List<Elbil> allCarsList = new ArrayList<>();
    private List<Elbil> mCarList = new ArrayList<>();

    private List<String> brands, models, modelYears, batteries, fastCharges;

    private FirestoreQuery firestoreQuery;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private EditText editTextSearchRegNr;
    private ImageButton searchRegNrBtn;
    private Button searchCarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_search);

        findViewsById();

        firestoreQuery = new FirestoreQuery(this, elbilReference);

        searchRegNrBtn.setOnClickListener(myOnClickListener);
        searchCarBtn.setOnClickListener(myOnClickListener);

        //Todo: comment out in activity_car_search.xml after all the cars have been added
        FloatingActionButton buttonAddCar = findViewById(R.id.button_add_car);
        buttonAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CarSearchActivity.this, NewCarActivity.class));
            }
        });
        // buttonAddCar.hide();
        System.out.println("ALL CARS LIST SIZE: " + allCarsList.size());
    }

    private void findViewsById() {
        editTextSearchRegNr = findViewById(R.id.edit_text_search_regNr);
        searchRegNrBtn = findViewById(R.id.image_button_search_icon);
        searchCarBtn = findViewById(R.id.button_search_car);
    }

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_button_search_icon:
                    initLists();
                    executeCarInfoApi();
                    break;
                case R.id.button_search_car:
//                    mCarList = searchCar();

                    if (allCarsList.size() > 0) {
                        Intent intent = new Intent(CarSearchActivity.this, CarSelectionActivity.class);
                        ArrayList<Elbil> elbils = new ArrayList<>(allCarsList);
                        intent.putParcelableArrayListExtra("CarList", elbils);
                        startActivity(intent);
                    } else System.out.println("ELBILS SIZE IS 0");

                    /*
                    if (mCarList.size() == 1) {
                        Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
                        intent.putExtra("Elbil", mCarList.get(0));
                        startActivity(intent);
                    } else {
                        initSpinner(BATTERY, spinnerBatteries);
                        spinnerSelection.filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
                        adapterBattery.notifyDataSetChanged();

                        // initSpinner(BATTERY, spinnerBatteries);
                        // initSpinner(FASTCHARGE, spinnerCharges);
                    }
                    */


                    /*
                    initSpinner(BATTERY, spinnerBatteries);
                    spinnerSelection.filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
                    adapterBattery.notifyDataSetChanged();
                     */

                    //  Toast.makeText(AddCarActivity.this, "LIST SIZE: " + allCarsList.size(), Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(CarSearchActivity.this, "CLICKABLE ID NOT FOUND..", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void executeCarInfoApi() {
        CarInfoAPI carInfoAPI = new CarInfoAPI();
        carInfoAPI.setCarSearchActivity(CarSearchActivity.this);
        //todo: validate user input ==> disable input space
        String regNr = editTextSearchRegNr.getText().toString();
        //  Toast.makeText(CarSearchActivity.this, "regNr: " + regNr.trim(), Toast.LENGTH_SHORT).show();
        carInfoAPI.execute(regNr.trim());
    }

    public void loadApiInfo(Elbil elbil) {
        if (elbil == null) editTextSearchRegNr.setText("LOADING...");
        else {
            //  editTextSearchRegNr.setText(elbil.getModel() + " : " + elbil.getModelYear());
            Toast.makeText(this, elbil.getModel() + " ; " + elbil.getModelYear(), Toast.LENGTH_LONG).show();
            //searchFirestoreData(elbil.getModel().toLowerCase());
            //if (elbil.getModel().isEmpty() && elbil.getModelYear().isEmpty()) System.out.println("EMPTY MODEL");

            String model = "";
            if (elbil.getModel() != null) model = elbil.getModel().toLowerCase();

           // String modelYear = "";
            if (elbil.getModel() != null) modelYear = elbil.getModelYear();


            if (model.isEmpty()) {
                System.out.println("MODEL RESPONSE IS EMPTY");
                //todo: Request Manual SELECTION (EXCLUDE modelYEAR if not null)
            } else {
                String[] response = model.split("\\W+"); //The \\W+ will match all non-alphabetic characters occurring one or more times.
                filteredCarSearch(model, response);
            }




           // if (!model.isEmpty() && !modelYear.isEmpty())
               // firestoreQuery.compoundFirestoreQuery(model.toLowerCase(), modelYear.toLowerCase());
           // Toast.makeText(this, "FIRESTORE SIZE: " + mCarList.size(), Toast.LENGTH_SHORT).show();
            //compoundFirestoreQuery(model.toLowerCase(), modelYear.toLowerCase());
            Toast.makeText(this, "model: " + model + " : " + modelYear, Toast.LENGTH_SHORT).show();


        }

    }

    public void handleFirestoreQuery(List<Elbil> mElbilList) {
        Toast.makeText(this, "FIRESTORE CAR LIST SIZE: " + mElbilList.size(), Toast.LENGTH_LONG).show();
        if (mElbilList.size() == 1) {
            Intent intent = new Intent(CarSearchActivity.this, CarInfoActivity.class);
            intent.putExtra("Elbil", mElbilList.get(0));
            startActivity(intent);
        } else {
            // carCheckBox.setChecked(true);
            //todo: sjekke opp mot lister og la bruker velge riktig model hvis flere finnes i databasen

        }
    }

    protected void filteredCarSearch(String dataField, String[] response) {
        // allElbilList = addCarActivity.getAllCars();
        //ArrayList<Elbil> filteredCars = new ArrayList<>();
        //  filteredList.clear();

        System.out.println("API MODEL RESPONSE: " + Arrays.toString(response) + "; LENGTH: " + response.length);

        //TODO: handle EK16553 // https://elbil.no/elbil/nissan-leaf/

        //todo: remove s1, s2, s3 if not needed
        String s1 = "";
        String s2 = "";
        String s3 = "";
        switchResponses(response, s1, s2, s3);
    }

    private void switchResponses(String[] response, String s1, String s2, String s3) {

        boolean brandFound = false;
        boolean modelFound = false;
        boolean batteryFound = false;

        boolean[] missing = new boolean[3]; //foundModel, foundBrand, foundBattery
        Arrays.fill(missing, Boolean.FALSE);

        System.out.println("MISSING: " + Arrays.toString(missing));

        brand = "";
        model = "";
        battery = "";

        //todo: declare outside or replace with lists brands, models, and batteries
        List<String> foundBrands = new ArrayList<>();
        List<String> foundModels = new ArrayList<>();
        List<String> foundBatteries = new ArrayList<>();

        switch (response.length) {
            case 1:
                for (Elbil elbil : allCarsList) {
                    iterateCars(BRAND, elbil, response[0], foundBrands, missing);
                    iterateCars(MODEL, elbil, response[0], foundModels, missing);
                   // iterateCars(BATTERY, elbil, response[0], foundBatteries, missing);
                }
                //  System.out.println(foundModels.size() + " FOUND MODELS: " + foundModels);
                //  System.out.println(foundBrands.size() + " FOUND BRANDS: " + foundBrands);
                //  System.out.println(foundBatteries.size() + " FOUND BATTERIES: " + foundBatteries);
                // System.out.println("MISSING: " + Arrays.toString(missing));
                break;
            case 2:
                for (Elbil elbil : allCarsList) {
                    iterateCars(BRAND, elbil, response[0], foundBrands, missing);
                    iterateCars(BRAND, elbil, response[1], foundBrands, missing);

                    iterateCars(MODEL, elbil, response[0], foundModels, missing);
                    iterateCars(MODEL, elbil, response[1], foundModels, missing);

                    iterateCars(BATTERY, elbil, response[0], foundBatteries, missing);
                    iterateCars(BATTERY, elbil, response[1], foundBatteries, missing);
                }
                break;
            case 3:
                // s1 = response[0]; s2 = response[1]; s3 = response[2];
                // System.out.println("S1 + S2 + s3: " + s1 + " + " + s2 + " + " + s3);
                for (Elbil elbil : allCarsList) {
                    iterateCars(BRAND, elbil, response[0], foundBrands, missing);
                    iterateCars(BRAND, elbil, response[1], foundBrands, missing);
                    iterateCars(BRAND, elbil, response[2], foundBrands, missing);

                    iterateCars(MODEL, elbil, response[0], foundModels, missing);
                    iterateCars(MODEL, elbil, response[1], foundModels, missing);
                    iterateCars(MODEL, elbil, response[2], foundModels, missing);

                    iterateCars(BATTERY, elbil, response[0], foundBatteries, missing);
                    iterateCars(BATTERY, elbil, response[1], foundBatteries, missing);
                    iterateCars(BATTERY, elbil, response[2], foundBatteries, missing);
                }
                // brands = new ArrayList<>();
                // filteredCarList(BRAND, brands);
                // System.out.println("BRANDS: " + brands.size());
                break;
            default:
                System.out.println("RESPONSES: " + response.length);
                // System.out.println("S1 + S2 + s3: " + s1 + " + " + s2 + " + " + s3);
        }
        System.out.println("MISSING: " + Arrays.toString(missing));
        System.out.println("BRAND: " + brand);
        System.out.println("MODEL: " + model);
        System.out.println("BATTERY: " + battery);

        determineMissing(missing);

        matchingCarsFound(BRAND, foundBrands);
        matchingCarsFound(MODEL, foundModels);
        matchingCarsFound(BATTERY, foundBatteries);

        /*
        if (missing[0]) System.out.println("FOUND_BRAND == TRUE");
        else System.out.println("FOUND_BRAND == FALSE");

        if (missing[1]) System.out.println("FOUND_MODEL == TRUE");
        else System.out.println("FOUND_MODEL == FALSE");

        if (missing[2]) System.out.println("FOUND_BATTERY == TRUE");
        else System.out.println("FOUND_BATTERY == FALSE");
        */
    }

    private void iterateCars(String dataField, Elbil elbil, String response,
                             List<String> foundResponse, boolean[] missing) {
        switch (dataField) {
            case BRAND:
                if (elbil.getBrand().contains(response)) {
                    System.out.println("FOUND BRAND: " + elbil.getBrand() + " CONTAINS " + response);
                    foundResponse.add(elbil.getBrand());
                    if (elbil.getBrand().equals(response) && elbil.getBrand().length() == response.length()) {
                        System.out.println("BRAND " + elbil.getBrand() + " EQUALS " + response +
                                " ; LENGTH: " + elbil.getBrand().length() + " == " + response.length());
                        brand = response;
                        missing[0] = true;
                    }
                }
                break;
            case MODEL:
                if (elbil.getModel().contains(response)) {
                    System.out.println("FOUND MODEL: " + elbil.getModel() + " CONTAINS " + response);
                    foundResponse.add(elbil.getModel());
                    if (elbil.getModel().equals(response) && elbil.getModel().length() == response.length()) {
                        System.out.println("MODEL " + elbil.getModel() + " EQUALS " + response +
                                " ; LENGTH: " + elbil.getModel().length() + " == " + response.length());
                        model = response;
                        missing[1] = true;
                    }
                }
                break;
            case BATTERY:
                String r = response.replace("kwh", "");
                if (elbil.getBattery().contains(r)) {
                    System.out.println("FOUND BATTERY: " + elbil.getBattery() + " CONTAINS " + response);
                    foundResponse.add(elbil.getBattery());
                    if (elbil.getBattery().equals(r)) {
                        System.out.println("BATTERY " + elbil.getBrand() + " EQUALS " + response);
                        battery = response;
                        missing[2] = true;
                    }
                }
                break;
            default:
                System.out.println("NO MATCH...");
        }
    }

    private void matchingCarsFound(String dataField, List<String> foundMatches) {
        switch (dataField) {
            case BRAND:
                if (!brand.isEmpty()) {
                    System.out.println("FOUND EXACT BRAND: " + brand);      //exact brand found
                    //todo: return found brand
                } else if (!foundMatches.isEmpty()) {
                    System.out.println("FOUND MATCHING BRANDS: " + foundMatches);    //matching models found
                    //todo: return list of possible brands for selection
                } else System.out.println("NO MATCHING BRANDS FOUND...");
                break;
            case MODEL:
                if (!model.isEmpty()) {
                    //todo: return found model
                    System.out.println("FOUND EXACT MODEL: " + model);    //exact model found
                } else if (!foundMatches.isEmpty()) {
                    //todo: return list of possible models for selection
                    System.out.println("FOUND MATCHING MODELS: " + foundMatches);    //matching models found
                } else System.out.println("NO MATCHING MODELS FOUND...");
                break;
            case BATTERY:
                if (!battery.isEmpty()) {
                    //todo: return found brand
                    System.out.println("FOUND EXACT BATTERY: " + battery);  //exact battery found
                } else if (!foundMatches.isEmpty()) {
                    //todo: return list of possible brands for selection
                    System.out.println("FOUND MATCHING BATTERIES: " + foundMatches); //matching batteries found
                } else System.out.println("NO MATCHING BATTERIES FOUND...");
                break;
            default:
                System.out.println("NO SUCH DATA FIELD TO MATCH");
        }
    }

    private void determineMissing(boolean[] missing) {
        System.out.println("<FROM DETERMINE MISSING>");
        if (missing[0] && missing[1] && missing[2])
            System.out.println("(FOUND_BRAND && FOUND_MODEL && FOUND_BATTERY) == TRUE");
        else if (missing[0] && missing[1]) System.out.println("(FOUND_BRAND && FOUND_MODEL) == TRUE");
        else if (missing[0] && missing[2]) System.out.println("(FOUND_BRAND && FOUND_BATTERY) == TRUE");
        else if (missing[1] && missing[2]) System.out.println("(FOUND_MODEL && FOUND_BATTERY) == TRUE");
        else if (missing[0]) System.out.println("FOUND_BRAND == TRUE");
        else if (missing[1]) {
            System.out.println("FOUND_MODEL == TRUE");

            System.out.println("MODEL: " + model + "; MODEL YEAR: " + modelYear);
            Toast.makeText(this, "EXECUTING COMPOUND FIRESTORE QUERY..)", Toast.LENGTH_LONG).show();
            if (!modelYear.isEmpty()) firestoreQuery.compoundFirestoreQuery(model, modelYear);
        }
        else if (missing[2]) System.out.println("FOUND_BATTERY == TRUE");
        else System.out.println("ALL_MISSING == TRUE");
    }


    private void initLists() {
        if (brands == null) {
            brands = new ArrayList<>();
            // filteredCarList(BRAND, brands);
        }
        if (models == null) {
            models = new ArrayList<>();
            // filteredCarList(MODEL, models);
        }
        if (modelYears == null) {
            modelYears = new ArrayList<>();
            // filteredCarList(MODELYEAR, modelYears);
        }
        if (batteries == null) {
            batteries = new ArrayList<>();
            // filteredCarList(BATTERY, batteries);
        }
        if (fastCharges == null) fastCharges = new ArrayList<>();
    }

    public List<Elbil> getAllCars() {
        return this.allCarsList;
    }

    public void setAllCarsList(List<Elbil> allCarsList) {
        this.allCarsList = allCarsList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (allCarsList.size() == 0) firestoreQuery.getInitFirestoreData();
        //  getInitFirestoreData(elbilReference);
    }
}
