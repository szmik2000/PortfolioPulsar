package com.example.portfoliopulsar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PortfolioList extends AppCompatActivity {
    static {
        System.loadLibrary("PortfolioPulsar");
    }

    private RecyclerView portfolioListRecyclerView;
    private FloatingActionButton addStockFab;
    private List<Stock> stocks;
    private StockAdapter stockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portfolioListRecyclerView = findViewById(R.id.portfolio_list_recycler_view);
        addStockFab = findViewById(R.id.add_stock_fab);

        // Initialize your StockAdapter and Portfolio data here
        stocks = new ArrayList<>();
        stockAdapter = new StockAdapter(stocks);

        portfolioListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        portfolioListRecyclerView.setAdapter(stockAdapter);

        addStockFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog addStockDialog = new Dialog(PortfolioList.this);
                addStockDialog.setContentView(R.layout.add_stock_dialog);

                EditText tickerSymbolInput = addStockDialog.findViewById(R.id.ticker_symbol_input);
                EditText avgBuyPriceInput = addStockDialog.findViewById(R.id.avg_buy_price_input);
                EditText amountInvestedInput = addStockDialog.findViewById(R.id.amount_invested_input);
                Button addStockButton = addStockDialog.findViewById(R.id.add_stock_button);

                addStockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tickerSymbol = tickerSymbolInput.getText().toString();
                        double avgBuyPrice = Double.parseDouble(avgBuyPriceInput.getText().toString());
                        double amountInvested = Double.parseDouble(amountInvestedInput.getText().toString());

                        // Perform validation on the input fields if necessary

                        // Add the stock to your portfolio data and update the RecyclerView
                        Stock stock = new Stock(tickerSymbol, avgBuyPrice, amountInvested);
                        stocks.add(stock);
                        stockAdapter.notifyDataSetChanged();

                        // After adding the stock, dismiss the dialog
                        addStockDialog.dismiss();
                    }
                });

                addStockDialog.show();
            }
        });

    }
}
