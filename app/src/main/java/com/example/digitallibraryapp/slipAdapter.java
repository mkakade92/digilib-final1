package com.example.digitallibraryapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

import io.opencensus.tags.Tag;

public class slipAdapter extends FirestoreRecyclerAdapter<demand_Slip,slipAdapter.slipHolder> {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    public slipAdapter(@NonNull FirestoreRecyclerOptions<demand_Slip> options) {
        super(options);
    }

    private onItemClickListener mlistener;

    public interface onItemClickListener {
    }

    public void setOnClickListener(onItemClickListener listener)
    {
        mlistener = listener;
    }
    @Override
    protected void onBindViewHolder(@NonNull slipHolder slipHolder, int i, @NonNull demand_Slip demandSlip) {
        slipHolder.textViewTitle.setText(demandSlip.getBookName());
        //Date date=demandSlip.getDemandDate();
        slipHolder.textViewDemand_date.setText(demandSlip.getDemandDate());
    }

    @NonNull
    @Override
    @ServerTimestamp
    public slipAdapter.slipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.demandsliplist,parent,false);
        return new slipHolder(v, mlistener);
    }

    public class slipHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewDemand_date;
        ImageView delBtn;
        ProgressBar progressBar;
        public slipHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.title1);
            textViewDemand_date=itemView.findViewById(R.id.demandDate);
            delBtn = itemView.findViewById(R.id.delBtn);
            progressBar = itemView.findViewById(R.id.proBar);
            progressBar.setVisibility(View.GONE);

            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    final CollectionReference col = db.collection("Users")
                            .document(firebaseAuth.getCurrentUser().getEmail())
                            .collection("Demand_Slips");
                    col.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty())
                            {
                                int pos = getAdapterPosition();
                                String bk = getItem(pos).getBookName();
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for(DocumentSnapshot d : list) {

                                    if(bk.trim().equalsIgnoreCase(d.getString("bookName").trim())) {
                                        db.collection("Users")
                                                .document(firebaseAuth.getCurrentUser().getEmail())
                                                .collection("Demand_Slips")
                                                .document(d.getId()).delete();
                                        progressBar.setVisibility(View.GONE);
                                        notifyItemRemoved(pos);
                                        notifyDataSetChanged();
                                    } } } }
                    });
                }});

        }


    }
}
