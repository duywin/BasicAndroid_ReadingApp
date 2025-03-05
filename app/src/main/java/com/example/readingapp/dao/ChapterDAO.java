package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;

public class ChapterDAO {
    public static final String TABLE_NAME = "Chapters";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "chapter_name TEXT NOT NULL, " +
                    "book_id INTEGER, " +
                    "link TEXT NOT NULL, " +
                    "FOREIGN KEY(book_id) REFERENCES Books(id));";

    private final SQLiteDatabase db;

    public ChapterDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }
}
