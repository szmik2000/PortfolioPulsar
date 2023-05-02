package com.example.portfoliopulsar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                amountInvestedInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            addStockButton.performClick();
                            return true;
                        }
                        return false;
                    }
                });

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
                        fetchStockPrice(tickerSymbol);
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
        if (stock == null) {
            // Show a warning message or handle the situation when the stock is null
            return;
        }
        // Handle the click event here, e.g., navigate to the ticker's page
        // You can use an Intent to start the TickerActivity and pass the stock data

        Intent intent = new Intent(this, TickerActivity.class);
        intent.putExtra("stock", stock.getTickerSymbol());
        startActivity(intent);
    }
    private void fetchStockPrice(String ticker) {
        String apiKey = "7F8U58IWRROUSTCB"; // Replace with your API key
        String function = "GLOBAL_QUOTE";

        String url = "https://www.alphavantage.co/query?function=" + function + "&symbol=" + ticker + "&apikey=" + apiKey;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONObject globalQuote = jsonObject.getJSONObject("Global Quote");
                        double price = globalQuote.getDouble("05. price");

                        // Update the stock price in your stocks list
                        for (Stock stock : stocks) {
                            if (stock.getTickerSymbol().equals(ticker)) {
                                stock.setPrice(price);
                                stock.setLoading(false);
                                break;
                            }
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update the UI (e.g., the RecyclerView) with the new stock prices
                                stockAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        // Handle the JSON parsing error
                    }
                } else {
                    // Handle the unsuccessful response
                }
            }
        });
    }
}
