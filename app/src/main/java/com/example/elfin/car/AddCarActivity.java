package com.example.elfin.car;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.elfin.R;
import com.example.elfin.adapter.RecyclerViewAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
    private List<Elbil> mElbilList = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private RecyclerView recyclerView;
    private Query querySearch;
    private FirestoreRecyclerAdapter recyclerAdapter;
    private FirestoreRecyclerOptions<Elbil> response;

    private RecyclerViewClickListener mListener;
    private RecyclerViewAdapter mAdapter;

    private DocumentSnapshot lastResult;


    private List<String> brands, models;
    private ArrayAdapter<String> adapterBrands, adapterModels;


    private EditText editTextSearchRegNr;
    private SearchView searchViewCar;
    private Spinner spinnerBrands, spinnerModels;
    private ImageButton searchRegNrBtn;
    private Button searchCarBtn;

    private AddCarActivity addCarActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        findViewsById();

        addCarActivity = this;
        searchRegNrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarInfoAPI carInfoAPI = new CarInfoAPI();
                carInfoAPI.setAddCarActivity(addCarActivity);
                //todo: valider userinput, fjerne space
                String regNr = editTextSearchRegNr.getText().toString();
                Toast.makeText(addCarActivity, "regNr: " + regNr.trim(), Toast.LENGTH_SHORT).show();
                carInfoAPI.execute(regNr.trim());
            }
        });

        String brand = getString(R.string.velg_bilmerke);
        brands = new ArrayList<>();
        brands.add(brand);
        initSpinnerBrands(brands);

        String model = getString(R.string.velg_bilmodel);
        models = new ArrayList<>();
        models.add(model);
        initSpinnerModels(models);


        //Todo: kan fjernes etter at alle bilene er lagt til
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
                        brands = new ArrayList<>(); //todo: fjern liste og initSpinner() her (kun for testing)
                        models = new ArrayList<>(); //todo: fjern liste og initSpinner() her (kun for testing)
                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                            Elbil elbil = documentSnapshot.toObject(Elbil.class);
                            elbil.setDocumentId(documentSnapshot.getId());

                            //elbil = getCarAttributes(elbil);
                            mElbilList.add(elbil);

                            brands.add(elbil.getBrand());
                            models.add(elbil.getModel() + " : " + elbil.getModelYear());
                        }
                        //textViewData.setText(data);

                        if (mElbilList.size() == 1) {
                            Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
                            intent.putExtra("Elbil", mElbilList.get(0));
                            startActivity(intent);
                        } else {
                            initSpinnerBrands(brands);
                            initSpinnerModels(models);
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


    private void initSpinnerBrands(List<String> brands) {
        adapterBrands = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, brands);
        adapterBrands.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrands.setAdapter(adapterBrands);
        fetchFirstoreData(elbilReference, BRAND, adapterBrands);
    }

    private void initSpinnerModels(List<String> models) {
        adapterModels = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, models);
        adapterModels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModels.setAdapter(adapterModels);
        fetchFirstoreData(elbilReference, MODEL, adapterModels);
    }

    private void fetchFirstoreData(CollectionReference querySearch, final String dataField,
                                   final ArrayAdapter<String> adapter) {
        querySearch.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
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

        DocumentSnapshot documentSnapshot = (DocumentSnapshot) recyclerAdapter.getSnapshots().getSnapshot(1);
        elbil = documentSnapshot.toObject(Elbil.class);
        //getCarAttributes(elbil);
        String path = documentSnapshot.getReference().getPath();
        String id = documentSnapshot.getId();
        elbil.setDocumentId(id);


        /*

        elbilReference.whereEqualTo("model", elbil.getModel()).get()
                //.get()
                //whereEqualTo("specs.effect", 150).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        String data = "";
                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                           // elbil = documentSnapshot.toObject(Elbil.class);
                            //getCarAttributes(elbil);
                            String path = documentSnapshot.getReference().getPath();
                            String id = documentSnapshot.getId();
                            //elbil.setDocumentId(id);
                        }
                        //textViewData.setText(data);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });



        Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
        intent.putExtra("Elbil", elbil);
        startActivity(intent);

         */


    }


    private void initRecyclerView() {
        querySearch = elbilReference.orderBy("brand", Query.Direction.ASCENDING).orderBy("model");

        buildRecyclerResponse(querySearch);
    }

    private void filterFirestoreData(String userInput) {
        recyclerAdapter.stopListening();

        String query = userInput.toLowerCase();
        querySearch = elbilReference.orderBy("brand").startAt(query).endAt(query + "\uf8ff");
        buildRecyclerResponse(querySearch);
    }

    private void buildRecyclerResponse(Query querySearch) {
        response = new FirestoreRecyclerOptions.Builder<Elbil>()
                .setQuery(querySearch, Elbil.class)
                .build();
        // attachRecyclerViewAdapter();

        recyclerAdapter.startListening();
        recyclerAdapter.notifyDataSetChanged();
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

}
