package com.example.elfin.car;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.MainActivity;
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
import java.util.Map;

public class AddCarActivity extends AppCompatActivity {

    private final String TAG = "AddCarActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";

    private Elbil elbil;
    private List<Elbil> allElbilList = new ArrayList<>();
    private List<Elbil> mElbilList = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private Query querySearch;

    private DocumentSnapshot lastResult;

    private List<String> brands, models;
    private ArrayAdapter<String> mAdapter;
    private ArrayAdapter<String> adapterBrands, adapterModels;

    private EditText editTextSearchRegNr;
    private SearchView searchViewCar;
    private Spinner spinnerBrands, spinnerModels;
    private ImageButton searchRegNrBtn;
    private Button searchCarBtn;

    //private AddCarActivity addCarActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        findViewsById();

        searchRegNrBtn.setOnClickListener(myOnClickListener);

        brands = new ArrayList<>();
        brands.add(getString(R.string.velg_bilmerke));
        initSpinner(BRAND, spinnerBrands, brands);

        models = new ArrayList<>();
        models.add(getString(R.string.velg_bilmodel));
        initSpinner(MODEL, spinnerModels, models);

        searchCarBtn.setOnClickListener(myOnClickListener);

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
        searchCarBtn = findViewById(R.id.button_search_car);
        spinnerBrands = findViewById(R.id.spinner_brands);
        spinnerModels = findViewById(R.id.spinner_models);
    }

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_button_search_icon:
                    executeCarInfoApi();
                case R.id.button_search_car:
                    Toast.makeText(AddCarActivity.this, "LIST SIZE: " + allElbilList.size(), Toast.LENGTH_LONG).show();
                    brands = new ArrayList<>();
                    initSpinner(BRAND, spinnerBrands, brands);
                    models = filterCarModels(brands);
                    initSpinner(MODEL, spinnerModels, models);
                default:
                    Toast.makeText(AddCarActivity.this, "CLICKABLE ID NOT FOUND..", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void executeCarInfoApi() {
        CarInfoAPI carInfoAPI = new CarInfoAPI();
        carInfoAPI.setAddCarActivity(AddCarActivity.this);
        //todo: valider userinput, fjerne space
        String regNr = editTextSearchRegNr.getText().toString();
        Toast.makeText(AddCarActivity.this, "regNr: " + regNr.trim(), Toast.LENGTH_SHORT).show();
        carInfoAPI.execute(regNr.trim());
    }

    private void initSpinner(String dataField, Spinner spinner, List<String> list) {
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);
        spinner.setSelection(0);

        switch (dataField) {
            case BRAND:
                System.out.println("BRANDS");
                if (list.isEmpty()) {
                    brands = getCarBrands(list);
                    mAdapter.notifyDataSetChanged();
                    System.out.println("SIZE FROM SPINNER: " + allElbilList.size());
                }
                //fetchFirstoreData(elbilReference, BRAND, mAdapter);
                else {
                    System.out.println("LIST NOT EMPTY");
                    //todo: hide model spinner and filter
                }
                break;
            case MODEL:
                System.out.println("MODELS");
                if (list.isEmpty()) //getCarModels(list);
                    //fetchFirstoreData(elbilReference, MODEL, mAdapter);
                    break;
            default:
                System.out.println("NO MATCH..");
        }
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
                            initSpinner(BRAND, spinnerBrands, brands); //todo: fjerne etter testing
                            initSpinner(MODEL, spinnerModels, models); //todo: fjerne etter testing
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


    private List<String> getCarBrands(List<String> brands) {
        if (allElbilList.isEmpty()) {
            Toast.makeText(this, "NO CARS AVAILABLE..!", Toast.LENGTH_SHORT).show();
            //getInitFirestoreData(elbilReference);
        } else {
            brands.clear();
            for (Elbil elbil : allElbilList)
                if (!brands.contains(elbil.getBrand())) brands.add(elbil.getBrand());
        }
        return brands;
    }

    private List<String> getCarModels(List<String> models) {
        if (allElbilList.isEmpty())
            Toast.makeText(this, "NO CARS AVAILABLE..!", Toast.LENGTH_SHORT).show();
        else {
            models.clear();
            for (Elbil elbil : allElbilList) brands.add(elbil.getModel());
        }
        return models;
    }

    private List<String> filterCarModels(List<String> brands) {
        ArrayList<Elbil> filteredCars = new ArrayList<>();

        for (Elbil elbil : allElbilList)
            if (elbil.getBrand().equals("bmw"))
                filteredCars.add(elbil); //todo: bytt "bmw" med "spinnerBrand.selectedItem"

        List<String> filteredModels = new ArrayList<>();
        for (Elbil elbil : filteredCars) filteredModels.add(elbil.getModel());
        return filteredModels;
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


    private void findParcelableCar(Elbil elbil) {

        /*
        DocumentSnapshot documentSnapshot = (DocumentSnapshot) recyclerAdapter.getSnapshots().getSnapshot(1);
        elbil = documentSnapshot.toObject(Elbil.class);
        //getCarAttributes(elbil);
        String path = documentSnapshot.getReference().getPath();
        String id = documentSnapshot.getId();
        elbil.setDocumentId(id);



        Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
        intent.putExtra("Elbil", elbil);
        startActivity(intent);

         */
    }


    private void getCarData(CollectionReference reference) {
        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                    Elbil elbil = documentSnapshot.toObject(Elbil.class);
                    allElbilList.add(elbil);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
                Toast.makeText(AddCarActivity.this,
                        "COULD NOT LOCATE FIRESTORE DATA..!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private Elbil getCarAttributes(Elbil elbil) {
        String documentId = elbil.getDocumentId();
        String brand = elbil.getBrand();
        String model = elbil.getModel();
        String modelYear = elbil.getModelYear();
        //String fastCharge = elbil.getFastCharge();
        Map<String, Double> specs = elbil.getSpecs();

        //elbil = new Elbil(brand, model, modelYear, specs);

        return elbil;
    }


    private String makeCarDescription(Elbil elbil) {
        String description = "";

        Map<String, Double> specs = elbil.getSpecs();

        description += elbil.getModelYear() +
                ", Batterikapasitet på " + specs.get("effect") +
                "\n, og " //+ elbil.getFastCharge() + " "
                + specs.get("battery") + "kW DC " + "Hurtiglader";

        return description;
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
