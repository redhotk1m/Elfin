package com.example.elfin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.model.Elbil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class AddCarActivity extends AppCompatActivity {

    private final String TAG = "AddCarActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilRef = db.collection("Elbiler");

    EditText editTextMerke, editTextLader, editTextBatteri;
    TextView textViewData;
    Button btnSave, btnRetrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        findViewsById();
    }

    @Override
    protected void onStart() {
        super.onStart();

        elbilRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    //leave method if there's an exception
                    Toast.makeText(AddCarActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                    return;
                }

                String data = "";
                //Todo: Add to ArrayList instead of String
                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                    Elbil elbil = documentSnapshot.toObject(Elbil.class);

                    String merke = elbil.getMerke();
                    String lader = elbil.getLader();
                    String batteri = elbil.getBatteri();

                    data += "Merke: " + merke + "\nLadetype: " + lader + "\nBatterikapasitet: " + batteri + "\n\n";
                }

                textViewData.setText(data);
            }
        });
    }

    public void AddCar(View view) {
        String merke = editTextMerke.getText().toString();
        String lader = editTextLader.getText().toString();
        String batteri = editTextBatteri.getText().toString();

        Elbil elbil = new Elbil(merke, lader, batteri);

        elbilRef.add(elbil);
    }


    public void loadCars(View view) {
        elbilRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshot) {
                String data = "";

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshot) {
                    Elbil elbil = documentSnapshot.toObject(Elbil.class);

                    String merke = elbil.getMerke();
                    String lader = elbil.getLader();
                    String batteri = elbil.getBatteri();

                    data += "Merke: " + merke + "\nLadetype: " + lader + "\nBatterikapasitet: " + batteri + "\n\n";
                }

                textViewData.setText(data);
            }
        });
    }


    private void findViewsById() {
        editTextMerke = findViewById(R.id.edit_text_merke);
        editTextLader = findViewById(R.id.edit_text_lader);
        editTextBatteri = findViewById(R.id.edit_text_batteri);

        textViewData = findViewById(R.id.text_view_data);

        btnSave = findViewById(R.id.btnAdd);
        btnRetrive = findViewById(R.id.btnLoad);
    }

}
