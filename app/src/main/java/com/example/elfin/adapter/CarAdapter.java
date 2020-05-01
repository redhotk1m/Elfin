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

import com.example.elfin.MainActivity;
import com.example.elfin.R;
import com.example.elfin.car.Elbil;

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

        //  convertView.setClickable(false);
        //  convertView.setLongClickable(false);


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
                //  System.out.println("CAR ADAPTER ON CLICKED: " + currentCarItem.toString());
                if (mContext instanceof MainActivity) {
                    if (onClick == 0) {
                        //  System.out.println("FIRST CLICK");
                        ((MainActivity) mContext).performSpinnerClick(currentCarItem, onClick);
                        onClick++;
                    } else if (onClick == 1) {
                        //  System.out.println("SECOND CLICK");
                        ((MainActivity) mContext).performSpinnerClick(currentCarItem, onClick);
                        onClick = 0;
                    }
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //  System.out.println("(CAR ADAPTER) ON LONG CLICKED: " + currentCarItem.toString());
                if (mContext instanceof MainActivity) {
                    //  ((MainActivity) mContext).showPopup(view, currentCarItem);
                    if (onClick == 1) {
                        //    System.out.println("SHOW POPUP MENU ; ON CLICK == " + onClick);
//                        ((MainActivity) mContext).showPopupMenu(view, currentCarItem);
                        ((MainActivity) mContext).setOnLongClicked(currentCarItem);
                        // onClick = 0;
                    } // else System.out.println("NOT SHOW POPUP MENU ; ON CLICK == " + onClick);
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
