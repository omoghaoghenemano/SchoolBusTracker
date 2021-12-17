package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
private FirebaseAuth  mAuth;
private TextView ForgotPassword,LoginRegister;
EditText Username,Userpassword;
private Button Login;
private TextView signup;
private ProgressBar progressBar;
    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Username = findViewById(R.id.username);
        Userpassword =  findViewById(R.id.userpassword);
        ForgotPassword =  findViewById(R.id.Forgot_Password);
        Login = findViewById(R.id.register);
        checkPermissions();
        progressBar = findViewById(R.id.progressbar);
       mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email, Password;
                Email = Username.getText().toString().trim();
                Password = Userpassword.getText().toString().trim();
                if(Email.isEmpty()){
                    Username.setError("Email is required");
                    Username.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    Username.setError("Email is invalid");
                    Username.requestFocus();
                    return;
                }
                if(Password.isEmpty()){
                    Userpassword.setError("Password is required");
                    Userpassword.requestFocus();
                    return;
                }
                if(Password.length()<6){
                    Userpassword.setError("Min password is 6 characters");
                    Userpassword.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();

                            startActivity(new Intent(LoginActivity.this,MapsActivity.class));

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(view, "Incorrect password or username", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }



                });
            }

        });



signup = findViewById(R.id.signup);
signup.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));


    }
});



    }

    @Override
    public void onClick(View view) {

    }
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    100);
            return false;
        }
        return true;
    }



    }




