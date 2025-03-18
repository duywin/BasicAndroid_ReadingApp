package com.example.readingapp.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.readingapp.model.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class userFavorite extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Book> favoriteBooks;
    private SharedPreferences sharedPreferences;
    private int accountId;
    private FavoriteDAO favoriteDAO;
    private FavoriteBookAdapter adapter;

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
            switch (item.getItemId()) {
                case R.id.nav_user_main:
                    startActivity(new Intent(userFavorite.this, userMain.class));
                    return true;
                case R.id.nav_user_search:
                    return true;
                case R.id.nav_user_favorite:
                    return true;
                case R.id.nav_user_profile:
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

            // Load book image
            File internalImage = new File(getFilesDir(), "books/" + book.getImageLink());
            if (internalImage.exists()) {
                Glide.with(holder.storyImage.getContext()).load(internalImage).into(holder.storyImage);
            } else {
                String assetPath = "file:///android_asset/books/" + book.getImageLink();
                Glide.with(holder.storyImage.getContext()).load(assetPath).into(holder.storyImage);
            }

            // Set favorite button state
            boolean isFavorite = favoriteDAO.isBookFavorited(accountId, book.getId());
            holder.favoriteButton.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.unfavorite);

            // Toggle favorite
            holder.favoriteButton.setOnClickListener(v -> {
                if (isFavorite) {
                    favoriteDAO.removeFavorite(accountId, book.getId());
                    books.remove(position);
                    notifyItemRemoved(position);
                } else {
                    favoriteDAO.addFavorite(accountId, book.getId());
                    holder.favoriteButton.setImageResource(R.drawable.favorite);
                }
            });

            /*// Open book details
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(userFavorite.this, userChapter.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
            });*/
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        public class FavoriteBookViewHolder extends RecyclerView.ViewHolder {
            TextView storyTitle, storyAuthor, storyDescription;
            ImageView storyImage;
            ImageButton favoriteButton;

            FavoriteBookViewHolder(View itemView) {
                super(itemView);
                storyTitle = itemView.findViewById(R.id.story_title);
                storyAuthor = itemView.findViewById(R.id.story_author);
                storyDescription = itemView.findViewById(R.id.story_description);
                storyImage = itemView.findViewById(R.id.story_image);
                favoriteButton = itemView.findViewById(R.id.favorite_button);
            }
        }
    }
}
