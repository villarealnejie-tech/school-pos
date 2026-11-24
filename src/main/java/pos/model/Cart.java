package pos.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The shopping cart of the current sale. Holds the business rules for
 * quantities: you can never put more of a product in the cart than is in stock.
 */
public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() { return Collections.unmodifiableList(items); }
    public boolean isEmpty() { return items.isEmpty(); }
    public int size() { return items.size(); }

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : items) total = total.add(ci.subtotal());
        return total;
    }

    /** Adds one of the product (or increases its quantity if it is already in the cart). */
    public void add(Product p) {
        for (CartItem ci : items) {
            if (ci.getProduct().id() == p.id()) {
                if (ci.getQuantity() + 1 > p.stock()) throw new ValidationException("Only " + p.stock() + " in stock.");
                ci.setQuantity(ci.getQuantity() + 1);
                return;
            }
        }
        if (p.stock() < 1) throw new ValidationException("\"" + p.name() + "\" is out of stock.");
        items.add(new CartItem(p, 1));
    }

    /** Changes the quantity of the item at index by delta. Going below 1 removes the item. */
    public void changeQuantity(int index, int delta) {
        CartItem ci = items.get(index);
        int q = ci.getQuantity() + delta;
        if (q < 1) items.remove(index);
        else if (q > ci.getProduct().stock()) throw new ValidationException("Only " + ci.getProduct().stock() + " in stock.");
        else ci.setQuantity(q);
    }

    public void remove(int index) { items.remove(index); }

    public void clear() { items.clear(); }
}
