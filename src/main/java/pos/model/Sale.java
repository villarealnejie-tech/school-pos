package pos.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * One row of the sales history.
 */
public record Sale(int id, Timestamp date, String cashier, BigDecimal total, BigDecimal cash, BigDecimal change) {

}
