package com.example.readingapp.model;


public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private int genreId;

    public Book(int id, String title, String author, String description, int genreId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genreId = genreId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public int getGenreId() { return genreId; }
}

