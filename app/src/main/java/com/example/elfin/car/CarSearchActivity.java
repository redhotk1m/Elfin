package com.example.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CarSearchActivity extends AppCompatActivity {

    private static final String TAG = "CarSearchActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private boolean[] missing;
    private String[] modelResponse;
    private String[] fields = new String[4];
    private String brand, model, modelYear, battery;
    private HashMap<String, String> fieldMap;
    private HashMap<String, List<String>> foundFieldsMap;
    // private String[] fields = {brand, model, modelYear, battery};

    private Elbil elbil;
    private List<Elbil> allCarsList = new ArrayList<>();
    private List<Elbil> mCarList = new ArrayList<>();

    private List<String> foundBrands, foundModels, foundModelYears, foundBatteries;

    private FirestoreQuery firestoreQuery;
    private Query query;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private EditText editTextSearchRegNr;
    private ImageButton searchRegNrBtn;
    private Button searchCarBtn;


    private Dialog myCarInfo;
    private TextView tvBrand, tvModel, tvModelYear, tvFastCharge, tvBattery;
    private TextView textView, txtclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_search);

        findViewsById();

        firestoreQuery = new FirestoreQuery(this, elbilReference);
        initHashMapFields();

        searchRegNrBtn.setOnClickListener(myOnClickListener);
        searchCarBtn.setOnClickListener(myOnClickListener);

        //Todo: comment out in activity_car_search.xml after all the cars have been added
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
                    initFoundResponseLists();
                    executeCarInfoApi();
                    break;
                case R.id.button_search_car:
                    initFoundResponseLists();
                    if (!editTextSearchRegNr.getText().toString().isEmpty()) executeCarInfoApi();
                    else handleFirestoreQuery(allCarsList);
                    break;
                case R.id.button_add_car:
                    // startActivity(new Intent(CarSearchActivity.this, NewCarActivity.class));
                    showCarInfo(v);
                    break;
                default:
                    Toast.makeText(CarSearchActivity.this, "CLICKABLE ID NOT FOUND..", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void executeCarInfoApi() {
        CarInfoAPI carInfoAPI = new CarInfoAPI();
        carInfoAPI.setCarSearchActivity(CarSearchActivity.this);
        String regNr = editTextSearchRegNr.getText().toString();
        Toast.makeText(CarSearchActivity.this, "regNr: " + regNr.toLowerCase().trim(), Toast.LENGTH_SHORT).show();
        carInfoAPI.execute(regNr.toLowerCase().trim());
    }

    public void loadApiInfo(Elbil elbil) {
        initFoundResponseLists();
        checkAPIResponse(elbil);
    }

    private void checkAPIResponse(Elbil elbil) {
        checkFilteredCars(MODELYEAR, elbil, elbil.getModelYear());
        checkFilteredCars("default", elbil, elbil.getModel().toLowerCase());

        //todo: Request Manual SELECTION (EXCLUDE modelYEAR if not null)
        handleFirestoreQuery(allCarsList);
    }

    public void handleFirestoreQuery(List<Elbil> mElbilList) {
        Toast.makeText(this, "FIRESTORE CAR LIST SIZE: " + mElbilList.size(), Toast.LENGTH_LONG).show();
        if (mElbilList.size() == 1) {
            Intent intent = new Intent(CarSearchActivity.this, CarInfoActivity.class);
            intent.putExtra("Elbil", mElbilList.get(0));
            startActivity(intent);
        } else if (mElbilList.size() > 1) {
            Intent intent = new Intent(this, CarSelectionActivity.class);
           // ArrayList<Elbil> carList = new ArrayList<>(mElbilList);
            intent.putParcelableArrayListExtra("CarList", new ArrayList<>(mElbilList));
           // intent.putExtra("Elbils", mElbilList);
            intent.putExtra("Missing", missing);
            intent.putExtra("FieldMap", fieldMap);
            intent.putExtra("FoundFieldsMap", foundFieldsMap);
            //todo: get HashMap data in next activity by using getSearializedExtra like this
            startActivity(intent);

            /*
            Toast.makeText(this, "EXACT FIELDS FOUND:\n\n"
                            + BRAND + " ; " + fieldMap.get(BRAND) + "\n\n"
                            + MODEL + " ; " + fieldMap.get(MODEL) + "\n\n"
                            + MODELYEAR + " ; " + fieldMap.get(MODELYEAR) + "\n\n"
                            + BATTERY + " ; " + fieldMap.get(BATTERY) + "\n\n"
                    , Toast.LENGTH_LONG).show();
             */
        } else {
            //todo: show popup dialog choice of "try again?" or "manual selection"?
            System.out.println("NO FIRESTORE CARS FOUND: " + mElbilList.size());
        }
    }


    private void searchFilteredCars(String[] responses) {
        Toast.makeText(this,
                "API MODEL RESPONSE:\n" + Arrays.toString(responses)
                        + "\n; (LENGTH: " + responses.length + " )\n"
                        + "\n API MODEL YEAR RESPONSE: " + modelYear,
                Toast.LENGTH_LONG).show();
        System.out.println("API MODEL RESPONSE: " + Arrays.toString(responses) + "; LENGTH: " + responses.length);

        for (Elbil elbil : allCarsList) {
            for (String response : responses) {
                checkFilteredCars(BRAND, elbil, response);
                checkFilteredCars(MODEL, elbil, response);
                checkFilteredCars(BATTERY, elbil, response);
            }
        }

        System.out.println("MISSING: " + Arrays.toString(missing));
        System.out.println("FIELDS MAP: " + fieldMap);
        System.out.println("FOUND FIELDS MAP: " + foundFieldsMap);

        determineMissing(missing, fields);
    }

    private void checkFilteredCars(String dataField, Elbil elbil, String response) {
        switch (dataField) {
            case BRAND:
                if (elbil.getBrand().contains(response)) {
                    if (!foundBrands.contains(response)) foundBrands.add(elbil.getBrand());
                    if (elbil.getBrand().equals(response) && elbil.getBrand().length() == response.length()) {
                        System.out.println("BRAND " + elbil.getBrand() + " EQUALS " + response +
                                " ; LENGTH: " + elbil.getBrand().length() + " == " + response.length());
                        brand = response;
                        missing[0] = true;
                    }
                }
                fieldMap.put(BRAND, brand);
                foundFieldsMap.put(BRAND, foundBrands);
                break;
            case MODEL:
                if (elbil.getModel().contains(response)) {
                    System.out.println("FOUND MODEL: " + elbil.getModel() + " CONTAINS " + response);
                    if (!foundModels.contains(response)) foundModels.add(elbil.getModel());
                    if (elbil.getModel().equals(response) && elbil.getModel().length() == response.length()) {
                        System.out.println("MODEL " + elbil.getModel() + " EQUALS " + response +
                                " ; LENGTH: " + elbil.getModel().length() + " == " + response.length());
                        model = response;
                        missing[1] = true;
                    }
                }
                fieldMap.put(MODEL, model);
                foundFieldsMap.put(MODEL, foundModels);
                break;
            case MODELYEAR:
                if (response != null) modelYear = response;
                if (!modelYear.isEmpty()) {
                    missing[2] = true;
                    foundModelYears.add(modelYear);
                }
                fieldMap.put(MODELYEAR, modelYear);
                foundFieldsMap.put(MODELYEAR, foundModelYears);
                break;
            case BATTERY:
                String r = response.replace("kwh", "");
                if (elbil.getBattery().contains(r)) {
                    System.out.println("FOUND BATTERY: " + elbil.getBattery() + " CONTAINS " + response);
                    if (!foundBatteries.contains(r)) foundBatteries.add(elbil.getBattery());
                    if (elbil.getBattery().equals(r)) {
                        System.out.println("BATTERY " + elbil.getBrand() + " EQUALS " + response);
                        battery = r.toLowerCase();
                        missing[3] = true;
                    }
                }
                fieldMap.put(BATTERY, battery);
                foundFieldsMap.put(BATTERY, foundBatteries);
                break;
            default:
                System.out.println("NO SUCH DATA FIELD..");
                if (response != null) model = response.toLowerCase();
                if (!model.isEmpty()) {
                    modelResponse = model.split("\\W+"); //The \\W+ will match all non-alphabetic characters occurring one or more times.
                    searchFilteredCars(modelResponse);
                }
        }
    }

    private void determineMissing(boolean[] missing, String[] fields) {
        System.out.println("<FROM DETERMINE MISSING>");
        if (missing[0] && missing[1] && missing[2]) {
            System.out.println("(FOUND_BRAND && FOUND_MODEL && FOUND_BATTERY) == TRUE");
            String modelResponse = BRAND + MODEL + BATTERY;
            System.out.println("API MODEL RESPONSE FIELDS: " + modelResponse);

            System.out.println(BRAND + " == " + brand);
            System.out.println(MODEL + " == " + model);
            System.out.println(MODELYEAR + " == " + modelYear);
            System.out.println(BATTERY + " == " + battery);

            // query = firestoreQuery.makeCompoundQuery(elbilReference, modelResponse, fieldMap);
            //todo: remove below query after testing
            query = elbilReference
                    .whereEqualTo(BRAND, brand)
                    .whereEqualTo(MODEL, model)
                    .whereEqualTo(BATTERY, battery);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[0] && missing[1]) {
            System.out.println("(FOUND_BRAND && FOUND_MODEL) == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND + MODEL, fieldMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[0] && missing[2]) {
            System.out.println("(FOUND_BRAND && FOUND_BATTERY) == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND + BATTERY, fieldMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[1] && missing[2]) {
            System.out.println("(FOUND_MODEL && FOUND_BATTERY) == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, MODEL + BATTERY, fieldMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[0]) {
            System.out.println("FOUND_BRAND == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND, fieldMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[1]) {
            System.out.println("FOUND_MODEL == TRUE");
            System.out.println("MODEL: " + model + "; MODEL YEAR: " + modelYear);

            System.out.println("API MODEL RESPONSE FIELDS: " + MODEL);
            System.out.println("API RESPONSE FIELDS" + Arrays.toString(fields));

            query = firestoreQuery.makeCompoundQuery(elbilReference, MODEL, fieldMap);
            firestoreQuery.executeCompoundQuery(query);

            //  Toast.makeText(this, "EXECUTING COMPOUND FIRESTORE QUERY..)", Toast.LENGTH_LONG).show();
            //  if (!modelYear.isEmpty()) firestoreQuery.compoundFirestoreQuery(model, modelYear);
        } else if (missing[2]) {
            System.out.println("FOUND_BATTERY == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BATTERY, fieldMap);
            firestoreQuery.executeCompoundQuery(query);
        } else {
            System.out.println("ALL_MISSING == TRUE");
            firestoreQuery.executeCompoundQuery(elbilReference);
        }
    }


    private void initHashMapFields() {
        initFoundResponseLists();

        fieldMap = new HashMap<>();
        fieldMap.put(BRAND, brand);
        fieldMap.put(MODEL, model);
        fieldMap.put(MODELYEAR, modelYear);
        fieldMap.put(BATTERY, battery);
        // fieldMap.put(FASTCHARGE, fastCharge);
        System.out.println("FIELDS MAP: " + fieldMap);

        foundFieldsMap = new HashMap<>();
        foundFieldsMap.put(BRAND, foundBrands);
        foundFieldsMap.put(MODEL, foundModels);
        foundFieldsMap.put(MODELYEAR, foundModelYears);
        foundFieldsMap.put(BATTERY, foundBatteries);
        // foundFieldsMap.put(FASTCHARGE, fastCharges);
        System.out.println("FOUND FIELDS MAP: " + foundFieldsMap);
    }

    private void initFoundResponseLists() {
        //Init new or clear existing foundBrands
        brand = "";
        if (foundBrands == null) foundBrands = new ArrayList<>();
        else foundBrands.clear();
        //Init new or clear existing foundModels
        model = "";
        if (foundModels == null) foundModels = new ArrayList<>();
        else foundModels.clear();
        //Init new or clear existing foundModelYears
        modelYear = "";
        if (foundModelYears == null) foundModelYears = new ArrayList<>();
        else foundModelYears.clear();
        //Init new or clear existing foundBatteries
        battery = "";
        if (foundBatteries == null) foundBatteries = new ArrayList<>();
        else foundBatteries.clear();

        missing = new boolean[4]; //foundBrand, foundModel, foundModelYear, foundBattery
        Arrays.fill(missing, Boolean.FALSE);
    }


    public void showCarInfo(View view) {
        myCarInfo = new Dialog(this);
        myCarInfo.setContentView(R.layout.car_info);

        //  String documentId = elbil.getDocumentId();
        String brand = "Bilmerke: " + fieldMap.get(BRAND);
        String model = "Bilmodell: " + fieldMap.get(MODEL);
        String modelYear = "Årsmodell: " + fieldMap.get(MODELYEAR);
        String battery = "Batterikappasitet: " + fieldMap.get(BATTERY);
        // String fastCharge = "Hurtiglader: " + elbil.getFastCharge() + " ; " + elbil.getEffect();

        tvBrand = findViewById(R.id.text_view_brand);
        tvModel = findViewById(R.id.text_view_model);
        tvModelYear = findViewById(R.id.text_view_model_year);
        tvBattery = findViewById(R.id.text_view_battery);
        tvFastCharge = findViewById(R.id.text_view_fast_charge);

        tvBrand.setText(brand);
        tvModel.setText(model);
        tvModelYear.setText(modelYear);
        // tvFastCharge.setText(fastCharge);
        tvBattery.setText(battery);


        txtclose = myCarInfo.findViewById(R.id.txtclose);
        txtclose.setText("X");
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCarInfo.dismiss();
            }
        });

        myCarInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myCarInfo.show();
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
