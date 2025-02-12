package com.example.readingapp;

import static org.junit.Assert.*;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        dbHelper.close();
    }

    @Test
    public void testDatabaseCreation() {
        assertNotNull(database);
    }

    @Test
    public void testTablesExist() {
        assertTrue(checkTableExists("accounts"));
        assertTrue(checkTableExists("genres"));
        assertTrue(checkTableExists("books"));
        assertTrue(checkTableExists("chapters"));
        assertTrue(checkTableExists("favorites"));
    }

    private boolean checkTableExists(String tableName) {
        Cursor cursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    @Test
    public void onUpgrade() {
    }

    @Test
    public void insertAccount() {
    }

    @Test
    public void updateAccount() {
    }

    @Test
    public void deleteAccount() {
    }

    @Test
    public void insertBook() {
    }

    @Test
    public void insertGenre() {
    }

    @Test
    public void insertChapter() {
    }

    @Test
    public void getChaptersByBook() {
    }

    @Test
    public void logDatabaseContents() {
    }

    @Test
    public void isUsernameExists() {
    }

    @Test
    public void isEmailExists() {
    }
}