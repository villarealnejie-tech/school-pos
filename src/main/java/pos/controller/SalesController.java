package pos.controller;

import pos.model.Cart;
import pos.model.CartItem;
import pos.model.Product;
import pos.model.User;
import pos.model.ValidationException;
import pos.model.dao.ProductDao;
import pos.model.dao.SaleDao;
import pos.util.Money;
import pos.view.SalesPanel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The cashier screen: product search, the cart, cash/change and checkout.
 */
public class SalesController {

    private final User user;
    private final SalesPanel view;
    private final Cart cart = new Cart();
    private List<Product> shown = new ArrayList<>();

    public SalesController(User user, SalesPanel view) {
        this.user = user;
        this.view = view;
        view.setController(this);
        refresh();
        updateCart();
    }

    /**
     * Reloads the product list from the database using the search text.
     */
    public void refresh() {
        try {
            shown = ProductDao.search(view.getSearchText());
        } catch (SQLException ex) {
            view.showError("Could not load products:\n" + ex.getMessage());
            return;
        }
        view.showProducts(shown);
    }

    public void addSelected(int row) {
        if (row < 0) {
            view.showInfo("Select a product first.");
            return;
        }
        addToCart(shown.get(row));
    }

    public void addByBarcode(String code) {
        code = code.trim();
        if (code.isEmpty()) {
            return;
        }
        try {
            Product p = ProductDao.findByBarcode(code);
            if (p == null) {
                view.showError("No product with barcode " + code);
            } else {
                addToCart(p);
            }
        } catch (SQLException ex) {
            view.showError(ex.getMessage());
        }
        view.clearBarcode();
    }

    private void addToCart(Product p) {
        try {
            cart.add(p);
        } catch (ValidationException ex) {
            view.showError(ex.getMessage());
            return;
        }
        updateCart();
    }

    public void changeQuantity(int row, int delta) {
        if (row < 0) {
            return;
        }
        try {
            cart.changeQuantity(row, delta);
        } catch (ValidationException ex) {
            view.showError(ex.getMessage());
            return;
        }
        updateCart();
        if (row < cart.size()) {
            view.selectCartRow(row);
        }
    }

    public void removeItem(int row) {
        if (row < 0) {
            return;
        }
        cart.remove(row);
        updateCart();
    }

    public void clearCart() {
        cart.clear();
        updateCart();
    }

    /**
     * Called whenever the cash field changes.
     */
    public void cashChanged(String cashText) {
        try {
            view.showChange(Money.parse(cashText).subtract(cart.total()));
        } catch (RuntimeException ex) {
            view.showChange(BigDecimal.ZERO);
        }
    }

    public void checkout(String cashText) {
        if (cart.isEmpty()) {
            view.showInfo("The cart is empty.");
            return;
        }
        BigDecimal cash;
        try {
            cash = Money.parse(cashText);
        } catch (RuntimeException ex) {
            view.showError("Enter the cash given as a number, e.g. 100");
            return;
        }
        BigDecimal total = cart.total();
        if (cash.compareTo(total) < 0) {
            view.showError("Cash given is less than the total.");
            return;
        }

        try {
            List<CartItem> snapshot = new ArrayList<>(cart.getItems());
            int saleId = SaleDao.checkout(user.id(), snapshot, cash);
            view.showReceipt(saleId, user.fullName(), snapshot, total, cash);
            cart.clear();
            view.clearCash();
            updateCart();
            refresh();
        } catch (SQLException ex) {
            view.showError("Checkout failed:\n" + ex.getMessage());
            refresh();
        }
    }

    private void updateCart() {
        view.showCart(cart.getItems(), cart.total());
        cashChanged(view.getCashText());
    }
}
