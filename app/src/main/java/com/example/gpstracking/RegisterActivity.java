package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    public EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView loginUser;
    private DatabaseReference eRootRef;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        register  = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        loginUser = (TextView) findViewById(R.id.login_user);
        loginUser.setOnClickListener(this);
        username = (EditText) findViewById(R.id.username);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById((R.id.progressBar));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_user:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.register:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        String Username = username.getText().toString().trim();
        String Name = name.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        if(Username.isEmpty()){
            username.setError("Student number is required");
            username.requestFocus();
            return;
        }
        if(Name.isEmpty()){
            name.setError("Name is required");
            name.requestFocus();
        }
        if(Email.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
        }
        if(Password.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
        }

       if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
           email.setError("Please enter a valid email!");
           email.requestFocus();
           return;
       }

        if(Password.length()<6){
            password.setError("Min password length should be 6 characters");
            password.requestFocus();

        }

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(Username, Name, Email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "User is registered", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        finish();
                                        startActivity(new Intent(RegisterActivity.this,MapsActivity.class));
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Registeration Unsucessful", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterActivity.this, "Registeration Unsucessful", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });






    }
}