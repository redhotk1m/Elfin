package com.example.elfin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.elfin.R;
import com.example.elfin.car.Elbil;

import java.util.ArrayList;

public class CarAdapter extends ArrayAdapter {

    public CarAdapter(Context context, ArrayList<Elbil> elbils) {
        super(context, 0, elbils);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.car_spinner_row, parent, false
            );
        }

        ImageView imageViewCarIcon = convertView.findViewById(R.id.image_view_car_icon);
        TextView textViewCarDisplay = convertView.findViewById(R.id.text_view_car_display);

        Elbil currentCarItem = (Elbil) getItem(position);

        if (currentCarItem != null) {
            imageViewCarIcon.setImageResource(currentCarItem.getIconImage());
            textViewCarDisplay.setText(currentCarItem.getSpinnerDisplay());
        }

        return convertView;
    }
}
