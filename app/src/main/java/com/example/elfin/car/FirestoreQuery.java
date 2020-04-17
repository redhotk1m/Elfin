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

    private AddCarActivity addCarActivity;
    private CarSearchActivity carSearchActivity;
    private CollectionReference reference;
    private Query query;

    private List<Elbil> mElbilList = new ArrayList<>();
    private List<Elbil> allElbilList = new ArrayList<>();

    public FirestoreQuery(AddCarActivity addCarActivity, CollectionReference reference) {
        this.addCarActivity = addCarActivity;
        this.reference = reference;
    }

    public FirestoreQuery(CarSearchActivity carSearchActivity, CollectionReference reference) {
        this.carSearchActivity = carSearchActivity;
        this.reference = reference;
    }

    public void getInitFirestoreData() {
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
                } else
                    Toast.makeText(addCarActivity, "COULD NOT LOCATE FIRESTORE DATA..!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void executeCompoundQuery(Query query) {
        executeQuery(query);
    }

    public Query makeCompoundQuery(CollectionReference reference, String modelResponse,
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
        if (!fieldsMap.get(MODELYEAR).isEmpty()) {
            query = query
                    .whereEqualTo(MODELYEAR, fieldsMap.get(MODELYEAR));
            System.out.println("MODEL YEAR: " + fieldsMap.get(MODELYEAR));
        } else System.out.println("MODEL YEAR EMPTY: " + fieldsMap.get(MODELYEAR));

        return query;
    }

    private void executeQuery(Query query) {
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
                } else {
                    System.out.println("TASK FAILED, COULD NOT LOCATE FIRESTORE DATA..!");
                }

                carSearchActivity.handleFirestoreQuery(mElbilList);
            }
        });

                /*
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        findElbil(querySnapshot);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        //todo: handle Firestore Query in Activity..?
                        // searchCarActivity.handleFirestoreQuery(mElbilList);
                    }
                });

                 */
    }

    private void findElbil(QuerySnapshot querySnapshot) {
        mElbilList.clear();
        for (DocumentSnapshot documentSnapshot : querySnapshot) {
            Elbil elbil = documentSnapshot.toObject(Elbil.class);
            elbil.setDocumentId(documentSnapshot.getId());

            System.out.println("FIRESTORE ELBIL: " + elbil.toString());

            mElbilList.add(elbil);
        }
        //  addCarActivity.handleFirestoreQuery(mElbilList);

        carSearchActivity.handleFirestoreQuery(mElbilList);
    }


    public void compoundFirestoreQuery(String model, String modelYear) {
        reference.whereEqualTo(MODEL, model)
                .whereEqualTo(MODELYEAR, modelYear)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        mElbilList.clear();
                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                            Elbil elbil = documentSnapshot.toObject(Elbil.class);
                            elbil.setDocumentId(documentSnapshot.getId());

                            mElbilList.add(elbil);
                        }
                        //  addCarActivity.handleFirestoreQuery(mElbilList);

                        carSearchActivity.handleFirestoreQuery(mElbilList);

                        //setmElbilList(mElbilList);
                        /*
                        if (mElbilList.size() == 1) {
                            Intent intent = new Intent(AddCarActivity.this, CarInfoActivity.class);
                            intent.putExtra("Elbil", mElbilList.get(0));
                            startActivity(intent);
                        } else {
                            // initSpinner(BRAND, spinnerBrands); //todo: fjerne etter testing
                            // initSpinner(MODEL, spinnerModels); //todo: fjerne etter testing
                        }

                         */
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void compoundFirestoreQuery(String brand, String model, String modelYear) {
    }

    private void fetchFirstoreData(CollectionReference querySearch, final String dataField,
                                   final ArrayAdapter<String> adapter) {
        querySearch.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //allElbilList.clear();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String data = documentSnapshot.getString(dataField);
                        //  if (dataField.equals(BRAND) && !brands.contains(data)) brands.add(data);
                        //  else if (dataField.equals(MODEL) && !models.contains(data)) models.add(data);
                        //  else System.out.println("...");
                        //Toast.makeText(AddCarActivity.this, "NO SUCH DATAFIELD!", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public List<Elbil> getmElbilList() {
        return mElbilList;
    }

    public void setmElbilList(List<Elbil> mElbilList) {
        this.mElbilList = mElbilList;
    }
}
