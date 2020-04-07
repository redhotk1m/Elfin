package com.example.elfin.car;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AddCarActivity extends AppCompatActivity {

    private final String TAG = "AddCarActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private Elbil elbil;
    private List<Elbil> allCarsList = new ArrayList<>();
    private List<Elbil> mCarList = new ArrayList<>();

    private CarSpinnerSelection spinnerSelection;

    private FirestoreQuery firestoreQuery;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private Query querySearch;

    private DocumentSnapshot lastResult;

    private List<String> brands, models, modelYears, batteries, fastCharges;
    private ArrayAdapter<String> mAdapter;
    private ArrayAdapter<String> adapterBrands, adapterModels, adapterModelYears, adapterBattery, adapterFastCharge;

    private EditText editTextSearchRegNr;
    private SearchView searchViewCar;
    private Spinner spinnerBrands, spinnerModels, spinnerModelYears, spinnerBatteries, spinnerCharges;
    private TextView tvSpinnerBrands;
    private ImageButton searchRegNrBtn;
    private Button searchCarBtn;
    private CheckBox carCheckBox;

    //private AddCarActivity addCarActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        findViewsById();
        tvSpinnerBrands.setOnClickListener(myOnClickListener);
        searchRegNrBtn.setOnClickListener(myOnClickListener);
        searchCarBtn.setOnClickListener(myOnClickListener);

        firestoreQuery = new FirestoreQuery(this, elbilReference);

        //Todo: utkommenter etter at alle bilene er lagt til
        FloatingActionButton buttonAddCar = findViewById(R.id.button_add_car);
        buttonAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddCarActivity.this, NewCarActivity.class));
            }
        });
        buttonAddCar.hide();
        System.out.println("ALL CARS LIST SIZE: " + allCarsList.size());
    }

    private void findViewsById() {
        editTextSearchRegNr = findViewById(R.id.edit_text_search_regNr);
        searchRegNrBtn = findViewById(R.id.image_button_search_icon);
        // carCheckBox = findViewById(R.id.check_box_manual_selection);
        searchCarBtn = findViewById(R.id.button_search_car);
        //TextView Spinner Prompts
        tvSpinnerBrands = findViewById(R.id.text_view_spinner_brands);
        //Spinners
        spinnerBrands = findViewById(R.id.spinner_brands);
        spinnerModels = findViewById(R.id.spinner_models);
        spinnerModelYears = findViewById(R.id.spinner_model_years);
        spinnerBatteries = findViewById(R.id.spinner_batteries);
        spinnerCharges = findViewById(R.id.spinner_charges);
    }

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_button_search_icon:
                    executeCarInfoApi();
                    break;
                case R.id.text_view_spinner_brands:
                    enableManualCarSelection();
                    break;
                case R.id.button_search_car:
                    mCarList = searchCar();

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
                    /*
                    initSpinner(BATTERY, spinnerBatteries);
                    spinnerSelection.filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
                    adapterBattery.notifyDataSetChanged();
                     */

                    //  Toast.makeText(AddCarActivity.this, "LIST SIZE: " + allCarsList.size(), Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(AddCarActivity.this, "CLICKABLE ID NOT FOUND..", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private AdapterView.OnItemSelectedListener myOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            switch (adapterView.getId()) {
                case R.id.spinner_brands:
                    spinnerOnItemSelection(BRAND, view);
                    break;
                case R.id.spinner_models:
                    spinnerOnItemSelection(MODEL, view);
                    break;
                case R.id.spinner_model_years:
                    spinnerOnItemSelection(MODELYEAR, view);
                    break;
                case R.id.spinner_batteries:
                    spinnerOnItemSelection(BATTERY, view);
                    break;
                case R.id.spinner_charges:
                    spinnerOnItemSelection(FASTCHARGE, view);
                    break;
                default:
                    Toast.makeText(adapterView.getContext(), "NO SUCH SPINNER LISTENER..", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void executeCarInfoApi() {
        CarInfoAPI carInfoAPI = new CarInfoAPI();
        carInfoAPI.setAddCarActivity(AddCarActivity.this);
        //todo: validate user input, disable input space
        String regNr = editTextSearchRegNr.getText().toString();
        Toast.makeText(AddCarActivity.this, "regNr: " + regNr.trim(), Toast.LENGTH_SHORT).show();
        carInfoAPI.execute(regNr.trim());
    }

    public void loadApiInfo(Elbil elbil) {
        if (elbil == null) editTextSearchRegNr.setText("LOADING...");
        else {
            editTextSearchRegNr.setText(elbil.getModel() + " : " + elbil.getModelYear());
            //searchFirestoreData(elbil.getModel().toLowerCase());
            //if (elbil.getModel().isEmpty() && elbil.getModelYear().isEmpty()) System.out.println("EMPTY MODEL");

            String model = elbil.getModel();
            String modelYear = elbil.getModelYear();
            if (model != null && model.length() != 0 && modelYear != null && modelYear.length() != 0)
                firestoreQuery.compoundFirestoreQuery(model.toLowerCase(), modelYear.toLowerCase());
            Toast.makeText(this, "FIRESTORE SIZE: " + mCarList.size(), Toast.LENGTH_SHORT).show();
            //compoundFirestoreQuery(model.toLowerCase(), modelYear.toLowerCase());
            Toast.makeText(this, "model: " + model + " : " + modelYear, Toast.LENGTH_SHORT).show();
        }

    }

    public void handleFirestoreQuery(List<Elbil> mElbilList) {
        Toast.makeText(this, "FIRESTORE CAR LIST SIZE: " + mElbilList.size(), Toast.LENGTH_LONG).show();
        if (mElbilList.size() == 1) {
            Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
            intent.putExtra("Elbil", mElbilList.get(0));
            startActivity(intent);
        } else {
            carCheckBox.setChecked(true);
            //todo: sjekke opp mot lister og la bruker velge riktig model hvis flere finnes i databasen
            // enableSpinnerSelection();
            spinnerSelection.getFilteredCars(spinnerBrands, BRAND, brands);
            spinnerSelection.setSpinnerSelection(spinnerBrands, brands);
            adapterBrands.notifyDataSetChanged();


            // initSpinner(BRAND, spinnerBrands); //todo: fjerne etter testing
            // initSpinner(MODEL, spinnerModels); //todo: fjerne etter testing
        }
    }

    private void initSpinner(String dataField, Spinner spinner) {
        spinner.setOnItemSelectedListener(myOnItemSelectedListener);
        spinner.setEnabled(false);
        switch (dataField) {
            case BRAND:
                brands = new ArrayList<>();
                // adapterBrands = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
                adapterBrands = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, brands) {

                    @Override
                    public int getCount() {
                        return (brands.size()); // Truncate the list
                    }
                    /*
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View v = null;
                        if (position == 0) {
                            TextView tv = new TextView(getContext());
                            tv.setVisibility(View.GONE);
                            v = tv;
                        } else {
                            v = super.getDropDownView(position, null, parent);
                        }
                        return v;
                    }
                    */
                };
                adapterBrands.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterBrands);
                //fetchFirstoreData(elbilReference, BRAND, mAdapter);
                break;
            case MODEL:
                models = new ArrayList<>();
                adapterModels = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
                adapterModels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterModels);
                break;
            case MODELYEAR:
                modelYears = new ArrayList<>();
                adapterModelYears = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modelYears);
                adapterModelYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterModelYears);
                break;
            case BATTERY:
                batteries = new ArrayList<>();
                adapterBattery = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, batteries);
                adapterBattery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterBattery);
                break;
            case FASTCHARGE:
                fastCharges = new ArrayList<>();
                // spinnerSelection.initSpinnerList(spinnerCharges, fastCharges, FASTCHARGE);
                adapterFastCharge = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fastCharges);
                adapterFastCharge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterFastCharge);
                break;
            default:
                System.out.println("NO SUCH SPINNER..");
        }
    }

    private void enableManualCarSelection() {
        spinnerSelection = new CarSpinnerSelection(this);
        initSpinner(BRAND, spinnerBrands);
        initSpinner(MODEL, spinnerModels);
        initSpinner(MODELYEAR, spinnerModelYears);
        initSpinner(BATTERY, spinnerBatteries);
        initSpinner(FASTCHARGE, spinnerCharges);
        spinnerSelection.filteredCarsSelection(spinnerBrands, BRAND, brands);
        adapterBrands.notifyDataSetChanged();
        // v.setOnClickListener(null); //removes setOnClickListener
        tvSpinnerBrands.setVisibility(View.GONE);
        spinnerBrands.performClick();
    }

    private void spinnerOnItemSelection(String dataField, View view) {
        switch (dataField) {
            case BRAND:
                spinnerSelection.spinnerBrandOnItemSelected(spinnerBrands, spinnerModels, brands, models);
                if (spinnerBrands.getSelectedItem().equals(getString(R.string.choose_none))) {
                    // ((TextView)view).setText(null);  // hide selection text
                    ((TextView) view).setText(getString(R.string.choose_brand));

                   // disableSpinner(MODEL);
                    // spinnerModels.setVisibility(View.GONE);
                } else {
                    spinnerModels.performClick();
                    adapterModels.notifyDataSetChanged();
                   // disableSpinner(MODELYEAR);
                }
                break;
            case MODEL:
                spinnerSelection.spinnerModelsOnItemSelected(spinnerModels, spinnerModelYears, modelYears);
                if (spinnerModels.getSelectedItem().equals(getString(R.string.choose_none))) {
                    ((TextView) view).setText(getString(R.string.choose_model));
                } else {
                    // if (modelYears == null) initSpinner(MODELYEAR, spinnerModelYears);
                    spinnerModelYears.performClick();
                    adapterModelYears.notifyDataSetChanged();
                }
                break;
            case MODELYEAR:
                spinnerSelection.spinnerModelYearsOnItemSelected(spinnerModelYears, spinnerBatteries, batteries);
                if (spinnerModelYears.getSelectedItem().equals(getString(R.string.choose_none))) {
                    ((TextView) view).setText(getString(R.string.choose_model_year));
                } else {
                    spinnerBatteries.performClick();
                    adapterBattery.notifyDataSetChanged();
                }
                break;
            case BATTERY:
                spinnerSelection.spinnerBatteriesOnItemSelected(spinnerBatteries, spinnerCharges, fastCharges);
                Toast.makeText(this, "BATTERY SELECTED: " + spinnerBatteries.getSelectedItem(), Toast.LENGTH_SHORT).show();
                if (spinnerBatteries.getSelectedItem().equals(getString(R.string.choose_none))) {
                    ((TextView) view).setText(getString(R.string.choose_battery));
                } else {
                    spinnerCharges.performClick();
                    adapterFastCharge.notifyDataSetChanged();
                }
                break;
            case FASTCHARGE:
                spinnerSelection.spinnerChargesOnItemSelected(spinnerCharges);
                if (spinnerBatteries.getSelectedItem().equals(getString(R.string.choose_none))) {
                    ((TextView) view).setText(getString(R.string.choose_charging));
                } else {
                    spinnerCharges.performClick();
                    adapterFastCharge.notifyDataSetChanged();
                }
                break;
            default:
                System.out.println("UNKNOWN ITEM SELECTION...");
        }
    }

    public void disableSpinner(String spinnerName) {
        switch (spinnerName) {
            case BRAND:
                spinnerBrands.setVisibility(View.GONE);
            case MODEL:
                spinnerModels.setVisibility(View.GONE);
            case MODELYEAR:
                spinnerModelYears.setVisibility(View.GONE);
            case BATTERY:
                spinnerBatteries.setVisibility(View.GONE);
            case FASTCHARGE:
                spinnerCharges.setVisibility(View.GONE);
                break;
            default:
                Toast.makeText(this, "NO SUCH SPINNER FOUND..", Toast.LENGTH_SHORT).show();
        }
        /*
        if (spinnerName.equals(BRAND)) spinnerBrands.setVisibility(View.GONE);
        else if (spinnerName.equals(MODEL)) spinnerModels.setVisibility(View.GONE);
        else if (spinnerName.equals(MODELYEAR)) spinnerModels.setVisibility(View.GONE);
        else if (spinnerName.equals(BATTERY)) spinnerModels.setVisibility(View.GONE);
        else if (spinnerName.equals(FASTCHARGE)) spinnerModels.setVisibility(View.GONE);
        else Toast.makeText(this, "NO SUCH SPINNER FOUND..", Toast.LENGTH_SHORT).show();
         */
    }

    private List<Elbil> searchCar() {
        mCarList = new ArrayList<>();
        for (Elbil elbil : allCarsList) {
            if (spinnerBrands.getSelectedItem().equals(elbil.getBrand())
                    && spinnerModels.getSelectedItem().equals(elbil.getModel())
                    && spinnerModelYears.getSelectedItem().equals(elbil.getModelYear())) {
                mCarList.add(elbil);
            }
        }
        return mCarList;
    }


    public List<Elbil> getAllCars() {
        return this.allCarsList;
    }

    public void setAllCarsList(List<Elbil> allCarsList) {
        this.allCarsList = allCarsList;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public void setModelYears(List<String> modelYears) {
        this.modelYears = modelYears;
    }

    public void setBatteries(List<String> batteries) {
        this.batteries = batteries;
    }

    public void setFastCharges(List<String> fastCharges) {
        this.fastCharges = fastCharges;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (allCarsList.size() == 0) firestoreQuery.getInitFirestoreData();
        //  getInitFirestoreData(elbilReference);
    }
}
