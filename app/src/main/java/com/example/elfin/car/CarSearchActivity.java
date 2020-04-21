package com.example.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
    private HashMap<String, String> foundHashMap;

    private Elbil elbil;
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

    private Dialog myCarInfo;
    // private TextView tvBrand, tvModel, tvModelYear, tvFastCharge, tvBattery;
    // private TextView textView, txtclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_search);
        //private method used to assign views by id
        findViewsById();

        //Custom Class to make and handle Fire Store queries
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
                    startActivity(new Intent(CarSearchActivity.this, NewCarActivity.class));
                    // showCarInfo(v);
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
        String regNr = editTextSearchRegNr.getText().toString();
        Toast.makeText(CarSearchActivity.this, "regNr: " + regNr.toLowerCase().trim(), Toast.LENGTH_SHORT).show();
        carInfoAPI.execute(regNr.toLowerCase().trim());
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
        System.out.println("CHECK FILTERED RESPONSES: " + modelResponse + " ; " + modelYearResponse);

        checkFilteredCars(MODELYEAR, elbil, modelYearResponse);
        checkFilteredCars("default", elbil, modelResponse.toLowerCase());

        //todo: handle if ModelYear is not found in amongst matching cars in database; exclude in the query if no match
        // ,or provide manual selection with all possible model years (i.e. 2010 - 2020) in selection

        //todo: check out regNr: EK23827

        System.out.println("RETURNED TO CHECK_API_RESPONSE ; NOW HANDLING FIRESTORE QUERY");
        System.out.println("FOUND FIELDS: " + Arrays.toString(found));
        System.out.println("FIELDS MAP: " + foundHashMap);

        determineFoundFields(found);
    }

    public void handleFirestoreQuery(List<Elbil> mElbilList) {
        Toast.makeText(this, "FIRESTORE CAR LIST SIZE: " + mElbilList.size(), Toast.LENGTH_LONG).show();
        if (mElbilList.size() == 1) {
            Intent intent = new Intent(CarSearchActivity.this, CarInfoActivity.class);
            intent.putExtra("Elbil", mElbilList.get(0));
            startActivity(intent);
        } else if (mElbilList.size() > 1) {
            //todo startActivity CarInfoActivity ==> CarSelectionActivity ==> CarInfoActivity
           // Intent intent = new Intent(this, CarSelectionActivity.class);
            Intent intent = new Intent(this, CarInfoActivity.class);
            intent.putParcelableArrayListExtra("CarList", new ArrayList<>(mElbilList));
            intent.putExtra("Missing", found);
            intent.putExtra("FieldMap", foundHashMap);
            // intent.putExtra("FoundFieldsMap", foundFieldsMap);
            //todo: get HashMap data in next activity by using getSearializedExtra like this
            startActivity(intent);
        } else {
            //todo: show popup dialog choice of "try again?" or "manual selection"?
            System.out.println("NO FIRESTORE CARS FOUND: " + mElbilList.size());
            Toast.makeText(this, "[POPUP MELDING] SJEKK AT REG NR STEMMER ELLER VELG BIL MANUELT \n (INGEN BIL MATCH FRA API)", Toast.LENGTH_LONG).show();
            //todo: check if manual selection:
            handleFirestoreQuery(allCarsList);
            //todo: else "try different regNr"
        }
    }


    private void iterateFilteredCars(String[] responses) {
        Toast.makeText(this,
                "API MODEL RESPONSE:\n" + Arrays.toString(responses)
                        + "\n; (LENGTH: " + responses.length + " )\n"
                        + "\n API MODEL YEAR RESPONSE: " + modelYear,
                Toast.LENGTH_LONG).show();
        System.out.println("API MODEL RESPONSE: " + Arrays.toString(responses) + " ; LENGTH: " + responses.length);

        if (Arrays.toString(responses).isEmpty()) {
            System.out.println("MODEL RESPONSE FROM API IS EMPTY ; RETURNING");
            return;
        }

        bitSet = new BitSet(4);
        System.out.println("INIT BIT SET LENGTH: " + bitSet.length() + " [Size: " + bitSet.size() + "]");
        System.out.println("RESPONSE LENGTH: " + responses.length);

        int count = 0;
        for (Elbil elbil : allCarsList) {
            for (String response : responses) {
                int bitSetLength = bitSet.length();
                System.out.println("CURRENT BIT SET/RESPONSES LENGTH: " + bitSetLength + " / " + responses.length);

                //check filtered car brands if brand not found
                if (!found[0]) {
                    System.out.println("( " + BRAND + " ) CURRENT RESPONSE: " + response + " ; COUNT [" + count + "]");
                    System.out.println("( " + BRAND + " ) CURRENT FOUND ARRAY: " + Arrays.toString(found));
                    checkFilteredCars(BRAND, elbil, response);
                    if (found[0]) {
                        System.out.println("FOUND EXACT BRAND: "
                                + foundHashMap.get(BRAND) + " ; " + found[0] + " ; CONTINUE");
                        continue;
                    } else System.out.println(BRAND + " WAS NOT FOUND ; " + found[0]);
                } else System.out.println(BRAND + " HAS ALREADY BEEN FOUND ==> " + found[0]);

                //check filtered car models if model not found
                if (!found[1]) {
                    System.out.println("( " + MODEL + " ) CURRENT RESPONSE: " + response + " ; COUNT [" + count + "]");
                    System.out.println("( " + MODEL + " ) CURRENT FOUND ARRAY: " + Arrays.toString(found));
                    checkFilteredCars(MODEL, elbil, response);
                    if (found[1]) {
                        System.out.println("FOUND EXACT MODEL: "
                                + foundHashMap.get(MODEL) + " ; " + found[1] + " ; CONTINUE");
                        continue;
                    } else System.out.println(MODEL + " WAS NOT FOUND ; " + found[1]);
                } else System.out.println(MODEL + " HAS ALREADY BEEN FOUND ==> " + found[1]);

                //check filtered car batteries if battery not found
                if (!found[3]) {
                    System.out.println("( " + BATTERY + " ) CURRENT RESPONSE: " + response + " ; COUNT [" + count + "]");
                    System.out.println("( " + BATTERY + " ) CURRENT FOUND ARRAY: " + Arrays.toString(found));
                    checkFilteredCars(BATTERY, elbil, response);
                    if (found[3]) {
                        System.out.println("FOUND EXACT BATTERY: "
                                + foundHashMap.get(BATTERY) + " ; " + found[3] + " ; CONTINUE");
                        continue;
                    } else System.out.println(BATTERY + " WAS NOT FOUND ; " + found[3]);
                } else System.out.println(BATTERY + " HAS ALREADY BEEN FOUND ==> " + found[3]);

                System.out.println("FINAL RESPONSE: " + response);
                System.out.println("CURRENT BIT SET LENGTH: " + bitSet.length() + " ; "
                        + " RESPONSES LENGTH: " + responses.length);
            }
            count++;
            if (bitSet.length() == responses.length) {
                System.out.println("RETURNING @ COUNT: " + count);
                System.out.println("FINISHED BIT SET LENGTH: " + bitSet.length()
                        + " ; RESPONSES [" + responses.length + "]");
                return;
            }
            System.out.println("COUNT: " + count + " / " + allCarsList.size());
        }
        System.out.println("FINISHED BIT SET LENGTH: " + bitSet.length()
                + " ; RESPONSES [" + responses.length + "]");
        //  determineFoundFields(found);
    }

    private void checkFilteredCars(String dataField, Elbil elbil, String response) {
        switch (dataField) {
            case BRAND:
                // foundBrands = carFilteredList.filterFields(elbil.getBrand(), response, foundBrands);
                System.out.println("FILTER BRAND: " + elbil.getBrand() + " ; " + response + " ; " + found[0]);
                String brandResponse = carFilteredList.filterExactMatch(elbil.getBrand(), response);
                if (response.equals(brandResponse)) {
                    brand = brandResponse;
                    System.out.println("EXACT BRAND RESPONSE " + brandResponse + " EQUALS " + response);
                    setBitSetValue(bitSet, found[0]);
                    System.out.println("BIT SET BRAND LENGTH: " + bitSet.length());
                    found[0] = true;
                    foundHashMap.put(BRAND, brandResponse);
                    System.out.println(response + " EQUALS " + foundHashMap.get(BRAND) + " IS " + found[0]);
                } else System.out.println("EXACT BRAND NOT FOUND ==> " + found[0]);
                break;
            case MODEL:
                System.out.println("FILTER MODEL: " + elbil.getModel() + " ; " + response + " ; " + found[1]);
                String modelResponse = carFilteredList.filterExactMatch(elbil.getModel(), response);
                if (response.equals(modelResponse)) {
                    model = modelResponse;
                    System.out.println("EXACT MODEL RESPONSE " + modelResponse + " EQUALS " + response);
                    setBitSetValue(bitSet, found[1]);
                    System.out.println("BIT SET MODEL LENGTH: " + bitSet.length());
                    found[1] = true;
                    foundHashMap.put(MODEL, model);
                    System.out.println(response + " EQUALS " + foundHashMap.get(MODEL) + " IS " + found[1]);
                } else System.out.println("EXACT MODEL NOT FOUND ==> " + found[1]);
                break;
            case MODELYEAR:
                if (response != null) {
                    modelYear = response;
                    if (!modelYear.isEmpty()) {
                        found[2] = true;
                        foundHashMap.put(MODELYEAR, modelYear);
                    }
                }
                break;
            case BATTERY:
                System.out.println("FILTER BATTERY: " + elbil.getBattery() + " ; " + response + " ; " + found[3]);
                String batteryResponse = response.replace("kwh", "");
                batteryResponse = carFilteredList.filterExactMatch(elbil.getBattery(), batteryResponse);
                if (response.replace("kwh", "").equals(batteryResponse)) {
                    battery = batteryResponse;
                    System.out.println("EXACT BATTERY RESPONSE " + batteryResponse + " EQUALS " + response);
                    setBitSetValue(bitSet, found[3]);
                    System.out.println("BIT SET BATTERY LENGTH: " + bitSet.length());
                    found[3] = true;
                    foundHashMap.put(BATTERY, batteryResponse);
                    System.out.println(response + " EQUALS " + foundHashMap.get(BATTERY) + " IS " + found[3]);
                } else System.out.println("EXACT BATTERY NOT FOUND ==> " + found[3]);
                break;
            default:
                if (response != null) {
                    String[] modelResponses = response.toLowerCase().split("\\W+"); //The \\W+ will match all non-alphabetic characters occurring one or more times.
                    iterateFilteredCars(modelResponses);
                } else System.out.println("NO SUCH DATA...");
        }
    }

    private void setBitSetValue(BitSet bitSet, boolean found) {
        int currentLength = bitSet.length();
        if (bitSet.length() == 0 && !found) {
            System.out.println("BIT SET LENGTH: " + currentLength + " == " + bitSet.length());
            bitSet.set(0, true);
            System.out.println("BIT SET LENGTH: " + currentLength + " ===> " + bitSet.length());
        } else if (bitSet.length() == 1 && !found) {
            System.out.println("BIT SET LENGTH: " + currentLength + " == " + bitSet.length());
            bitSet.set(1, true);
            System.out.println("BIT SET LENGTH: " + currentLength + " ===> " + bitSet.length());
        } else if (bitSet.length() == 2 && !found) {
            System.out.println("BIT SET LENGTH: " + currentLength + " == " + bitSet.length());
            bitSet.set(2, true);
            System.out.println("BIT SET LENGTH: " + currentLength + " ===> " + bitSet.length());
        } else if (bitSet.length() == 3 && !found) {
            System.out.println("BIT SET LENGTH: " + currentLength + " == " + bitSet.length());
            bitSet.set(3, true);
            System.out.println("BIT SET LENGTH: " + currentLength + " ===> " + bitSet.length());
        } else {
            System.out.println(found + " ; BIT SET LENGTH: " + currentLength);
        }
    }

    private void determineFoundFields(boolean[] found) {
        System.out.println("<FROM DETERMINE MISSING>");
        //todo: check found[2] ==> foundModelYear
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
            firestoreQuery.executeCompoundQuery(elbilReference);
        }
    }

    private void initFields() {
        brand = "";
        model = "";
        modelYear = "";
        battery = "";
        // fastCharge = "";
    }

    public void showCarInfo(View view) {
        myCarInfo = new Dialog(this);
        myCarInfo.setContentView(R.layout.car_info);

        //  String documentId = elbil.getDocumentId();
        String brand = "Bilmerke: " + "nissan"; //+ foundHashMap.get(BRAND);
        String model = "Bilmodell: " + "leaf"; //+ foundHashMap.get(MODEL);
        String modelYear = "Årsmodell: " + "2018";//+ foundHashMap.get(MODELYEAR);
        String battery = "Batterikappasitet: " + "24 kwh"; //+ foundHashMap.get(BATTERY);
        String fastCharge = "Hurtiglader: " + "CCS ; 110"; //elbil.getFastCharge() + " ; " + elbil.getEffect();

        TextView tvBrand = findViewById(R.id.txt_brand);
        TextView tvModel = findViewById(R.id.txt_model);
        TextView tvModelYear = findViewById(R.id.txt_model_year);
        TextView tvBattery = findViewById(R.id.txt_battery);
        TextView tvFastCharge = findViewById(R.id.txt_fast_charge);

        tvBrand.setText("BRAND");
        tvModel.setText("MODEL");
        tvModelYear.setText("MODEL YEAR");
        tvFastCharge.setText("FAST CHARGE");
        tvBattery.setText("BATTERY");


        TextView txtclose = myCarInfo.findViewById(R.id.txtclose);
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
