package pos.model;

import java.math.BigDecimal;

public record Product(int id, String barcode, String name, int categoryId, String categoryName,
        BigDecimal price, int stock) {

}
