package com.example.elfin.listener;

import android.view.View;
import android.widget.AdapterView;

import com.example.elfin.car.CarInfoActivity;

public class OnSelectedItemListener implements AdapterView.OnItemSelectedListener {

    private CarInfoActivity carInfoActivity;

    public OnSelectedItemListener(CarInfoActivity carInfoActivity) {
        this.carInfoActivity = carInfoActivity;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
