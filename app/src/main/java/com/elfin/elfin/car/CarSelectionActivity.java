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

    private static final String TAG = "CarSelectionActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private boolean[] found;
    private HashMap<String, String> fieldMap;
    private HashMap<String, List<String>> foundFieldsMap;

    private Elbil elbil;
    private List<Elbil> allCarsList = new ArrayList<>();
    private List<Elbil> mCarList = new ArrayList<>();

    private CarSpinnerSelection spinnerSelection;
    private List<String> brands, models, modelYears, batteries, fastCharges;
    private Spinner spinnerBrands, spinnerModels, spinnerModelYears, spinnerBatteries, spinnerCharges;
    private ArrayAdapter<String> adapterBrands, adapterModels, adapterModelYears, adapterBattery, adapterFastCharge;

    private TextView tvAddCar, tvSpinnerBrands,
            tvBrandSelection, tvModelSelection, tvModelYearSelection, tvBatterySelection, tvFastChargeSelection;
    private Button searchCarBtn;

    private DialogBox dialogBox;

    private boolean manualSelection;

    private FirestoreQuery firestoreQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_selection);

        findViewsById();

        disableSpinner(MODEL);


        Intent intent = getIntent();


        allCarsList = intent.getParcelableArrayListExtra("AllCarsList");
        if (allCarsList == null) allCarsList = new ArrayList<>();
        /*
        // System.out.println("#####################################################################");
        if (allCarsList != null)
            // System.out.println("(CAR SELECTION ACTIVITY) ALL CAR LIST SIZE: " + allCarsList.size());
        else {
            // System.out.println("(CAR SELECTION ACTIVITY) ALL CAR LIST SIZE IS NULL!");
            allCarsList = new ArrayList<>();
        }
         */
        // System.out.println("#####################################################################");
        // setAllCarsList(allCarsList);


        fieldMap = (HashMap<String, String>) intent.getSerializableExtra("FieldMap");
        //  System.out.println("INTENT HASH MAP: " + fieldMap);
        //  Toast.makeText(this, "INTENT HASH MAP: " + fieldMap, Toast.LENGTH_LONG).show();
        /*
        Toast.makeText(this, "EXACT FIELDS FOUND:\n\n"
                        + BRAND + " ; " + fieldMap.get(BRAND) + "\n\n"
                        + MODEL + " ; " + fieldMap.get(MODEL) + "\n\n"
                        + MODELYEAR + " ; " + fieldMap.get(MODELYEAR) + "\n\n"
                        + BATTERY + " ; " + fieldMap.get(BATTERY) + "\n\n"
                , Toast.LENGTH_LONG).show();

         */
        /*



        foundFieldsMap = (HashMap<String, List<String>>) intent.getSerializableExtra("FoundFieldsMap");
        System.out.println("INTENT HASH MAP 2: " + foundFieldsMap);
        Toast.makeText(this, "INTENT HASH MAP: " + foundFieldsMap, Toast.LENGTH_LONG).show();

         */

        found = intent.getBooleanArrayExtra("Missing");
        // Toast.makeText(this, "INTEN MISSING[]: " + Arrays.toString(found), Toast.LENGTH_LONG).show();
        //  System.out.println("INTEN MISSING[]: " + Arrays.toString(found));

        ArrayList<Elbil> elbils = getIntent().getParcelableArrayListExtra("CarList");
        /*
        if (elbils != null) {
           // manualSelection = false;
           // elbils = new ArrayList<>();
            //  System.out.println("ELBILS RECEIVED: " + elbils.get(0).toString());
            // for (Elbil elbil : elbils) System.out.println(elbil.getModel());
            // manualSelection = false;
        } else {
            // System.out.println("NO ELBILS RECEIVED!");
            manualSelection = true;
        }
         */
        if (elbils == null) manualSelection = true;

        //todo: handle this a bit better:
        // System.out.println("#####################################################################");
        if (elbils != null) {
            // System.out.println("(CAR SELECTION ACTIVITY) ELBILS LIST SIZE: " + elbils.size());
            setAllCarsList(elbils);
        }// else System.out.println("(CAR SELECTION ACTIVITY) ELBILS LIST SIZE IS NULL");
        // System.out.println("#####################################################################");


        //    if (fieldMap.isEmpty()) manualSelection = true;
        //todo: fjern etter testing
        // manualSelection = true;


        if (manualSelection) {
            tvAddCar.setText(getString(R.string.manual_selection));
            tvSpinnerBrands.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // manualSelection = true;
                    enableManualCarSelection();
                }
            });
        } else {
            // manualSelection = false;
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
                    startActivity(intent);
                }
                /*
                else {
                    initSpinner(BATTERY, spinnerBatteries);
                    spinnerSelection.filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
                    adapterBattery.notifyDataSetChanged();

                    // initSpinner(BATTERY, spinnerBatteries);
                    // initSpinner(FASTCHARGE, spinnerCharges);
                }

                 */
            }
        });
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
                    // Toast.makeText(adapterView.getContext(), "NO SUCH SPINNER LISTENER..", Toast.LENGTH_SHORT).show();
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
                // System.out.println("NO SUCH SPINNER..");
        }
    }

    private void enableManualCarSelection() {
        spinnerSelection = new CarSpinnerSelection(this);
        initSpinners();
        spinnerSelection.filteredCarsSelection(spinnerBrands, BRAND, brands);
        spinnerSelection.setSpinnerSelection(spinnerBrands, brands);
        adapterBrands.notifyDataSetChanged();
        // v.setOnClickListener(null); //removes setOnClickListener
        tvSpinnerBrands.setVisibility(View.GONE);
        // if (manualSelection) spinnerBrands.performClick();
        // else spinnerBrands.setSelection(0);
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
                // spinnerSelection.spinnerBrandOnItemSelected(spinnerBrands, spinnerModels, brands, models);
                String selectedBrand = spinnerBrands.getSelectedItem().toString();
                if (spinnerBrands.getSelectedItem().equals(getString(R.string.choose_none))) {
                    //  ((TextView) view).setText(null);  // hide selection text
                    // ((TextView) view).setText(getString(R.string.choose_brand));
                    makeSpinnerDisplay(BRAND, selectedBrand, view);
                    // disableSpinner(MODEL);
                    // spinnerModels.setVisibility(View.GONE);
                    if (manualSelection) spinnerBrands.performClick();
                    else clickableSelection(spinnerBrands, found[0]);
                } else {
                    makeSpinnerDisplay(BRAND, selectedBrand, view);
                    // ((TextView) view).setText(spinnerBrandsDisplay);
                    tvModelSelection.setVisibility(View.VISIBLE);
                    if (manualSelection) spinnerModels.performClick();
                    else clickableSelection(spinnerModels, found[1]);
                    adapterModels.notifyDataSetChanged();
                    // disableSpinner(MODELYEAR);
                }
                break;
            case MODEL:
                spinnerSelection.spinnerOnItemSelected(MODEL, spinnerModels, spinnerModelYears, modelYears, manualSelection);
                // spinnerSelection.spinnerModelsOnItemSelected(spinnerModels, spinnerModelYears, modelYears);
                String selectedModel = spinnerModels.getSelectedItem().toString();
                makeSpinnerDisplay(MODEL, selectedModel, view);
                if (!selectedModel.equals(getString(R.string.choose_none))) {
                    tvModelYearSelection.setVisibility(View.VISIBLE);
                    if (manualSelection) spinnerModelYears.performClick();
                    else clickableSelection(spinnerModelYears, found[2]);
                    adapterModelYears.notifyDataSetChanged();
                }
                break;
            case MODELYEAR:
                spinnerSelection.spinnerOnItemSelected(MODELYEAR, spinnerModelYears, spinnerBatteries, batteries, manualSelection);
                // spinnerSelection.spinnerModelYearsOnItemSelected(spinnerModelYears, spinnerBatteries, batteries);
                String selectedModelYear = spinnerModelYears.getSelectedItem().toString();
                makeSpinnerDisplay(MODELYEAR, selectedModelYear, view);
                if (!selectedModelYear.equals(getString(R.string.choose_none))) {
                    tvBatterySelection.setVisibility(View.VISIBLE);
                    if (manualSelection) spinnerBatteries.performClick();
                    else clickableSelection(spinnerBatteries, found[3]);
                    adapterBattery.notifyDataSetChanged();
                }
                break;
            case BATTERY:
                spinnerSelection.spinnerOnItemSelected(BATTERY, spinnerBatteries, spinnerCharges, fastCharges, manualSelection);
                // spinnerSelection.spinnerBatteriesOnItemSelected(spinnerBatteries, spinnerCharges, fastCharges);
                // Toast.makeText(this, "BATTERY SELECTED: " + spinnerBatteries.getSelectedItem(), Toast.LENGTH_SHORT).show();
                String selectedBattery = spinnerBatteries.getSelectedItem().toString();
                makeSpinnerDisplay(BATTERY, selectedBattery, view);
                if (!selectedBattery.equals(getString(R.string.choose_none))) {
                    tvFastChargeSelection.setVisibility(View.VISIBLE);
                    if (manualSelection) spinnerCharges.performClick();
                    adapterFastCharge.notifyDataSetChanged();
                }
                break;
            case FASTCHARGE:
                spinnerSelection.spinnerOnItemSelected(FASTCHARGE, spinnerCharges, null, null, manualSelection);
                // spinnerSelection.spinnerChargesOnItemSelected(spinnerCharges);
                String selectedFastCharge = spinnerCharges.getSelectedItem().toString();
                makeSpinnerDisplay(FASTCHARGE, selectedFastCharge, view);
                if (!selectedFastCharge.equals(getString(R.string.choose_none))) {
                    // if (manualSelection) spinnerCharges.performClick();
                    adapterFastCharge.notifyDataSetChanged();
                }
                break;
            default:
                // System.out.println("UNKNOWN ITEM SELECTION...");
        }
    }

    public void disableSpinner(String spinnerName) {
        //  if (manualSelection) {
        switch (spinnerName) {
            case BRAND:
                tvBrandSelection.setVisibility(View.GONE);
                spinnerBrands.setVisibility(View.GONE);
            case MODEL:
                tvModelSelection.setVisibility(View.GONE);
                spinnerModels.setVisibility(View.GONE);
            case MODELYEAR:
                tvModelYearSelection.setVisibility(View.GONE);
                spinnerModelYears.setVisibility(View.GONE);
            case BATTERY:
                tvBatterySelection.setVisibility(View.GONE);
                spinnerBatteries.setVisibility(View.GONE);
            case FASTCHARGE:
                tvFastChargeSelection.setVisibility(View.GONE);
                spinnerCharges.setVisibility(View.GONE);
                break;
            default:
                // Toast.makeText(this, "NO SUCH SPINNER FOUND..", Toast.LENGTH_SHORT).show();
        }
        //  }
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
                    spinnerDisplay = // getString(R.string.chosen_brand) + " " +
                            spinnerBrands.getSelectedItem().toString();
                break;
            case MODEL:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_model);
                else
                    spinnerDisplay = // getString(R.string.chosen_model) + " " +
                            spinnerModels.getSelectedItem().toString();
                break;
            case MODELYEAR:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_model_year);
                else
                    spinnerDisplay = // getString(R.string.chosen_model_year) + " " +
                            spinnerModelYears.getSelectedItem().toString();
                break;
            case BATTERY:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_battery);
                else
                    spinnerDisplay = // getString(R.string.chosen_battery) + " " +
                            spinnerBatteries.getSelectedItem().toString();
                break;
            case FASTCHARGE:
                if (selected.equals(getString(R.string.choose_none)))
                    spinnerDisplay = getString(R.string.choose_charging);
                else
                    spinnerDisplay = // getString(R.string.chosen_charging) + " " +
                            spinnerCharges.getSelectedItem().toString() + " kwh";
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
