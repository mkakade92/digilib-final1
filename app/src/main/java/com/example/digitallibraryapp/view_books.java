package com.example.digitallibraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class view_books extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("books");
    private bookAdapter adapter;
    private SearchView searchView;
    private BookViewAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Books");
        setupRecyclerView();
    }

    public void setupRecyclerView()
    {
        Query query = collectionReference.orderBy("title").limit(50);
        FirestoreRecyclerOptions<book> options = new FirestoreRecyclerOptions.Builder<book>()
                .setQuery(query, book.class).build();

        adapter = new bookAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerview2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListner(new bookAdapter.OnItemClickListener() {
            private static final String TAG = "DONE";

            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                book item=documentSnapshot.toObject(book.class);
               Toast.makeText(view_books.this,"title"+item.getTitle(),Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);

        MenuItem searchItem=menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        Query q=FirebaseFirestore.getInstance().collection("books")
                .orderBy("title");
            q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    adapter1=new BookViewAdapter(task.getResult().toObjects(book.class));
                    RecyclerView recyclerView=findViewById(R.id.recyclerview2);
                    recyclerView.setAdapter(adapter1);
                }
            });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter1.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    public  void  onStart() {

        super.onStart();
        adapter.startListening();
    }
    public  void  onStop() {

        super.onStop();
        adapter.stopListening();
    }

}


