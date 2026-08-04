package com.banksphere.core.constants;

public enum CurrencyCode {
    INR("Indian Rupee", "₹"),
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    GBP("British Pound", "£"),
    JPY("Japanese Yen", "¥"),
    SGD("Singapore Dollar", "S$"),
    AED("UAE Dirham", "د.إ"),
    AUD("Australian Dollar", "A$");

    private final String displayName;
    private final String symbol;

    CurrencyCode(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }
}
