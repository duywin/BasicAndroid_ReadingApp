package com.example.readingapp.model;

public class Chapter {
    private int id;
    private String chapterName;
    private int bookId;
    private String link;

    public Chapter(int id, String chapterName, int bookId, String link) {
        this.id = id;
        this.chapterName = chapterName;
        this.bookId = bookId;
        this.link = link;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getChapterName() { return chapterName; }
    public int getBookId() { return bookId; }
    public String getLink() {return link;}
}
