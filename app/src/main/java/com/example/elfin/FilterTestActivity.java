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
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FilterTestActivity extends AppCompatActivity {

    private ConstraintLayout filter_layout;

    private ConstraintSet constraintSetOld = new ConstraintSet();
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private boolean altLayout;

    private boolean isOpen = false;

    private FloatingActionButton floatingActionButton;

    private ImageView btn1, btn2, btn3, btn4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_test);

        filter_layout =  findViewById(R.id.filter_layout);

        constraintSetOld.clone(filter_layout);
        constraintSetNew.clone(this, R.xml.filtrering);

        floatingActionButton = findViewById(R.id.floatingActionButton);

        btn1 = findViewById(R.id.free_park);
        final int btn01 = 0;
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn01 == 0) {
                    btn1.setBackgroundColor(134);
                }
            }
        });
        //btn2 = findViewById(R.id.)
        btn3 = findViewById(R.id.restaurant);

    }


    public void toggleView(View view){

        Transition changeBounds = new ChangeBounds();
        changeBounds.setInterpolator(new OvershootInterpolator());

        TransitionManager.beginDelayedTransition(filter_layout, changeBounds);


        /*
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isOpen){
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    layout2.applyTo(constraintLayout);
                    isOpen = !isOpen;
                } else {
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    layout1.applyTo(constraintLayout);
                    isOpen = !isOpen;
                }

            }
        });
        */


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
