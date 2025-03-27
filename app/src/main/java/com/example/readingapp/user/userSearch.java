package com.example.readingapp.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.dao.BookDAO;
import com.example.readingapp.dao.FavoriteDAO;
import com.example.readingapp.dao.RatingDAO;
import com.example.readingapp.model.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class userSearch extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton sortButton;
    private List<Book> bookList;
    private SharedPreferences sharedPreferences;
    private int accountId;
    private BookDAO bookDAO;
    private FavoriteDAO favoriteDAO;
    private RatingDAO ratingDAO;
    private BookAdapter adapter;
    private boolean isAscending = true; // Sorting state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        accountId = sharedPreferences.getInt("account_id", -1);

        if (accountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }

        bookDAO = new BookDAO(this);
        favoriteDAO = new FavoriteDAO(this);
        ratingDAO = new RatingDAO(this);

        recyclerView = findViewById(R.id.story_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sortButton = findViewById(R.id.sort_button);
        sortButton.setOnClickListener(v -> toggleSortOrder());

        loadBooks();
        setupBottomNavigation();
    }

    private void loadBooks() {
        Intent intent = getIntent();
        String query = intent.getStringExtra("search_query");
        int genreId = intent.getIntExtra("genre_id", -1);

        if (query != null) {
            bookList = bookDAO.searchBooksByTitle(query);
        } else if (genreId != -1) {
            bookList = bookDAO.getBooksByGenre(genreId);
        } else {
            bookList = bookDAO.getAllBooks();
        }

        if (bookList == null) {
            bookList = new ArrayList<>();
        }

        sortBooks();
        adapter = new BookAdapter(bookList);
        recyclerView.setAdapter(adapter);
    }

    private void toggleSortOrder() {
        isAscending = !isAscending;
        sortBooks();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Sắp xếp: " + (isAscending ? "A → Z" : "Z → A"), Toast.LENGTH_SHORT).show();
    }

    private void sortBooks() {
        Collections.sort(bookList, (b1, b2) ->
                isAscending ? b1.getTitle().compareToIgnoreCase(b2.getTitle())
                        : b2.getTitle().compareToIgnoreCase(b1.getTitle())
        );
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.user_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_user_search);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_user_main:
                    startActivity(new Intent(userSearch.this, userMain.class));
                    return true;
                case R.id.nav_user_search:
                    return true;
                case R.id.nav_user_favorite:
                    startActivity(new Intent(userSearch.this, userFavorite.class));
                    return true;
                case R.id.nav_user_profile:
                    startActivity(new Intent(userSearch.this, userProfile.class));
                    return true;
            }
            return false;
        });
    }

    private class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
        private final List<Book> books;

        BookAdapter(List<Book> books) {
            this.books = books;
        }

        @NonNull
        @Override
        public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_book_item, parent, false);
            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
            Book book = books.get(position);
            holder.storyTitle.setText(book.getTitle());
            holder.storyAuthor.setText("Tác giả: " + book.getAuthor());
            holder.storyDescription.setText(book.getDescription());

            // Load book image
            File internalImage = new File(getFilesDir(), "books/" + book.getImageLink());
            if (internalImage.exists()) {
                Glide.with(holder.storyImage.getContext()).load(internalImage).into(holder.storyImage);
            } else {
                String assetPath = "file:///android_asset/books/" + book.getImageLink();
                Glide.with(holder.storyImage.getContext()).load(assetPath).into(holder.storyImage);
            }

            float avgRating = ratingDAO.getAverageRating(book.getId());
            holder.storyRating.setRating(avgRating);

            // Check if book is favorited
            boolean isFavorite = favoriteDAO.isBookFavorited(accountId, book.getId());
            holder.favoriteButton.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.unfavorite);

            // Toggle favorite status
            holder.favoriteButton.setOnClickListener(v -> {
                if (isFavorite) {
                    favoriteDAO.removeFavorite(accountId, book.getId());
                    holder.favoriteButton.setImageResource(R.drawable.unfavorite);
                } else {
                    favoriteDAO.addFavorite(accountId, book.getId());
                    holder.favoriteButton.setImageResource(R.drawable.favorite);
                }
            });

            // Open book details
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(userSearch.this, userChapter.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        public class BookViewHolder extends RecyclerView.ViewHolder {
            TextView storyTitle, storyAuthor, storyDescription;
            ImageView storyImage;

            ImageButton favoriteButton;
            RatingBar storyRating;


            BookViewHolder(View itemView) {
                super(itemView);
                storyTitle = itemView.findViewById(R.id.story_title);
                storyAuthor = itemView.findViewById(R.id.story_author);
                storyDescription = itemView.findViewById(R.id.story_description);
                storyRating = itemView.findViewById(R.id.story_rating);
                storyImage = itemView.findViewById(R.id.story_image);
                favoriteButton = itemView.findViewById(R.id.favorite_button);
            }
        }
    }
}
