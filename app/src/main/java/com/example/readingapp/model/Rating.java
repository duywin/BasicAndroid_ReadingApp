package com.example.readingapp.model;

public class Rating {
    private int id;
    private int accountId;
    private int bookId;
    private float rating;

    public Rating(int id, int accountId, int bookId, float rating) {
        this.id = id;
        this.accountId = accountId;
        this.bookId = bookId;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getBookId() {
        return bookId;
    }

    public float getRating() {
        return rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
