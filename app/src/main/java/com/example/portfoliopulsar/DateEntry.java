package com.example.portfoliopulsar;

import com.github.mikephil.charting.data.Entry;

public class DateEntry extends Entry {
    private String date;

    public DateEntry(float x, float y, String date) {
        super(x, y);
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
