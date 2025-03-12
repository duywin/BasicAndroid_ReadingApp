package com.example.readingapp.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

import com.example.readingapp.R;
import com.example.readingapp.dao.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class adminChart extends AppCompatActivity {
    private BarChart barChart;
    private PieChart pieChart;
    private AccountDAO accountDAO;
    private GenreDAO genreDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chart);

        // Initialize DAOs
        accountDAO = new AccountDAO(this);
        genreDAO = new GenreDAO(this);

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

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
            int id = item.getItemId();
            if (id == R.id.nav_admin_chart) {
                return true;
            } else if (id == R.id.nav_admin_story) {
                startActivity(new Intent(adminChart.this, adminStory.class));
                return true;
            } else if (id == R.id.nav_admin_genre) {
                startActivity(new Intent(adminChart.this, adminGenre.class));
                return true;
            } else if (id == R.id.nav_admin_account) {
                startActivity(new Intent(adminChart.this, adminAccount.class));
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
}
