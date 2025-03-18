package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public static final String TABLE_NAME = "Books";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "author TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "image_link TEXT NOT NULL, " +
                    "genre_id INTEGER, " +
                    "FOREIGN KEY(genre_id) REFERENCES Genres(id));";

    private final SQLiteDatabase db;

    public BookDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }
    public boolean insertBook(String title, String author, String description, String image_link, int genreId) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("author", author);
        values.put("description", description);
        values.put("image_link", image_link);
        values.put("genre_id", genreId);

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    public boolean insertBook(Book book) {
        return insertBook(book.getTitle(), book.getAuthor(), book.getDescription(), book.getImageLink(), book.getGenreId());
    }
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

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
                books.add(book);
                Log.d("BookDAO", "Retrieved: " + book.getTitle() + " (ID: " + book.getId() + ")");
            } while (cursor.moveToNext());
        } else {
            Log.d("BookDAO", "No books found in database.");
        }

        cursor.close();
        return books;
    }
    public List<Book> searchBooksByTitle(String title) {
        List<Book> bookList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE title LIKE ?", new String[]{"%" + title + "%"});

        if (cursor.moveToFirst()) {
            do {
                bookList.add(cursorToBook(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookList;
    }

    public List<Book> getBooksByGenre(int genreId) {
        List<Book> bookList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE genre_id = ?", new String[]{String.valueOf(genreId)});

        if (cursor.moveToFirst()) {
            do {
                bookList.add(cursorToBook(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookList;
    }


    public boolean editBook(int id, String title, String author, String description, String image_link, int genreId) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("author", author);
        values.put("description", description);
        values.put("image_link", image_link);
        values.put("genre_id", genreId);

        return db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }
    public Book getBookById(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            Book book = new Book(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("author")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image_link")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("genre_id"))
            );
            cursor.close();
            return book;
        }
        cursor.close();
        return null;
    }
    public boolean deleteBook(int id) {
        return db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }
    private Book cursorToBook(Cursor cursor) {
        return new Book(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("author")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("image_link")),
                cursor.getInt(cursor.getColumnIndexOrThrow("genre_id"))
        );
    }
}
