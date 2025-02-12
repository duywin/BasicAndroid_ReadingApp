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
import android.os.Bundle;

import com.example.readingapp.R;

public class adminChart extends AppCompatActivity {
    private BarChart barChart;
    private PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chart);

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        // Tạo dữ liệu
        List<BarEntry> entries = createBarChartData();
        // Tạo dữ liệu
        List<PieEntry> entriess = createPieChartData();

        // Tạo BarDataSet
        BarDataSet dataSet = new BarDataSet(entries, "Số chương đã đọc");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Chọn màu cho các cột

        // Tạo PieDataSet
        PieDataSet dataSets = new PieDataSet(entriess, "Tỷ lệ truyện đã đọc");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Chọn màu cho các phần
        // Tạo BarData
        BarData barData = new BarData(dataSet);
        // Tạo PieData
        PieData pieData = new PieData(dataSets);

        // Thiết lập dữ liệu cho BarChart
        barChart.setData(barData);
        // Thiết lập dữ liệu cho PieChart
        pieChart.setData(pieData);

        // Tùy chỉnh biểu đồ (tùy chọn)
        barChart.getDescription().setText("Số chương đã đọc trong tuần");
        barChart.animateY(1000); // Hiệu ứng animation khi hiển thị
        barChart.setFitBars(true); // Đảm bảo các cột vừa khít với trục x
        barChart.invalidate(); // Vẽ lại biểu đồ
        // Tùy chỉnh biểu đồ (tùy chọn)
        pieChart.getDescription().setEnabled(false); // Ẩn mô tả
        pieChart.setCenterText("Truyện đã đọc"); // Thêm text ở giữa
        pieChart.animateY(1000); // Hiệu ứng animation khi hiển thị
        pieChart.invalidate(); // Vẽ lại biểu đồ
    }


    // Hàm tạo dữ liệu
    private List<BarEntry> createBarChartData() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 10f)); // Ngày 1: 10 chương
        entries.add(new BarEntry(1f, 15f)); // Ngày 2: 15 chương
        entries.add(new BarEntry(2f, 8f));  // Ngày 3: 8 chương
        entries.add(new BarEntry(3f, 12f)); // Ngày 4: 12 chương
        entries.add(new BarEntry(4f, 20f)); // Ngày 5: 20 chương
        entries.add(new BarEntry(5f, 5f));  // Ngày 6: 5 chương
        entries.add(new BarEntry(6f, 18f)); // Ngày 7: 18 chương
        return entries;
    }

    // Hàm tạo dữ liệu
    private List<PieEntry> createPieChartData() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(30f, "Truyện 1")); // 30%
        entries.add(new PieEntry(20f, "Truyện 2")); // 20%
        entries.add(new PieEntry(15f, "Truyện 3")); // 15%
        entries.add(new PieEntry(35f, "Truyện 4")); // 35%
        return entries;
    }
}