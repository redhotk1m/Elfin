package com.example.elfin.car;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.API.CarInfoAPI;
import com.example.elfin.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CarSearchActivity extends AppCompatActivity {

    private static final String TAG = "CarSearchActivity";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";
    private final String FASTCHARGE = "fastCharge";

    private Elbil elbil;
    private List<Elbil> allCarsList = new ArrayList<>();
    private List<Elbil> mCarList = new ArrayList<>();

    private FirestoreQuery firestoreQuery;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference elbilReference = db.collection("elbiler");

    private EditText editTextSearchRegNr;
    private ImageButton searchRegNrBtn;
    private Button searchCarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_search);

        findViewsById();

        firestoreQuery = new FirestoreQuery(this, elbilReference);

        searchRegNrBtn.setOnClickListener(myOnClickListener);
        searchCarBtn.setOnClickListener(myOnClickListener);
    }

    private void findViewsById() {
        editTextSearchRegNr = findViewById(R.id.edit_text_search_regNr);
        searchRegNrBtn = findViewById(R.id.image_button_search_icon);
        searchCarBtn = findViewById(R.id.button_search_car);
    }

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_button_search_icon:
                   // executeCarInfoApi();

                    break;
                case R.id.button_search_car:
//                    mCarList = searchCar();

                    if (allCarsList.size() > 0) {
                        Intent intent = new Intent(CarSearchActivity.this, CarSelectionActivity.class);
                        ArrayList<Elbil> elbils = new ArrayList<>(allCarsList);
                        intent.putParcelableArrayListExtra("CarList", elbils);
                        startActivity(intent);
                    } else System.out.println("ELBILS SIZE IS 0");

                    /*
                    if (mCarList.size() == 1) {
                        Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
                        intent.putExtra("Elbil", mCarList.get(0));
                        startActivity(intent);
                    } else {
                        initSpinner(BATTERY, spinnerBatteries);
                        spinnerSelection.filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
                        adapterBattery.notifyDataSetChanged();

                        // initSpinner(BATTERY, spinnerBatteries);
                        // initSpinner(FASTCHARGE, spinnerCharges);
                    }
                    */


                    /*
                    initSpinner(BATTERY, spinnerBatteries);
                    spinnerSelection.filteredCarsSelection(spinnerModelYears, BATTERY, batteries);
                    adapterBattery.notifyDataSetChanged();
                     */

                    //  Toast.makeText(AddCarActivity.this, "LIST SIZE: " + allCarsList.size(), Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(CarSearchActivity.this, "CLICKABLE ID NOT FOUND..", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void executeCarInfoApi() {
        CarInfoAPI carInfoAPI = new CarInfoAPI();
        carInfoAPI.setCarSearchActivity(CarSearchActivity.this);
        //todo: validate user input, disable input space
        String regNr = editTextSearchRegNr.getText().toString();
        Toast.makeText(CarSearchActivity.this, "regNr: " + regNr.trim(), Toast.LENGTH_SHORT).show();
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
                firestoreQuery.compoundFirestoreQuery(model.toLowerCase(), modelYear.toLowerCase());
            Toast.makeText(this, "FIRESTORE SIZE: " + mCarList.size(), Toast.LENGTH_SHORT).show();
            //compoundFirestoreQuery(model.toLowerCase(), modelYear.toLowerCase());
            Toast.makeText(this, "model: " + model + " : " + modelYear, Toast.LENGTH_SHORT).show();
        }

    }

    public void handleFirestoreQuery(List<Elbil> mElbilList) {
        Toast.makeText(this, "FIRESTORE CAR LIST SIZE: " + mElbilList.size(), Toast.LENGTH_LONG).show();
        if (mElbilList.size() == 1) {
            Intent intent = new Intent(CarSearchActivity.this, CarInfoActivity.class);
            intent.putExtra("Elbil", mElbilList.get(0));
            startActivity(intent);
        } else {
           // carCheckBox.setChecked(true);
            //todo: sjekke opp mot lister og la bruker velge riktig model hvis flere finnes i databasen
            // enableSpinnerSelection();
          //  spinnerSelection.getFilteredCars(spinnerBrands, BRAND, brands);
          //  spinnerSelection.setSpinnerSelection(spinnerBrands, brands);
          //  adapterBrands.notifyDataSetChanged();


            // initSpinner(BRAND, spinnerBrands); //todo: fjerne etter testing
            // initSpinner(MODEL, spinnerModels); //todo: fjerne etter testing
        }
    }

    public List<Elbil> getAllCars() {
        return this.allCarsList;
    }

    public void setAllCarsList(List<Elbil> allCarsList) {
        this.allCarsList = allCarsList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (allCarsList.size() == 0) firestoreQuery.getInitFirestoreData();
        //  getInitFirestoreData(elbilReference);
    }
}
