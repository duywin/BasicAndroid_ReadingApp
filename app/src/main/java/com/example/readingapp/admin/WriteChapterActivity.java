package com.example.readingapp.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.dao.ChapterDAO;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

public class WriteChapterActivity extends AppCompatActivity {
    private EditText chapterNameInput, chapterContentInput;
    private Button btnBold, btnItalic, btnSave;
    private ChapterDAO chapterDAO;
    private int bookId;
    private int accountId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_chapter);

        chapterNameInput = findViewById(R.id.chapter_name_input);
        chapterContentInput = findViewById(R.id.chapter_content_input);
        btnBold = findViewById(R.id.btn_bold);
        btnItalic = findViewById(R.id.btn_italic);
        btnSave = findViewById(R.id.btn_save_chapter);
        chapterDAO = new ChapterDAO(this);

        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy BOOK_ID!", Toast.LENGTH_SHORT).show();
            finish(); // Prevent saving without a valid bookId
        }

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        accountId = sharedPreferences.getInt("account_id", -1);

        if (accountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }


        btnBold.setOnClickListener(v -> applyTextStyle(BOLD));
        btnItalic.setOnClickListener(v -> applyTextStyle(ITALIC));

        btnSave.setOnClickListener(v -> saveChapter());
    }

    private void applyTextStyle(int style) {
        SpannableStringBuilder builder = new SpannableStringBuilder(chapterContentInput.getText());
        builder.setSpan(new StyleSpan(style), chapterContentInput.getSelectionStart(), chapterContentInput.getSelectionEnd(), 0);
        chapterContentInput.setText(builder);
    }

    private void saveChapter() {
        String chapterName = chapterNameInput.getText().toString().trim();
        String content = chapterContentInput.getText().toString().trim();

        if (chapterName.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Chương và nội dung không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Create the directory if it doesn't exist
        File directory = new File(getFilesDir(), "chapters");
        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            if (!dirCreated) {
                Toast.makeText(this, "Lỗi khi tạo thư mục!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // ✅ Save file inside "chapters" directory
        File file = new File(directory, chapterName + ".docx");

        try (FileOutputStream fos = new FileOutputStream(file);
             XWPFDocument document = new XWPFDocument()) {

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(content);
            document.write(fos);

            // ✅ Save only the file name (not full path)
            String savedLink = chapterName + ".docx";

            // ✅ Insert into database
            boolean inserted = chapterDAO.insertChapter(chapterName, bookId, savedLink);
            if (inserted) {
                Toast.makeText(this, "Lưu chương thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi lưu vào cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu chương!", Toast.LENGTH_SHORT).show();
        }
    }
}
