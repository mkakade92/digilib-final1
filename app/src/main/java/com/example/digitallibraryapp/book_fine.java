package com.example.digitallibraryapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class book_fine {
    private String bookName;
    @ServerTimestamp
    private Timestamp demandDate;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getDemandDate() {

        Date d = demandDate.toDate();
        return String.valueOf(d);
    }

    public int getFine(){
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
    public void setDemandDate(Timestamp demandDate) {
        this.demandDate = demandDate;
    }
}

