package com.example.gpstracking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PushingRoutes extends AppCompatActivity {
    EditText Name, latit, longit;
    Button routebtn;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushing_routes);
        Name = findViewById(R.id.Routename);
        latit = findViewById(R.id.Routelatitude);
        longit = findViewById(R.id.Routelongitude);
        routebtn = findViewById(R.id.Btnroute);


        ref = FirebaseDatabase.getInstance().getReference().child("Stops");
       routebtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String RouteName = Name.getText().toString().trim();
               Double lat = Double.parseDouble(latit.getText().toString().trim());
               Double lng = Double.parseDouble(longit.getText().toString().trim());
               Methods methods = new Methods(RouteName, lat, lng);
               ref.push().setValue(methods);
               Toast.makeText(getApplicationContext(),"data inserted", Toast.LENGTH_SHORT).show();


           }
       });

    }
}