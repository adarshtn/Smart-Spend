package com.example.smartspend;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseActionListener {

    private ExpenseManager expenseManager;
    private ExpenseAdapter expenseAdapter;
    private RecyclerView expenseRecyclerView;
    private TextView totalExpenseTextView;
    private FloatingActionButton addExpenseButton;
    private SimpleDateFormat dateFormat;
    private Date selectedDate;
    private static final String[] CATEGORIES = {"Food", "Transport", "Entertainment", "Shopping", "Bills", "Health", "Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize
        expenseManager = new ExpenseManager(this);
        dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        selectedDate = new Date();

        // Setup UI elements
        totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        addExpenseButton = findViewById(R.id.addExpenseButton);

        // Setup RecyclerView
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(this, expenseManager.getAllExpenses(), this);
        expenseRecyclerView.setAdapter(expenseAdapter);

        // Setup FAB
        addExpenseButton.setOnClickListener(v -> showAddExpenseDialog(null));

        // Update UI
        updateUI();
    }

    private void updateUI() {
        // Update total expense
        double total = expenseManager.getTotalExpenses();
        totalExpenseTextView.setText(String.format("Total: ₹%.2f", total));

        // Update expense list
        expenseAdapter.updateExpenses(expenseManager.getAllExpenses());
    }

    private void showAddExpenseDialog(Expense existingExpense) {
        final boolean isEditing = existingExpense != null;
        final Expense expense = isEditing ? existingExpense : new Expense();

        // Create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_expense);
        dialog.setCancelable(true);

        // Get views
        TextInputEditText amountEditText = dialog.findViewById(R.id.amountEditText);
        AutoCompleteTextView categoryDropdown = dialog.findViewById(R.id.categoryDropdown);
        TextInputEditText descriptionEditText = dialog.findViewById(R.id.descriptionEditText);
        Button datePickerButton = dialog.findViewById(R.id.datePickerButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        // Setup category dropdown
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        categoryDropdown.setAdapter(categoryAdapter);

        // Fill with existing data if editing
        if (isEditing) {
            amountEditText.setText(String.valueOf(expense.getAmount()));
            categoryDropdown.setText(expense.getCategory(), false);
            descriptionEditText.setText(expense.getDescription());
            selectedDate = expense.getDate();
        } else {
            selectedDate = new Date();
        }

        datePickerButton.setText(dateFormat.format(selectedDate));

        // Setup date picker
        datePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedDate = calendar.getTime();
                        datePickerButton.setText(dateFormat.format(selectedDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Setup buttons
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        saveButton.setOnClickListener(v -> {
            // Validate input
            String amountString = amountEditText.getText().toString().trim();
            String category = categoryDropdown.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (amountString.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountString);

                // Update or create expense
                expense.setAmount(amount);
                expense.setCategory(category);
                expense.setDescription(description);
                expense.setDate(selectedDate);

                if (isEditing) {
                    expenseManager.updateExpense(expense);
                } else {
                    expenseManager.addExpense(expense);
                }

                // Update UI and dismiss dialog
                updateUI();
                dialog.dismiss();

                // Show success message
                String message = isEditing ? "Expense updated" : "Expense added";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onEditExpense(Expense expense) {
        showAddExpenseDialog(expense);
    }

    @Override
    public void onDeleteExpense(Expense expense) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    expenseManager.deleteExpense(expense.getId());
                    updateUI();
                    Toast.makeText(MainActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("About SmartSpend")
                    .setMessage("SmartSpend is a simple expense tracker app to help you manage your finances.\n\nVersion 1.0")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}