package com.example.readingapp.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.*;

public class UploadChapterActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;
    private EditText chapterNameInput;
    private Button btnSelectFile, btnSaveFile;
    private Uri fileUri;
    private ChapterDAO chapterDAO;
    private int bookId;

    private int accountId;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_chapter);

        chapterNameInput = findViewById(R.id.chapter_name_input);
        btnSelectFile = findViewById(R.id.btn_select_file);
        btnSaveFile = findViewById(R.id.btn_save_file);
        Button backButton = findViewById(R.id.back_button);
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


        btnSelectFile.setOnClickListener(v -> openFileChooser());
        btnSaveFile.setOnClickListener(v -> saveFileAsDocx());
        backButton.setOnClickListener(v -> ReturnChapter());
    }

    private void ReturnChapter(){
        Intent i = new Intent (UploadChapterActivity.this, adminChapter.class);
        startActivity(i);
        finish();
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"text/plain", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileAsDocx() {
        if (fileUri == null) {
            Toast.makeText(this, "Vui lòng chọn một tệp!", Toast.LENGTH_SHORT).show();
            return;
        }

        String chapterName = chapterNameInput.getText().toString().trim();
        if (chapterName.isEmpty()) {
            Toast.makeText(this, "Tên chương không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure "chapters" directory exists in internal storage
        File chaptersDir = new File(getFilesDir(), "chapters");
        if (!chaptersDir.exists()) {
            chaptersDir.mkdirs();  // ✅ Create directory if not exists
        }

        // Save file inside "chapters" directory
        File file = new File(chaptersDir, chapterName + ".docx");

        try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
             XWPFDocument document = new XWPFDocument();
             FileOutputStream fos = new FileOutputStream(file)) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(line);
            }

            document.write(fos);

            // ✅ Save only the file name (not full path) for correct retrieval
            String savedLink = chapterName + ".docx";

            // ✅ Insert into database
            boolean inserted = chapterDAO.insertChapter(chapterName, bookId, savedLink);
            if (inserted) {
                Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi lưu vào cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu tệp!", Toast.LENGTH_SHORT).show();
        }
    }
}
