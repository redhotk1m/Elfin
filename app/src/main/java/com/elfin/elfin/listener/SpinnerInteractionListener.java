package com.elfin.elfin.listener;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.elfin.elfin.MainActivity;

public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private MainActivity mainActivity;
    private boolean selection = false;
    // private boolean selection = true;

    public SpinnerInteractionListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        selection = true;
        //  System.out.println("ON TOUCH SELECTION ; SETTING CAF ADAPTER ON CLICK == 1");
        mainActivity.setAdapterOnClickState(1);
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (selection) {
            // Selection handling code
            //  System.out.println("ON ITEM SELECTED ; SELECTION == " + selection);
            mainActivity.getSelectedCar(view);
            selection = false;
        } //else System.out.println("ON ITEM NOT SELECTED ; SELECTION == " + selection);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }
}
