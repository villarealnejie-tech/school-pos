package pos.controller;

import pos.model.User;
import pos.view.HistoryPanel;
import pos.view.LoginFrame;
import pos.view.MainFrame;
import pos.view.ProductsPanel;
import pos.view.SalesPanel;

/**
 * Builds the main window for the logged-in user. Each tab gets its own
 * view + controller pair; admins also get the Products tab.
 */
public class MainController {

    private final MainFrame view;

    public MainController(User user) {
        view = new MainFrame();
        view.setController(this);
        view.setTitle("School POS - " + user.fullName() + " (" + user.role() + ")");

        SalesPanel salesView = new SalesPanel();
        SalesController sales = new SalesController(user, salesView);
        view.addTab("Point of Sale", salesView, sales::refresh);

        if (user.isAdmin()) {
            ProductsPanel productsView = new ProductsPanel();
            ProductController products = new ProductController(productsView);
            view.addTab("Products", productsView, products::refresh);
        }

        HistoryPanel historyView = new HistoryPanel();
        HistoryController history = new HistoryController(historyView);
        view.addTab("Sales History", historyView, history::refresh);
    }

    public void show() {
        view.setVisible(true);
    }

    public void logout() {
        view.dispose();
        new LoginController(new LoginFrame()).show();
    }
}
