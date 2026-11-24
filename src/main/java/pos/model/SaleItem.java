package pos.model;

import java.math.BigDecimal;

/** One line of a past sale. */
public record SaleItem(String productName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {}
