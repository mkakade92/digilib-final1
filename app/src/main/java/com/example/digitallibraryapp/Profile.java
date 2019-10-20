package com.example.digitallibraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference1,collectionReference2;
    private issuedBookAdapter adapter;
    private ProgressBar progressBar;
    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_draw);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_profile);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,login.class));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

       // buttonLogout=(Button)findViewById(R.id.buttonLogout);
       /* buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogout();
            }
        });*/

       setup_personalInfo();
       setup_IssuedBooks();
        progressBar.setVisibility(View.GONE);

    }
    public void setup_personalInfo()
    {

        final TextView[] t1 = new TextView[3];
        firebaseUser=firebaseAuth.getCurrentUser();
        String userEmail=firebaseUser.getEmail();
        CollectionReference c1;
        c1=db.collection("Users");
        DocumentReference d1=c1.document(userEmail);
        d1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc=task.getResult();
                    if(doc.exists())
                    {
                        String na=doc.getString("name");
                        String cl=doc.getString("class_");
                        String de=doc.getString("department");
                        t1[0] =(TextView)findViewById(R.id.name);
                        t1[1] =(TextView)findViewById(R.id.cls);
                        t1[2] =(TextView)findViewById(R.id.department);
                        t1[0].setText("Name : " + na);
                        t1[1].setText("Class : " + cl);
                        t1[2].setText("Department : " + de);
                    }
                }
            }
        });



    }
    public void setup_IssuedBooks()
    {

        firebaseUser=firebaseAuth.getCurrentUser();
        String userEmail=firebaseUser.getEmail();
        CollectionReference c1;
        c1=db.collection("Users").document(userEmail).collection("Books_Issued");
        Query query=c1.orderBy("title").limit(50);
        FirestoreRecyclerOptions<book> options=new FirestoreRecyclerOptions.Builder<book>()
                .setQuery(query,book.class).build();

        adapter=new issuedBookAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.recyclerview4);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            count++;
            if (count > 1) {
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



    private void userLogout()
    {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this,login.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            if(this.getClass() != Profile.class) {
                finish();
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
            else{
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_books) {
//            finish();
            startActivity(new Intent(getApplicationContext(),view_books.class));
        } else if (id == R.id.nav_fine) {
//            finish();
            startActivity(new Intent(getApplicationContext(),fine.class));
        } else if (id == R.id.nav_about) {
//            finish();
            startActivity(new Intent(getApplicationContext(),about.class));
        } else if (id == R.id.nav_demand_slip) {
//            finish();
            startActivity(new Intent(getApplicationContext(), DemandSlip.class));
        }   else if (id == R.id.nav_request_book) {
//            finish();
                startActivity(new Intent(getApplicationContext(),RequestBooks.class));
        } else if (id == R.id.nav_share) {
//            startActivity(new Intent(getApplicationContext(),login.class));
            Toast.makeText(Profile.this, "Sharing options", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_send) {
//            startActivity(new Intent(getApplicationContext(),login.class));
            Toast.makeText(Profile.this, "Sending options", Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.nav_logout) {
            userLogout();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


