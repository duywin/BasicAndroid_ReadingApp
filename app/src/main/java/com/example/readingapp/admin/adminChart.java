package com.example.readingapp.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readingapp.LogIn;
import com.example.readingapp.R;
import com.example.readingapp.dao.AccountDAO;
import com.example.readingapp.dao.GenreDAO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class adminChart extends AppCompatActivity {
    private BarChart barChart;
    private PieChart pieChart;
    private AccountDAO accountDAO;
    private GenreDAO genreDAO;
    private int accountId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chart);

        // Retrieve account_id from SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        accountId = sharedPreferences.getInt("account_id", -1);

        if (accountId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
            finish();
            return;
        }

        // Initialize UI components
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        Button btnLogout = findViewById(R.id.btn_logout);
        TextView titleHome = findViewById(R.id.title_home);

        // Set title
        titleHome.setText("Trang chủ");

        // Logout button click event
        btnLogout.setOnClickListener(v -> logout());

        // Initialize DAOs
        accountDAO = new AccountDAO(this);
        genreDAO = new GenreDAO(this);

        // Fetch and display data
        setupBarChart();
        setupPieChart();

        // Initialize Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBarChart() {
        List<BarEntry> barEntries = createBarChartData();
        BarDataSet barDataSet = new BarDataSet(barEntries, "Số sách theo thể loại");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setText("Tổng số sách theo thể loại");
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void setupPieChart() {
        List<PieEntry> pieEntries = createPieChartData();
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Phân bổ giới tính");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("Nam vs Nữ");
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_chart);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent = null;
            int id = item.getItemId();

            if (id == R.id.nav_admin_chart) {
                return true;
            } else if (id == R.id.nav_admin_story) {
                intent = new Intent(this, adminStory.class);
            } else if (id == R.id.nav_admin_genre) {
                intent = new Intent(this, adminGenre.class);
            } else if (id == R.id.nav_admin_account) {
                intent = new Intent(this, adminAccount.class);
            }

            if (intent != null) {
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private List<BarEntry> createBarChartData() {
        List<BarEntry> entries = new ArrayList<>();
        List<Object[]> genreData = genreDAO.getTotalBooksByGenre();
        for (int i = 0; i < genreData.size(); i++) {
            Object[] data = genreData.get(i);
            entries.add(new BarEntry(i, (int) data[1]));
        }
        return entries;
    }

    private List<PieEntry> createPieChartData() {
        List<PieEntry> entries = new ArrayList<>();
        int[] genderDistribution = accountDAO.getGenderDistribution();
        entries.add(new PieEntry(genderDistribution[0], "Nam"));
        entries.add(new PieEntry(genderDistribution[1], "Nữ"));
        return entries;
    }

    private void logout() {
        // Clear account_id from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("account_id");
        editor.apply();

        // Redirect to login screen
        startActivity(new Intent(this, LogIn.class));
        finish();
    }
}
