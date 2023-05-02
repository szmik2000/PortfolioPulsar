package com.example.portfoliopulsar;

public class Stock {
    private String tickerSymbol;
    private double avgBuyPrice;
    private double amountInvested;
    private double price;
    private boolean isLoading;


    public Stock(String tickerSymbol, double avgBuyPrice, double amountInvested) {
        this.tickerSymbol = tickerSymbol;
        this.avgBuyPrice = avgBuyPrice;
        this.amountInvested = amountInvested;
        this.price = 0.0;
        this.isLoading = true;
    }

    public String getTickerSymbol() { return tickerSymbol; }
    public double getAvgBuyPrice() { return avgBuyPrice; }
    public double getAmountInvested() { return amountInvested; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isLoading() { return isLoading; }
    public void setLoading(boolean isLoading) { this.isLoading = isLoading; }
}
