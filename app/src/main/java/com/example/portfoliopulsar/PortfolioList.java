package com.example.portfoliopulsar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class PortfolioList extends AppCompatActivity implements StockAdapter.OnItemClickListener{
    static {
        System.loadLibrary("PortfolioPulsar");
    }
    private static final String TAG = "PortfolioList";
    private static final String SHARED_PREFS_NAME = "portfolioPulsar";
    private static final String STOCKS_KEY = "stocks";
    private RecyclerView portfolioListRecyclerView;
    private FloatingActionButton addStockFab;
    private List<Stock> stocks;
    private StockAdapter stockAdapter;
    private Handler stockPriceUpdateHandler = new Handler(Looper.getMainLooper());
    private Runnable stockPriceUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateAllStockPrices();
            stockPriceUpdateHandler.postDelayed(this, 60000); // Run the task every 60000 milliseconds (1 minute)
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        portfolioListRecyclerView = findViewById(R.id.portfolio_list_recycler_view);
        addStockFab = findViewById(R.id.add_stock_fab);
        stocks = loadStocksFromSharedPreferences();
        stockAdapter = new StockAdapter(stocks, this, new StockAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(Stock stock) {
                // Show a confirmation dialog before removing the stock
                new AlertDialog.Builder(PortfolioList.this)
                        .setTitle("Remove Stock")
                        .setMessage("Are you sure you want to remove this stock from your portfolio?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove the stock from the portfolio and update the RecyclerView
                                stocks.remove(stock);
                                stockAdapter.notifyDataSetChanged();
                                // Save the updated list of stocks to SharedPreferences
                                saveStocksToSharedPreferences();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
        portfolioListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        portfolioListRecyclerView.setAdapter(stockAdapter);
        // Start periodic stock price updates
        stockPriceUpdateHandler.post(stockPriceUpdateRunnable);
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
                        saveStocksToSharedPreferences();
                        // After adding the stock, dismiss the dialog
                        addStockDialog.dismiss();
                        updateToolbarTitle();
                    }
                });

                addStockDialog.show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stockPriceUpdateHandler.removeCallbacks(stockPriceUpdateRunnable);
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
            return;
        }
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
                Log.e("fetchStockPrice", "Failed to fetch stock price for " + ticker, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONObject globalQuote = jsonObject.getJSONObject("Global Quote");
                        double price = globalQuote.getDouble("05. price");
                        Log.i("fetchStockPrice", "Fetched price for " + ticker + ": $" + price);
                        // Update the stock price
                        for (Stock stock : stocks) {
                            if (stock.getTickerSymbol().equals(ticker)) {
                                Log.d(TAG, "Updating stock price...");
                                stock.setPrice(price);
                                stock.setLoading(false);
                                break;
                            }
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stockAdapter.notifyDataSetChanged();
                                updateToolbarTitle();
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("fetchStockPrice", "Failed to parse JSON response for " + ticker, e);
                    }
                } else {
                    Log.e("fetchStockPrice", "Unsuccessful response for " + ticker + ": " + response.message());
                }
            }
        });
    }
    private void updateAllStockPrices() {
        Log.d(TAG, "Updating stock prices...");
        for (Stock stock : stocks) {
            fetchStockPrice(stock.getTickerSymbol());
        }
        saveStocksToSharedPreferences();
    }
    private List<Stock> loadStocksFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String stocksJson = sharedPreferences.getString(STOCKS_KEY, "[]");
        List<Stock> loadedStocks = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(stocksJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject stockJson = jsonArray.getJSONObject(i);
                Stock stock = Stock.fromJson(stockJson);
                loadedStocks.add(stock);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to load stocks from SharedPreferences", e);
        }

        return loadedStocks;
    }



    private void saveStocksToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONArray jsonArray = new JSONArray();
        for (Stock stock : stocks) {
            jsonArray.put(stock.toJson());
        }

        editor.putString(STOCKS_KEY, jsonArray.toString());
        editor.apply();
    }
    private double calculateTotalPortfolioValue() {
        double totalValue = 0;
        for (Stock stock : stocks) {
            totalValue += stock.getPrice() * stock.getAmountInvested() / stock.getAvgBuyPrice();
        }
        return totalValue;
    }
    private void updateToolbarTitle() {
        double totalValue = calculateTotalPortfolioValue();
        String title = String.format(Locale.US, "$%.2f", totalValue);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onAddPositionClick(Stock stock) {
        showAddPositionDialog(stock);
    }

    private void showAddPositionDialog(Stock stock) {
        final Dialog addPositionDialog = new Dialog(this);
        addPositionDialog.setContentView(R.layout.add_stock_dialog);
        TextView dialogTitle = addPositionDialog.findViewById(R.id.dialog_title);
        dialogTitle.setText(getString(R.string.add_position_title, stock.getTickerSymbol()));
        EditText tickerSymbolInput = addPositionDialog.findViewById(R.id.ticker_symbol_input);
        EditText avgBuyPriceInput = addPositionDialog.findViewById(R.id.avg_buy_price_input);
        EditText amountInvestedInput = addPositionDialog.findViewById(R.id.amount_invested_input);
        Button addStockButton = addPositionDialog.findViewById(R.id.add_stock_button);

        tickerSymbolInput.setVisibility(View.GONE);

        addStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String avgBuyPriceStr = avgBuyPriceInput.getText().toString();
                String amountInvestedStr = amountInvestedInput.getText().toString();

                if (avgBuyPriceStr.isEmpty() || amountInvestedStr.isEmpty()) {
                    showCustomToast("Please fill in all fields");
                    return;
                }

                double newAvgBuyPrice;
                double newAmountInvested;

                try {
                    newAvgBuyPrice = Double.parseDouble(avgBuyPriceStr);
                    newAmountInvested = Double.parseDouble(amountInvestedStr);
                } catch (NumberFormatException e) {
                    showCustomToast("Invalid number format");
                    return;
                }

                double currentAmountInvested = stock.getAmountInvested();
                double currentAvgBuyPrice = stock.getAvgBuyPrice();

                // Calculate the weighted average of the cost basis
                double newTotalAmountInvested = currentAmountInvested + newAmountInvested;
                double weightedAvgBuyPrice = (currentAmountInvested * currentAvgBuyPrice + newAmountInvested * newAvgBuyPrice) / newTotalAmountInvested;

                stock.setAvgBuyPrice(weightedAvgBuyPrice);
                stock.setAmountInvested(newTotalAmountInvested);

                stockAdapter.notifyDataSetChanged();
                addPositionDialog.dismiss();
            }
        });

        addPositionDialog.show();
    }

}
