package com.example.digitallibraryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class fineAdapter extends FirestoreRecyclerAdapter<book_fine,fineAdapter.fineHolder> {

    public fineAdapter(@NonNull FirestoreRecyclerOptions<book_fine> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull fineHolder fineHolder, int i, @NonNull book_fine book_fine) {
        fineHolder.textViewTitle.setText(book_fine.getBookName());
        //Date date=demandSlip.getDemandDate();
        fineHolder.textViewDemand_date.setText(book_fine.getDemandDate());
        fineHolder.textViewFine.setText("₹"+String.valueOf(book_fine.getFine()));
    }

    @NonNull
    @Override
    @ServerTimestamp
    public fineAdapter.fineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.finelist,parent,false);
        return new fineHolder(v);
    }

    public class fineHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewDemand_date;
        TextView textViewFine;
        public fineHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.bname);
            textViewDemand_date=itemView.findViewById(R.id.idate);
            textViewFine=itemView.findViewById(R.id.fine);
        }
    }
}
