package com.example.digitallibraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandSlip extends AppCompatActivity /*implements AdapterView.OnItemSelectedListener*/ {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    EditText editText_book;
    Button button_demand,button_viewDemandSlips;
    private String TAG=DemandSlip.class.getName();
    private ProgressBar progressBar;
    private Spinner spinner;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demand_slip);
        editText_book=(EditText)findViewById(R.id.editText_book);
        button_demand=(Button)findViewById(R.id.button_demand);
        button_viewDemandSlips=(Button)findViewById(R.id.button_viewDemandSlip);
        progressBar  = (ProgressBar)findViewById(R.id.progressBar_demand);
        progressBar.setVisibility(View.GONE);

        spinner=(Spinner)findViewById(R.id.spinner1);
        final List<String> items = new ArrayList<>();
        items.add(0,"Select Book");
        final ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, items);
        adapter0.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter0);
        firebaseFirestore.collection("books")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("title");
                        items.add(subject);
                    }
                    adapter0.notifyDataSetChanged();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(parent.getItemAtPosition(position).equals("Select Book")))
                {
                    editText_book.setText(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        button_demand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth=FirebaseAuth.getInstance();
                final FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                CollectionReference c1= firebaseFirestore.collection("books");
                c1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty())
                        {
                            List<DocumentSnapshot> list=queryDocumentSnapshots.getDocuments();
                            flag=0;
                            for(DocumentSnapshot d :list)
                            {
                                editText_book=(EditText)findViewById(R.id.editText_book);
                                String bname=editText_book.getText().toString().toLowerCase().trim();
                                if(bname.equalsIgnoreCase(d.get("title").toString().toLowerCase().trim())
                                    && d.get("count").toString().trim().equalsIgnoreCase("0"))
                                {
                                    flag=1;
                                    break;
                                }

                            }
                            if(flag==1)
                            {
                                CollectionReference c2 = firebaseFirestore.collection("Users")
                                        .document(firebaseUser.getEmail())
                                        .collection("Demand_Slips");
                                c2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if(!queryDocumentSnapshots.isEmpty())
                                        {
                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                            flag=0;
                                            for(DocumentSnapshot d : list)
                                            {
                                                Log.d(TAG, "Document" + d.getString("bookName"));
                                                editText_book=(EditText)findViewById(R.id.editText_book);
                                                String bName=editText_book.getText().toString().toLowerCase().trim();

                                                if(bName.equals(d.getString("bookName").toLowerCase().trim())) {
                                                    flag = 1;
                                                    break;
                                                }
                                            }
                                            if(flag==0)
                                                createDemandSlip();
                                            else {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(), "Demand Slip Already Exists",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else
                                        {
                                            createDemandSlip();
                                        }

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"No such book exists Or Count is 0",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });


            }
        });
        button_viewDemandSlips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                finish();
                startActivity(new Intent(getApplicationContext(),viewDemandSlips.class));
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public void createDemandSlip()
    {
        if(TextUtils.isEmpty(editText_book.getText().toString()))
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this,"Empty Field ",Toast.LENGTH_LONG).show();
            return;
        }

        Map<String,Object> docData1=new HashMap<>();
        docData1.put("bookName", editText_book.getText().toString());
        docData1.put("demandDate", FieldValue.serverTimestamp( ));
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        firebaseFirestore.collection("Users").document(firebaseUser.getEmail())
                .collection("Demand_Slips")
                .add(docData1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(getApplicationContext(),"Demand Slip Added Successfully",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot written with ID in users database: "
                                + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(getApplicationContext(),"Oops!!! Error",
                                Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        final String bName=parent.getItemAtPosition(position).toString().toLowerCase().trim();
//        firebaseAuth=FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
//        CollectionReference c2 = firebaseFirestore.collection("Users")
//                .document(firebaseUser.getEmail())
//                .collection("Demand_Slips");
//        c2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if(!queryDocumentSnapshots.isEmpty())
//                {
//                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                    flag=0;
//                    for(DocumentSnapshot d : list)
//                    {
//                        Log.d(TAG, "Document" + d.getString("bookName"));
//
//                        if(bName.equals(d.getString("bookName").toLowerCase().trim())) {
//                            flag = 1;
//                            break;
//                        }
//                    }
//                    if(flag==0)
//                        createDemandSlip();
//                    else {
//                        Toast.makeText(getApplicationContext(), "Demand Slip Already Exists",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}

