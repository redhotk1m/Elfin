package com.elfin.elfin.car;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirestoreQuery {

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
                    carSearchActivity.setAllCarsList(allElbilList);
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
                        mElbilList.add(elbil);
                    }
                }

                carSearchActivity.handleFirestoreQuery(mElbilList);
            }
        });
    }

    protected Query makeCompoundQuery(CollectionReference reference, String modelResponse,
                                      HashMap<String, String> fieldsMap) {
        query = reference;
        switch (modelResponse) {
            case BRAND + MODEL + BATTERY:
                query = query
                        .whereEqualTo(BRAND, fieldsMap.get(BRAND))
                        .whereEqualTo(MODEL, fieldsMap.get(MODEL))
                        .whereEqualTo(BATTERY, fieldsMap.get(BATTERY));
                break;
            case BRAND + MODEL:
                query = query
                        .whereEqualTo(BRAND, fieldsMap.get(BRAND))
                        .whereEqualTo(MODEL, fieldsMap.get(MODEL));
                break;
            case BRAND + BATTERY:
                query = query
                        .whereEqualTo(BRAND, fieldsMap.get(BRAND))
                        .whereEqualTo(BATTERY, fieldsMap.get(BATTERY));
                break;
            case MODEL + BATTERY:
                query = query
                        .whereEqualTo(MODEL, fieldsMap.get(MODEL))
                        .whereEqualTo(BATTERY, fieldsMap.get(BATTERY));
                break;
            case BRAND:
                query = query
                        .whereEqualTo(BRAND, fieldsMap.get(BRAND));
                break;
            case MODEL:
                query = query.whereEqualTo(MODEL, fieldsMap.get(MODEL));
                break;
            case BATTERY:
                query = query
                        .whereEqualTo(BATTERY, fieldsMap.get(BATTERY));
                break;
            default:
        }


        //todo: handle if FOUND MODEL YEAR IS NOT IN DATABASE


        if (fieldsMap.get(MODELYEAR) == null) fieldsMap.put(MODELYEAR, "");
        if (!fieldsMap.get(MODELYEAR).isEmpty()) {
            query = query
                    .whereEqualTo(MODELYEAR, fieldsMap.get(MODELYEAR));
        }

        return query;
    }
}
