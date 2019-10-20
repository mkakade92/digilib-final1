package com.example.digitallibraryapp;

public class book {
    private String title;
    private String author;
    private int count;

    public book(){}
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getCount() {
        return count;
    }

    public book(String title, String author,int count) {
        this.title = title;
        this.author = author;
        this.count = count;
    }
}
