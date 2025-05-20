package com.example.smartspend;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {
    private static final String PREF_EXPENSES = "expenses";
    private SharedPreferences prefs;
    private List<Expense> expenses;
    private Gson gson;

    public ExpenseManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
        loadExpenses();
    }

    private void loadExpenses() {
        String json = prefs.getString(PREF_EXPENSES, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Expense>>() {}.getType();
            expenses = gson.fromJson(json, type);
        } else {
            expenses = new ArrayList<>();
        }
    }

    private void saveExpenses() {
        String json = gson.toJson(expenses);
        prefs.edit().putString(PREF_EXPENSES, json).apply();
    }

    public List<Expense> getAllExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveExpenses();
    }

    public void updateExpense(Expense updatedExpense) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(updatedExpense.getId())) {
                expenses.set(i, updatedExpense);
                saveExpenses();
                return;
            }
        }
    }

    public void deleteExpense(String expenseId) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(expenseId)) {
                expenses.remove(i);
                saveExpenses();
                return;
            }
        }
    }

    public double getTotalExpenses() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }
}