package com.elfin.elfin.car;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.elfin.elfin.R;
import com.elfin.elfin.Utils.DialogBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CarSelectionActivity extends AppCompatActivity {

    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private boolean[] found;
    private HashMap<String, String> fieldMap;

    private List<Elbil> allCarsList = new ArrayList<>();
    private List<Elbil> mCarList = new ArrayList<>();

    private CarSpinnerSelection spinnerSelection;
    private List<String> brands, models, modelYears, batteries, fastCharges;
    private Spinner spinnerBrands, spinnerModels, spinnerModelYears, spinnerBatteries, spinnerCharges;
    private ArrayAdapter<String> adapterBrands, adapterModels, adapterModelYears, adapterBattery, adapterFastCharge;
    private String selectedBrand, selectedModel, selectedModelYear, selectedFastCharge, selectedBattery;

    private TextView tvAddCar, tvSpinnerBrands,
            tvBrandSelection, tvModelSelection, tvModelYearSelection, tvBatterySelection, tvFastChargeSelection;
    private Button searchCarBtn;

    private boolean manualSelection, nextSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_selection);

        findViewsById();

        disableSpinner(MODEL);


        Intent intent = getIntent();


        allCarsList = intent.getParcelableArrayListExtra("AllCarsList");
        if (allCarsList == null) allCarsList = new ArrayList<>();

        fieldMap = (HashMap<String, String>) intent.getSerializableExtra("FieldMap");
        found = intent.getBooleanArrayExtra("Missing");

        ArrayList<Elbil> elbils = getIntent().getParcelableArrayListExtra("CarList");
        if (elbils == null) manualSelection = true;

        //todo: handle this a bit better:
       if (elbils != null) {
            setAllCarsList(elbils);
        }

        //todo: fjern etter testing
        // manualSelection = true;

        if (manualSelection) {
            tvAddCar.setText(getString(R.string.manual_selection));
            tvSpinnerBrands.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enableManualCarSelection();
                }
            });
        } else {
            tvAddCar.setText(R.string.missing_selection);
            enableManualCarSelection();
        }

        searchCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCarList = searchCar();

                if (mCarList.size() == 1) {
                    Intent intent = new Intent(CarSelectionActivity.this, CarInfoActivity.class);
                    intent.putExtra("Elbil", mCarList.get(0));
                    intent.putExtra("manualSelection", true);
                    startActivity(intent);
                } else {
                    //todo: HANDLE THIS!
                    Intent intent = new Intent(CarSelectionActivity.this, CarInfoActivity.class);
                    intent.putParcelableArrayListExtra("CarList", new ArrayList<>(mCarList));
                    intent.putExtra("manualSelection", true);
                }
            }
        });

        searchCarBtn.setClickable(false);
    }

    private void findViewsById() {
        searchCarBtn = findViewById(R.id.button_search_car);
        tvAddCar = findViewById(R.id.text_view_add_car);
        tvBrandSelection = findViewById(R.id.text_view_select_brand);
        tvModelSelection = findViewById(R.id.text_view_select_model);
        tvModelYearSelection = findViewById(R.id.text_view_select_model_year);
        tvBatterySelection = findViewById(R.id.text_view_select_battery);
        tvFastChargeSelection = findViewById(R.id.text_view_select_fast_charge);

        tvSpinnerBrands = findViewById(R.id.text_view_spinner_brands);
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
                    nextSelection = false;
                    spinnerOnItemSelection(BRAND, view);
                    selectedBrand = spinnerBrands.getSelectedItem().toString();
                    if (nextSelection) {
                        spinnerModels.setSelection(models.size() - 1);
                        adapterModels.notifyDataSetChanged();
                    }
                    break;
                case R.id.spinner_models:
                    nextSelection = false;
                    spinnerOnItemSelection(MODEL, view);
                    selectedModel = spinnerModels.getSelectedItem().toString();
                    if (nextSelection) {
                        spinnerModelYears.setSelection(modelYears.size() - 1);
                        adapterModelYears.notifyDataSetChanged();
                    }
                    break;
                case R.id.spinner_model_years:
                    nextSelection = false;
                    spinnerOnItemSelection(MODELYEAR, view);
                    selectedModelYear = spinnerModelYears.getSelectedItem().toString();
                    if (nextSelection) {
                        spinnerBatteries.setSelection(batteries.size() - 1);
                        adapterBattery.notifyDataSetChanged();
                    }
                    break;
                case R.id.spinner_batteries:
                    nextSelection = false;
                    spinnerOnItemSelection(BATTERY, view);
                    selectedBattery = spinnerBatteries.getSelectedItem().toString();
                    if (nextSelection) {
                        spinnerCharges.setSelection(fastCharges.size() - 1);
                        adapterFastCharge.notifyDataSetChanged();
                    }
                    break;
                case R.id.spinner_charges:
                    nextSelection = false;
                    spinnerOnItemSelection(FASTCHARGE, view);
                    selectedFastCharge = spinnerCharges.getSelectedItem().toString();
                    if (nextSelection) {
                        //todo: enable search btn
                        searchCarBtn.setEnabled(true);
                        searchCarBtn.setClickable(true);
                    }
                    break;
                default:
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
                adapterBrands = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, brands) {
                    @Override
                    public int getCount() {
                        return (brands.size()); // Truncate the list
                    }
                };
                adapterBrands.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterBrands);
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
        }
    }

    private void enableManualCarSelection() {
        spinnerSelection = new CarSpinnerSelection(this);
        initSpinners();
        spinnerSelection.filteredCarsSelection(spinnerBrands, BRAND, brands);
        spinnerSelection.setSpinnerSelection(spinnerBrands, brands);
        adapterBrands.notifyDataSetChanged();
        tvSpinnerBrands.setVisibility(View.GONE);
        }

    private void initSpinners() {
        initSpinner(BRAND, spinnerBrands);
        initSpinner(MODEL, spinnerModels);
        initSpinner(MODELYEAR, spinnerModelYears);
        initSpinner(BATTERY, spinnerBatteries);
        initSpinner(FASTCHARGE, spinnerCharges);
    }

    private void spinnerOnItemSelection(String dataField, View view) {
        switch (dataField) {
            case BRAND:
                spinnerSelection.spinnerOnItemSelected(BRAND, spinnerBrands, spinnerModels, models, manualSelection);
                selectedBrand = spinnerBrands.getSelectedItem().toString();

                if (spinnerBrands.getSelectedItem().equals(getString(R.string.choose_none))) {
                    makeSpinnerDisplay(BRAND, selectedBrand, view);
                    if (manualSelection) {
                        if (brands.size() <= 2) spinnerBrands.setSelection(0);
                        else spinnerBrands.performClick();
                    } else clickableSelection(spinnerBrands, found[0]);
                } else {
                    makeSpinnerDisplay(BRAND, selectedBrand, view);
                    tvModelSelection.setVisibility(View.VISIBLE);
                    if (selectedModel == null) selectedModel = "";

                    if (selectedModel.isEmpty() || !selectedModel.equals(getString(R.string.choose_none))) {
                        nextSelection = true;
                    }

                    adapterModels.notifyDataSetChanged();
                }

                if (nextSelection) adapterModels.notifyDataSetChanged();

                break;
            case MODEL:
                spinnerSelection.spinnerOnItemSelected(MODEL, spinnerModels, spinnerModelYears, modelYears, manualSelection);
                selectedModel = spinnerModels.getSelectedItem().toString();
                makeSpinnerDisplay(MODEL, selectedModel, view);

                if (models.size() <= 2) {
                    spinnerModels.setSelection(0);
                    selectedModel = spinnerModels.getSelectedItem().toString();
                } else if (selectedModel.equals(getString(R.string.choose_none)))
                    spinnerModels.performClick();

                if (!selectedModel.equals(getString(R.string.choose_none))) {
                    tvModelYearSelection.setVisibility(View.VISIBLE);


                    if (selectedModelYear == null) selectedModelYear = "";

                    if (selectedModelYear.isEmpty() || !selectedModelYear.equals(getString(R.string.choose_none))) {
                        nextSelection = true;
                    }

                    adapterModelYears.notifyDataSetChanged();
                }

                if (nextSelection) adapterModelYears.notifyDataSetChanged();

                break;
            case MODELYEAR:
                spinnerSelection.spinnerOnItemSelected(MODELYEAR, spinnerModelYears, spinnerBatteries, batteries, manualSelection);
                selectedModelYear = spinnerModelYears.getSelectedItem().toString();
                makeSpinnerDisplay(MODELYEAR, selectedModelYear, view);

                if (modelYears.size() <= 2) {
                    spinnerModelYears.setSelection(0);
                    selectedModelYear = spinnerModelYears.getSelectedItem().toString();
                } else if (selectedModelYear.equals(getString(R.string.choose_none)))
                    spinnerModelYears.performClick();

                if (!selectedModelYear.equals(getString(R.string.choose_none))) {
                    tvBatterySelection.setVisibility(View.VISIBLE);


                    if (selectedBattery == null) selectedBattery = "";

                    if (selectedBattery.isEmpty() || !selectedBattery.equals(getString(R.string.choose_none))) {
                         nextSelection = true;
                    }

                    adapterBattery.notifyDataSetChanged();
                }
                if (nextSelection) adapterBattery.notifyDataSetChanged();

                break;
            case BATTERY:
                spinnerSelection.spinnerOnItemSelected(BATTERY, spinnerBatteries, spinnerCharges, fastCharges, manualSelection);
                selectedBattery = spinnerBatteries.getSelectedItem().toString();
                makeSpinnerDisplay(BATTERY, selectedBattery, view);

                if (batteries.size() <= 2) {
                    spinnerBatteries.setSelection(0);
                    selectedBattery = spinnerBatteries.getSelectedItem().toString();
                } else if (selectedBattery.equals(getString(R.string.choose_none)))
                    spinnerBatteries.performClick();


                if (!selectedBattery.equals(getString(R.string.choose_none))) {
                    tvFastChargeSelection.setVisibility(View.VISIBLE);


                    if (selectedFastCharge == null) selectedFastCharge = "";

                    if (selectedFastCharge.isEmpty() || !selectedFastCharge.equals(getString(R.string.choose_none))) {
                        nextSelection = true;
                    }

                    adapterFastCharge.notifyDataSetChanged();
                }

                if (nextSelection) adapterFastCharge.notifyDataSetChanged();

                break;
            case FASTCHARGE:
                spinnerSelection.spinnerOnItemSelected(FASTCHARGE, spinnerCharges, null, null, manualSelection);
                selectedFastCharge = spinnerCharges.getSelectedItem().toString();
                makeSpinnerDisplay(FASTCHARGE, selectedFastCharge, view);


                if (fastCharges.size() <= 2) {
                    spinnerCharges.setSelection(0);
                    selectedFastCharge = spinnerCharges.getSelectedItem().toString();
                } else if (selectedFastCharge.equals(getString(R.string.choose_none)))
                    spinnerCharges.performClick();
                if (!selectedFastCharge.equals(getString(R.string.choose_none))) {

                    if (fastCharges.size() <= 2) spinnerCharges.setSelection(0);
                    nextSelection = true;

                    adapterFastCharge.notifyDataSetChanged();
                }

                break;
            default:
        }
    }


    //todo: IMPLEMENT FILTERED SELECTION METHOD USED IN CAR INFO


    public void disableSpinner(String spinnerName) {
        switch (spinnerName) {
            case BRAND:
                tvBrandSelection.setVisibility(View.GONE);
                spinnerBrands.setVisibility(View.GONE);
                selectedBrand = "";
            case MODEL:
                tvModelSelection.setVisibility(View.GONE);
                spinnerModels.setVisibility(View.GONE);
                selectedModel = "";
            case MODELYEAR:
                tvModelYearSelection.setVisibility(View.GONE);
                spinnerModelYears.setVisibility(View.GONE);
                selectedModelYear = "";
            case BATTERY:
                tvBatterySelection.setVisibility(View.GONE);
                spinnerBatteries.setVisibility(View.GONE);
                selectedBattery = "";
            case FASTCHARGE:
                tvFastChargeSelection.setVisibility(View.GONE);
                spinnerCharges.setVisibility(View.GONE);
                selectedFastCharge = "";
                break;
            default:
        }
    }

    private void clickableSelection(Spinner spinner, boolean foundField) {
        if (foundField) {
            spinner.setSelection(0);
            spinner.setBackgroundColor(ContextCompat.getColor(this, R.color.whiteColor));
            spinner.setClickable(false);
        }
    }

    private void makeSpinnerDisplay(String dataField, String selected, View view) {
        String spinnerDisplay;
        switch (dataField) {
            case BRAND:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_brand);
                else
                    spinnerDisplay =
                            spinnerBrands.getSelectedItem().toString();
                break;
            case MODEL:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_model);
                else
                    spinnerDisplay =
                            spinnerModels.getSelectedItem().toString();
                break;
            case MODELYEAR:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_model_year);
                else
                    spinnerDisplay = spinnerModelYears.getSelectedItem().toString();
                break;
            case BATTERY:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_battery);
                else
                    spinnerDisplay = spinnerBatteries.getSelectedItem().toString();
                break;
            case FASTCHARGE:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_charging);
                else
                    spinnerDisplay = spinnerCharges.getSelectedItem().toString() + " kwh";
                break;
            default:
                spinnerDisplay = getString(R.string.choose_none);
        }

        ((TextView) view).setText(spinnerDisplay);
    }

    private List<Elbil> searchCar() {
        mCarList = new ArrayList<>();
        //todo: get allCarsList from CAR INFO!!!
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

    public HashMap<String, String> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(HashMap<String, String> fieldMap) {
        this.fieldMap = fieldMap;
    }
}
