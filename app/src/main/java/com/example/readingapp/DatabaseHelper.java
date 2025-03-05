package com.example.readingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.readingapp.dao.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReadingApp.db";
    private static final int DATABASE_VERSION = 2;
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AccountDAO.CREATE_TABLE);
        db.execSQL(GenreDAO.CREATE_TABLE);
        db.execSQL(BookDAO.CREATE_TABLE);
        db.execSQL(FavoriteDAO.CREATE_TABLE);
        db.execSQL(ChapterDAO.CREATE_TABLE);

        preloadData(db);
    }

    private void preloadData(SQLiteDatabase db) {
        String jsonString = loadJSONFromAsset("preload_data.json");
        if (jsonString == null) {
            Log.e("DatabaseHelper", "Failed to load JSON");
            return;
        }

        db.beginTransaction();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            insertData(db, jsonObject.getJSONArray("accounts"), AccountDAO.TABLE_NAME);
            insertData(db, jsonObject.getJSONArray("genres"), GenreDAO.TABLE_NAME);
            insertData(db, jsonObject.getJSONArray("books"), BookDAO.TABLE_NAME);
            insertData(db, jsonObject.getJSONArray("chapters"), ChapterDAO.TABLE_NAME);
            insertData(db, jsonObject.getJSONArray("favorites"), FavoriteDAO.TABLE_NAME);

            db.setTransactionSuccessful();
            Log.d("DatabaseHelper", "Preloaded all data successfully.");
        } catch (JSONException e) {
            Log.e("DatabaseHelper", "JSON Parsing Error: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void insertData(SQLiteDatabase db, JSONArray dataArray, String tableName) throws JSONException {
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject data = dataArray.getJSONObject(i);
            ContentValues values = new ContentValues();

            Iterator<String> keys = data.keys(); // Get keys iterator
            while (keys.hasNext()) { // Iterate over keys
                String key = keys.next();
                values.put(key, data.get(key).toString());
            }

            db.insert(tableName, null, values);
            Log.d("DatabaseHelper", "Inserted into " + tableName + ": " + values);
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AccountDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GenreDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BookDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ChapterDAO.TABLE_NAME);
        onCreate(db);
    }
}
