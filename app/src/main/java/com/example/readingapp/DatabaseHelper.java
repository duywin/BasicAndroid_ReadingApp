package com.example.readingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.readingapp.model.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReadingApp.db";
    private static final int DATABASE_VERSION = 2;
    private final Context context;

    // Table Names
    private static final String TABLE_ACCOUNTS = "Accounts";
    private static final String TABLE_BOOKS = "Books";
    private static final String TABLE_GENRES = "Genres";
    private static final String TABLE_FAVORITES = "Favorites";
    private static final String TABLE_CHAPTERS = "Chapters";

    // Common Columns
    private static final String COLUMN_ID = "id";

    // Accounts Table Columns
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_GENDER = "gender"; // 1 = male, 0 = female
    private static final String COLUMN_TYPE = "type"; // 1 = Normal, 2 = Premium, 3 = Admin

    // Books Table Columns
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_GENRE_ID = "genre_id";

    // Genres Table Columns
    private static final String COLUMN_GENRE_NAME = "name";
    private static final String COLUMN_TOTAL_BOOKS = "total_books";

    // Favorites Table Columns
    private static final String COLUMN_ACCOUNT_ID = "account_id";
    private static final String COLUMN_BOOK_ID = "book_id";

    // Chapters Table Columns
    private static final String COLUMN_CHAPTER_NAME = "chapter_name";
    private static final String COLUMN_BOOK_IDC_FK = "book_id";
    private static final String COLUMN_LINK = "link";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_DOB + " TEXT NOT NULL, " +
                COLUMN_GENDER + " BOOLEAN NOT NULL, " +
                COLUMN_TYPE + " INTEGER NOT NULL);");

        db.execSQL("CREATE TABLE " + TABLE_GENRES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GENRE_NAME + " TEXT NOT NULL, " +
                COLUMN_TOTAL_BOOKS + " INTEGER NOT NULL);");

        db.execSQL("CREATE TABLE " + TABLE_BOOKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_GENRE_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_GENRE_ID + ") REFERENCES " + TABLE_GENRES + "(" + COLUMN_ID + "));");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ACCOUNT_ID + " INTEGER, " +
                COLUMN_BOOK_ID + " INTEGER, " +
                "PRIMARY KEY(" + COLUMN_ACCOUNT_ID + ", " + COLUMN_BOOK_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + "));");

        db.execSQL("CREATE TABLE " + TABLE_CHAPTERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CHAPTER_NAME + " TEXT NOT NULL, " +
                COLUMN_BOOK_IDC_FK + " INTEGER, " +
                COLUMN_LINK + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_BOOK_IDC_FK + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + "));");

        preloadData(db);
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

    private void preloadData(SQLiteDatabase db) {
        String jsonString = loadJSONFromAsset("preload_data.json");
        if (jsonString == null) {
            Log.e("DatabaseHelper", "Failed to load JSON");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray accountsArray = jsonObject.getJSONArray("accounts");
            for (int i = 0; i < accountsArray.length(); i++) {
                JSONObject account = accountsArray.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(COLUMN_USERNAME, account.getString("username"));
                values.put(COLUMN_EMAIL, account.getString("email"));
                values.put(COLUMN_PASSWORD, account.getString("password"));
                values.put(COLUMN_DOB, account.getString("dob"));
                values.put(COLUMN_GENDER, account.getBoolean("gender") ? 1 : 0);
                values.put(COLUMN_TYPE, account.getInt("type"));
                db.insert(TABLE_ACCOUNTS, null, values);
            }
        } catch (JSONException e) {
            Log.e("DatabaseHelper", "JSON Parsing Error: " + e.getMessage());
        }
    }

    private String loadJSONFromAsset(String fileName) {
        try (InputStream is = context.getAssets().open(fileName)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e("DatabaseHelper", "Error reading JSON file", ex);
            return null;
        }
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

