package com.example.portfoliopulsar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TickerActivity extends AppCompatActivity {
    private static final String TAG = "TickerActivity";
    private LineChart lineChart;
    private List<Entry> chartData;
    private List<String> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticker);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String tickerSymbol = intent.getStringExtra("stock");

        lineChart = findViewById(R.id.line_chart);
        chartData = new ArrayList<>(); // Initialize chartData list
        dates = new ArrayList<>(); // Initialize dates list
        setUpChart(chartData, dates);

        fetchHistoricalStockData(tickerSymbol, (chartData, dates) -> {
            this.chartData = chartData;
            this.dates = dates;
            LineDataSet dataSet = new LineDataSet(chartData, "Historical Stock Price");
            dataSet.setColor(getResources().getColor(R.color.colorPrimary));
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            lineChart.invalidate(); // Refresh the chart
        });
    }

    private void setUpChart(List<Entry> chartData, List<String> dates) {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setPinchZoom(true);
        lineChart.setDoubleTapToZoomEnabled(true);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = (int) value;
                if (index < 0 || index >= dates.size()) {
                    return "";
                }
                return dates.get(index);
            }
        });
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(Collections.min(chartData, new EntryYComparator()).getY() * 0.9f);
        yAxis.setAxisMaximum(Collections.max(chartData, new EntryYComparator()).getY() * 1.1f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
    }


    private void fetchHistoricalStockData(String tickerSymbol, StockDataCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + tickerSymbol + "&outputsize=full&apikey=" + BuildConfig.ALPHAVANTAGE_API_KEY;
        Log.d(TAG, "Request URL: " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    CustomToast.show(TickerActivity.this, "Failed to fetch historical stock data");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Response data: " + responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);

                        if (jsonObject.has("Note")) {
                            runOnUiThread(() -> {
                                CustomToast.show(TickerActivity.this, "API call frequency exceeded. Please try again later.");
                            });
                            return;
                        }

                        JSONObject timeSeriesData = jsonObject.getJSONObject("Time Series (Daily)");
                        Iterator<String> keys = timeSeriesData.keys();
                        List<Entry> chartData = new ArrayList<>();

                        int i = 0;
                        List<String> dates = new ArrayList<>();
                        while (keys.hasNext()) {
                            String date = keys.next();
                            float closePrice = (float) timeSeriesData.getJSONObject(date).getDouble("5. adjusted close");
                            chartData.add(new DateEntry(i, closePrice, date));
                            dates.add(date);
                            i++;
                        }

                        Collections.reverse(chartData);
                        Collections.reverse(dates);
                        runOnUiThread(() -> callback.onDataFetched(chartData, dates));
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            CustomToast.show(TickerActivity.this, "Failed to parse historical stock data");
                        });
                    }
                } else {
                    Log.d(TAG, "Failed response: " + response.code() + " - " + response.message());
                    runOnUiThread(() -> {
                        CustomToast.show(TickerActivity.this, "Failed to fetch historical stock data");
                    });
                }
            }
        });
    }

    interface StockDataCallback {
        void onDataFetched(List<Entry> historicalStockDataList, List<String> dates);
    }
}

