package com.example.elfin.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.webkit.PermissionRequest;

import androidx.core.app.ActivityCompat;

public class DialogBox {

    Context context;
    String title;
    String message;
    String yesButton;
    String noButton;
    int option;
    Activity activity;

    public DialogBox(Context context, String title, String message, String yesButton, String noButton, int option){
        this.context=context;
        this.title=title;
        this.message=message;
        this.yesButton=yesButton;
        this.noButton=noButton;
        this.option=option;
    }


    public void createDialogBox(){
        //AlertDialog.Builder alertDialog= new AlertDialog().Builder(context);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(yesButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                switch (option){
                    case 1:
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    case 2:
                        //ActivityCompat.requestPermissions();
                    default:
                        dialog.cancel();
                }
            }
        });

        alertDialog.setNegativeButton(noButton, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }


}
