package com.example.readingapp;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
            StringBuilder text = new StringBuilder();

            for (XWPFParagraph para : document.getParagraphs()) {
                text.append(para.getText()).append("\n\n");
            }

            chapterContent.setText(text.toString());
            Log.d("ChapterContent", "Đọc file thành công!");

        } catch (IOException e) {
            chapterContent.setText("Lỗi khi tải nội dung chương.");
            Log.e("ChapterContent", "Lỗi khi đọc file: " + filePath, e);
        }
    }



    private String readDocxFromAssets(String filePath) {
        StringBuilder content = new StringBuilder();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(filePath);
            XWPFDocument document = new XWPFDocument(inputStream);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph para : paragraphs) {
                content.append(para.getText()).append("\n\n");
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khi đọc file";
        }
        return content.toString();
    }
}
