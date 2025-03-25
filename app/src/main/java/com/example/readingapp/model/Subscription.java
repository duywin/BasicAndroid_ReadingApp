package com.example.readingapp.model;

public class Subscription {
    private int id;
    private int accountId;
    private String subDate;
    private String expDate;

    public Subscription(int id, int accountId, String subDate, String expDate) {
        this.id = id;
        this.accountId = accountId;
        this.subDate = subDate;
        this.expDate = expDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getSubDate() {
        return subDate;
    }

    public void setSubDate(String subDate) {
        this.subDate = subDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}
