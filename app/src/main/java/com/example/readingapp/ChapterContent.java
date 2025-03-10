package com.example.readingapp;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

public class ChapterContent extends AppCompatActivity {
    private TextView chapterContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_content);

        chapterContent = findViewById(R.id.chapter_content);

        // Kiểm tra Intent nhận được
        String chapterName = getIntent().getStringExtra("CHAPTER_NAME");
        String fileName = getIntent().getStringExtra("CHAPTER_LINK");

        Log.d("ChapterContent", "Nhận được CHAPTER_NAME: " + chapterName);
        Log.d("ChapterContent", "Nhận được CHAPTER_LINK: " + fileName);

        loadChapterContent(fileName);
    }

    private void loadChapterContent(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            chapterContent.setText("Không tìm thấy nội dung chương.");
            Log.e("ChapterContent", "Lỗi: Tên file chapter rỗng hoặc null!");
            return;
        }

        String filePath = "chapters/" + fileName;
        Log.d("ChapterContent", "Đang mở file: " + filePath);

        try (InputStream inputStream = getAssets().open(filePath)) {
            XWPFDocument document = new XWPFDocument(inputStream);
            SpannableStringBuilder formattedText = new SpannableStringBuilder();

            for (XWPFParagraph para : document.getParagraphs()) {
                int start = formattedText.length(); // Start index of this paragraph
                for (XWPFRun run : para.getRuns()) {
                    int runStart = formattedText.length(); // Start index of this run
                    formattedText.append(run.text());

                    // Apply Bold
                    if (run.isBold()) {
                        formattedText.setSpan(new StyleSpan(BOLD), runStart, formattedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    // Apply Italic
                    if (run.isItalic()) {
                        formattedText.setSpan(new StyleSpan(ITALIC), runStart, formattedText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                }
                formattedText.append("\n\n"); // Newline after each paragraph
            }

            chapterContent.setText(formattedText);
            Log.d("ChapterContent", "Đọc file thành công!");

        } catch (IOException e) {
            chapterContent.setText("Lỗi khi tải nội dung chương.");
            Log.e("ChapterContent", "Lỗi khi đọc file: " + filePath, e);
        }
    }
}
