package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubscriptionDAO {
    public static final String TABLE_NAME = "Subscriptions";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "account_id INTEGER NOT NULL, " +
                    "sub_date TEXT NOT NULL, " +
                    "exp_date TEXT NOT NULL, " +
                    "FOREIGN KEY(account_id) REFERENCES Accounts(id) ON DELETE CASCADE);";

    private final SQLiteDatabase db;

    public SubscriptionDAO(Context context) {
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }

    // Check if an account already has a subscription
    public boolean isAccountSubscribed(int accountId) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_NAME + " WHERE account_id = ?", new String[]{String.valueOf(accountId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Insert a new subscription
    public boolean insertSubscription(int accountId, String subDate, String expDate) {
        if (isAccountSubscribed(accountId)) {
            return false; // Prevent duplicate subscriptions
        }

        ContentValues values = new ContentValues();
        values.put("account_id", accountId);
        values.put("sub_date", subDate);
        values.put("exp_date", expDate);

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    // Insert using Subscription object
    public boolean insertSubscription(Subscription subscription) {
        return insertSubscription(subscription.getAccountId(), subscription.getSubDate(), subscription.getExpDate());
    }

    // Update subscription dates
    public boolean updateSubscription(int accountId, String subDate, String expDate) {
        if (!isAccountSubscribed(accountId)) {
            return false; // Cannot update non-existent subscription
        }

        ContentValues values = new ContentValues();
        values.put("sub_date", subDate);
        values.put("exp_date", expDate);

        return db.update(TABLE_NAME, values, "account_id = ?", new String[]{String.valueOf(accountId)}) > 0;
    }

    // Get subscription by account ID
    public Subscription getSubscriptionByAccountId(int accountId) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE account_id = ?", new String[]{String.valueOf(accountId)});
        Subscription subscription = null;
        if (cursor.moveToFirst()) {
            subscription = new Subscription(
                    cursor.getInt(0),  // id
                    cursor.getInt(1),  // account_id
                    cursor.getString(2), // sub_date
                    cursor.getString(3)  // exp_date
            );
        }
        cursor.close();
        return subscription;
    }

    public boolean updateSubscription(int accountId, int months) {
        Subscription subscription = getSubscriptionByAccountId(accountId);
        if (subscription == null) {
            return false; // No subscription found, cannot update
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date expDate;

        try {
            expDate = sdf.parse(subscription.getExpDate());
            if (expDate == null) throw new Exception(); // Ensure valid date
        } catch (Exception e) {
            expDate = new Date(); // Default to today if parsing fails
        }

        // Extend expiration date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expDate);
        calendar.add(Calendar.MONTH, months);
        String newExpDate = sdf.format(calendar.getTime());

        ContentValues values = new ContentValues();
        values.put("exp_date", newExpDate);

        return db.update(TABLE_NAME, values, "account_id = ?", new String[]{String.valueOf(accountId)}) > 0;
    }


    // Check if the subscription is expired
    public boolean isSubscriptionExpired(int accountId) {
        Cursor cursor = db.rawQuery("SELECT exp_date FROM " + TABLE_NAME + " WHERE account_id = ?", new String[]{String.valueOf(accountId)});
        boolean isExpired = true;
        if (cursor.moveToFirst()) {
            String expDate = cursor.getString(0);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date expirationDate = sdf.parse(expDate);
                isExpired = expirationDate != null && expirationDate.before(new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return isExpired;
    }

    public boolean addSubscription(int accountId, int months) {
        if (isAccountSubscribed(accountId)) {
            return false; // Prevent duplicate subscriptions
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String subDate = sdf.format(new Date()); // Current date

        // Calculate expiration date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, months);
        String expDate = sdf.format(calendar.getTime());

        return insertSubscription(accountId, subDate, expDate);
    }


    // Get all subscriptions
    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subscriptionList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            subscriptionList.add(new Subscription(
                    cursor.getInt(0),  // id
                    cursor.getInt(1),  // account_id
                    cursor.getString(2), // sub_date
                    cursor.getString(3)  // exp_date
            ));
        }
        cursor.close();
        return subscriptionList;
    }

    // Delete a subscription
    public boolean deleteSubscription(int accountId) {
        return db.delete(TABLE_NAME, "account_id = ?", new String[]{String.valueOf(accountId)}) > 0;
    }
}
