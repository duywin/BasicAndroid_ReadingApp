package com.example.readingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.readingapp.model.Account;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReadingApp.db";
    private static final int DATABASE_VERSION = 2;  // Increment version when modifying schema

    // Table: Accounts
    public static final String TABLE_ACCOUNTS = "Accounts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_GENDER = "gender"; // 1 = male, 0 = female
    public static final String COLUMN_TYPE = "type"; // 1 = Normal, 2 = Premium, 3 = Admin

    // Table: Books
    public static final String TABLE_BOOKS = "Books";
    public static final String COLUMN_BOOK_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_GENRE_ID = "genre_id";

    // Table: Genres
    public static final String TABLE_GENRES = "Genres";
    public static final String COLUMN_GENRE_NAME = "name";
    public static final String COLUMN_TOTAL_BOOKS = "total_books";

    // Table: Favorites
    public static final String TABLE_FAVORITES = "Favorites";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_BOOK_IDF_FK = "book_id";

    // Table: Chapters
    public static final String TABLE_CHAPTERS = "Chapters";
    public static final String COLUMN_CHAPTER_ID = "id";
    public static final String COLUMN_CHAPTER_NAME = "chapter_name";
    public static final String COLUMN_BOOK_IDC_FK = "book_id"; // Foreign key referencing Books table

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Accounts table
        db.execSQL("CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_DOB + " TEXT NOT NULL, " +
                COLUMN_GENDER + " BOOLEAN NOT NULL, " +
                COLUMN_TYPE + " INTEGER NOT NULL);");

        // Create Books table
        db.execSQL("CREATE TABLE " + TABLE_BOOKS + " (" +
                COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_GENRE_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_GENRE_ID + ") REFERENCES " + TABLE_GENRES + "(" + COLUMN_ID + "));");

        // Create Genres table
        db.execSQL("CREATE TABLE " + TABLE_GENRES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GENRE_NAME + " TEXT NOT NULL, " +
                COLUMN_TOTAL_BOOKS + " INTEGER NOT NULL);");

        // Create Favorites table
        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ACCOUNT_ID + " INTEGER, " +
                COLUMN_BOOK_IDF_FK + " INTEGER, " +
                "PRIMARY KEY(" + COLUMN_ACCOUNT_ID + ", " + COLUMN_BOOK_IDF_FK + "), " +
                "FOREIGN KEY(" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_BOOK_IDF_FK + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "));");

        // Create Chapters table
        db.execSQL("CREATE TABLE " + TABLE_CHAPTERS + " (" +
                COLUMN_CHAPTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CHAPTER_NAME + " TEXT NOT NULL, " +
                COLUMN_BOOK_IDC_FK + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_BOOK_IDC_FK + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        onCreate(db);
    }

    // Insert an Account
    public boolean insertAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, account.getUsername());
        values.put(COLUMN_EMAIL, account.getEmail());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_DOB, account.getDob());
        values.put(COLUMN_GENDER, account.isGender() ? 1 : 0);
        values.put(COLUMN_TYPE, account.getType());

        long result = db.insert(TABLE_ACCOUNTS, null, values);
        return result != -1;
    }

    // Update an Account
    public boolean updateAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, account.getUsername());
        values.put(COLUMN_EMAIL, account.getEmail());
        values.put(COLUMN_PASSWORD, account.getPassword());
        values.put(COLUMN_DOB, account.getDob());
        values.put(COLUMN_GENDER, account.isGender() ? 1 : 0);
        values.put(COLUMN_TYPE, account.getType());

        int rowsAffected = db.update(TABLE_ACCOUNTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(account.getId())});
        return rowsAffected > 0;
    }

    // Delete an Account
    public boolean deleteAccount(int accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_ACCOUNTS, COLUMN_ID + " = ?", new String[]{String.valueOf(accountId)});
        return rowsDeleted > 0;
    }


    // Insert a Book
    public boolean insertBook(String title, String author, String description, int genreId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_GENRE_ID, genreId);

        long result = db.insert(TABLE_BOOKS, null, values);
        return result != -1;
    }

    // Insert a Genre
    public boolean insertGenre(String name, int totalBooks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENRE_NAME, name);
        values.put(COLUMN_TOTAL_BOOKS, totalBooks);

        long result = db.insert(TABLE_GENRES, null, values);
        return result != -1;
    }

    // Insert a Chapter
    public boolean insertChapter(String chapterName, int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAPTER_NAME, chapterName);
        values.put(COLUMN_BOOK_IDC_FK, bookId);

        long result = db.insert(TABLE_CHAPTERS, null, values);
        return result != -1;
    }

    // Retrieve Chapters by Book ID
    public Cursor getChaptersByBook(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CHAPTERS + " WHERE " + COLUMN_BOOK_IDC_FK + "=?", new String[]{String.valueOf(bookId)});
    }

    // Log database contents for debugging
    public void logDatabaseContents() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS, null);
        Log.d("DATABASE", "Accounts Table:");
        while (cursor.moveToNext()) {
            Log.d("DATABASE", "ID: " + cursor.getInt(0) + ", Username: " + cursor.getString(1));
        }
        cursor.close();

        cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKS, null);
        Log.d("DATABASE", "Books Table:");
        while (cursor.moveToNext()) {
            Log.d("DATABASE", "Book ID: " + cursor.getInt(0) + ", Title: " + cursor.getString(1));
        }
        cursor.close();
    }
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM Accounts WHERE username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM Accounts WHERE email = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

}

