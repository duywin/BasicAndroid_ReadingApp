package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;

public class GenreDAO {
    public static final String TABLE_NAME = "Genres";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "total_books INTEGER NOT NULL);";

    private final SQLiteDatabase db;

    public GenreDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }

    public boolean insertGenre(String name, int totalBooks) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("total_books", totalBooks);

        return db.insert(TABLE_NAME, null, values) != -1;
    }
}
