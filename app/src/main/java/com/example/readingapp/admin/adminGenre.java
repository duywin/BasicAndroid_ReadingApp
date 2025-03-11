package com.example.readingapp.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingapp.R;
import com.example.readingapp.dao.GenreDAO;
import com.example.readingapp.model.Genre;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class adminGenre extends AppCompatActivity {

    private TableLayout genreTable;
    private GenreDAO genreDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_genre);

        genreDAO = new GenreDAO(this);
        genreTable = findViewById(R.id.genre_table);
        Button addGenreButton = findViewById(R.id.add_genre_button);
        Button statsButton = findViewById(R.id.stats_button);

        addGenreButton.setOnClickListener(v -> addGenre());
        statsButton.setOnClickListener(v -> updateAllGenres());

        loadGenres();
        setupBottomNavigation();
    }

    private void loadGenres() {
        genreTable.removeAllViews();
        List<Genre> genres = genreDAO.getAllGenres();

        // Table header
        TableRow header = new TableRow(this);
        header.addView(createTextView("ID", true));
        header.addView(createTextView("Thể loại", true));
        header.addView(createTextView("Tổng sách", true));
        header.addView(createTextView("Thao tác", true));
        genreTable.addView(header);

        for (Genre genre : genres) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(String.valueOf(genre.getId()), false));
            row.addView(createTextView(genre.getName(), false));
            row.addView(createTextView(String.valueOf(genre.getTotalBooks()), false));

            ImageButton deleteButton = new ImageButton(this);
            deleteButton.setImageResource(R.drawable.delete_icon); // Use your delete icon
            deleteButton.setBackground(null); // Remove background for a cleaner look
            TableRow.LayoutParams params = new TableRow.LayoutParams(120, 120); // 16dp (48px) in pixels
            deleteButton.setLayoutParams(params);
            deleteButton.setPadding(4, 4, 4, 4);
            deleteButton.setOnClickListener(v -> deleteGenre(genre.getId()));

            row.addView(deleteButton);
            genreTable.addView(row);
        }
    }

    private TextView createTextView(String text, boolean isHeader) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        if (isHeader) textView.setTextSize(16);
        return textView;
    }

    private void addGenre() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm thể loại");

        // Create an input field
        final EditText input = new EditText(this);
        input.setHint("Nhập tên thể loại");
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);

        // Add buttons
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String genreName = input.getText().toString().trim();
            if (!genreName.isEmpty()) {
                if (genreDAO.insertGenre(genreName)) {
                    Toast.makeText(this, "Thêm thể loại thành công", Toast.LENGTH_SHORT).show();
                    loadGenres();
                } else {
                    Toast.makeText(this, "Lỗi khi thêm thể loại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Tên thể loại không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void deleteGenre(int genreId) {
        if (genreDAO.deleteGenre(genreId)) {
            Toast.makeText(this, "Xóa thể loại thành công", Toast.LENGTH_SHORT).show();
            loadGenres();
        } else {
            Toast.makeText(this, "Lỗi khi xóa thể loại", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAllGenres() {
        List<Genre> genres = genreDAO.getAllGenres();
        for (Genre genre : genres) {
            genreDAO.updateTotalBooks(genre.getId());
        }
        Toast.makeText(this, "Cập nhật tổng số sách hoàn tất", Toast.LENGTH_SHORT).show();
        loadGenres();
    }


    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_genre);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_admin_chart) {
                startActivity(new Intent(adminGenre.this, adminChart.class));
                return true;
            } else if (id == R.id.nav_admin_story) {
                startActivity(new Intent(adminGenre.this, adminStory.class));
                return true;
            } else if (id == R.id.nav_admin_genre) {
                return true;
            } else if (id == R.id.nav_admin_account) {
                return true;
            }
            return false;
        });
    }
}
