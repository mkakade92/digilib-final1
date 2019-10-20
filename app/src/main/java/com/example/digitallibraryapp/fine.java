package com.example.digitallibraryapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class fine extends AppCompatActivity{

    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();;

    FirebaseUser firebaseUser;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference1,collectionReference2;
    private fineAdapter adapter;

    String amount;
    String note = " Library Fine";
    String upiId = "9420753349@upi";
    String name = "Pranav Kulkarni";
    TextView totfin;


    final int UPI_PAYMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine);
        Button send = (Button) findViewById(R.id.payBtn);
        setup_IssuedBooks();
        totfin = (TextView)findViewById(R.id.textViewTotalFine);
        totalfine();
        if(amount != "0") {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, 0);

            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME
                        , NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription(CHANNEL_DESC);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(notificationChannel);
            }

        }



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payUsingUpi(amount, upiId, name, note);
            }
        });
    }
    public void setup_IssuedBooks()
    {
        firebaseUser=firebaseAuth.getCurrentUser();
        String userEmail=firebaseUser.getEmail();
        CollectionReference c1;
        c1=db.collection("Users").document(userEmail).collection("Books_Issued");
        Query query=c1.orderBy("demandDate").limit(50);
        FirestoreRecyclerOptions<book_fine> options=new FirestoreRecyclerOptions.Builder<book_fine>()
                .setQuery(query,book_fine.class).build();

        adapter=new fineAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.recyclerview4);
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


    void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(fine.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(fine.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(fine.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(fine.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(fine.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(fine.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public void totalfine()
    {
        firebaseUser=firebaseAuth.getCurrentUser();
        String userEmail=firebaseUser.getEmail();
        CollectionReference c1,c2;
        c1=db.collection("Users");
        DocumentReference d1=c1.document(userEmail);
        c2 = d1.collection("Books_Issued");
        final int[] cost = {0};

        c2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d : list)
                    {
                        cost[0] = cost[0] + getFine(d.getTimestamp("demandDate"));
                        Log.d(String.valueOf(cost[0])," ");
                    }

                }
                amount = String.valueOf(cost[0]);
                totfin.setText("Total Fine : ₹ "+String.valueOf(cost[0]));

            }
        });

    }
    public int getFine(Timestamp demandDate){
        Date date = demandDate.toDate();
        Timestamp t = Timestamp.now();
        Date date1 = t.toDate();
        long diff = date1.getTime() - date.getTime();
        int days = (int)(diff / (1000*60*60*24));
        days = days-7;
        if(days > 0)
        {
            return days*10;
        }
        else{
            return 0;
        }
    }


    private static final String CHANNEL_ID="digilib";
    private static final String CHANNEL_NAME="digilib";
    private static final String CHANNEL_DESC="digilib notif";


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fine)
//
//    private void displayNotification()
//    {
//        NotificationCompat.Builder mBuilder=
//                new NotificationCompat.Builder(this,CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_event_note_black_24dp)
//                .setContentTitle("its working")
//                .setContentText("Notification")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(1,mBuilder.build());
//    }


}


