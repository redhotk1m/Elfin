package com.example.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.R;

import java.util.ArrayList;
import java.util.List;

public class CarSelectionActivity extends AppCompatActivity {

    private static final String TAG = "CarSelectionActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private Elbil elbil;
    private List<Elbil> allCarsList = new ArrayList<>();
    private List<Elbil> mCarList = new ArrayList<>();

    private CarSpinnerSelection spinnerSelection;

    private List<String> brands, models, modelYears, batteries, fastCharges;
    private ArrayAdapter<String> mAdapter;
    private ArrayAdapter<String> adapterBrands, adapterModels, adapterModelYears, adapterBattery, adapterFastCharge;

    private Spinner spinnerBrands, spinnerModels, spinnerModelYears, spinnerBatteries, spinnerCharges;
    private TextView tvSpinnerBrands;
    private Button searchCarBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_selection);

        findViewsById();

        ArrayList<Elbil> elbils = getIntent().getParcelableArrayListExtra("CarList");
        if (elbils != null) {
            for (Elbil elbil : elbils) System.out.println(elbil.getModel());
        } else System.out.println("NO ELBILS RECEIVED!");

        setAllCarsList(elbils);

        tvSpinnerBrands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableManualCarSelection();
            }
        });

        searchCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCarList = searchCar();

                if (mCarList.size() == 1) {
                    Intent intent = new Intent(CarSelectionActivity.this, CarInfoActivity.class);
                    intent.putExtra("Elbil", mCarList.get(0));
                    startActivity(intent);
                } else {
                    initSpinner(BATTERY, spinnerBatteries);
                    spinnerSelection.filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
                    adapterBattery.notifyDataSetChanged();

                    // initSpinner(BATTERY, spinnerBatteries);
                    // initSpinner(FASTCHARGE, spinnerCharges);
                }
            }
        });
    }

    private void findViewsById() {
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
        spinnerSelection.setSpinnerSelection(spinnerBrands, brands);
        adapterBrands.notifyDataSetChanged();
        // v.setOnClickListener(null); //removes setOnClickListener
        tvSpinnerBrands.setVisibility(View.GONE);
        spinnerBrands.performClick();
    }

    private void spinnerOnItemSelection(String dataField, View view) {
        switch (dataField) {
            case BRAND:
                spinnerSelection.spinnerOnItemSelected(BRAND, spinnerBrands, spinnerModels, models);
               // spinnerSelection.spinnerBrandOnItemSelected(spinnerBrands, spinnerModels, brands, models);
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
                spinnerSelection.spinnerOnItemSelected(MODEL, spinnerModels, spinnerModelYears, modelYears);
               // spinnerSelection.spinnerModelsOnItemSelected(spinnerModels, spinnerModelYears, modelYears);
                if (spinnerModels.getSelectedItem().equals(getString(R.string.choose_none))) {
                    ((TextView) view).setText(getString(R.string.choose_model));
                } else {
                    // if (modelYears == null) initSpinner(MODELYEAR, spinnerModelYears);
                    spinnerModelYears.performClick();
                    adapterModelYears.notifyDataSetChanged();
                }
                break;
            case MODELYEAR:
                spinnerSelection.spinnerOnItemSelected(MODELYEAR, spinnerModelYears, spinnerBatteries, batteries);
               // spinnerSelection.spinnerModelYearsOnItemSelected(spinnerModelYears, spinnerBatteries, batteries);
                if (spinnerModelYears.getSelectedItem().equals(getString(R.string.choose_none))) {
                    ((TextView) view).setText(getString(R.string.choose_model_year));
                } else {
                    spinnerBatteries.performClick();
                    adapterBattery.notifyDataSetChanged();
                }
                break;
            case BATTERY:
                spinnerSelection.spinnerOnItemSelected(BATTERY, spinnerBatteries, spinnerCharges, fastCharges);
               // spinnerSelection.spinnerBatteriesOnItemSelected(spinnerBatteries, spinnerCharges, fastCharges);
                Toast.makeText(this, "BATTERY SELECTED: " + spinnerBatteries.getSelectedItem(), Toast.LENGTH_SHORT).show();
                if (spinnerBatteries.getSelectedItem().equals(getString(R.string.choose_none))) {
                    ((TextView) view).setText(getString(R.string.choose_battery));
                } else {
                    spinnerCharges.performClick();
                    adapterFastCharge.notifyDataSetChanged();
                }
                break;
            case FASTCHARGE:
                spinnerSelection.spinnerOnItemSelected(FASTCHARGE, spinnerCharges, null, null);
               // spinnerSelection.spinnerChargesOnItemSelected(spinnerCharges);
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

}
