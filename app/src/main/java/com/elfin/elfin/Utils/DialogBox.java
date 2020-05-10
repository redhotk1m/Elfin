package com.elfin.elfin.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.elfin.elfin.Activities.Station.ChargingStations;
import com.elfin.elfin.car.CarInfoActivity;

/**
 * CLass for making our different dialogBoxes. Depending on the option parameter it decides what to do.
 */

public class DialogBox {

    Context context;
    String title;
    String message;
    String yesButton;
    String noButton;
    int option;
    AlertDialog.Builder alertDialog;

    private Intent intent;
    private ChargingStations chargingStations;

    public DialogBox(Context context, String title, String message, String yesButton, String noButton, int option){
        this.context=context;
        this.title=title;
        this.message=message;
        this.yesButton=yesButton;
        this.noButton=noButton;
        this.option=option;
        alertDialog=defaultDialog();
    }

    public void simpleDialogBox(){
        alertDialog.setPositiveButton(yesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });

    }

    public AlertDialog.Builder defaultDialog(){
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        return alertDialog;
    }


    public void createDialogBox(){
        alertDialog.setPositiveButton(yesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                switch (option){
                    case 1:
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                        break;
                    case 2:
                        chargingStations.createNewRoute();
                        break;
                    case 3:
                        intent = getIntent();
                        context.startActivity(intent);
                        if (context instanceof CarInfoActivity) ((CarInfoActivity) context).finish();
                    default:
                        dialog.cancel();
                }
            }
        });

        alertDialog.setNegativeButton(noButton, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (context instanceof CarInfoActivity) ((CarInfoActivity) context).finish();
                if (option == 2) {
                    chargingStations.setHasCheckedIfCreateNewRoute(false);
                }
                dialog.cancel();
            }
        });


        alertDialog.show();
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public void setChargingStations(ChargingStations chargingStations) {
        this.chargingStations = chargingStations;
    }
}
