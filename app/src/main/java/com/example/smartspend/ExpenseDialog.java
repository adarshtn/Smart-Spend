package com.example.smartspend;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseDialog {

    public interface OnExpenseEditedListener {
        void onExpenseEdited(Expense updatedExpense);
    }

    private final Context context;
    private final Expense expense; // Can be null for new expense
    private final OnExpenseEditedListener listener;

    public ExpenseDialog(Context context, Expense expense, OnExpenseEditedListener listener) {
        this.context = context;
        this.expense = expense;
        this.listener = listener;
    }

    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_expense, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle(expense == null ? "Add Expense" : "Edit Expense")
                .create();

        EditText amountEditText = view.findViewById(R.id.amountEditText);
        EditText descriptionEditText = view.findViewById(R.id.descriptionEditText);
        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        EditText dateEditText = view.findViewById(R.id.dateEditText);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        List<String> categories = Arrays.asList("Food", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set default values for new expense or populate existing
        Date selectedDate = new Date();
        if (expense != null) {
            amountEditText.setText(String.valueOf(expense.getAmount()));
            descriptionEditText.setText(expense.getDescription());
            selectedDate = expense.getDate();
            int index = categories.indexOf(expense.getCategory());
            if (index >= 0) categorySpinner.setSelection(index);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateEditText.setText(sdf.format(selectedDate));

        // Date picker
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        dateEditText.setOnClickListener(v -> {
            new DatePickerDialog(context, (DatePicker view1, int year, int month, int dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                dateEditText.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Save action
        saveButton.setOnClickListener(v -> {
            try {
                double amount = Double.parseDouble(amountEditText.getText().toString());
                String desc = descriptionEditText.getText().toString().trim();
                String category = categorySpinner.getSelectedItem().toString();
                Date date = sdf.parse(dateEditText.getText().toString());

                Expense result = (expense != null) ? expense : new Expense();
                result.setAmount(amount);
                result.setCategory(category);
                result.setDescription(desc);
                result.setDate(date);

                listener.onExpenseEdited(result);
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                // Optional: Toast for error
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
