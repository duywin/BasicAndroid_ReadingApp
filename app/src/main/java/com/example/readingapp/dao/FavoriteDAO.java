package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;

public class FavoriteDAO {
    public static final String TABLE_NAME = "Favorites";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "account_id INTEGER, " +
                    "book_id INTEGER, " +
                    "PRIMARY KEY(account_id, book_id), " +
                    "FOREIGN KEY(account_id) REFERENCES Accounts(id), " +
                    "FOREIGN KEY(book_id) REFERENCES Books(id));";

    private final SQLiteDatabase db;

    public FavoriteDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }
}
