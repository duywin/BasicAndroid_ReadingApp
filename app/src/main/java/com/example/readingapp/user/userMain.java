package com.example.readingapp.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.BookCarouselAdapter;
import com.example.readingapp.GenreAdapter;
import com.example.readingapp.dao.BookDAO;
import com.example.readingapp.dao.GenreDAO;
import com.example.readingapp.model.Book;
import com.example.readingapp.model.Genre;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.List;

public class userMain extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private int accountId;
    private ViewPager hotBooksCarousel;
    private RecyclerView categoryList;
    private Button btnLogout;
    private BookDAO bookDAO;
    private GenreDAO genreDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        // Initialize UI elements
        hotBooksCarousel = findViewById(R.id.hotBooksCarousel);
        categoryList = findViewById(R.id.categoryList);
        btnLogout = findViewById(R.id.btnLogout);
        BottomNavigationView bottomNavigationView = findViewById(R.id.user_bottom_navigation);

        // Initialize DAOs
        bookDAO = new BookDAO(this);
        genreDAO = new GenreDAO(this);

        // Retrieve account_id from SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        accountId = sharedPreferences.getInt("account_id", -1);

        if (accountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }

        // Load recommended books into the carousel
        loadRecommendedBooks();

        // Load genres into the category list
        loadGenres();

        // Set up logout button
        btnLogout.setOnClickListener(v -> logout());

        // Handle bottom navigation (you can add actual navigation logic here)
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_user_main:
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

    private void loadRecommendedBooks() {
        List<Book> books = bookDAO.getAllBooks();
        Collections.shuffle(books); // Shuffle to get random books
        if (books.size() > 5) {
            books = books.subList(0, 5); // Take only 5 books
        }
        BookCarouselAdapter adapter = new BookCarouselAdapter(this, books);
        hotBooksCarousel.setAdapter(adapter);
    }

    private void loadGenres() {
        List<Genre> genres = genreDAO.getAllGenres();
        GenreAdapter adapter = new GenreAdapter(this, genres, genre -> {
            Toast.makeText(this, "Selected Genre: " + genre.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Implement navigation to books of selected genre
        });

        categoryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryList.setAdapter(adapter);
    }


    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("account_id");
        editor.apply();
        startActivity(new Intent(this, LogIn.class));
        finish();
    }
}
