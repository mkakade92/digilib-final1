package com.example.digitallibraryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class bookAdapter extends FirestoreRecyclerAdapter<book,bookAdapter.bookHolder>  implements Filterable {
    private List<book> bookList;
    private List<book> bookList2;
    private OnItemClickListener listener;

    public bookAdapter(@NonNull FirestoreRecyclerOptions<book> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull bookHolder bookHolder, int i, @NonNull book book) {
        bookHolder.textViewTitle.setText(book.getTitle());
        bookHolder.textViewAuthor.setText(book.getAuthor());
        bookHolder.textViewCount.setText(String.valueOf(book.getCount()));
    }

    @NonNull
    @Override
    public bookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list,parent,false);
        return new bookHolder(v,listener);
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class bookHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewCount;
        public bookHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewTitle=itemView.findViewById(R.id.title);
            textViewAuthor=itemView.findViewById(R.id.author);
            textViewCount=itemView.findViewById(R.id.count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null)
                    {
                        listener.OnItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener
    {
        void OnItemClick(DocumentSnapshot documentSnapshot,int position);
    }
    public void setOnItemClickListner(OnItemClickListener listner)
    {
        this.listener=listner;
    }

}
