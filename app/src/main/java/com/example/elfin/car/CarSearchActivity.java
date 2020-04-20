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
    private String[] modelResponse;
    private String[] fields = new String[4];
    private String brand, model, modelYear, battery;
    private HashMap<String, String> foundHashMap;
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

    private CarFilteredList carFilteredList;

    private Dialog myCarInfo;
    // private TextView tvBrand, tvModel, tvModelYear, tvFastCharge, tvBattery;
    // private TextView textView, txtclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_search);

        findViewsById();

        firestoreQuery = new FirestoreQuery(this, elbilReference);
        // initFoundResponseLists();
        carFilteredList = new CarFilteredList();
        bitSet = new BitSet(4);
        initHashMapFields();

        searchRegNrBtn.setOnClickListener(myOnClickListener);
        searchCarBtn.setOnClickListener(myOnClickListener);

        //Todo: comment out in activity_car_search.xml after all the cars have been added
        // or use .hide() method on buttonAddCar
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
        //Sets foundBrand, foundModel, foundModelYear & foundBattery inside found array to be false
        Arrays.fill(found, Boolean.FALSE);

        initHashMapFields();

        //todo: handle if ModelYear is not found in amongst matching cars in database; exclude in the query if no match
        // ,or provide manual selection with all possible model years (i.e. 2010 - 2020) in selection
        checkFilteredCars(MODELYEAR, elbil, elbil.getModelYear());
        checkFilteredCars("default", elbil, elbil.getModel().toLowerCase());

        //todo: check out regNr: EK23827


        //todo: Request Manual SELECTION (EXCLUDE modelYEAR if not null or empty)
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
            intent.putParcelableArrayListExtra("CarList", new ArrayList<>(mElbilList));
            intent.putExtra("Missing", found);
            intent.putExtra("FieldMap", foundHashMap);
            // intent.putExtra("FoundFieldsMap", foundFieldsMap);
            //todo: get HashMap data in next activity by using getSearializedExtra like this
            startActivity(intent);
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


        System.out.println("INIT BIT SET LENGTH / SIZE: " + bitSet.length() + " / " + bitSet.size());

        System.out.println("RESPONSE LENGTH: " + responses.length);

        int count = 0;

        for (Elbil elbil : allCarsList) {
            for (String response : responses) {
                System.out.println("FOUND ARRAY: " + Arrays.toString(found) + " ( " +BRAND+ " )");
                if (!found[0]) checkFilteredCars(BRAND, elbil, response);
                else System.out.println("FOUND: " + BRAND + " ==> " + found[0]);

                System.out.println("FOUND ARRAY: " + Arrays.toString(found) + " ( " +MODEL+ " )");
                if (!found[1]) checkFilteredCars(MODEL, elbil, response);
                else System.out.println("FOUND: " + MODEL + " ==> " + found[1]);

                System.out.println("FOUND ARRAY: " + Arrays.toString(found) + " ( " +BATTERY+ " )");
                // if (!found[2]) checkFilteredCars(MODELYEAR, elbil, modelYear);
                if (!found[3]) checkFilteredCars(BATTERY, elbil, response);
                else System.out.println("FOUND: " + BATTERY + " ==> " + found[2]);


                System.out.println("CURRENT BIT SET LENGTH: " + bitSet.length());

                count++;
                System.out.println("COUNT: " + count + " / " + allCarsList.size());

                if (bitSet.length() == responses.length) {
                    System.out.println("(INNER) RETURNING at count: " + count);
                    System.out.println("BIT LENGTH: " + bitSet.length() + " == " + responses.length);
                   // return;
                }
            }
            //todo: Check out Java Bits ==> java bitset to boolean array?
            // return if (responses.length <==> number of values in boolean array that is found)

            // if (bitSet.length() == responses.length) System.out.println("BIT SET == RESPONSES!");

            System.out.println("CURRENT BIT SET LENGTH: " + bitSet.length() + " <===> " + "RESPONSES LENGTH: " + responses.length);

            /*
            System.out.println("CURRENT BIT SET LENGTH: " + bitSet.length());

            count++;
            System.out.println("COUNT: " + count + " / " + allCarsList.size());

             */

            if (bitSet.length() == responses.length) {
                System.out.println("RETURNING at count: " + count);
                return;
            }
        }
        // foundFieldsMap.put(BRAND, foundBrands);
        // foundFieldsMap.put(MODEL, foundModels);
        // foundFieldsMap.put(MODELYEAR, foundModelYears);
        // foundFieldsMap.put(BATTERY, foundBatteries);


        System.out.println("FINISHED BIT SET LENGTH: " + bitSet.length());

        System.out.println("MISSING: " + Arrays.toString(found));
        System.out.println("FIELDS MAP: " + foundHashMap);
        System.out.println("FOUND FIELDS MAP: " + foundFieldsMap);

        determineMissing(found, fields);
    }

    private void checkFilteredCars(String dataField, Elbil elbil, String response) {
        //todo: ONLY NEED EXACT FIELDS HERE ==> FILTER LISTS TO BE USED IN SPINNER SELECTION INSTEAD
        switch (dataField) {
            case BRAND:
                // foundBrands = carFilteredList.filterFields(elbil.getBrand(), response, foundBrands);
                if (brand.isEmpty()) {
                    System.out.println("FILTER BRAND: " + elbil.getBrand() + " ; " + response);
                    String brandResponse = carFilteredList.filterExactMatch(elbil.getBrand(), response);
                    if (brandResponse.equals(response)) {
                        brand = brandResponse;

                        System.out.println("FOUND[1] == " + found[0]);

                        // }
                        // if (!brand.isEmpty()) {
                        System.out.println("FOUND BRAND: " + brand + " EQUALS " + response);
                        foundHashMap.put(BRAND, brand);
                        //  found[0] = true;

                        System.out.println("BIT SET BRAND LENGTH: " + bitSet.length());
                        //  bitSet.set(0, true);
                      //  if (!found[0])
                            setBitSetValue(bitSet, found[0]);
                        System.out.println("BIT SET BRAND LENGTH: " + bitSet.length());

                        found[0] = true;
                    }
                }
                break;
            case MODEL:
                // foundModels = carFilteredList.filterFields(elbil.getModel(), response, foundModels);
                // System.out.println("MODEL: " + elbil.getModel() + "===============> " + model);
                if (model.isEmpty()) {
                    System.out.println("FILTER MODEL: " + elbil.getModel() + " ; " + response);
                    String modelResponse = carFilteredList.filterExactMatch(elbil.getModel(), response);
                    if (modelResponse.equals(response)) {
                        model = modelResponse;
                        // found[1] = true;

                        if (!found[1]) {

                            System.out.println("FOUND[1] == " + found[1]);

                            // if (!model.isEmpty()) {
                            System.out.println("FOUND MODEL: " + model + " EQUALS " + response);
                            foundHashMap.put(MODEL, model);
                            // found[1] = true;
                            System.out.println("BIT SET MODEL LENGTH: " + bitSet.length());
                            // bitSet.set(1, true);
                           // if (!found[1])
                                setBitSetValue(bitSet, found[0]);
                            System.out.println("BIT SET MODEL LENGTH: " + bitSet.length());
                            //}

                            found[1] = true;
                            // System.out.println("FOUND[1] == " + found[1]);


                        }
                    }
                }
                break;
            case MODELYEAR:
                if (response != null) {
                    modelYear = response;
                    if (!modelYear.isEmpty()) {
                        found[2] = true;

                        // foundModelYears.add(modelYear);
                    }
                }
                foundHashMap.put(MODELYEAR, modelYear);
                foundFieldsMap.put(MODELYEAR, foundModelYears);
                break;
            case BATTERY:
                String br = response.replace("kwh", "");
                // foundBatteries = carFilteredList.filterFields(elbil.getBattery(), br, foundBatteries);

                System.out.println("FOUND[1] == " + found[3]);

                if (battery.isEmpty()) {
                    System.out.println("FILTER BATTERY: " + elbil.getBattery() + " ; " + response);
                    String batteryResponse = carFilteredList.filterExactMatch(elbil.getBattery(), br);
                    if (batteryResponse.equals(br)) {
                        battery = br;
                    }
                    if (!battery.isEmpty()) {
                        System.out.println("FOUND BATTERY: " + battery + " EQUALS " + response);
                        foundHashMap.put(BATTERY, battery);
                     //   found[3] = true;


                        System.out.println("BIT SET BATTERY LENGTH: " + bitSet.length());
                        // bitSet.set(2, true);
                        setBitSetValue(bitSet, found[3]);
                        System.out.println("BIT SET BATTERY LENGTH: " + bitSet.length());
                    }

                    found[3] = true;
                }
                break;
            default:
                if (response != null)
                    modelResponse = response.toLowerCase().split("\\W+"); //The \\W+ will match all non-alphabetic characters occurring one or more times.
                else System.out.println("NO SUCH DATA...");
                searchFilteredCars(modelResponse);
        }
    }

    private void setBitSetValue(BitSet bitSet, boolean found) {
        if (bitSet.length() == 0 && !found) {
            System.out.println(bitSet.length() + " == " + 0);
            bitSet.set(0, true);
            System.out.println(bitSet.length() + " ===> " + 1);
        } else if (bitSet.length() == 1 && !found) {
            System.out.println("BIT SET LENGTH: " + bitSet.length() + " == " + 1);
            bitSet.set(1, true);
            System.out.println("BIT SET LENGTH: " + bitSet.length() + " ===> " + 1);
        } else if (bitSet.length() == 2 && !found) {
            System.out.println("BIT SET LENGTH: " + bitSet.length() + " == " + 2);
            bitSet.set(2, true);
            System.out.println("BIT SET LENGTH: " + bitSet.length() + " ===> " + 3);
        } else if (bitSet.length() == 3 && !found) {
            System.out.println("BIT SET LENGTH: " + bitSet.length() + " == " + 3);
            bitSet.set(3, true);
            System.out.println("BIT SET LENGTH: " + bitSet.length() + " ===> " + 3);
        } else {
            System.out.println(found + " BIT SET LENGTH: " + bitSet.length());
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

            // query = firestoreQuery.makeCompoundQuery(elbilReference, modelResponse, foundHashMap);
            //todo: remove below query after testing
            query = elbilReference
                    .whereEqualTo(BRAND, brand)
                    .whereEqualTo(MODEL, model)
                    .whereEqualTo(BATTERY, battery);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[0] && missing[1]) {
            System.out.println("(FOUND_BRAND && FOUND_MODEL) == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND + MODEL, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[0] && missing[2]) {
            System.out.println("(FOUND_BRAND && FOUND_BATTERY) == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND + BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[1] && missing[2]) {
            System.out.println("(FOUND_MODEL && FOUND_BATTERY) == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, MODEL + BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[0]) {
            System.out.println("FOUND_BRAND == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BRAND, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else if (missing[1]) {
            System.out.println("FOUND_MODEL == TRUE");
            System.out.println("MODEL: " + model + "; MODEL YEAR: " + modelYear);

            System.out.println("API MODEL RESPONSE FIELDS: " + MODEL);
            System.out.println("API RESPONSE FIELDS" + Arrays.toString(fields));

            query = firestoreQuery.makeCompoundQuery(elbilReference, MODEL, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);

            //  Toast.makeText(this, "EXECUTING COMPOUND FIRESTORE QUERY..)", Toast.LENGTH_LONG).show();
            //  if (!modelYear.isEmpty()) firestoreQuery.compoundFirestoreQuery(model, modelYear);
        } else if (missing[2]) {
            System.out.println("FOUND_BATTERY == TRUE");
            query = firestoreQuery.makeCompoundQuery(elbilReference, BATTERY, foundHashMap);
            firestoreQuery.executeCompoundQuery(query);
        } else {
            System.out.println("ALL_MISSING == TRUE");
            firestoreQuery.executeCompoundQuery(elbilReference);
        }
    }


    private void initHashMapFields() {
        foundHashMap = new HashMap<>();
        foundHashMap.put(BRAND, brand);
        foundHashMap.put(MODEL, model);
        foundHashMap.put(MODELYEAR, modelYear);
        foundHashMap.put(BATTERY, battery);
        // foundHashMap.put(FASTCHARGE, fastCharge);
        System.out.println("FIELDS MAP: " + foundHashMap);

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

        found = new boolean[4]; //foundBrand, foundModel, foundModelYear, foundBattery
        //todo: make all true at first and change to false if exact field is found
        // or change name to "found" instad of "found"
        Arrays.fill(found, Boolean.FALSE);
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
