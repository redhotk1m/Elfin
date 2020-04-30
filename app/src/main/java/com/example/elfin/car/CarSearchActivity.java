package com.example.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Range;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.R;
import com.example.elfin.Utils.DialogBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class CarSearchActivity extends AppCompatActivity {

    private static final String TAG = "CarSearchActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private BitSet bitSet;
    private boolean[] found;
    private String brand, model, modelYear, battery;
    private String exactModelYear;
    private HashMap<String, String> foundHashMap;

    private Elbil mElbil;
    private List<Elbil> allCarsList = new ArrayList<>();
    private List<Elbil> mCarList = new ArrayList<>();

    private FirestoreQuery firestoreQuery;
    private Query query;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private EditText editTextSearchRegNr;
    private ImageButton searchRegNrBtn;
    private Button searchCarBtn;

    private CarFilteredList carFilteredList;

    private DialogBox dialogBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_search);
        //private method used to assign views by id
        findViewsById();

        //Custom Class to handle Fire Store queries and fetch data
        firestoreQuery = new FirestoreQuery(this, elbilReference);

        //Custom Class used to filter lists
        carFilteredList = new CarFilteredList();

        //Set custom myOnClickListener on buttons to handle all onClicks
        searchRegNrBtn.setOnClickListener(myOnClickListener);
        searchCarBtn.setOnClickListener(myOnClickListener);

        //Todo: comment out in activity_car_search.xml after all the cars have been added
        // or use .hide() method to hide buttonAddCar
        FloatingActionButton buttonAddCar = findViewById(R.id.button_add_car);
        buttonAddCar.setOnClickListener(myOnClickListener);
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
                    initFoundHashMapFields();
                    executeCarInfoApi();
                    break;
                case R.id.button_search_car:
                    initFoundHashMapFields();
                    if (!editTextSearchRegNr.getText().toString().isEmpty()) executeCarInfoApi();
                    else handleFirestoreQuery(allCarsList);
                    break;
                case R.id.button_add_car:
                    // startActivityDialogBox(1, null);
                    startActivity(new Intent(CarSearchActivity.this, NewCarActivity.class));
                    break;
                default:
                    Toast.makeText(CarSearchActivity.this, "CLICKABLE ID NOT FOUND..", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void initFoundHashMapFields() {
        initFields();
        if (foundHashMap == null) foundHashMap = new HashMap<>();
        foundHashMap.put(BRAND, brand);
        foundHashMap.put(MODEL, model);
        foundHashMap.put(MODELYEAR, modelYear);
        foundHashMap.put(BATTERY, battery);
        // foundHashMap.put(FASTCHARGE, fastCharge);
        foundHashMap.put("exactModelYear", exactModelYear);
        System.out.println("INIT FIELDS MAP: " + foundHashMap);

        if (found == null)
            found = new boolean[4]; //foundBrand, foundModel, foundModelYear, foundBattery
        //Sets foundBrand, foundModel, foundModelYear & foundBattery inside found array to be false
        Arrays.fill(found, Boolean.FALSE);
        System.out.println("INIT FOUND ARRAY: " + Arrays.toString(found));
    }

    private void executeCarInfoApi() {
        CarInfoAPI carInfoAPI = new CarInfoAPI();
        carInfoAPI.setCarSearchActivity(CarSearchActivity.this);
        //todo: validate regNr input from user
        String regNr = editTextSearchRegNr.getText().toString();
        foundHashMap.put("regNr", regNr.replaceAll("\\s", "").toLowerCase());
        carInfoAPI.execute(regNr.replaceAll("\\s", "").toLowerCase());
    }

    public void loadApiInfo(Elbil elbil) {
        checkAPIResponse(elbil);
    }

    private void checkAPIResponse(Elbil elbil) {
        System.out.println("API RESPONSES: " + elbil.getModel() + " ; " + elbil.getModelYear());
        String modelResponse = "";
        if (elbil.getModel() != null) modelResponse = elbil.getModel().toLowerCase();
        String modelYearResponse = "";
        if (elbil.getModelYear() != null) modelYearResponse = elbil.getModelYear();

        if (modelYearResponse.isEmpty())
            System.out.println("MODEL YEAR RESPONSE IS EMPTY: " + modelYearResponse);
        else checkFilteredCars(MODELYEAR, elbil, modelYearResponse);

        if (modelResponse.isEmpty())
            System.out.println("MODEL RESPONSE IS EMPTY: " + modelResponse);
        else checkFilteredCars("default", elbil, modelResponse.toLowerCase());

        //todo: handle if ModelYear is not found in amongst matching cars in database; exclude in the query if no match
        // ,or provide manual selection with all possible model years (i.e. 2010 - 2020) in selection

        //todo: check out regNr: EK23827

        System.out.println("CHECKED API RESPONSE ; NOW HANDLING FIRESTORE QUERY");
        System.out.println("FOUND FIELDS: " + Arrays.toString(found));
        System.out.println("FIELDS MAP: " + foundHashMap);

        determineFoundFields(found);
    }

    public void handleFirestoreQuery(List<Elbil> mElbilList) {
        // Toast.makeText(this, "FIRESTORE CAR LIST SIZE: " + mElbilList.size(), Toast.LENGTH_LONG).show();
        if (mElbilList.size() == 1) {
            System.out.println("#################################################################");
            System.out.println("EXACT MATCH FOUND: " + mElbilList.get(0).toString()
                    + "[ " + mElbilList.size() + " / " + allCarsList.size() + " ]");
            System.out.println("#################################################################");

            System.out.println("EXACT MODEL YEAR FROM API VS DATABASE: " + exactModelYear + " <==> " + allCarsList.get(0).getModelYear());
            // allCarsList.get(0).setModelYear(exactModelYear);

            Intent intent = new Intent(this, CarInfoActivity.class);
            intent.putExtra("Elbil", mElbilList.get(0));
            intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allCarsList));


            // dialogBox = new DialogBox(this, "title", "message", "yes", "no", 3);
            // dialogBox.setIntent(intent);
            // dialogBox.createDialogBox();

            startActivity(intent);
        } else if (mElbilList.size() == allCarsList.size() && allCarsList.size() != 0) {
            //todo: popup dialog: if "manual selection" ; else "try different regNr"
            System.out.println("#################################################################");
            System.out.println("ALL MATCHES FOUND: [ " + mElbilList.size() + " / " + allCarsList.size() + " ]");
            System.out.println("#################################################################");
            Intent intent = new Intent(this, CarSelectionActivity.class);
            intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allCarsList));
            // startActivity(intent);
            startActivityDialogBox(0, intent);
        } else if (mElbilList.size() > 1) {
            System.out.println("#################################################################");
            System.out.println("MATCHES FOUND: [ " + mElbilList.size() + " / " + allCarsList.size() + " ]");
            System.out.println("#################################################################");

            System.out.println("EXACT MODEL YEAR FROM API VS DATABASE: " + exactModelYear + " <==> " + allCarsList.get(0).getModelYear());
            // allCarsList.get(0).setModelYear(exactModelYear);

            Intent intent = new Intent(this, CarInfoActivity.class);
            intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allCarsList));
            intent.putParcelableArrayListExtra("CarList", new ArrayList<>(mElbilList));
            intent.putExtra("Missing", found);
            intent.putExtra("FieldMap", foundHashMap);
            // intent.putExtra("FoundFieldsMap", foundFieldsMap);
            startActivity(intent);
        } else {
            //todo: make sure all firestore cars have been fetched successfully beforehand
            System.out.println("#################################################################");
            System.out.println("NO FIRESTORE CARS FOUND: [ " + mElbilList.size() + " / " + allCarsList.size() + " ]");
            System.out.println("#################################################################");
            Toast.makeText(this, "[POPUP DIALOG]\n\nNOE GIKK GALT, PRØV IGJEN!", Toast.LENGTH_LONG).show();
            if (allCarsList.size() == 0) {
                firestoreQuery.getInitFirestoreData();
                System.out.println("FETCHING FIRESTORE CARS: " + allCarsList.size());
            } else {
                System.out.println("FIRESTORE CARS ALREADY FETCHED!");
                //todo: Handle if something goes wrong
            }
        }
    }

    private void iterateFilteredCars(String[] responses) {
        System.out.println("API MODEL RESPONSE: " + Arrays.toString(responses) + " ; LENGTH: " + responses.length
                + "\n [ API MODEL YEAR RESPONSE: " + modelYear + " ]");

        //bits used to keep track og the number of responses that have been found
        bitSet = new BitSet(responses.length);
        System.out.println("INIT BIT SET LENGTH: " + bitSet.length() + " [Size: " + bitSet.size() + "]");

        int count = 0;
        for (Elbil elbil : allCarsList) {
            System.out.println("ELBIL: " + elbil.toString()
                    + "\n ITERATION: [ " + ++count + " / " + allCarsList.size() + " ]"
                    + "\n BIT SET LENGTH: [ " + bitSet.length() + " / " + responses.length + " ]");
            for (String response : responses) {
                //check filtered car brands if brand not found
                if (!found[0]) {
                    System.out.println("(" + BRAND + ") CURRENT RESPONSE: " + response + " ; ITERATION [" + count + "]");
                    checkFilteredCars(BRAND, elbil, response);
                    if (found[0]) continue; //continue with the next response within responses[]
                    // else System.out.println("(" + BRAND + ") FILTERED EXACT FIELD ; " + found[0]);
                } else System.out.println("EXACT (" + BRAND + ") ALREADY FOUND ; " + found[0]);

                System.out.println("CURRENT BIT SET LENGTH: [ " + bitSet.length() + " / " + responses.length + " ]");

                //check filtered car models if model not found
                if (!found[1]) {
                    System.out.println("(" + MODEL + ") CURRENT RESPONSE: " + response + " ; ITERATION [" + count + "]");
                    checkFilteredCars(MODEL, elbil, response);
                    if (found[1]) continue; //continue with the next response within responses[]
                    else if (!found[0]) break; //if brand nor model found, break & iterate next car
                } else System.out.println("EXACT (" + MODEL + ") ALREADY FOUND ; " + found[1]);

                System.out.println("CURRENT BIT SET LENGTH: [ " + bitSet.length() + " / " + responses.length + " ]");


                //todo: check to find model year if elbil model and/or brand has been found
                if (!found[2] && (found[0] || found[1])) {
                    // checkModelYear();
                    if (found[0] && found[1]) System.out.println("CHECK ELBIL BRAND AND MODEL");
                    else if (found[0]) System.out.println("CHECK ELBIL BRAND FOR MODEL YEAR");
                    else if (found[1]) System.out.println("CHECK ELBIL MODEL FOR MODEL YEAR");
                } else System.out.println("EXACT (" + MODELYEAR + ") ALREADY FOUND ; " + found[0]);


                //check filtered car batteries if battery not found
                if (!found[3] && (found[0] || found[1])) {
                    System.out.println("(" + BATTERY + ") CURRENT RESPONSE: " + response + " ; ITERATION [" + count + "]");
                    checkFilteredCars(BATTERY, elbil, response);
                    if (found[3]) continue;
                    //  else System.out.println("(" + BATTERY + ") FILTERED EXACT FIELD ; " + found[3]);
                } else System.out.println("EXACT (" + BATTERY + ") ALREADY FOUND ; " + found[3]);

                System.out.println("CURRENT BIT SET LENGTH: [ " + bitSet.length() + " / " + responses.length + " ]");
            }
            if (bitSet.length() == responses.length) {
                System.out.println("RETURNING @ ITERATION: [ " + count + " / " + allCarsList.size() + " ]");
                System.out.println("FINISHED BIT SET LENGTH: [ " + bitSet.length() + " / " + responses.length + " ]");
                return;
            }
        }
    }

    private void checkFilteredCars(String dataField, Elbil elbil, String response) {
        switch (dataField) {
            case BRAND:
                // foundBrands = carFilteredList.filterFields(elbil.getBrand(), response, foundBrands);
                System.out.println("( " + BRAND + " ) FILTER FIELD: " + elbil.getBrand() + " ; " + response + " ; " + found[0]);
                String brandResponse = carFilteredList.filterExactMatch(elbil.getBrand(), response);
                if (response.equals(brandResponse)) {
                    setExactResponseFound(BRAND, brandResponse, found[0]);
                    System.out.println("( " + BRAND + " ) FILTERED EXACT FIELD: " + brand + " ; " + response + " ; " + found[0]);
                }
                break;
            case MODEL:
                System.out.println("( " + MODEL + " ) FILTER FIELD: " + elbil.getModel() + " ; " + response + " ; " + found[1]);
                String modelResponse = carFilteredList.filterExactMatch(elbil.getModel(), response);
                if (response.equals(modelResponse)) {
                    setExactResponseFound(MODEL, modelResponse, found[1]);
                    System.out.println("( " + MODEL + " ) FILTERED EXACT FIELD: " + model + " ; " + response + " ; " + found[1]);

                    //todo: make sure the found model is unique to each brand
                    //If BRAND NOT FOUND, SET BRAND AS FOUND MODEL'S BRAND
                    if (!found[0]) {
                        System.out.println(BRAND + " ; " + found[0]);
                        setExactResponseFound(BRAND, elbil.getBrand(), found[1]);
                        System.out.println("EXACT BRAND MUST EQUAL " + brand + " ; " + found[0]);
                    }

                    checkModelYear();
                } else
                    System.out.println("(" + MODEL + ") FILTERED EXACT FIELD ; " + found[1] + " ; BREAK");
                break;
            case MODELYEAR:
                if (response != null) {
                    modelYear = response;
                    exactModelYear = response;
                    if (!modelYear.isEmpty()) {
                        //todo: remove found[2] ==> checkModelYear()
                       // found[2] = true;
                        foundHashMap.put("exactModelYear", exactModelYear);
                        // foundHashMap.put(MODELYEAR, modelYear);
                    }


                    //todo: check model year response with cars in database
                    /*
                    String[] modelYearResponses = response.trim().split("-");
                    if (modelYearResponses.length == 1) {
                        if (modelYear.equals(modelYearResponses[0])) {
                            System.out.println(modelYear + " == " + modelYearResponses[0]);
                            found[2] = true;
                        } else if (modelYearResponses.length == 2) {
                            //todo: check if model year is between YEAR0 <--> YEAR1
                            System.out.println(modelYear + " IN BETWWEN " + modelYearResponses[0] + " - " + modelYearResponses[1]);
                            found[2] = true;
                        }
                    }
                    */
                }
                break;
            case BATTERY:
                //todo: check if ( elbil.getBrand.eqals(brand) && elbil.getModel.equals(model) )
                System.out.println("( " + BATTERY + " ) FILTER FIELD: " + elbil.getBattery() + " ; " + response + " ; " + found[3]);
                String batteryResponse = response.replace("kwh", "");
                batteryResponse = carFilteredList.filterExactMatch(elbil.getBattery(), batteryResponse);
                if (response.replace("kwh", "").equals(batteryResponse)) {
                    setExactResponseFound(BATTERY, batteryResponse, found[3]);
                    System.out.println("( " + BATTERY + " ) FILTERED EXACT FIELD: " + battery + " ; " + response + " ; " + found[3]);
                }
                break;
            default:
                if (response != null) {
                    String[] modelResponses = response.toLowerCase().split("\\s+");
                    iterateFilteredCars(modelResponses);
                } else System.out.println("NO SUCH DATA...");
        }
    }

    private void setExactResponseFound(String dataField, String foundResponse, boolean found) {
        setBitSetValue(bitSet, found);
        foundHashMap.put(dataField, foundResponse);
        switchFieldFound(dataField);
    }

    private void setBitSetValue(BitSet bitSet, boolean found) {
        int currentLength = bitSet.length();
        System.out.println("CURRENT BIT SET LENGTH: " + bitSet.length());
        if (bitSet.length() == 0 && !found) {
            bitSet.set(0, true);
            System.out.println("BIT SET LENGTH: " + currentLength + " ===> " + bitSet.length());
        } else if (bitSet.length() == 1 && !found) {
            bitSet.set(1, true);
            System.out.println("BIT SET LENGTH: " + currentLength + " ===> " + bitSet.length());
        } else if (bitSet.length() == 2 && !found) {
            bitSet.set(2, true);
            System.out.println("BIT SET LENGTH: " + currentLength + " ===> " + bitSet.length());
        } else if (bitSet.length() == 3 && !found) {
            bitSet.set(3, true);
            System.out.println("BIT SET LENGTH: " + currentLength + " ===> " + bitSet.length());
        } else {
            System.out.println(found + " ; BIT SET LENGTH: " + currentLength);
        }
    }

    private void determineFoundFields(boolean[] found) {
        System.out.println("<FROM DETERMINE MISSING>");
        //todo: check found[2] ==> foundModelYear
        // if (found[2]) query = query.whereEqualTo(MODELYEAR, modelYear);
        if (found[0] && found[1] && found[3]) {
            System.out.println("(FOUND_BRAND && FOUND_MODEL && FOUND_BATTERY) == TRUE ; "
                    + brand + " && " + model + " && " + battery + " [" + modelYear + "]");
            String modelResponse = BRAND + MODEL + BATTERY;
            query = firestoreQuery.makeCompoundQuery(elbilReference, modelResponse, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[0] && found[1]) {
            System.out.println("(FOUND_BRAND && FOUND_MODEL) == TRUE ; " + brand + " && " + model + " [" + modelYear + "]");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND + MODEL, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[0] && found[3]) {
            System.out.println("(FOUND_BRAND && FOUND_BATTERY) == TRUE ; " + brand + " && " + battery + " [" + modelYear + "]");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND + BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[1] && found[3]) {
            System.out.println("(FOUND_MODEL && FOUND_BATTERY) == TRUE ; " + model + " && " + battery + " [" + modelYear + "]");
            query = firestoreQuery.makeCompoundQuery(elbilReference, MODEL + BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[0]) {
            System.out.println("FOUND_BRAND == TRUE ; " + brand + " [" + modelYear + "]");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[1]) {
            System.out.println("FOUND_MODEL == TRUE ; " + model + " [" + modelYear + "]");
            query = firestoreQuery.makeCompoundQuery(elbilReference, MODEL, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[3]) {
            System.out.println("FOUND_BATTERY == TRUE ; " + battery + " [" + modelYear + "]");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else {
            System.out.println("ALL_MISSING == TRUE");
            handleFirestoreQuery(allCarsList);
            // firestoreQuery.executeCompoundQuery(elbilReference);
        }
    }

    private void checkModelYear() {
        // mCarList = new ArrayList<>();
        // String[] modelYearResponse;

        System.out.println("( " + MODELYEAR + " ) " + modelYear + " ; " + found[2]);

        int count = 0;
        for (Elbil elbil : allCarsList) {
            System.out.println("\n ITERATION: [ " + ++count + " / " + allCarsList.size() + " ]");
            if (model.equals(elbil.getModel()) && brand.equals(elbil.getBrand())) {
                System.out.println("(CHECK MODEL YEAR) ELBIL: " + elbil.toString());
                checkModelYearRange(elbil);
                System.out.println(modelYear + " ; " + elbil.getModelYear() + " ; " + found[2]);
            }
            if (found[2]) {
                System.out.println("RETURNING @ ITERATION: [ " + count + " / " + allCarsList.size() + " ]");
                return;
            }
        }

    }

    private void checkModelYearRange(Elbil elbil) {
        String[] modelYearResponse = elbil.getModelYear().split("-");
        int value = Integer.parseInt(modelYear);
        if (modelYearResponse.length == 1) {
            int min = Integer.parseInt(modelYearResponse[0]);
            if (value == min) {
                System.out.println(value + " == " + min);
                modelYear = elbil.getModelYear();
                foundHashMap.put(MODELYEAR, modelYear);
                found[2] = true;
            }
        } else if (modelYearResponse.length == 2) {
            int min = Integer.parseInt(modelYearResponse[0]);
            int max = Integer.parseInt(modelYearResponse[1]);
            if (min <= value && value <= max) {
                System.out.println(value + " in in between: " + min + " - " + max);
                modelYear = elbil.getModelYear();
                foundHashMap.put(MODELYEAR, modelYear);
                found[2] = true;
            }
        }
    }

    private void startActivityDialogBox(int identifier, Intent intent) {
        String title, msg, yesBtn, noBtn;

        switch (identifier) {
            case 0:
                title = "Ingen treff";
                msg = "Vi fant desverre ikke bilen i vårt register basert på registrerings nummert";
                yesBtn = "Velg bil manulet";
                noBtn = "Søk igjen";
                dialogBox = new DialogBox(this, title, msg, yesBtn, noBtn, 3);
                break;
            case 1:
                title = "NOE GIKK GALT, PRØV IGJEN!";
                msg = "Vi fant ikke bilen i vårt register basert registrerings nummert";
                yesBtn = "Velg manulet";
                noBtn = "Søk igjen";
                dialogBox = new DialogBox(this, title, msg, yesBtn, noBtn, 99);
                dialogBox.defaultDialog();
                return;
            // break;
        }

        System.out.println("<SHOWING DIALOG BOX>");
        dialogBox.setIntent(intent);
        // dialogBox.simpleDialogBox();
        dialogBox.createDialogBox();
    }


    private void initFields() {
        brand = "";
        model = "";
        modelYear = "";
        battery = "";
        // fastCharge = "";
    }

    private void switchFieldFound(String dataField) {
        switch (dataField) {
            case BRAND:
                brand = foundHashMap.get(BRAND);
                found[0] = true;
                System.out.println("(" + BRAND + "): " + brand + " EQUALS " + foundHashMap.get(BRAND) + " ; " + found[0]);
                break;
            case MODEL:
                model = foundHashMap.get(MODEL);
                found[1] = true;
                System.out.println("(" + MODEL + "): " + model + " EQUALS " + foundHashMap.get(MODEL) + " ; " + found[1]);
                break;
            case MODELYEAR:
                modelYear = foundHashMap.get(MODELYEAR);
                found[2] = true;
                System.out.println("(" + MODELYEAR + "): " + modelYear + " EQUALS " + foundHashMap.get(MODELYEAR) + " ; " + found[2]);
                break;
            case BATTERY:
                battery = foundHashMap.get(BATTERY);
                found[3] = true;
                System.out.println("(" + BATTERY + "): " + battery + " EQUALS " + foundHashMap.get(BATTERY) + " ; " + found[3]);
                break;
            default:
                System.out.println("NO SUCH FIELD TO BE FOUND: " + foundHashMap);
        }
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
        else {
            System.out.println("#####################################################################");
            System.out.println("(ON START) FIRE STORE CARS: " + allCarsList.size());
            System.out.println("#####################################################################");
        }
    }
}
