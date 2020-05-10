package com.elfin.elfin.listener;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.elfin.elfin.MainActivity;

public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private MainActivity mainActivity;
    private boolean selection = false;
    public SpinnerInteractionListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        selection = true;
        mainActivity.setAdapterOnClickState(1);
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (selection) {
            // Selection handling code
            mainActivity.getSelectedCar(view);
            selection = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }
}
