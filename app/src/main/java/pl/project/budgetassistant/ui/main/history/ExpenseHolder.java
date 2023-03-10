package pl.project.budgetassistant.ui.main.history;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pl.project.budgetassistant.R;

public class ExpenseHolder extends RecyclerView.ViewHolder {

    final TextView dateTextView;
    final TextView moneyTextView;
    final TextView categoryTextView;
    final TextView nameTextView;
    final ImageView iconImageView;
    public View view;

    public ExpenseHolder(View itemView) {
        super(itemView);

        this.view = itemView;

        moneyTextView = itemView.findViewById(R.id.money_textview);
        categoryTextView = itemView.findViewById(R.id.category_textview);
        nameTextView = itemView.findViewById(R.id.name_textview);
        dateTextView = itemView.findViewById(R.id.date_textview);
        iconImageView = itemView.findViewById(R.id.icon_imageview);
    }
}
