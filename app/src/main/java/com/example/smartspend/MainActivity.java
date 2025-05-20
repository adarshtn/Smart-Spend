package com.example.smartspend;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseActionListener {

    private ExpenseManager expenseManager;
    private ExpenseAdapter adapter;
    private List<Expense> allExpenses;
    private TextView totalExpenseTextView;
    private Spinner categorySpinner;
    private EditText searchEditText;
    private Button filterButton;
    private RecyclerView expenseRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expenseManager = new ExpenseManager(this);
        allExpenses = expenseManager.getAllExpenses();

        totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
        categorySpinner = findViewById(R.id.categorySpinner);
        searchEditText = findViewById(R.id.searchEditText);
        filterButton = findViewById(R.id.filterButton);
        Button monthlyReportButton = findViewById(R.id.monthlyReportButton);
        monthlyReportButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MonthlyReportActivity.class);
            startActivity(intent);
        });

        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExpenseAdapter(this, allExpenses, expenseManager, this); // updated constructor
        expenseRecyclerView.setAdapter(adapter);

        setupCategorySpinner();
        displayTotalExpense(allExpenses);

        filterButton.setOnClickListener(v -> applyFilters());
        findViewById(R.id.addExpenseButton).setOnClickListener(v -> {
            ExpenseDialog dialog = new ExpenseDialog(this, null, newExpense -> {
                expenseManager.addExpense(newExpense);
                refreshExpenses();
            });
            dialog.show();
        });

    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Food");
        categories.add("Transport");
        categories.add("Shopping");
        categories.add("Bills");
        categories.add("Entertainment");
        categories.add("Other");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void applyFilters() {
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String searchText = searchEditText.getText().toString().trim();

        List<Expense> filteredList = new ArrayList<>();

        for (Expense expense : allExpenses) {
            boolean matchesCategory = selectedCategory.equals("All") || expense.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesSearch = TextUtils.isEmpty(searchText) || expense.getDescription().toLowerCase().contains(searchText.toLowerCase());

            if (matchesCategory && matchesSearch) {
                filteredList.add(expense);
            }
        }

        adapter.updateExpenses(filteredList);
        displayTotalExpense(filteredList);
    }

    private void refreshExpenses() {
        allExpenses = expenseManager.getAllExpenses();
        applyFilters();  // Maintain filters after update
    }

    private void displayTotalExpense(List<Expense> expenseList) {
        double total = 0;
        for (Expense e : expenseList) {
            total += e.getAmount();
        }
        totalExpenseTextView.setText("Total: ₹" + String.format("%.2f", total));
    }

    // Implemented from OnExpenseActionListener
    @Override
    public void onExpenseUpdated() {
        refreshExpenses();
    }

    // Called from ExpenseAdapter when Edit is clicked
    public void showEditDialog(Expense expense) {
        ExpenseDialog dialog = new ExpenseDialog(this, expense, updatedExpense -> {
            expenseManager.updateExpense(updatedExpense);
            refreshExpenses();
        });
        dialog.show();
    }
}
