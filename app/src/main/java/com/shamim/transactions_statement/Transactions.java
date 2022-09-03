package com.shamim.transactions_statement;

public class Transactions {
    private int id;
    private int number;
    private int amount;
    private Object date;
    private String time;

    public Transactions() {
    }

    public Transactions(int id, int number, int amount, Object date, String time) {
        this.id = id;
        this.number = number;
        this.amount = amount;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getAmount() {
        return amount;
    }

    public Object getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
