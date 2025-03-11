package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Genre;

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

    public boolean insertGenre(String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("total_books", 0); // Default to 0

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    // Delete genre by ID
    public boolean deleteGenre(int genreId) {
        return db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(genreId)}) > 0;
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
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT id, name, total_books FROM " + TABLE_NAME, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int totalBooks = cursor.getInt(2);
            genres.add(new Genre(id, name, totalBooks));
        }
        cursor.close();
        return genres;
    }

    // Update total_books count based on number of books in the same genre
    public void updateTotalBooks(int genreId) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Books WHERE genre_id = ?",
                new String[]{String.valueOf(genreId)}
        );

        if (cursor.moveToFirst()) {
            int newTotal = cursor.getInt(0);
            cursor.close();

            Cursor checkCursor = db.rawQuery(
                    "SELECT total_books FROM Genres WHERE id = ?",
                    new String[]{String.valueOf(genreId)}
            );

            if (checkCursor.moveToFirst()) {
                int currentTotal = checkCursor.getInt(0);
                checkCursor.close();

                if (currentTotal != newTotal) {
                    ContentValues values = new ContentValues();
                    values.put("total_books", newTotal);
                    db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(genreId)});
                }
            }
        } else {
            cursor.close();
        }
    }

}
