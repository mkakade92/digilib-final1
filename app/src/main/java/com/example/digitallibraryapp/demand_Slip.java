package com.example.digitallibraryapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class demand_Slip {
    private String bookName;
    @ServerTimestamp
    private Date demandDate;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getDemandDate() {
        return String.valueOf(demandDate);
    }

    public void setDemandDate(Date demandDate) {
        this.demandDate = demandDate;
    }
}

