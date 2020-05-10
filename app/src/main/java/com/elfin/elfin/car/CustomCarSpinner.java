package com.elfin.elfin.car;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatSpinner;

import com.elfin.elfin.MainActivity;
import com.elfin.elfin.listener.OnSpinnerEventsListener;

public class CustomCarSpinner extends AppCompatSpinner implements OnSpinnerEventsListener {

    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;

    public CustomCarSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomCarSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCarSpinner(Context context) {
        super(context);
    }


    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status indicator for the activity
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened(this);
        }
        return super.performClick();
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onSpinnerClosed(this);
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasBeenOpened()) setOnClickState(1);
        if (hasBeenOpened() && hasWindowFocus) {
            performClosedEvent();
           setOnClickState(0);
        }
    }

    private void setOnClickState(int state) {
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext()).setAdapterOnClickState(state);
        }
    }

    @Override
    public void onSpinnerOpened(Spinner spin) {
    }

    @Override
    public void onSpinnerClosed(Spinner spin) {
    }
}
