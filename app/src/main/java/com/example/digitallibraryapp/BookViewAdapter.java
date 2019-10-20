package com.example.digitallibraryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookViewAdapter extends RecyclerView.Adapter<BookViewAdapter.BookViewHolder> implements Filterable {

    private List<book> bookList;
    private List<book> bookList2;

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list,parent,false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        book item=bookList.get(position);

        holder.textView1.setText(item.getTitle());
        holder.textView2.setText(item.getAuthor());
        holder.textView3.setText(String.valueOf(item.getCount()));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    @Override
    public Filter getFilter() {
        return bookFilter;
    }
    private Filter bookFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<book> filteredList=new ArrayList<>();

            if(constraint==null || constraint.length()==0)
            {
                filteredList.addAll(bookList2);
            }else {
                String filterPattern=constraint.toString().toLowerCase().trim();
                for(book item: bookList2)
                {
                    if(item.getTitle().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            bookList.clear();
            bookList.addAll((List) results.values);
            notifyDataSetChanged();
        }

    };

    public class BookViewHolder extends RecyclerView.ViewHolder {

        TextView textView1,textView2,textView3;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1=itemView.findViewById(R.id.title);
            textView2=itemView.findViewById(R.id.author);
            textView3=itemView.findViewById(R.id.count);
        }
    }

    public BookViewAdapter(List<book> bookList) {
        this.bookList = bookList;
        bookList2=new ArrayList<>(bookList);
    }
}
