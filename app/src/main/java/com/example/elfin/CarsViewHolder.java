package com.example.elfin;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private RecyclerViewClickListener mListener;

    TextView textViewBrand, textViewModel, textViewDescription;

    public CarsViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
        super(itemView);
        mListener = listener;
        itemView.setOnClickListener(this);

        textViewBrand = itemView.findViewById(R.id.text_view_brand);
        textViewModel = itemView.findViewById(R.id.text_view_model);
        textViewDescription = itemView.findViewById(R.id.text_view_description);

    }

    @Override
    public void onClick(View view) {
        mListener.onItemClick(view, getAdapterPosition());
    }
}


