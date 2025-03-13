package com.example.readingapp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_content);

        chapterContent = findViewById(R.id.chapter_content);

        String chapterName = getIntent().getStringExtra("CHAPTER_NAME");
        String fileName = getIntent().getStringExtra("CHAPTER_LINK");

        Log.d("ChapterContent", "CHAPTER_NAME: " + chapterName);
        Log.d("ChapterContent", "CHAPTER_LINK: " + fileName);

        loadChapterContent(fileName);
    }

    private void loadChapterContent(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            chapterContent.setText("Không tìm thấy nội dung chương.");
            Log.e("ChapterContent", "Error: Empty or null filename!");
            return;
        }

        String assetPath = "chapters/" + fileName;
        String filePath = new File(getFilesDir(), "chapters/" + fileName).getAbsolutePath();

        try (InputStream inputStream = getAssets().open(assetPath)) {
            loadDocxContent(inputStream);
        } catch (IOException e1) {
            try (InputStream inputStream = new FileInputStream(filePath)) {
                loadDocxContent(inputStream);
            } catch (IOException e2) {
                chapterContent.setText("Error loading chapter.");
                Log.e("ChapterContent", "Error reading from assets or storage: " + fileName, e2);
            }
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
        Log.d("ChapterContent", "Successfully read file!");
    }
}
