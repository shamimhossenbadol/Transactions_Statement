package com.shamim.transactions_statement;

public class Transactions {
    private int number;
    private int amount;
    private String date;
    private String time;

    public Transactions() {
    }

    public Transactions(int number, int amount, String date, String time) {
        this.number = number;
        this.amount = amount;
        this.date = date;
        this.time = time;
    }

    public int getNumber() {
        return number;
    }

    public int getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
