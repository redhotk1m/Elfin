package com.elfin.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.elfin.elfin.API.CarInfoAPI;
import com.elfin.elfin.R;
import com.elfin.elfin.Utils.DialogBox;
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

    private List<Elbil> allCarsList = new ArrayList<>();

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
        buttonAddCar.hide();
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
                    startActivity(new Intent(CarSearchActivity.this, NewCarActivity.class));
                    break;
                default:
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
        foundHashMap.put("exactModelYear", exactModelYear);

        if (found == null)
            found = new boolean[4]; //foundBrand, foundModel, foundModelYear, foundBattery
        //Sets foundBrand, foundModel, foundModelYear & foundBattery inside found array to be false
        Arrays.fill(found, Boolean.FALSE);
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
        String modelResponse = "";
        if (elbil.getModel() != null) modelResponse = elbil.getModel().toLowerCase();
        String modelYearResponse = "";
        if (elbil.getModelYear() != null) modelYearResponse = elbil.getModelYear();

        if (modelYearResponse.isEmpty())
            modelYearResponse = "";
        else checkFilteredCars(MODELYEAR, elbil, modelYearResponse);

        if (modelResponse.isEmpty())
            modelResponse = "";
        else checkFilteredCars("default", elbil, modelResponse.toLowerCase());

        //todo: handle if ModelYear is not found in amongst matching cars in database; exclude in the query if no match
        // ,or provide manual selection with all possible model years (i.e. 2010 - 2020) in selection

        //todo: check out regNr: EK23827

        determineFoundFields(found);
    }

    public void handleFirestoreQuery(List<Elbil> mElbilList) {
        if (mElbilList.size() == 1) {
            Intent intent = new Intent(this, CarInfoActivity.class);
            String regNr2 = editTextSearchRegNr.getText().toString();

            regNr2 = regNr2.replaceAll("\\s+", "");
            regNr2 = regNr2.toUpperCase();
            intent.putExtra("regNr", regNr2);
            intent.putExtra("Elbil", mElbilList.get(0));
            intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allCarsList));

            startActivity(intent);
        } else if (mElbilList.size() == allCarsList.size() && allCarsList.size() != 0) {
            //todo: popup dialog: if "manual selection" ; else "try different regNr"
            Intent intent = new Intent(this, CarSelectionActivity.class);
            intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allCarsList));
            startActivityDialogBox(0, intent);
        } else if (mElbilList.size() > 1) {

            Intent intent = new Intent(this, CarInfoActivity.class);
            intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allCarsList));
            intent.putParcelableArrayListExtra("CarList", new ArrayList<>(mElbilList));
            intent.putExtra("Missing", found);
            intent.putExtra("FieldMap", foundHashMap);
            startActivity(intent);
        } else {
            //todo: make sure all firestore cars have been fetched successfully beforehand
            if (allCarsList.size() == 0) {
                firestoreQuery.getInitFirestoreData();
            } else {
                //todo: Handle if something goes wrong
                Intent intent = new Intent(this, CarSelectionActivity.class);
                intent.putParcelableArrayListExtra("AllCarsList", new ArrayList<>(allCarsList));
                startActivityDialogBox(0, intent);
            }
        }
    }

    private void iterateFilteredCars(String[] responses) {
        //bits used to keep track og the number of responses that have been found
        bitSet = new BitSet(responses.length);

        for (Elbil elbil : allCarsList) {
            for (String response : responses) {
                //check filtered car brands if brand not found
                if (!found[0]) {
                    checkFilteredCars(BRAND, elbil, response);
                    if (found[0]) continue; //continue with the next response within responses[]
                }
                //check filtered car models if model not found
                if (!found[1]) {
                    checkFilteredCars(MODEL, elbil, response);
                    if (found[1]) continue; //continue with the next response within responses[]
                    else if (!found[0]) break; //if brand nor model found, break & iterate next car
                }
                //todo: check to find model year if elbil model and/or brand has been found

                //check filtered car batteries if battery not found
                if (!found[3] && (found[0] || found[1])) {
                    checkFilteredCars(BATTERY, elbil, response);
                    if (found[3]) continue;
                }
            }
            if (bitSet.length() == responses.length) {
                return;
            }
        }
    }

    private void checkFilteredCars(String dataField, Elbil elbil, String response) {
        switch (dataField) {
            case BRAND:
                String brandResponse = carFilteredList.filterExactMatch(elbil.getBrand(), response);
                if (response.equals(brandResponse)) {
                    setExactResponseFound(BRAND, brandResponse, found[0]);
                }
                break;
            case MODEL:
                String modelResponse = carFilteredList.filterExactMatch(elbil.getModel(), response);
                if (response.equals(modelResponse)) {
                    setExactResponseFound(MODEL, modelResponse, found[1]);
                    //todo: make sure the found model is unique to each brand
                    //If BRAND NOT FOUND, SET BRAND AS FOUND MODEL'S BRAND
                    if (!found[0]) {
                        setExactResponseFound(BRAND, elbil.getBrand(), found[1]);
                    }

                    checkModelYear();
                }break;
            case MODELYEAR:
                if (response != null) {
                    modelYear = response;
                    exactModelYear = response;
                    if (!modelYear.isEmpty()) {
                        //todo: remove found[2] ==> checkModelYear()
                        // found[2] = true;
                        foundHashMap.put("exactModelYear", exactModelYear);
                    }

                    //todo: check model year response with cars in database

                }
                break;
            case BATTERY:
                //todo: check if ( elbil.getBrand.eqals(brand) && elbil.getModel.equals(model) )
                // System.out.println("( " + BATTERY + " ) FILTER FIELD: " + elbil.getBattery() + " ; " + response + " ; " + found[3]);
                String batteryResponse = response.replace("kwh", "");
                batteryResponse = carFilteredList.filterExactMatch(elbil.getBattery(), batteryResponse);
                if (response.replace("kwh", "").equals(batteryResponse)) {
                    setExactResponseFound(BATTERY, batteryResponse, found[3]);
                 }
                break;
            default:
                if (response != null) {
                    String[] modelResponses = response.toLowerCase().split("\\s+");
                    iterateFilteredCars(modelResponses);
                }
        }
    }

    private void setExactResponseFound(String dataField, String foundResponse, boolean found) {
        setBitSetValue(bitSet, found);
        foundHashMap.put(dataField, foundResponse);
        switchFieldFound(dataField);
    }

    private void setBitSetValue(BitSet bitSet, boolean found) {
        int currentLength = bitSet.length();
        if (bitSet.length() == 0 && !found) {
            bitSet.set(0, true);
        } else if (bitSet.length() == 1 && !found) {
            bitSet.set(1, true);
        } else if (bitSet.length() == 2 && !found) {
            bitSet.set(2, true);
        } else if (bitSet.length() == 3 && !found) {
            bitSet.set(3, true);
        }
    }

    private void determineFoundFields(boolean[] found) {
        //todo: check found[2] ==> foundModelYear
        // if (found[2]) query = query.whereEqualTo(MODELYEAR, modelYear);
        if (found[0] && found[1] && found[3]) {
            String modelResponse = BRAND + MODEL + BATTERY;
            query = firestoreQuery.makeCompoundQuery(elbilReference, modelResponse, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[0] && found[1]) {
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND + MODEL, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[0] && found[3]) {
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND + BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[1] && found[3]) {
            query = firestoreQuery.makeCompoundQuery(elbilReference, MODEL + BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[0]) {
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[1]) {
            query = firestoreQuery.makeCompoundQuery(elbilReference, MODEL, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (found[3]) {
            query = firestoreQuery.makeCompoundQuery(elbilReference, BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else {
            handleFirestoreQuery(allCarsList);
        }
    }

    private void checkModelYear() {
        for (Elbil elbil : allCarsList) {
            if (model.equals(elbil.getModel()) && brand.equals(elbil.getBrand())) {
                checkModelYearRange(elbil);
            }
            if (found[2]) {
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
                modelYear = elbil.getModelYear();
                foundHashMap.put(MODELYEAR, modelYear);
                found[2] = true;
            }
        } else if (modelYearResponse.length == 2) {
            int min = Integer.parseInt(modelYearResponse[0]);
            int max = Integer.parseInt(modelYearResponse[1]);
            if (min <= value && value <= max) {
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
        }

        dialogBox.setIntent(intent);
        dialogBox.createDialogBox();
    }


    private void initFields() {
        brand = "";
        model = "";
        modelYear = "";
        battery = "";
        }

    private void switchFieldFound(String dataField) {
        switch (dataField) {
            case BRAND:
                brand = foundHashMap.get(BRAND);
                found[0] = true;
                break;
            case MODEL:
                model = foundHashMap.get(MODEL);
                found[1] = true;
                break;
            case MODELYEAR:
                modelYear = foundHashMap.get(MODELYEAR);
                found[2] = true;
                break;
            case BATTERY:
                battery = foundHashMap.get(BATTERY);
                found[3] = true;
                break;
            default:
        }
    }

    public void setAllCarsList(List<Elbil> allCarsList) {
        this.allCarsList = allCarsList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (allCarsList.size() == 0) firestoreQuery.getInitFirestoreData();
    }
}
