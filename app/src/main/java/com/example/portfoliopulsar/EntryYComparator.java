package com.example.portfoliopulsar;

import com.github.mikephil.charting.data.Entry;

import java.util.Comparator;

public class EntryYComparator implements Comparator<Entry> {
    @Override
    public int compare(Entry e1, Entry e2) {
        return Float.compare(e1.getY(), e2.getY());
    }
}
