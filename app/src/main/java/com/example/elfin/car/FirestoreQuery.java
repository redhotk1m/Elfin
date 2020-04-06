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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreQuery {

    private static final String TAG = "FirestoreQuery";
    private final String BRAND = "brand";
    private final String MODEL = "model";
    private final String MODELYEAR = "modelYear";
    private final String BATTERY = "battery";

    private AddCarActivity addCarActivity;
    private CollectionReference reference;

    private List<Elbil> mElbilList = new ArrayList<>();
    private List<Elbil> allElbilList = new ArrayList<>();

    public FirestoreQuery(AddCarActivity addCarActivity, CollectionReference reference) {
        this.addCarActivity = addCarActivity;
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
                    addCarActivity.setAllCarsList(allElbilList);
                } else
                    Toast.makeText(addCarActivity, "COULD NOT LOCATE FIRESTORE DATA..!", Toast.LENGTH_LONG).show();
            }
        });
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
                        addCarActivity.handleFirestoreQuery(mElbilList);

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
