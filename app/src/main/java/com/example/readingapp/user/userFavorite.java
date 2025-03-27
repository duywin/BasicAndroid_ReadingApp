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
import com.example.readingapp.dao.FavoriteDAO;
import com.example.readingapp.dao.RatingDAO;
import com.example.readingapp.model.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class userFavorite extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteBookAdapter adapter;
    private List<Book> favoriteBooks = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private int accountId;
    private FavoriteDAO favoriteDAO;
    private RatingDAO ratingDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorite);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        accountId = sharedPreferences.getInt("account_id", -1);

        if (accountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }

        favoriteDAO = new FavoriteDAO(this);
        ratingDAO = new RatingDAO(this);

        recyclerView = findViewById(R.id.story_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadFavoriteBooks();
        setupBottomNavigation();
    }

    private void loadFavoriteBooks() {
        favoriteBooks = favoriteDAO.getFavoriteBooksByAccountId(accountId);
        if (favoriteBooks == null) {
            favoriteBooks = new ArrayList<>();
        }
        adapter = new FavoriteBookAdapter(favoriteBooks);
        recyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.user_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_user_favorite);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_main) {
                startActivity(new Intent(this, userMain.class));
                finish();
                return true;
            } else if (id == R.id.nav_user_search) {
                startActivity(new Intent(this, userSearch.class));
                finish();
                return true;
            } else if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, userProfile.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private class FavoriteBookAdapter extends RecyclerView.Adapter<FavoriteBookAdapter.FavoriteBookViewHolder> {
        private final List<Book> books;

        FavoriteBookAdapter(List<Book> books) {
            this.books = books;
        }

        @NonNull
        @Override
        public FavoriteBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_book_item, parent, false);
            return new FavoriteBookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavoriteBookViewHolder holder, int position) {
            Book book = books.get(position);
            holder.storyTitle.setText(book.getTitle());
            holder.storyAuthor.setText("Tác giả: " + book.getAuthor());
            holder.storyDescription.setText(book.getDescription());

            loadImage(holder.storyImage, book.getImageLink());

            // Handle Favorite Toggle
            updateFavoriteButton(holder.favoriteButton, book);

            holder.favoriteButton.setOnClickListener(v -> toggleFavorite(holder, book, position));

            // Load and display average rating
            float avgRating = ratingDAO.getAverageRating(book.getId());
            holder.storyRating.setRating(avgRating);

            // Open book details
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(userFavorite.this, userChapter.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
            });
        }

        private void toggleFavorite(FavoriteBookViewHolder holder, Book book, int position) {
            boolean isFavorite = favoriteDAO.isBookFavorited(accountId, book.getId());

            if (isFavorite) {
                favoriteDAO.removeFavorite(accountId, book.getId());
                books.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, books.size());
            } else {
                favoriteDAO.addFavorite(accountId, book.getId());
                holder.favoriteButton.setImageResource(R.drawable.favorite);
            }
        }

        private void updateFavoriteButton(ImageButton button, Book book) {
            boolean isFavorite = favoriteDAO.isBookFavorited(accountId, book.getId());
            button.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.unfavorite);
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        public class FavoriteBookViewHolder extends RecyclerView.ViewHolder {
            TextView storyTitle, storyAuthor, storyDescription;
            ImageView storyImage;
            ImageButton favoriteButton;
            RatingBar storyRating;

            FavoriteBookViewHolder(View itemView) {
                super(itemView);
                storyTitle = itemView.findViewById(R.id.story_title);
                storyAuthor = itemView.findViewById(R.id.story_author);
                storyDescription = itemView.findViewById(R.id.story_description);
                storyImage = itemView.findViewById(R.id.story_image);
                favoriteButton = itemView.findViewById(R.id.favorite_button);
                storyRating = itemView.findViewById(R.id.story_rating);
            }
        }
    }

    private void loadImage(ImageView imageView, String imageLink) {
        File internalImage = new File(getFilesDir(), "books/" + imageLink);
        if (internalImage.exists()) {
            Glide.with(this).load(internalImage).into(imageView);
        } else {
            String assetPath = "file:///android_asset/books/" + imageLink;
            Glide.with(this).load(assetPath).into(imageView);
        }
    }
}
