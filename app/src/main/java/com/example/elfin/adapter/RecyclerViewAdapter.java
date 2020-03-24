package com.example.elfin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.elfin.R;
import com.example.elfin.RecyclerViewClickListener;
import com.example.elfin.car.CarsViewHolder;
import com.example.elfin.car.Elbil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends FirestoreRecyclerAdapter<Elbil, CarsViewHolder> {
        //RecyclerView.Adapter<RecyclerView.ViewHolder> {
        //FirestoreRecyclerAdapter<Elbil, CarsViewHolder> {

    private RecyclerViewClickListener mListener;
    private List<Elbil> mElbilList = new ArrayList<>();


    public RecyclerViewAdapter(FirestoreRecyclerOptions<Elbil> options, RecyclerViewClickListener listener){
        super(options);
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mElbilList.size();
    }








    public void updateData(List<Elbil> elbilList) {
        mElbilList.clear();
        mElbilList.addAll(mElbilList);
        notifyDataSetChanged();
    }

    /*
    RecyclerViewAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.car_item, parent, false);
        return new CarsViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CarsViewHolder) {
            CarsViewHolder carsViewHolder = (CarsViewHolder) holder;

            Elbil elbil = new Elbil();
            //set values of data here
            carsViewHolder.textViewBrand.setText(elbil.getBrand());
            carsViewHolder.textViewModel.setText(elbil.getModel());
            carsViewHolder.textViewDescription.setText(makeCarDescription(elbil));
        }
    }

    @Override
    public int getItemCount() {
        return mElbilList.size();
    }

     */




    private String makeCarDescription(Elbil elbil) {

        String description = "";

        Map<String, Double> specs = elbil.getSpecs();

        description += "2019-2020, " +
                "Batterikapasitet på " + specs.get("effect") +
                "\n, og " + elbil.getFastCharge() + " "
                + specs.get("battery") + "kW DC " + "Hurtiglader";

        return description;
    }



    @Override
    protected void onBindViewHolder(CarsViewHolder viewHolder, int i, Elbil elbil) {

        //set values of data here
        viewHolder.textViewBrand.setText(elbil.getBrand());
        viewHolder.textViewModel.setText(elbil.getModel());
        viewHolder.textViewDescription.setText(makeCarDescription(elbil));
    }

    @NonNull
    @Override
    public CarsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.car_item, parent, false);
        return new CarsViewHolder(v, mListener);
    }


}
