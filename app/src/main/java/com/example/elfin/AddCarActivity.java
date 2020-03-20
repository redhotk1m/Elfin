package com.example.elfin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.adapter.CarListAdapter;
import com.example.elfin.model.Elbil;
import com.example.elfin.testing.NewCarActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    public static Elbil elbil = new Elbil();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilRef = db.collection("Elbiler");

    private CarListAdapter mCarListAdapter;

    private DocumentSnapshot lastResult;

    private List<Elbil> mElbilList;

    private EditText editTextSearchRegNr, editTextSearchCar;
    private ImageButton searchRegNrBtn, searchCarBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        findViewsById();

        createElbilList();

        initRecyclerView();


        searchRegNrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarInfoAPI carInfoAPI = new CarInfoAPI();
                carInfoAPI.execute(editTextSearchRegNr.getText().toString());
            }
        });

        editTextSearchCar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });



        searchCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestoreCarSearch();
            }
        });



        //Todo: kan fjernes etter at alle bilene er lagt til
        FloatingActionButton buttonAddCar = findViewById(R.id.button_add_car);
        buttonAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddCarActivity.this, NewCarActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //start listening to database changes when app goes into the foreground
        mCarListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stop recycler view updates while app goes into the background
        mCarListAdapter.stopListening();
    }

    private void findViewsById() {
        editTextSearchRegNr = findViewById(R.id.edit_text_search_regNr);
        editTextSearchCar = findViewById(R.id.edit_text_search_car);
        searchRegNrBtn = findViewById(R.id.image_button_search_icon);
        searchCarBtn = findViewById(R.id.image_button_search_icon2);
    }

    private void initRecyclerView() {
        Query query = elbilRef.orderBy("brand", Query.Direction.ASCENDING).orderBy("model");

        //getting the query into the adapter
        FirestoreRecyclerOptions<Elbil> elbiler = new FirestoreRecyclerOptions.Builder<Elbil>()
                .setQuery(query, Elbil.class)
                .build();

        mCarListAdapter = new CarListAdapter(elbiler);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_cars);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mCarListAdapter);

        mCarListAdapter.setOnItemClickListener(new CarListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Elbil elbil = documentSnapshot.toObject(Elbil.class);
                String path = documentSnapshot.getReference().getPath();
                String id = documentSnapshot.getId();
                elbil.setDocumentId(id);

                Toast.makeText(AddCarActivity.this,
                        "Position: " + position + "\nPath: " + path + "\nID: " + elbil.getDocumentId(),
                        Toast.LENGTH_LONG).show();
                //startActivity(new Intent(AddCarActivity.this, CarInfoActivity.class));
            }
        });
    }

    private void filter(String toString) {
        ArrayList<Elbil> filteredList = new ArrayList<>();

        for (Elbil elbil : mElbilList) {
            if (elbil.getBrand().toLowerCase().contains(toString.toLowerCase()) ) {
                    //|| elbil.getModel().toLowerCase().contains(toString.toLowerCase())) {
                filteredList.add(elbil);
            }
        }

        mCarListAdapter.filterList(filteredList);
    }


    private void firestoreCarSearch() {
        final CollectionReference elbilRef = FirebaseFirestore.getInstance()
                .collection("Elbiler");
        elbilRef.whereEqualTo("model", "i7").get()
        //elbilRef.whereEqualTo("model", editTextSearchCar.getText().toString()).get()
                //.get()
                //whereEqualTo("specs.effect", 150).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                            elbil = documentSnapshot.toObject(Elbil.class);
                            elbil.setDocumentId(documentSnapshot.getId());

                            elbil = getCarAttributes(elbil);
                        }
                        Toast.makeText(AddCarActivity.this,
                                "ID: " + elbil.getDocumentId()
                                + " Brand: " + elbil.getBrand() + " Model: " + elbil.getModel()
                                , Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }


    public void createElbilList() {
        mElbilList = new ArrayList<>();

        CollectionReference elbilRef = FirebaseFirestore.getInstance().collection("Elbiler");
        elbilRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                            Elbil elbil = documentSnapshot.toObject(Elbil.class);
                            elbil.setDocumentId(documentSnapshot.getId());

                            mElbilList.add(getCarAttributes(elbil));
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

    private Elbil getCarAttributes(Elbil elbil) {
        String documentId = elbil.getDocumentId();
        String brand = elbil.getBrand();
        String model = elbil.getModel();
        String fastCharge = elbil.getFastCharge();
        Map<String, Double> specs = elbil.getSpecs();

        elbil = new Elbil(brand, model, fastCharge, specs);


        return elbil;
    }


}
