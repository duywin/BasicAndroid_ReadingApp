package com.example.readingapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.readingapp.DatabaseHelper;
import com.example.readingapp.model.Chapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChapterDAO {
    public static final String TABLE_NAME = "Chapters";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "chapter_name TEXT NOT NULL, " +
                    "book_id INTEGER, " +
                    "link TEXT NOT NULL, " +
                    "FOREIGN KEY(book_id) REFERENCES Books(id));";

    private final SQLiteDatabase db;
    private final Context context;

    public ChapterDAO(Context context) {
        this.context = context;
        this.db = new DatabaseHelper(context).getWritableDatabase();
    }

    public List<Chapter> getChaptersByBookId(int bookId) {
        List<Chapter> chapters = new ArrayList<>();

        // Get chapters from database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE book_id = ?",
                new String[]{String.valueOf(bookId)});
        while (cursor.moveToNext()) {
            chapters.add(new Chapter(
                    cursor.getInt(0),     // id
                    cursor.getString(1),  // chapter_name
                    cursor.getInt(2),     // book_id
                    cursor.getString(3)   // link (file path)
            ));
        }
        cursor.close();

        // Search for the actual files for these chapters in assets and internal storage
        for (Chapter chapter : chapters) {
            chapter.setLink(findChapterFile(chapter.getLink()));
        }

        return chapters;
    }

    // Find the correct file path for a chapter, checking both internal storage and assets
    private String findChapterFile(String link) {
        File internalFile = new File(context.getFilesDir(), "chapters/" + link);

        // ✅ Check if file exists in internal storage
        if (internalFile.exists() && internalFile.isFile()) {
            Log.d("ChapterDAO", "Found chapter in internal storage: " + internalFile.getAbsolutePath());
            return internalFile.getAbsolutePath();  // ✅ Return full path for internal storage
        }

        // ✅ Check if file exists in assets
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list("chapters");
            if (files != null) {
                for (String file : files) {
                    if (file.equals(link)) {
                        Log.d("ChapterDAO", "Found chapter in assets: " + file);
                        return "chapters/" + file;  // ✅ Only return relative path
                    }
                }
            }
        } catch (IOException e) {
            Log.e("ChapterDAO", "Error checking assets/chapters/", e);
        }

        Log.w("ChapterDAO", "Chapter file not found: " + link);
        return null; // Return null if the file is not found
    }


    public boolean insertChapter(String chapterName, int bookId, String filePath) {
        ContentValues values = new ContentValues();
        values.put("chapter_name", chapterName);
        values.put("book_id", bookId);
        values.put("link", filePath);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;  // Returns true if insert was successful
    }

    public boolean deleteChapter(int chapterId) {
        return db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(chapterId)}) > 0;
    }

    public boolean deleteInternalChapter(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }
}
