package com.example.portfoliopulsar;

public class NativeWrapper {
    static {
        System.loadLibrary("PortfolioPulsar");
    }

    public static native long createPortfolio();
    public static native void destroyPortfolio(long portfolioPtr);
    public static native void addStock(long portfolioPtr, String symbol, int quantity, double purchasePrice);
    public static native void removeStock(long portfolioPtr, String symbol);
}
