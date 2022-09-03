package com.shamim.transactions_statement;

public class Payments {
    private int id;
    private Object date;
    private int amount;

    public Payments() {

    }

    public Payments(int id, Object date, int amount) {
        this.id = id;
        this.date = date;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public Object getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }
}
