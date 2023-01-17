package pl.project.budgetassistant.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import pl.project.budgetassistant.firebase.models.Currency;
import pl.project.budgetassistant.firebase.models.User;

public class CurrencyHelper {
    public static String formatCurrency(Currency currency, long money) {
        long absMoney = Math.abs(money);
        return (currency.leftSide ? (currency.symbol + (currency.hasSpace ? " " : "")): "") +
                (money < 0 ? "-" : "") +
                (absMoney / 100) + "." +
                (absMoney % 100 < 10 ? "0" : "") +
                (absMoney % 100)  +
                (currency.leftSide ? "" : ((currency.hasSpace ? " " : "") + currency.symbol));
    }

    public static void setupAmountEditText(EditText editText, User user) {
        editText.setText(CurrencyHelper.formatCurrency(user.currency,0), TextView.BufferType.EDITABLE);
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!charSequence.toString().equals(current)) {
                    editText.removeTextChangedListener(this);
                    current = CurrencyHelper.formatCurrency(user.currency,convertAmountStringToLong(charSequence));
                    editText.setText(current);
                    editText.setSelection(current.length() -
                            (user.currency.leftSide ? 0 : (user.currency.symbol.length() + (user.currency.hasSpace ? 1 : 0))));

                    editText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static long convertAmountStringToLong(CharSequence s) {
        String cleanString = s.toString().replaceAll("[^0-9]", "");
        return Long.valueOf(cleanString);
    }
}
