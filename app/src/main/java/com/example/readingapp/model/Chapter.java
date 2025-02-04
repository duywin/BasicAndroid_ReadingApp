package com.example.readingapp.model;

public class Chapter {
    private int id;
    private String chapterName;
    private int bookId;

    public Chapter(int id, String chapterName, int bookId) {
        this.id = id;
        this.chapterName = chapterName;
        this.bookId = bookId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getChapterName() { return chapterName; }
    public int getBookId() { return bookId; }
}
