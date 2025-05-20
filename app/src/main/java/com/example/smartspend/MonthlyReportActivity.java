package com.example.smartspend;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MonthlyReportActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ExpenseManager expenseManager;
    private Button selectMonthButton;
    private Calendar selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        pieChart = findViewById(R.id.pieChart);
        selectMonthButton = findViewById(R.id.selectMonthButton);
        expenseManager = new ExpenseManager(this);
        selectedMonth = Calendar.getInstance();

        updateButtonLabel();
        showMonthlyPieChart();

        selectMonthButton.setOnClickListener(v -> showMonthPickerDialog());
    }

    private void showMonthPickerDialog() {
        int year = selectedMonth.get(Calendar.YEAR);
        int month = selectedMonth.get(Calendar.MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedMonth.set(Calendar.YEAR, year1);
                    selectedMonth.set(Calendar.MONTH, month1);
                    selectedMonth.set(Calendar.DAY_OF_MONTH, 1);
                    updateButtonLabel();
                    showMonthlyPieChart();
                }, year, month, 1);

        // Hide the day picker
        try {
            Field[] datePickerFields = dialog.getDatePicker().getClass().getDeclaredFields();
            for (Field field : datePickerFields) {
                if ("mDaySpinner".equals(field.getName()) || "mDayPicker".equals(field.getName())) {
                    field.setAccessible(true);
                    Object dayPicker = field.get(dialog.getDatePicker());
                    ((View) dayPicker).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog.show();
    }

    private void updateButtonLabel() {
        String label = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedMonth.getTime());
        selectMonthButton.setText(label);
    }

    private void showMonthlyPieChart() {
        List<Expense> allExpenses = expenseManager.getAllExpenses();

        Map<String, Float> categoryTotals = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
        String selectedMonthKey = sdf.format(selectedMonth.getTime());

        for (Expense expense : allExpenses) {
            String expenseMonth = sdf.format(expense.getDate());
            if (expenseMonth.equals(selectedMonthKey)) {
                String category = expense.getCategory();
                float amount = (float) expense.getAmount();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // Use custom color list to avoid repetition
        List<Integer> customColors = new ArrayList<>();
        customColors.add(Color.parseColor("#F44336")); // Red
        customColors.add(Color.parseColor("#2196F3")); // Blue
        customColors.add(Color.parseColor("#4CAF50")); // Green
        customColors.add(Color.parseColor("#FF9800")); // Orange
        customColors.add(Color.parseColor("#9C27B0")); // Purple
        customColors.add(Color.parseColor("#e6e200")); // Light Blue
        customColors.add(Color.parseColor("#8BC34A")); // Lime
        customColors.add(Color.parseColor("#FFC107")); // Amber
        customColors.add(Color.parseColor("#E91E63")); // Pink
        customColors.add(Color.parseColor("#009688")); // Teal

        dataSet.setColors(customColors);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);

        // Proper legend alignment
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);

        pieChart.invalidate(); // Refresh chart
    }
}
