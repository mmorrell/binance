package com.morrell.binance;

public class Kline {

    private long t; // Minute of KLine
    private long T; // Time of event
    private double c; // Close price

    public double getPrice() {
        return c;
    }

    public long getKlineTime() {
        return t;
    }
}
