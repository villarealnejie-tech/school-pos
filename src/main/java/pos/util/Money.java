package pos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Formats and parses peso amounts. Used by both the controllers and the views. */
public final class Money {
    public static final String PESO = "₱";

    private Money() {}

    public static String format(BigDecimal value) {
        return PESO + String.format("%,.2f", value);
    }

    /** Parses text such as "1,250.5" into 1250.50. Throws a RuntimeException if it isn't a number. */
    public static BigDecimal parse(String text) {
        return new BigDecimal(text.trim().replace(",", "")).setScale(2, RoundingMode.HALF_UP);
    }
}
