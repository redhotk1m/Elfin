package com.example.elfin.car;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddCarActivity extends AppCompatActivity {

    private final String TAG = "AddCarActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";

    public static Elbil elbil = new Elbil();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private RecyclerView recyclerView;
    private Query querySearch;
    private FirestoreRecyclerAdapter recyclerAdapter;
    private FirestoreRecyclerOptions<Elbil> response;

    private RecyclerViewClickListener mListener;
    private RecyclerViewAdapter mAdapter;

    private DocumentSnapshot lastResult;

    private List<Elbil> mElbilList;


    private List<String> brands, models;
    private ArrayAdapter<String> adapterBrands, adapterModels;


    private EditText editTextSearchRegNr;
    private SearchView searchViewCar;
    private Spinner spinnerBrands, spinnerModels;
    private ImageButton searchRegNrBtn, searchCarBtn;
    private Button searchBtn;

    private AddCarActivity addCarActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        findViewsById();

        initRecyclerView();

        addCarActivity = this;


        searchRegNrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarInfoAPI carInfoAPI = new CarInfoAPI();

                carInfoAPI.setAddCarActivity(addCarActivity);

                carInfoAPI.execute(editTextSearchRegNr.getText().toString());

                //searchFirestoreData("i3");
            }
        });

        /**
        searchViewCar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) filterFirestoreData(newText);
                else initRecyclerView();
                return false;
            }
        });
         **/


        /*
        searchCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //firestoreCarSearch();
                //searchData();
                //getModeldata();
            }
        });

         */






        brands = new ArrayList<>();
        initSpinnerBrands(brands);



        models = new ArrayList<>();
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
       // searchViewCar = findViewById(R.id.search_view_car);
        searchRegNrBtn = findViewById(R.id.image_button_search_icon);
       // searchCarBtn = findViewById(R.id.image_button_search_icon2);

        spinnerBrands = findViewById(R.id.spinner_brands);
        spinnerModels = findViewById(R.id.spinner_models);

        searchBtn = findViewById(R.id.button3);

        recyclerView = findViewById(R.id.recycler_view_cars);
    }



    public void loadApiInfo(Elbil elbil) {
        if (elbil == null) editTextSearchRegNr.setText("LOADING...");
        else {
            editTextSearchRegNr.setText(elbil.getModel() + " : " + elbil.getModelYear());
            searchFirestoreData(elbil.getModel().toLowerCase());


            models = new ArrayList<>();
            models.add(elbil.getModel());
            initSpinnerModels(models);

           // brands = new ArrayList<>();
            //brands.add()


        }



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

    //private void setUpSpinner(Array);

    private void fetchFirstoreData(CollectionReference querySearch, final String dataField,
                                   final ArrayAdapter<String> adapter) {
        querySearch.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String data = documentSnapshot.getString(dataField);
                        if (dataField.equals(BRAND) && !brands.contains(data)) brands.add(data);
                        else if (dataField.equals(MODEL) && !models.contains(data)) models.add(data);
                        else Toast.makeText(AddCarActivity.this, "NO SUCH DATAFIELD!", Toast.LENGTH_SHORT).show();
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

    private void searchFirestoreData(String userInput) {
        recyclerAdapter.stopListening();

        String query = userInput.toLowerCase();
        querySearch = elbilReference.orderBy(MODEL).startAt(query).endAt(query + "\uf8ff");
        //querySearch = elbilReference.orderBy(MODEL).where
        buildRecyclerResponse(querySearch);

    }

    private void buildRecyclerResponse(Query querySearch) {
        response = new FirestoreRecyclerOptions.Builder<Elbil>()
                .setQuery(querySearch, Elbil.class)
                .build();
        attachRecyclerViewAdapter();
        // mAdapter.startListening();
        // mAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerAdapter.notifyDataSetChanged();
    }

    private void attachRecyclerViewAdapter() {
        //mAdapter = new RecyclerViewAdapter(response, mListener);
        recyclerAdapter = new FirestoreRecyclerAdapter<Elbil, CarsViewHolder>(response) {

            @NonNull
            @Override
            public CarsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item,
                        parent, false);

                return new CarsViewHolder(view, mListener);
            }

            @Override
            protected void onBindViewHolder(CarsViewHolder carsViewHolder, int i, final Elbil elbil) {

                carsViewHolder.textViewBrand.setText(elbil.getBrand());
                carsViewHolder.textViewModel.setText(elbil.getModel());
                carsViewHolder.textViewDescription.setText(makeCarDescription(elbil));

            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                super.onError(e);
                Log.d(TAG, e.toString());
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mListener = new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position != recyclerView.NO_POSITION && mListener != null) {
                    DocumentSnapshot documentSnapshot = (DocumentSnapshot) recyclerAdapter.getSnapshots().getSnapshot(position);
                    elbil = documentSnapshot.toObject(Elbil.class);
                    //getCarAttributes(elbil);
                    String path = documentSnapshot.getReference().getPath();
                    String id = documentSnapshot.getId();
                    elbil.setDocumentId(id);

                    Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
                    intent.putExtra("Elbil", elbil);
                    startActivity(intent);
                }
            }
        };
        //recyclerView.setAdapter(mAdapter);
        //RecyclerViewAdapter adapter = new RecyclerViewAdapter(mListener);
        //recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(recyclerAdapter);
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
        //start listening to database changes when app goes into the foreground
        if (recyclerAdapter != null) recyclerAdapter.startListening();
        //mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop recycler view updates while app goes into the background
        // mAdapter.stopListening();
        recyclerAdapter.stopListening();
    }
}
