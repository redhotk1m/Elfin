package com.example.elfin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elfin.R;
import com.example.elfin.model.Elbil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CarListAdapter extends FirestoreRecyclerAdapter<Elbil, CarListAdapter.CarHolder> {

    private ArrayList<Elbil> mElbilList;

    private OnItemClickListener listener;

    public CarListAdapter(FirestoreRecyclerOptions<Elbil> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(CarHolder carHolder, int position, Elbil elbil) {
        carHolder.textViewBrand.setText(elbil.getBrand());
        carHolder.textViewModel.setText(elbil.getModel());
        carHolder.textViewDescription.setText(makeCarDescription(elbil));
    }

    @NonNull
    @Override
    public CarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item,
                parent, false);
        return new CarHolder(view);
    }


    public void filterList(ArrayList<Elbil> filteredlist) {
        mElbilList = filteredlist;
        notifyDataSetChanged();
    }


    public class CarHolder extends RecyclerView.ViewHolder {
        TextView textViewBrand, textViewModel, textViewDescription;

        private CarHolder(@NonNull View itemView) {
            super(itemView);
            textViewBrand = itemView.findViewById(R.id.text_view_brand);
            textViewModel = itemView.findViewById(R.id.text_view_model);
            textViewDescription = itemView.findViewById(R.id.text_view_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    private String makeCarDescription(Elbil elbil) {

        String description = "";

        Map<String, Double> specs = elbil.getSpecs();

        description += "2019-2020, " +
                "Batterikapasitet på " + specs.get("effect") +
                "\n, og " + elbil.getFastCharge() + " "
                + specs.get("battery") + "kW DC " + "Hurtiglader";

        return description;
    }

}
