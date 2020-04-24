package com.example.elfin.car;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirestoreQuery {

    private static final String TAG = "FirestoreQuery";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";

    private CarSearchActivity carSearchActivity;
    private CollectionReference reference;
    private Query query;

    private List<Elbil> mElbilList = new ArrayList<>();
    private List<Elbil> allElbilList = new ArrayList<>();

    public FirestoreQuery(CarSearchActivity carSearchActivity, CollectionReference reference) {
        this.carSearchActivity = carSearchActivity;
        this.reference = reference;
    }

    protected void getInitFirestoreData() {
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Elbil elbil = documentSnapshot.toObject(Elbil.class);
                        allElbilList.add(elbil);
                    }
                    // addCarActivity.setAllCarsList(allElbilList);
                    carSearchActivity.setAllCarsList(allElbilList);
                } else {
                    System.out.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
                    System.out.println("UNABLE TO FETCH INIT FIRESTORE DATA...!");
                    System.out.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
                  //  Toast.makeText(addCarActivity, "UNABLE TO FETCH FIRESTORE DATA..!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    protected void executeCompoundQuery(Query query) {
        mElbilList.clear();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Elbil elbil = documentSnapshot.toObject(Elbil.class);
                        elbil.setDocumentId(documentSnapshot.getId());

                        System.out.println("FIRESTORE ELBIL: " + elbil.toString());

                        mElbilList.add(elbil);
                    }
                } else System.out.println("TASK FAILED, UNABLE TO FETCH FIRESTORE DATA..!");

                carSearchActivity.handleFirestoreQuery(mElbilList);
            }
        });
    }

    protected Query makeCompoundQuery(CollectionReference reference, String modelResponse,
                                      HashMap<String, String> fieldsMap) {
        System.out.println("\n\nMAKING COMPOUND FIRESTORE QUERY>\n");
        query = reference;
        switch (modelResponse) {
            case BRAND + MODEL + BATTERY:
                System.out.println(modelResponse + " == " + BRAND + MODEL + BATTERY);
                query = query
                        .whereEqualTo(BRAND, fieldsMap.get(BRAND))
                        .whereEqualTo(MODEL, fieldsMap.get(MODEL))
                        .whereEqualTo(BATTERY, fieldsMap.get(BATTERY));
                break;
            case BRAND + MODEL:
                System.out.println(modelResponse + " == " + BRAND + MODEL);
                query = query
                        .whereEqualTo(BRAND, fieldsMap.get(BRAND))
                        .whereEqualTo(MODEL, fieldsMap.get(MODEL));
                break;
            case BRAND + BATTERY:
                System.out.println(modelResponse + " == " + BRAND + BATTERY);
                query = query
                        .whereEqualTo(BRAND, fieldsMap.get(BRAND))
                        .whereEqualTo(BATTERY, fieldsMap.get(BATTERY));
                break;
            case MODEL + BATTERY:
                System.out.println(modelResponse + " == " + MODEL + BATTERY);
                query = query
                        .whereEqualTo(MODEL, fieldsMap.get(MODEL))
                        .whereEqualTo(BATTERY, fieldsMap.get(BATTERY));
                break;
            case BRAND:
                System.out.println(modelResponse + " == " + BRAND);
                query = query
                        .whereEqualTo(BRAND, fieldsMap.get(BRAND));
                break;
            case MODEL:
                System.out.println(modelResponse + " == " + MODEL);
                query = query.whereEqualTo(MODEL, fieldsMap.get(MODEL));
                break;
            case BATTERY:
                System.out.println(modelResponse + " == " + BATTERY);
                query = query
                        .whereEqualTo(BATTERY, fieldsMap.get(BATTERY));
                break;
            default:
                System.out.println("...");
        }


        //todo: handle if FOUND MODEL YEAR IS NOT IN DATABASE

        /*

        if (!fieldsMap.get(MODELYEAR).isEmpty()) {
            query = query
                    .whereEqualTo(MODELYEAR, fieldsMap.get(MODELYEAR));
            System.out.println("MODEL YEAR: " + fieldsMap.get(MODELYEAR));
        } else System.out.println("MODEL YEAR EMPTY: " + fieldsMap.get(MODELYEAR));

         */

        return query;
    }
}
