package com.example.elfin.testing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elfin.R;
import com.example.elfin.model.Elbil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCarActivity extends AppCompatActivity {

    EditText txtMerke, txtLader, txtBatteri;
    Button btnSave, btnRetrive;
    TextView tvMerke, tvType;

    DatabaseReference dbReff;

    Elbil elbil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        tvMerke = findViewById(R.id.tvMerke);
        tvType = findViewById(R.id.tvType);
        btnRetrive = findViewById(R.id.btnRetrive);

        btnRetrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbReff = FirebaseDatabase.getInstance().getReference().child("elfin-electric-cars").child("1");

                // Write a message to the database
                //FirebaseDatabase database = FirebaseDatabase.getInstance();
                //DatabaseReference myRef = database.getReference("elbiler");


                dbReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String merke = dataSnapshot.child("merke").getValue().toString();
                        //String age = dataSnapshot.child("type").getValue().toString();

                        Toast.makeText(getApplicationContext(),
                                dataSnapshot.child("merke").getValue().toString() + " retrieved!",
                                Toast.LENGTH_SHORT).show();

                        tvMerke.setText(merke);
                        //tvType.setText(age);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Failed to read value!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });




        /*
        txtMerke = findViewById(R.id.txtMerke);
        txtLader = findViewById(R.id.txtLader);
        txtBatteri = findViewById(R.id.txtBatteri);
        btnSave = findViewById(R.id.btnSave);

        elbil = new Elbil();

        //Reference variable to connect to database
        dbRef = FirebaseDatabase.getInstance().getReference();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elbil.setMerke(txtMerke.getText().toString().trim());
                elbil.setHurtiglader(txtLader.getText().toString().trim());
                elbil.setBatterikapasitet(txtBatteri.getText().toString().trim());

                //push value and define database table name as "elbil"
                dbRef.push().setValue(elbil);

                Toast.makeText(getApplicationContext(), "data inserted successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        */


    }
}
