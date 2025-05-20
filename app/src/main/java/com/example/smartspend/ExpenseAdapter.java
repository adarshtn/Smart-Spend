package com.example.smartspend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private Context context;
    private ExpenseManager expenseManager;
    private OnExpenseActionListener listener;
    private SimpleDateFormat dateFormat;

    // Functional interface with one method for lambda usage
    public interface OnExpenseActionListener {
        void onExpenseUpdated();
    }

    public ExpenseAdapter(Context context, List<Expense> expenseList,
                          ExpenseManager manager,
                          OnExpenseActionListener listener) {
        this.context = context;
        this.expenseList = expenseList;
        this.expenseManager = manager;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void updateExpenses(List<Expense> expenses) {
        this.expenseList = expenses;
        notifyDataSetChanged();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView, amountTextView, descriptionTextView, dateTextView;
        ImageButton menuButton;

        ExpenseViewHolder(View view) {
            super(view);
            categoryTextView = view.findViewById(R.id.categoryTextView);
            amountTextView = view.findViewById(R.id.amountTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            menuButton = view.findViewById(R.id.menuButton);
        }

        void bind(final Expense expense) {
            categoryTextView.setText(expense.getCategory());
            amountTextView.setText(String.format("₹%.2f", expense.getAmount()));
            descriptionTextView.setText(expense.getDescription());
            dateTextView.setText(dateFormat.format(expense.getDate()));

            // Set category background color
            setCategoryBackground(expense.getCategory());

            menuButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, menuButton);
                popupMenu.inflate(R.menu.expense_item_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.action_edit) {
                        // Show edit dialog from context if it's MainActivity
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).showEditDialog(expense);
                        }
                        return true;
                    } else if (id == R.id.action_delete) {
                        expenseManager.deleteExpense(expense.getId());
                        expenseList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        listener.onExpenseUpdated(); // Refresh chart or summary
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
        }

        private void setCategoryBackground(String category) {
            int backgroundColor;
            switch (category.toLowerCase()) {
                case "food":
                    backgroundColor = R.color.category_food;
                    break;
                case "transport":
                    backgroundColor = R.color.category_transport;
                    break;
                case "entertainment":
                    backgroundColor = R.color.category_entertainment;
                    break;
                case "shopping":
                    backgroundColor = R.color.category_shopping;
                    break;
                case "bills":
                    backgroundColor = R.color.category_bills;
                    break;
                case "health":
                    backgroundColor = R.color.category_health;
                    break;
                default:
                    backgroundColor = R.color.category_others;
                    break;
            }
            categoryTextView.setBackgroundResource(R.drawable.category_background);
            categoryTextView.getBackground().setTint(context.getResources().getColor(backgroundColor));
        }
    }
}
