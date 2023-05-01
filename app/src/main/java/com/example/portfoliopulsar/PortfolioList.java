package com.example.portfoliopulsar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PortfolioList extends AppCompatActivity implements StockAdapter.OnItemClickListener{
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
        stockAdapter = new StockAdapter(stocks, this);

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
                        String avgBuyPriceStr = avgBuyPriceInput.getText().toString();
                        String amountInvestedStr = amountInvestedInput.getText().toString();

                        // Check if any of the input fields is empty
                        if (tickerSymbol.isEmpty() || avgBuyPriceStr.isEmpty() || amountInvestedStr.isEmpty()) {
                            showCustomToast("Please fill in all fields");
                            return;
                        }

                        double avgBuyPrice;
                        double amountInvested;

                        try {
                            avgBuyPrice = Double.parseDouble(avgBuyPriceStr);
                            amountInvested = Double.parseDouble(amountInvestedStr);
                        } catch (NumberFormatException e) {
                            showCustomToast("Invalid number format");
                            return;
                        }

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
    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView text = layout.findViewById(R.id.custom_toast_text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onItemClick(Stock stock) {
        // Handle the click event here, e.g., navigate to the ticker's page
        // You can use an Intent to start the TickerActivity and pass the stock data

        Intent intent = new Intent(this, TickerActivity.class);
        intent.putExtra("stock", stock.getTickerSymbol());
        startActivity(intent);
    }
}
