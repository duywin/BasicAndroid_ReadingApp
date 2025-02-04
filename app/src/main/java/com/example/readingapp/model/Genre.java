package com.example.readingapp.model;

public class Genre {
    private int id;
    private String name;
    private int totalBooks;

    public Genre(int id, String name, int totalBooks) {
        this.id = id;
        this.name = name;
        this.totalBooks = totalBooks;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getTotalBooks() { return totalBooks; }
}
