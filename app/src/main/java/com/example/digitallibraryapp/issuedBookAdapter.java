package com.example.digitallibraryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class issuedBookAdapter extends FirestoreRecyclerAdapter<book,issuedBookAdapter.bookHolder>  {

    public issuedBookAdapter(@NonNull FirestoreRecyclerOptions<book> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull issuedBookAdapter.bookHolder bookHolder, int i,@NonNull book book) {
        bookHolder.textViewTitle.setText(book.getTitle());
        bookHolder.textViewAuthor.setText(book.getAuthor());
    }

    @NonNull
    @Override
    public issuedBookAdapter.bookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.issued_book_list,parent,false);
        return new issuedBookAdapter.bookHolder(v);
    }

    public class bookHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewAuthor;
        public bookHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.title);
            textViewAuthor=itemView.findViewById(R.id.author);

        }
    }


}
