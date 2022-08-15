package com.shamim.transactions_statement;

public class Payments {
    String date;
    int amount;

    public Payments(){

    }

    public Payments(String date, int amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }
}
