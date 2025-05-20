package com.example.smartspend;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Expense implements Serializable {
    private String id;
    private double amount;
    private String category;
    private String description;
    private Date date;

    public Expense() {
        this.id = UUID.randomUUID().toString();
        this.date = new Date(); // Current date by default
    }

    public Expense(double amount, String category, String description, Date date) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}