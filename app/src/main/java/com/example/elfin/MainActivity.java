package com.example.elfin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.elfin.API.CarInfoAPI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner dropdown = findViewById(R.id.spinner);
        //dropdown.setPrompt("EB12342 VW e-Golf");
        String[] items = new String[]{"EB 12342 VW e-Golf", "Legg til bil"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setPrompt("EB12342 VW e-Golf");

        System.out.println("yoyoyo");



    }
}
