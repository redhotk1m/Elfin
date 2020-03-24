package com.example.elfin.testing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.R;
import com.example.elfin.car.Elbil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class NewCarActivity extends AppCompatActivity {

    private static final String TAG = "NewCarActivity";

    private EditText editTextMerke, editTextModell, editTextModelYear, editTextFastCharge, editTextSpecs;
    private TextView textViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);

        findViewsById();
    }

    public void saveCar(View view) {
        String merke = editTextMerke.getText().toString();
        String modell = editTextModell.getText().toString();
        String modelYear = editTextModelYear.getText().toString();
        String hurtiglader = editTextFastCharge.getText().toString();
        String specInput = editTextSpecs.getText().toString();

        Map<String, Double> specs = new HashMap<>();
        String[] specArray = specInput.split("\\s*,\\s*");
        boolean firstIterationFlag = true;
        for (String spec : specArray) {
            if (firstIterationFlag) specs.put("battery", Double.parseDouble(spec));
            else specs.put("effect", Double.parseDouble(spec));

            firstIterationFlag = false;
        }

        CollectionReference elbilRef = FirebaseFirestore.getInstance()
                .collection("Elbiler");
        elbilRef.add(new Elbil(merke, modell, modelYear, hurtiglader, specs));
        Toast.makeText(this, "Elbil added!", Toast.LENGTH_SHORT).show();
        //finish();
    }


    public void loadCar(View view) {
        CollectionReference elbilRef = FirebaseFirestore.getInstance()
                .collection("Elbiler");
        elbilRef.whereEqualTo("model", editTextModell.getText().toString()).get()
                //.get()
                //whereEqualTo("specs.effect", 150).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        String data = "";
                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                            Elbil elbil = documentSnapshot.toObject(Elbil.class);
                            elbil.setDocumentId(documentSnapshot.getId());

                            data = getCarAttributes(elbil, data);
                        }
                        textViewData.setText(data);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }


    private void findViewsById() {
        editTextMerke = findViewById(R.id.edit_text_add_brand);
        editTextModell = findViewById(R.id.edit_text_add_model);
        editTextModelYear = findViewById(R.id.edit_text_add_model_year);
        editTextFastCharge = findViewById(R.id.edit_text_add_fast_charge);
        editTextSpecs = findViewById(R.id.edit_text_add_specs);
        textViewData = findViewById(R.id.text_view_data);
    }

    private String getCarAttributes(Elbil elbil, String data) {
        String documentId = elbil.getDocumentId();
        String merke = elbil.getBrand();
        String modell = elbil.getModel();
        String modelYear = elbil.getModelYear();
        String hurtiglader = elbil.getFastCharge();

        data += "ID: " + documentId
                + "\nMerke: " + merke
                + "\nModell: " + modell
                + "\nModell Year: " + modelYear
                + "\nLadetype: " + hurtiglader
                + "\nSpecs: ";

        for (String spec : elbil.getSpecs().keySet()) {
            data += "\n-" + spec + ": " + elbil.getSpecs().get(spec);
        }

        data += "\n\n";


        return data;
    }


}
