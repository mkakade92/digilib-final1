package com.example.digitallibraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {


    private static final String TAG = "LoginActivity";
    FirebaseAuth firebaseAuth;
    EditText editTextUsername,editTextPassword;
    Button buttonLogin;
    TextView textViewRegiistration;
    private ProgressBar progressBar;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar  = (ProgressBar)findViewById(R.id.proBarlogin);
        progressBar.setVisibility(View.GONE);
        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
        {
//            finish();
            startActivity(new Intent(this,Profile.class));
        }
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextUsername=(EditText)findViewById(R.id.editTextEmail);
        buttonLogin=(Button)findViewById(R.id.buttonLogin);
        textViewRegiistration=(TextView)findViewById(R.id.textViewRegistration);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        textViewRegiistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegistration();
            }
        });
    }

    private void userRegistration()
    {
        startActivity(new Intent(this,Registration.class));
        finish();

    }
    private void userLogin()
    {

        progressBar.setVisibility(View.VISIBLE);
        String username=editTextUsername.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(username))
        {
            editTextUsername.setError("Enter A Valid Username");
            Toast.makeText(this,"Empty Field ",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            editTextPassword.setError("Enter A Valid password");
            Toast.makeText(this,"Empty Field ",Toast.LENGTH_LONG).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(),Profile.class));
                    finish();
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Invalid username or password!!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        count++;
        if (count >= 1) {
            finish();
            finishAffinity();
        } else {
            Toast.makeText(this, "Press back again to Leave!", Toast.LENGTH_SHORT).show();

            // resetting the counter in 2s
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                }
            }, 2000);
        }
    }

}

