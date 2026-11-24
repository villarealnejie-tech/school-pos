package pos.controller;

import pos.model.Category;
import pos.model.Product;
import pos.model.dao.ProductDao;
import pos.util.Money;
import pos.view.ProductsPanel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Admin screen: add / edit / delete products and add categories. */
public class ProductController {

    private static final int DUPLICATE_KEY = 1062; // MySQL error code

    private final ProductsPanel view;
    private List<Product> current = new ArrayList<>();
    private int selectedId = 0;

    public ProductController(ProductsPanel view) {
        this.view = view;
        view.setController(this);
        refresh();
    }

    public void refresh() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(0, "(none)"));
        try {
            current = ProductDao.search("");
            categories.addAll(ProductDao.categories());
        } catch (SQLException ex) {
            view.showError("Could not load products:\n" + ex.getMessage());
            return;
        }
        view.showCategories(categories);
        view.showProducts(current);
        clearForm();
    }

    public void selectProduct(int row) {
        if (row < 0) return;
        Product p = current.get(row);
        selectedId = p.id();
        view.fillForm(p);
    }

    public void clearForm() {
        selectedId = 0;
        view.clearForm();
    }

    public void save(String barcode, String name, String priceText, String stockText, Category category) {
        name = name.trim();
        if (name.isEmpty()) { view.showError("Name is required."); return; }
        BigDecimal price;
        int stock;
        try {
            price = Money.parse(priceText);
            if (price.signum() < 0) throw new NumberFormatException();
        } catch (RuntimeException ex) {
            view.showError("Price must be a number, e.g. 25.50");
            return;
        }
        try {
            stock = Integer.parseInt(stockText.trim());
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            view.showError("Stock must be a whole number (0 or more).");
            return;
        }
        int categoryId = category == null ? 0 : category.id();
        try {
            if (selectedId == 0) ProductDao.insert(barcode, name, categoryId, price, stock);
            else ProductDao.update(selectedId, barcode, name, categoryId, price, stock);
            refresh();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == DUPLICATE_KEY) view.showError("That barcode is already used by another product.");
            else view.showError("Could not save:\n" + ex.getMessage());
        }
    }

    public void delete(String name) {
        if (selectedId == 0) { view.showInfo("Select a product to delete."); return; }
        if (!view.confirm("Delete \"" + name + "\"?")) return;
        try {
            ProductDao.delete(selectedId);
            refresh();
        } catch (SQLException ex) {
            view.showError("Could not delete:\n" + ex.getMessage());
        }
    }

    public void addCategory() {
        String name = view.askCategoryName();
        if (name == null || name.isBlank()) return;
        try {
            ProductDao.addCategory(name);
            refresh();
        } catch (SQLException ex) {
            view.showError(ex.getErrorCode() == DUPLICATE_KEY ? "That category already exists." : ex.getMessage());
        }
    }
}
