package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Log;

import java.util.ArrayList;
import java.util.List;

public class LogDAO {
    public static final String TABLE_NAME = "Logs";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "account_id INTEGER, " +
                    "description TEXT NOT NULL, " +
                    "record_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(account_id) REFERENCES Accounts(id));";

    private final SQLiteDatabase db;

    public LogDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }

    // ✅ Insert a new log entry
    public boolean insertLog(int accountId, String description) {
        ContentValues values = new ContentValues();
        values.put("account_id", accountId);
        values.put("description", description);

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    // ✅ Retrieve all logs
    public List<Log> getAllLogs() {
        List<Log> logs = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY record_time DESC", null);

        while (cursor.moveToNext()) {
            logs.add(new Log(
                    cursor.getInt(0),   // id
                    cursor.getInt(1),   // account_id
                    cursor.getString(2), // description
                    cursor.getString(3)  // record_time
            ));
        }
        cursor.close();
        return logs;
    }
}
