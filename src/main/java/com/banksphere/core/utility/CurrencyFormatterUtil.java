package com.banksphere.core.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public final class CurrencyFormatterUtil {

    private CurrencyFormatterUtil() {}

    public static String format(BigDecimal amount, String currencyCode) {
        if (amount == null) return null;
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance(currencyCode));
        return format.format(amount);
    }

    public static String format(BigDecimal amount) {
        return format(amount, "INR");
    }

    public static String formatWithSymbol(BigDecimal amount, String currencyCode) {
        return format(amount, currencyCode);
    }

    public static BigDecimal roundToTwoDecimal(BigDecimal amount) {
        if (amount == null) return null;
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        // TODO: integrate exchange rate API
        return amount;
    }
}
