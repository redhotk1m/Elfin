package com.example.elfin.car;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.elfin.MainActivity;
import com.example.elfin.listener.OnSpinnerEventsListener;

public class CustomCarSpinner extends AppCompatSpinner implements OnSpinnerEventsListener {

    private static final String TAG = "CustomCarSpinner";
    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;

    public CustomCarSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public CustomCarSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomCarSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCarSpinner(Context context, int mode) {
        super(context, mode);
    }

    public CustomCarSpinner(Context context) {
        super(context);
    }

    /*
    public interface OnSpinnerEventsListener {

        void onSpinnerOpened(Spinner spin);

        void onSpinnerClosed(Spinner spin);

    }
    */

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status indicator for the activity
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened(this);
        }
        return super.performClick();
    }

    public void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
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
        System.out.println("DROP DOWN WINDOW STATE;");
        if (hasBeenOpened()) setOnClickState(1);
        if (hasBeenOpened() && hasWindowFocus) {
            performClosedEvent();
            System.out.println("DROP DOWN WINDOW CLOSED");
            setOnClickState(0);
           // ((MainActivity) getContext()).registerForContextMenu();
        }
    }

    private void setOnClickState(int state) {
        if (getContext() instanceof MainActivity) {
            System.out.println("MAIN ACTIVITY WINDOW ON CLICK STATE: " + state);
            ((MainActivity) getContext()).setAdapterOnClickState(state);
        }
    }

    @Override
    public void onSpinnerOpened(Spinner spin) {
        System.out.println("ON SPINNER OPENED");
    }

    @Override
    public void onSpinnerClosed(Spinner spin) {
        System.out.println("ON SPINNER CLOSED");
    }
}
