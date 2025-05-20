package com.example.smartspend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthlyReportActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ExpenseManager expenseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        pieChart = findViewById(R.id.pieChart);
        expenseManager = new ExpenseManager(this);

        showMonthlyPieChart();
    }

    private void showMonthlyPieChart() {
        List<Expense> allExpenses = expenseManager.getAllExpenses();

        Map<String, Float> categoryTotals = new HashMap<>();
        long currentMonthMillis = System.currentTimeMillis();

        for (Expense expense : allExpenses) {
            // Only show current month expenses
            if (android.text.format.DateFormat.format("MM-yyyy", expense.getDate())
                    .equals(android.text.format.DateFormat.format("MM-yyyy", currentMonthMillis))) {
                String category = expense.getCategory();
                float amount = (float) expense.getAmount();

                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Monthly Expense");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChart.invalidate(); // refresh chart
    }
}
