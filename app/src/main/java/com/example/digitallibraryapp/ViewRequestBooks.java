package com.example.digitallibraryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewRequestBooks extends AppCompatActivity {

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private CollectionReference collectionReference=db.collection("Users")
            .document(firebaseAuth.getCurrentUser().getEmail())
            .collection("Requested_Books");
    private reqAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_books);
        setupRecyclerView();

    }

    public  void setupRecyclerView()
    {
        Query query=collectionReference.orderBy("demandDate").limit(50);
        FirestoreRecyclerOptions<demand_Slip> options=new FirestoreRecyclerOptions.Builder<demand_Slip>()
                .setQuery(query,demand_Slip.class).build();

        adapter=new reqAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.recyclerview5);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
    public  void  onStart() {

        super.onStart();
        adapter.startListening();
    }
    public  void  onStop() {

        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RequestBooks.class);
        startActivity(intent);
        finish();
    }
}
