package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    public static final String TABLE_NAME = "Accounts";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "dob TEXT NOT NULL, " +
                    "gender BOOLEAN NOT NULL, " +
                    "type INTEGER NOT NULL);";

    private final SQLiteDatabase db;
    public boolean updateAccountType(int accountId, int type) {
        ContentValues values = new ContentValues();
        values.put("type", type);

        return db.update("Accounts", values, "id = ?", new String[]{String.valueOf(accountId)}) > 0;
    }


    public AccountDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }

    public boolean insertAccount(String username, String email, String password, String dob, boolean gender, int type) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("dob", dob);
        values.put("gender", gender ? 1 : 0);
        values.put("type", type); //1: thường; 2: vip; 3: admin

        return db.insert(TABLE_NAME, null, values) != -1;
    }
    public boolean insertAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put("username", account.getUsername());
        values.put("email", account.getEmail());
        values.put("password", account.getPassword());
        values.put("dob", account.getDob());
        values.put("gender", account.isGender() ? 1 : 0);
        values.put("type", account.getType()); //1 la thuong, 2 la vip, 3 la admin

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    public boolean isUsernameExists(String username) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_NAME + " WHERE username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_NAME + " WHERE email = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public Account getAccountById(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Account account = new Account(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5) == 1,
                    cursor.getInt(6)
            );
            cursor.close();
            return account;
        }
        cursor.close();
        return null;
    }

    public Account getAccountByUsername(String username) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            Account account = new Account(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5) == 1,
                    cursor.getInt(6)
            );
            cursor.close();
            return account;
        }
        cursor.close();
        return null;
    }

    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            Account account = new Account(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5) == 1,
                    cursor.getInt(6)
            );
            accountList.add(account);
        }
        cursor.close();
        return accountList;
    }

    public boolean updateAccount(int id, String username, String email, String password, String dob, boolean gender, int type) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("dob", dob);
        values.put("gender", gender ? 1 : 0);
        values.put("type", type);

        return db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteAccount(int id) {
        return db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }
    public int[] getGenderDistribution() {
        int maleCount = 0, femaleCount = 0;
        Cursor cursor = db.rawQuery("SELECT gender, COUNT(*) FROM " + TABLE_NAME + " GROUP BY gender", null);
        while (cursor.moveToNext()) {
            int gender = cursor.getInt(0); // 1 = Male, 0 = Female
            int count = cursor.getInt(1);
            if (gender == 1) {
                maleCount = count;
            } else {
                femaleCount = count;
            }
        }
        cursor.close();
        return new int[]{maleCount, femaleCount};
    }
}
