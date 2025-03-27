package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Rating;

import java.util.ArrayList;
import java.util.List;

public class RatingDAO {
    public static final String TABLE_NAME = "Ratings";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "account_id INTEGER NOT NULL, " +
                    "book_id INTEGER NOT NULL, " +
                    "rating REAL NOT NULL, " +
                    "FOREIGN KEY(account_id) REFERENCES Accounts(id), " +
                    "FOREIGN KEY(book_id) REFERENCES Books(id));";

    private final SQLiteDatabase db;

    public RatingDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }

    public boolean insertRating(int accountId, int bookId, float rating) {
        ContentValues values = new ContentValues();
        values.put("account_id", accountId);
        values.put("book_id", bookId);
        values.put("rating", rating);

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    public boolean insertRating(Rating rating) {
        return insertRating(rating.getAccountId(), rating.getBookId(), rating.getRating());
    }

    public boolean updateRating(int id, float newRating) {
        ContentValues values = new ContentValues();
        values.put("rating", newRating);

        return db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteRating(int id) {
        return db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public Rating getRatingById(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Rating rating = new Rating(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("account_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("book_id")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("rating"))
            );
            cursor.close();
            return rating;
        }
        cursor.close();
        return null;
    }

    public List<Rating> getRatingsByBook(int bookId) {
        List<Rating> ratings = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE book_id = ?", new String[]{String.valueOf(bookId)});

        while (cursor.moveToNext()) {
            ratings.add(new Rating(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("account_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("book_id")),
                    cursor.getFloat(cursor.getColumnIndexOrThrow("rating"))
            ));
        }
        cursor.close();
        return ratings;
    }

    public float getAverageRating(int bookId) {
        Cursor cursor = db.rawQuery("SELECT AVG(rating) FROM " + TABLE_NAME + " WHERE book_id = ?", new String[]{String.valueOf(bookId)});
        if (cursor.moveToFirst()) {
            float avgRating = cursor.getFloat(0);
            cursor.close();
            return avgRating;
        }
        cursor.close();
        return 0;
    }
}
