package com.example.readingapp.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readingapp.R;
import com.example.readingapp.dao.BookDAO;
import com.example.readingapp.dao.GenreDAO;
import com.example.readingapp.model.Book;
import com.example.readingapp.model.Genre;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class addStoryActivity extends AppCompatActivity {
    private EditText titleInput, authorInput, descriptionInput;
    private Spinner genreSpinner;
    private ImageView storyImagePreview;
    private Uri selectedImageUri;
    private int selectedGenreId = -1;
    private int bookId = -1; // Default -1 means adding a new book
    private String currentImagePath = null; // Store existing image path if editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        titleInput = findViewById(R.id.story_title);
        authorInput = findViewById(R.id.story_author);
        descriptionInput = findViewById(R.id.story_description);
        genreSpinner = findViewById(R.id.story_genre);
        storyImagePreview = findViewById(R.id.story_image_preview);
        Button chooseImageButton = findViewById(R.id.choose_image_button);
        Button saveButton = findViewById(R.id.save_story_button);

        loadGenres();

        // Check if editing
        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId != -1) {
            loadBookDetails(bookId);
        }

        chooseImageButton.setOnClickListener(v -> selectImage());
        saveButton.setOnClickListener(v -> saveStory());
    }

    private void loadGenres() {
        GenreDAO genreDAO = new GenreDAO(this);
        List<Genre> genres = genreDAO.getAllGenres();
        List<String> genreNames = new ArrayList<>();
        final List<Integer> genreIds = new ArrayList<>();

        for (Genre genre : genres) {
            genreNames.add(genre.getName());
            genreIds.add(genre.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(adapter);

        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenreId = genreIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadBookDetails(int bookId) {
        BookDAO bookDAO = new BookDAO(this);
        Book book = bookDAO.getBookById(bookId);

        if (book != null) {
            titleInput.setText(book.getTitle());
            authorInput.setText(book.getAuthor());
            descriptionInput.setText(book.getDescription());
            selectedGenreId = book.getGenreId();
            currentImagePath = book.getImageLink(); // Store existing image

            // Set spinner selection
            GenreDAO genreDAO = new GenreDAO(this);
            List<Genre> genres = genreDAO.getAllGenres();
            for (int i = 0; i < genres.size(); i++) {
                if (genres.get(i).getId() == selectedGenreId) {
                    genreSpinner.setSelection(i);
                    break;
                }
            }

            // Load existing image
            loadImage(currentImagePath);
        }
    }

    private void loadImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return;

        if (imagePath.startsWith("assets/")) { // Preloaded assets
            try {
                InputStream inputStream = getAssets().open(imagePath.replace("assets/", ""));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                storyImagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // Saved in internal storage
            File file = new File(getFilesDir(), imagePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                storyImagePreview.setImageBitmap(bitmap);
            }
        }
    }

    private void saveStory() {
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || description.isEmpty() || selectedGenreId == -1) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Keep existing image unless a new one is selected
        String imageFileName = currentImagePath;

        if (selectedImageUri != null) {
            // Use a consistent filename pattern to avoid multiple books issue
            imageFileName = "book_" + (bookId == -1 ? System.currentTimeMillis() : bookId) + ".png";

            if (!saveImage(selectedImageUri, imageFileName)) {
                Toast.makeText(this, "Lỗi khi lưu ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        BookDAO bookDAO = new BookDAO(this);

        if (bookId == -1) { // Add new book
            boolean inserted = bookDAO.insertBook(title, author, description, imageFileName, selectedGenreId);
            if (inserted) {
                Toast.makeText(this, "Thêm truyện thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi thêm truyện!", Toast.LENGTH_SHORT).show();
            }
        } else { // Edit existing book
            boolean updated = bookDAO.editBook(bookId, title, author, description, imageFileName, selectedGenreId);
            if (updated) {
                Toast.makeText(this, "Cập nhật truyện thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật truyện!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean saveImage(Uri imageUri, String fileName) {
        try {
            File directory = new File(getFilesDir(), "books");
            if (!directory.exists()) directory.mkdirs();

            File imageFile = new File(directory, fileName);
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private final ActivityResultLauncher<Intent> selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    storyImagePreview.setImageURI(selectedImageUri);
                }
            });

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }
}
