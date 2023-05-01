package com.example.portfoliopulsar;

public class Stock {
    private String tickerSymbol;
    private double avgBuyPrice;
    private double amountInvested;

    public Stock(String tickerSymbol, double avgBuyPrice, double amountInvested) {
        this.tickerSymbol = tickerSymbol;
        this.avgBuyPrice = avgBuyPrice;
        this.amountInvested = amountInvested;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public double getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public double getAmountInvested() {
        return amountInvested;
    }
}
