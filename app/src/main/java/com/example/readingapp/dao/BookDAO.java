package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;

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
}
