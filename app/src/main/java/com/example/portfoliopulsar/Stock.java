package com.example.portfoliopulsar;

import org.json.JSONException;
import org.json.JSONObject;

public class Stock {
    private String tickerSymbol;
    private double avgBuyPrice;
    private double amountInvested;
    private double price;
    private boolean isLoading;
    private double shares;

    //private String companyName;

    public Stock(String tickerSymbol, double avgBuyPrice, double amountInvested) {
        this.tickerSymbol = tickerSymbol;
        this.avgBuyPrice = avgBuyPrice;
        this.amountInvested = amountInvested;
        this.price = 0.0;
        this.isLoading = true;
        //this.companyName = companyName;
    }
    /*public String getCompanyName() {
        return companyName;
    }*/
    public String getTickerSymbol() { return tickerSymbol; }
    public double getAvgBuyPrice() { return avgBuyPrice; }
    public void setAvgBuyPrice(double avgBuyPrice) { this.avgBuyPrice = avgBuyPrice; }
    public double getAmountInvested() { return amountInvested; }
    public void setAmountInvested(double amountInvested) { this.amountInvested = amountInvested; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getShares() { return shares; }
    public void setShares(double shares) { this.shares = shares; }
    public boolean isLoading() { return isLoading; }
    public void setLoading(boolean isLoading) { this.isLoading = isLoading; }
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tickerSymbol", tickerSymbol);
            jsonObject.put("avgBuyPrice", avgBuyPrice);
            jsonObject.put("amountInvested", amountInvested);
            jsonObject.put("price", price);
            jsonObject.put("loading", isLoading);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Stock fromJson(JSONObject jsonObject) throws JSONException {
        String tickerSymbol = jsonObject.getString("tickerSymbol");
        double avgBuyPrice = jsonObject.getDouble("avgBuyPrice");
        double amountInvested = jsonObject.getDouble("amountInvested");
        double price = jsonObject.getDouble("price");
        boolean loading = jsonObject.getBoolean("loading");

        Stock stock = new Stock(tickerSymbol, avgBuyPrice, amountInvested);
        stock.setPrice(price);
        stock.setLoading(loading);
        return stock;
    }
    public double getPercentageGainLoss() {
        if (price > 0 && avgBuyPrice > 0) {
            return ((price - avgBuyPrice) / avgBuyPrice) * 100;
        }
        return 0;
    }


}
