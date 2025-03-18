package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Book;

import java.util.ArrayList;
import java.util.List;

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

    // Check if a book is in the user's favorites
    public boolean isBookFavorited(int accountId, int bookId) {
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE account_id = ? AND book_id = ?",
                new String[]{String.valueOf(accountId), String.valueOf(bookId)}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Add book to favorites
    public boolean addFavorite(int accountId, int bookId) {
        if (isBookFavorited(accountId, bookId)) return false;

        ContentValues values = new ContentValues();
        values.put("account_id", accountId);
        values.put("book_id", bookId);

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    public boolean isBookFavorite(int bookId) {
        String query = "SELECT * FROM Favorites WHERE book_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(bookId)});
        boolean isFavorite = cursor.moveToFirst(); // True if a record exists
        cursor.close();
        return isFavorite;
    }

    // Remove book from favorites
    public boolean removeFavorite(int accountId, int bookId) {
        return db.delete(TABLE_NAME, "account_id = ? AND book_id = ?",
                new String[]{String.valueOf(accountId), String.valueOf(bookId)}) > 0;
    }

    // Get all favorite books for a specific user
    public List<Book> getFavoriteBooksByAccountId(int accountId) {
        List<Book> favoriteBooks = new ArrayList<>();
        String query = "SELECT Books.* FROM Books " +
                "INNER JOIN " + TABLE_NAME + " ON Books.id = Favorites.book_id " +
                "WHERE Favorites.account_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(accountId)});
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("author")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image_link")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("genre_id"))
                );
                favoriteBooks.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteBooks;
    }
}
