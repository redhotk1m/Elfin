package com.elfin.elfin.car;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.elfin.elfin.R;
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

    private EditText editTextMerke, editTextModell, editTextModelYear, editTextBattery, editTextFastCharge;
    private TextView textViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);

        findViewsById();
    }



    public void saveCar(View view) {
        String merke = editTextMerke.getText().toString().toLowerCase();
        String model = editTextModell.getText().toString().toLowerCase();
        String modelYear = editTextModelYear.getText().toString().toLowerCase();
        String battery = editTextBattery.getText().toString().toLowerCase();
       // String fastCharge = editTextFastCharge.getText().toString();
        String fastCharge[] = editTextFastCharge.getText().toString().split("\\s*,\\s*");

       // String specInput = editTextSpecs.getText().toString().toLowerCase();
        /*
        Map<String, Double> specs = new HashMap<>();
        String[] specArray = specInput.split("\\s*,\\s*");
        for(int i = 0; i < specArray.length; i++) {
            //specs.put(specArray[i], Double.parseDouble(specArray[s]));
            if (i == 0) specs.put(specArray[i], Double.parseDouble(specArray[1]));
                //System.out.println(specArray[i] + " ; " + specArray[1]);
            if (i == 2) specs.put("battery", Double.parseDouble(specArray[i]));
                //System.out.println("battery" + " ; " + specArray[i]);
            //if (i == 1) specs.put("battery", Double.parseDouble(specArray[i]));
        }
        */

        CollectionReference elbiler = FirebaseFirestore.getInstance()
                .collection("elbiler");


        Map<String, Object> elbilMap = new HashMap<>();
        elbilMap.put("brand", merke);
        elbilMap.put("model", model);
        elbilMap.put("modelYear", modelYear);
        elbilMap.put("battery", battery);
        elbilMap.put("fastCharge", fastCharge[0]);
        elbilMap.put("effect", fastCharge[1]);
        //elbilMap.put("specs", Arrays.asList(specArray));
        //elbilMap.put("specs", specs);

        elbiler.document().set(elbilMap);


        //elbilRef.document(merke).set(new Elbil(model, modelYear, specs));

        //elbilRef.document(merke).collection(model).add(new Elbil(modelYear, specs));

        //elbilRef.add(new Elbil(merke, model, modelYear, specs));
        //Toast.makeText(this, "Elbil added!", Toast.LENGTH_SHORT).show();
        //finish();

        //elbilRef.document(modell).

        //elbilRef.document().collection(modell).add(new Elbil(modelYear, hurtiglader, specs));
       // Toast.makeText(this, "Elbil added!", Toast.LENGTH_SHORT).show();
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
        editTextBattery = findViewById(R.id.edit_text_add_battery);
        editTextFastCharge = findViewById(R.id.edit_text_add_fast_charge);
        textViewData = findViewById(R.id.text_view_data);
    }

    private String getCarAttributes(Elbil elbil, String data) {
        String documentId = elbil.getDocumentId();
        String merke = elbil.getBrand();
        String modell = elbil.getModel();
        String modelYear = elbil.getModelYear();
        String battery = elbil.getBattery();
        String fastCharge = elbil.getFastCharge();
        //String hurtiglader = elbil.getFastCharge();

        data += "ID: " + documentId
                + "\nMerke: " + merke
                + "\nModell: " + modell
                + "\nModell Year: " + modelYear
                + "\nBattery: " + battery
                + "\nFastCharge: " + fastCharge
                + "\nSpecs: ";

        for (String spec : elbil.getSpecs().keySet()) {
            data += "\n-" + spec + ": " + elbil.getSpecs().get(spec);
        }

        data += "\n\n";


        return data;
    }


}
