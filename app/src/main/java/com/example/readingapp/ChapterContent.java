package com.example.readingapp;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

public class ChapterContent extends AppCompatActivity {
    private TextView chapterContent;
    private static final String TAG = "chapterContent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_content);

        chapterContent = findViewById(R.id.chapter_content);

        String chapterName = getIntent().getStringExtra("CHAPTER_NAME");
        String filePath = getIntent().getStringExtra("CHAPTER_LINK");

        Log.d(TAG, "CHAPTER_NAME: " + chapterName);
        Log.d(TAG, "CHAPTER_LINK: " + filePath);

        if (filePath == null || filePath.isEmpty()) {
            chapterContent.setText("Không tìm thấy nội dung chương.");
            Log.e(TAG, "Error: Empty or null filePath!");
            return;
        }

        // ✅ Check both internal storage and assets
        loadChapterContent(filePath);
    }

    private void loadChapterContent(String filePath) {
        if (filePath == null) {
            chapterContent.setText("Không tìm thấy nội dung chương.");
            Log.e(TAG, "Error: filePath is null!");
            return;
        }

        File internalFile = new File(filePath);

        // ✅ Load from internal storage (absolute path)
        if (internalFile.exists() && internalFile.isFile()) {
            Log.d(TAG, "Loading from internal storage: " + filePath);
            try (InputStream inputStream = new FileInputStream(internalFile)) {
                loadDocxContent(inputStream);
                return;
            } catch (IOException e) {
                Log.e(TAG, "Error reading internal file: " + filePath, e);
            }
        }

        // ✅ Load from assets (relative path)
        try (InputStream inputStream = getAssets().open(filePath)) {
            Log.d(TAG, "Loading from assets: " + filePath);
            loadDocxContent(inputStream);
        } catch (IOException e) {
            chapterContent.setText("Không thể tải chương này.");
            Log.e(TAG, "Error loading from assets: " + filePath, e);
        }
    }


    private void loadDocxContent(InputStream inputStream) throws IOException {
        XWPFDocument document = new XWPFDocument(inputStream);
        SpannableStringBuilder formattedText = new SpannableStringBuilder();

        for (XWPFParagraph para : document.getParagraphs()) {
            int start = formattedText.length();
            for (XWPFRun run : para.getRuns()) {
                int runStart = formattedText.length();
                formattedText.append(run.text());

                if (run.isBold()) {
                    formattedText.setSpan(new StyleSpan(BOLD), runStart, formattedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (run.isItalic()) {
                    formattedText.setSpan(new StyleSpan(ITALIC), runStart, formattedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            formattedText.append("\n\n");
        }

        chapterContent.setText(formattedText);
        Log.d(TAG, "Successfully read chapter content!");
    }
}
