package com.elfin.elfin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.elfin.elfin.MainActivity;
import com.elfin.elfin.R;
import com.elfin.elfin.car.Elbil;

import java.util.ArrayList;

public class CarAdapter extends ArrayAdapter {

    private Context mContext;
    private int onClick = 0;

    public CarAdapter(Context context, ArrayList<Elbil> elbils) {
        super(context, 0, elbils);
        this.mContext = context;
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

        final Elbil currentCarItem = (Elbil) getItem(position);

        if (currentCarItem != null) {
            imageViewCarIcon.setImageResource(currentCarItem.getIconImage());
            textViewCarDisplay.setText(currentCarItem.getSpinnerDisplay());
        }

        convertView.setClickable(false);
        convertView.setLongClickable(false);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof MainActivity) {
                    if (onClick == 0) {
                        ((MainActivity) mContext).performSpinnerClick(currentCarItem, onClick);
                        onClick++;
                    } else if (onClick == 1) {
                        ((MainActivity) mContext).performSpinnerClick(currentCarItem, onClick);
                        onClick = 0;
                    }
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mContext instanceof MainActivity) {
                    if (onClick == 1) {
                        ((MainActivity) mContext).setOnLongClicked(currentCarItem);
                    }
                    onClick = 0;
                }
                return false;
            }
        });

        return convertView;
    }

    public void setOnClick(int onClick) {
        this.onClick = onClick;
    }
}
