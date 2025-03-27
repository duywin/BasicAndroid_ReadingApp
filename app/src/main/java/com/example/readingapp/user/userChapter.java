package com.example.readingapp.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readingapp.ChapterContent;
import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.dao.BookDAO;
import com.example.readingapp.dao.ChapterDAO;
import com.example.readingapp.dao.RatingDAO;
import com.example.readingapp.model.Book;
import com.example.readingapp.model.Chapter;
import com.example.readingapp.model.Rating;

import java.io.File;
import java.util.List;

public class userChapter extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView bookImage;
    private TextView bookTitle, bookAuthor, bookDescription, avgRatingText;
    private RatingBar ratingBar;
    private Button btnSubmitRating;

    private List<Chapter> chapterList;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;
    private RatingDAO ratingDAO;
    private int bookId;
    private SharedPreferences sharedPreferences;
    private int accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chapter);

        // Initialize UI elements
        recyclerView = findViewById(R.id.chapter_recycler_view);
        bookImage = findViewById(R.id.book_image);
        bookTitle = findViewById(R.id.book_title);
        bookAuthor = findViewById(R.id.book_author);
        bookDescription = findViewById(R.id.book_description);
        avgRatingText = findViewById(R.id.avg_rating_text);
        ratingBar = findViewById(R.id.rating_bar);
        btnSubmitRating = findViewById(R.id.btn_submit_rating);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chapterDAO = new ChapterDAO(this);
        bookDAO = new BookDAO(this);
        ratingDAO = new RatingDAO(this);

        // Retrieve account_id from SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        accountId = sharedPreferences.getInt("account_id", -1);

        if (accountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId != -1) {
            loadBookDetails();
            loadChapters();
            loadAverageRating();
            checkUserRating();
        }

        btnSubmitRating.setOnClickListener(v -> submitRating());
    }

    private void loadBookDetails() {
        Book book = bookDAO.getBookById(bookId);
        if (book != null) {
            bookTitle.setText(book.getTitle());
            bookAuthor.setText("Tác giả: " + book.getAuthor());
            bookDescription.setText(book.getDescription());

            // Check if the image exists in internal storage
            File internalImage = new File(getFilesDir(), "books/" + book.getImageLink());

            if (internalImage.exists()) {
                // Load from internal storage
                Glide.with(this).load(internalImage).into(bookImage);
            } else {
                // Load from assets
                String assetPath = "file:///android_asset/books/" + book.getImageLink();
                Glide.with(this).load(assetPath).into(bookImage);
            }
        }
    }

    private void loadChapters() {
        chapterList = chapterDAO.getChaptersByBookId(bookId);
        recyclerView.setAdapter(new ChapterAdapter(chapterList));
    }

    private void loadAverageRating() {
        float avgRating = ratingDAO.getAverageRating(bookId);
        avgRatingText.setText("Đánh giá trung bình: " + String.format("%.1f", avgRating) + " ★");
    }

    private void checkUserRating() {
        List<Rating> ratings = ratingDAO.getRatingsByBook(bookId);
        for (Rating rating : ratings) {
            if (rating.getAccountId() == accountId) {
                ratingBar.setRating(rating.getRating());
                break;
            }
        }
    }

    private void submitRating() {
        float userRating = ratingBar.getRating();

        if (userRating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao trước khi gửi!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Rating> ratings = ratingDAO.getRatingsByBook(bookId);
        boolean ratingExists = false;

        for (Rating rating : ratings) {
            if (rating.getAccountId() == accountId) {
                ratingDAO.updateRating(rating.getId(), userRating);
                ratingExists = true;
                break;
            }
        }

        if (!ratingExists) {
            ratingDAO.insertRating(accountId, bookId, userRating);
        }

        Toast.makeText(this, "Đánh giá đã được gửi!", Toast.LENGTH_SHORT).show();
        loadAverageRating();
    }

    private class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
        private final List<Chapter> chapters;

        ChapterAdapter(List<Chapter> chapters) {
            this.chapters = chapters;
        }

        @NonNull
        @Override
        public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chapter_user_item, parent, false);
            return new ChapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
            Chapter chapter = chapters.get(position);
            holder.chapterName.setText(chapter.getChapterName());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(userChapter.this, ChapterContent.class);
                intent.putExtra("CHAPTER_NAME", chapter.getChapterName());
                intent.putExtra("CHAPTER_LINK", chapter.getLink());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return chapters.size();
        }

        class ChapterViewHolder extends RecyclerView.ViewHolder {
            TextView chapterName;

            ChapterViewHolder(View itemView) {
                super(itemView);
                chapterName = itemView.findViewById(R.id.chapter_name);
            }
        }
    }
}
