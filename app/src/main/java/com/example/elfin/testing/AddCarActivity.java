package com.example.elfin.testing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCarActivity extends AppCompatActivity {

    private final String TAG = "AddCarActivity";

    private final String KEY_MERKE = "merke";
    private final String KEY_LADER = "lader";
    private final String KEY_BATTERI = "batteri";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference elRef = db.document("Elbiler/EL 1");

    EditText editTextMerke, editTextLader, editTextBatteri;
    TextView textViewMerke, textViewType, textViewBatteri;
    Button btnSave, btnRetrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        findViewsById();
    }

    private void findViewsById() {

        editTextMerke = findViewById(R.id.edit_text_merke);
        editTextLader = findViewById(R.id.edit_text_lader);
        editTextBatteri = findViewById(R.id.edit_text_batteri);

        textViewMerke = findViewById(R.id.text_view_merke);
        textViewType = findViewById(R.id.text_view_type);
        textViewBatteri = findViewById(R.id.text_view_batteri);

        btnSave = findViewById(R.id.btnSave);
        btnRetrive = findViewById(R.id.btnRetrive);
    }

    public void saveCar(View view) {

        String merke = editTextMerke.getText().toString();
        String lader = editTextLader.getText().toString();
        String batteri = editTextBatteri.getText().toString();

        Map<String, Object> elbil = new HashMap<>();

        elbil.put(KEY_MERKE, merke);
        elbil.put(KEY_LADER, lader);
        elbil.put(KEY_BATTERI, batteri);

        elRef.set(elbil)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddCarActivity.this, "Car saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCarActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }


    public void loadCar(View view) {

        elRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String merke = documentSnapshot.getString(KEY_MERKE);
                            String lader = documentSnapshot.getString(KEY_LADER);
                            String batteri = documentSnapshot.getString(KEY_BATTERI);

                            //Map<String, Object> elbil = documentSnapshot.getData();

                            textViewMerke.setText(merke);
                            textViewType.setText(lader);
                            textViewBatteri.setText(batteri);
                        } else {
                            Toast.makeText(AddCarActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCarActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

    }


}
