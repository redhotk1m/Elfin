package com.example.elfin.car;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddCarActivity extends AppCompatActivity {

    private final String TAG = "AddCarActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";

    private Elbil elbil;
    private List<Elbil> allElbilList = new ArrayList<>();
    private List<Elbil> mElbilList = new ArrayList<>();

    private CarSpinnerSelection spinnerSelection;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private Query querySearch;

    private DocumentSnapshot lastResult;

    private List<String> brands, models, modelYears;
    private ArrayAdapter<String> mAdapter;
    private ArrayAdapter<String> adapterBrands, adapterModels, adapterModelYears;

    private EditText editTextSearchRegNr;
    private SearchView searchViewCar;
    private Spinner spinnerBrands, spinnerModels, spinnerModelYears;
    private ImageButton searchRegNrBtn;
    private Button searchCarBtn;
    private CheckBox carCheckBox;

    //private AddCarActivity addCarActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        findViewsById();

        spinnerSelection = new CarSpinnerSelection(this);
        initSpinner(BRAND, spinnerBrands);
        initSpinner(MODEL, spinnerModels);
        initSpinner(MODELYEAR, spinnerModelYears);


        searchRegNrBtn.setOnClickListener(myOnClickListener);
        searchCarBtn.setOnClickListener(myOnClickListener);
        carCheckBox.setOnClickListener(myOnClickListener);

        //Todo: utkommenter etter at alle bilene er lagt til
        FloatingActionButton buttonAddCar = findViewById(R.id.button_add_car);
        buttonAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddCarActivity.this, NewCarActivity.class));
            }
        });
    }

    private void findViewsById() {
        editTextSearchRegNr = findViewById(R.id.edit_text_search_regNr);
        searchRegNrBtn = findViewById(R.id.image_button_search_icon);
        carCheckBox = findViewById(R.id.check_box_manual_selection);
        searchCarBtn = findViewById(R.id.button_search_car);
        spinnerBrands = findViewById(R.id.spinner_brands);
        spinnerModels = findViewById(R.id.spinner_models);
        spinnerModelYears = findViewById(R.id.spinner_model_years);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        System.out.println("ELBIL LIST SIZE: " + allElbilList.size());
        // brands = new ArrayList<>();
        // getCarBrands(brands);
    }

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_button_search_icon:
                    executeCarInfoApi();
                    break;
                case R.id.check_box_manual_selection:
                    enableSpinnerSelection(v);
                    break;
                case R.id.button_search_car:
                    Toast.makeText(AddCarActivity.this, "LIST SIZE: " + allElbilList.size(), Toast.LENGTH_LONG).show();
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
                    spinnerSelection.spinnerBrandOnItemSelected(
                            spinnerBrands, spinnerModels, spinnerModelYears, models, modelYears);
                    adapterModels.notifyDataSetChanged();
                   // spinnerOnItemSelected(BRAND);
                    break;
                case R.id.spinner_models:
                    spinnerSelection.spinnerModelsOnItemSelected(
                            spinnerModels, spinnerModelYears, modelYears);
                    adapterModelYears.notifyDataSetChanged();
                   // spinnerOnItemSelected(MODEL);
                    break;
                case R.id.spinner_model_years:
                    spinnerSelection.spinnerModelYearsOnItemSelected(spinnerModelYears, modelYears);
                    // spinnerOnItemSelected(MODELYEAR);
                    break;
                default:
                    Toast.makeText(adapterView.getContext(), "NO SPINNER", Toast.LENGTH_SHORT).show();
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
                // brands = spinnerSelection.initSpinnerList(BRAND);
                initSpinnerList(BRAND);
                spinner.setAdapter(adapterBrands);
                //fetchFirstoreData(elbilReference, BRAND, mAdapter);
                break;
            case MODEL:
                initSpinnerList(MODEL);
                spinner.setAdapter(adapterModels);
                break;
            case MODELYEAR:
                initSpinnerList(MODELYEAR);
                spinner.setAdapter(adapterModelYears);
                break;
            default:
                System.out.println("NO SUCH SPINNER..");
        }
    }

    private void initSpinnerList(String dataField) {
        switch (dataField) {
            case BRAND:
                System.out.println("INIT BRANDS");
                brands = new ArrayList<>();
                brands.add(getString(R.string.choose_brand));
                adapterBrands = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
                adapterBrands.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                break;
            case MODEL:
                System.out.println("INIT MODELS");
                models = new ArrayList<>();
                models.add(getString(R.string.choose_model));
                adapterModels = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
                adapterModels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                break;
            case MODELYEAR:
                System.out.println("INIT MODEL YEARS");
                modelYears = new ArrayList<>();
                modelYears.add(getString(R.string.choose_model_year));
                adapterModelYears = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modelYears);
                adapterModelYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                break;
            default:
                System.out.println("NO SPINNER LIST");
        }
    }

    private void enableSpinnerSelection(View v) {
        if (((CheckBox) v).isChecked()) {
            spinnerSelection.getFilteredCars(spinnerBrands, BRAND, brands);
            adapterBrands.notifyDataSetChanged();
        } else {
            spinnerSelection.disableSpinner(BRAND, spinnerBrands, brands);
            adapterBrands.notifyDataSetChanged();
            spinnerSelection.disableSpinner(MODEL, spinnerModels, models);
            adapterModels.notifyDataSetChanged();
            spinnerSelection.disableSpinner(MODELYEAR, spinnerModelYears, modelYears);
            adapterModelYears.notifyDataSetChanged();
        }
    }


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
                compoundFirestoreQuerry(model.toLowerCase(), modelYear.toLowerCase());
            Toast.makeText(this, "model: " + model + " : " + modelYear, Toast.LENGTH_SHORT).show();
        }

    }


    private void fetchFirstoreData(CollectionReference querySearch, final String dataField,
                                   final ArrayAdapter<String> adapter) {
        querySearch.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //allElbilList.clear();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String data = documentSnapshot.getString(dataField);
                        if (dataField.equals(BRAND) && !brands.contains(data)) brands.add(data);
                        else if (dataField.equals(MODEL) && !models.contains(data))
                            models.add(data);
                        else
                            System.out.println("...");
                        //Toast.makeText(AddCarActivity.this, "NO SUCH DATAFIELD!", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void compoundFirestoreQuerry(String brand, String model, String modelYear) {
    }

    private void compoundFirestoreQuerry(String model, String modelYear) {
        elbilReference.whereEqualTo(MODEL, model)
                .whereEqualTo(MODELYEAR, modelYear)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        mElbilList.clear();
                        brands = new ArrayList<>(); //todo: fjern liste og initSpinner() etter testing
                        models = new ArrayList<>(); //todo: fjern liste og initSpinner() etter testing
                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                            Elbil elbil = documentSnapshot.toObject(Elbil.class);
                            elbil.setDocumentId(documentSnapshot.getId());

                            //elbil = getCarAttributes(elbil);
                            mElbilList.add(elbil);

                            brands.add(elbil.getBrand());
                            models.add(elbil.getModel() + " : " + elbil.getModelYear());
                        }

                        if (mElbilList.size() == 1) {
                            Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
                            intent.putExtra("Elbil", mElbilList.get(0));
                            startActivity(intent);
                        } else {
                            // initSpinner(BRAND, spinnerBrands); //todo: fjerne etter testing
                            // initSpinner(MODEL, spinnerModels); //todo: fjerne etter testing
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public List<Elbil> getAllCars() {
        return this.allElbilList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (allElbilList.size() == 0) getInitFirestoreData(elbilReference);
    }


    private void getInitFirestoreData(CollectionReference reference) {
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Elbil elbil = documentSnapshot.toObject(Elbil.class);
                        allElbilList.add(elbil);
                    }
                } else
                    Toast.makeText(AddCarActivity.this, "COULD NOT LOCATE FIRESTORE DATA..!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
