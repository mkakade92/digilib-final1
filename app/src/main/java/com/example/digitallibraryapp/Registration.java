package com.example.digitallibraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration extends AppCompatActivity {


    FirebaseFirestore db=FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth;
    EditText name,class_,roll,department,editTextUsername,editTextPassword,editTextConfirm;
    Button buttonRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(this,Profile.class));
        }

        name=(EditText)findViewById(R.id.Name_of_Student);
        class_=(EditText)findViewById(R.id.Class_);
        roll=(EditText)findViewById(R.id.RollNo);
        department=(EditText)findViewById(R.id.Department);
        editTextUsername=(EditText)findViewById(R.id.editTextUsername);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextConfirm=(EditText)findViewById(R.id.editTextConfirm);
        buttonRegister=(Button)findViewById(R.id.buttonRegistration);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegistration();
            }
        });

    }
    private void userRegistration()
    {
        final String username=editTextUsername.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        String confirm=editTextConfirm.getText().toString().trim();

        if(TextUtils.isEmpty(username))
        {
            editTextUsername.setError("Enter valid username");
            Toast.makeText(Registration.this,"Empty Field",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            editTextPassword.setError("Enter valid password");
            Toast.makeText(Registration.this,"Empty Field",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(confirm))
        {
            editTextConfirm.setError("Enter valid password");
            Toast.makeText(Registration.this,"Empty Field",Toast.LENGTH_LONG).show();
            return;
        }
        if (password.equals(confirm)) {
            firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String name_=name.getText().toString().trim();
                        String department_=department.getText().toString().trim();
                        String roll_=roll.getText().toString().trim();
                        String _class_=class_.getText().toString().trim();
                        UserInformation user=new UserInformation(name_,department_,Integer.parseInt(roll_),_class_);
                        db.collection("Users").document(username)
                                .set(user);
                        finish();
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong!!!!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(Registration.this, "Password and Confirm Password must match!!!", Toast.LENGTH_LONG).show();
            return;
        }

    }

}
