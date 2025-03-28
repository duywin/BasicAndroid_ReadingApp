package com.example.readingapp.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

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
    private RecyclerView hotBooksCarousel;
    private RecyclerView categoryList;
    private Button btnLogout;
    private BookDAO bookDAO;
    private GenreDAO genreDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        // Initialize UI elements
        EditText searchBox = findViewById(R.id.searchBox);
        ImageView searchButton = findViewById(R.id.searchButton);
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

        searchButton.setOnClickListener(v -> {
            String query = searchBox.getText().toString().trim();
            if (!query.isEmpty()) {
                Intent intent = new Intent(userMain.this, userSearch.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });


        // Handle bottom navigation (you can add actual navigation logic here)
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_user_main:
                    return true;
                case R.id.nav_user_search:
                    startActivity(new Intent(userMain.this, userSearch.class));
                    return true;
                case R.id.nav_user_favorite:
                    startActivity(new Intent(userMain.this, userFavorite.class));
                    return true;
                case R.id.nav_user_profile:
                    startActivity(new Intent(userMain.this, userProfile.class));
                    return true;
            }
            return false;
        });
    }

    private void loadRecommendedBooks() {
        List<Book> books = bookDAO.getAllBooks();
        Collections.shuffle(books);
        if (books.size() > 5) {
            books = books.subList(0, 5);
        }

        BookCarouselAdapter adapter = new BookCarouselAdapter(books);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hotBooksCarousel.setLayoutManager(layoutManager);
        hotBooksCarousel.setAdapter(adapter);

        // Attach SnapHelper for smooth snapping
        SnapHelper snapHelper = new PagerSnapHelper(); // You can also use LinearSnapHelper
        snapHelper.attachToRecyclerView(hotBooksCarousel);
    }




    private void loadGenres() {
        List<Genre> genres = genreDAO.getAllGenres();
        GenreAdapter adapter = new GenreAdapter(this, genres, genre -> {
            Toast.makeText(this, "Selected Genre: " + genre.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Implement navigation to books of selected genre
        });

        // Use GridLayoutManager with 2 columns
        categoryList.setLayoutManager(new GridLayoutManager(this, 2));
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
