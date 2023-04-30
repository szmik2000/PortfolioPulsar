package com.example.portfoliopulsar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PortfolioList extends AppCompatActivity {
    static {
        System.loadLibrary("PortfolioPulsar");
    }

    private RecyclerView portfolioListRecyclerView;
    private FloatingActionButton addStockFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portfolioListRecyclerView = findViewById(R.id.portfolio_list_recycler_view);
        addStockFab = findViewById(R.id.add_stock_fab);

        // Initialize your StockAdapter and Portfolio data here

        portfolioListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set the StockAdapter for the RecyclerView

        addStockFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Add Stock screen
                //startActivity(new Intent(PortfolioListActivity.this, AddStockActivity.class));
            }
        });
    }
}