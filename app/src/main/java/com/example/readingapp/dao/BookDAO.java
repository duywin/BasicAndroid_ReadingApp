package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Book;

public class BookDAO {
    public static final String TABLE_NAME = "Books";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "imageLink TEXT NOT NULL, " +
                    "genre_id INTEGER, " +
                    "FOREIGN KEY(genre_id) REFERENCES Genres(id));";

    private final SQLiteDatabase db;

    public BookDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }
    public boolean insertBook(String title, String author, String description, String imageLink, int genreId) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("author", author);
        values.put("description", description);
        values.put("imageLink", imageLink);
        values.put("genre_id", genreId);

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    public boolean insertBook(Book book) {
        return insertBook(book.getTitle(), book.getAuthor(), book.getDescription(), book.getImageLink(), book.getGenreId());
    }
}
