package com.example.readingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final int DATABASE_VERSION = 4;
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
        db.execSQL(LogDAO.CREATE_TABLE);
        db.execSQL(SubscriptionDAO.CREATE_TABLE);

        preloadData(db);
        Log.d("DatabaseHelper", "All tables created successfully!");
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

            Iterator<String> keys = data.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.equals("id")) {
                    values.put(key, data.getInt(key)); // Ensure the ID is explicitly inserted
                } else {
                    values.put(key, data.get(key).toString());
                }
            }

            // Check if the record already exists before inserting
            if (!recordExists(db, tableName, values)) {
                long result = db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (result == -1) {
                    Log.d("DatabaseHelper", "Skipping duplicate entry for " + tableName + ": " + values);
                } else {
                    Log.d("DatabaseHelper", "Inserted into " + tableName + ": " + values);
                }
            }
        }
    }


    private boolean recordExists(SQLiteDatabase db, String tableName, ContentValues values) {
        String columnToCheck = "id"; // Assuming 'id' is the primary key
        if (!values.containsKey(columnToCheck)) return false;

        String id = values.getAsString(columnToCheck);
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + tableName + " WHERE id = ?", new String[]{id});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
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
        if (oldVersion < newVersion) {
            Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + BookDAO.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + GenreDAO.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + AccountDAO.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + FavoriteDAO.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ChapterDAO.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LogDAO.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SubscriptionDAO.TABLE_NAME);


            onCreate(db); // ✅ Recreate database tables
        }
    }

}
