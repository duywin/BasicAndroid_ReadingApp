package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

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
    public List<Object[]> getTotalBooksByGenre() {
        List<Object[]> genreData = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name, total_books FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int totalBooks = cursor.getInt(1);
            genreData.add(new Object[]{name, totalBooks});
        }
        cursor.close();
        return genreData;
    }
}
