package com.example.elfin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FilterTestActivity extends AppCompatActivity {

    private ConstraintLayout filter_layout;

    private ConstraintSet constraintSetOld = new ConstraintSet();
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private boolean altLayout;


    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_test);

        filter_layout =  findViewById(R.id.filter_layout);

        constraintSetOld.clone(filter_layout);
        constraintSetNew.clone(this, R.layout.filtrering);

        floatingActionButton = findViewById(R.id.floatingActionButton);

    }


    public void toggleView(View view){

        Transition changeBounds = new ChangeBounds();
        changeBounds.setInterpolator(new OvershootInterpolator());

        TransitionManager.beginDelayedTransition(filter_layout, changeBounds);

        if (!altLayout) {
            constraintSetNew.applyTo(filter_layout);

            floatingActionButton.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.rotation) );

            //floatingActionButton.setRotation(180);

            altLayout = true;
        } else {
            constraintSetOld.applyTo(filter_layout);
            altLayout = false;
        }

    }


}
