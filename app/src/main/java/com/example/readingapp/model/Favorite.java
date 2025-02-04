package com.example.readingapp.model;

public class Favorite {
    private int accountId;
    private int bookId;

    public Favorite(int accountId, int bookId) {
        this.accountId = accountId;
        this.bookId = bookId;
    }

    // Getters and Setters
    public int getAccountId() { return accountId; }
    public int getBookId() { return bookId; }
}
