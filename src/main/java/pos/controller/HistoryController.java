package pos.controller;

import pos.model.Sale;
import pos.model.dao.SaleDao;
import pos.view.HistoryPanel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Sales history: past sales, the items of the selected sale, and today's total. */
public class HistoryController {

    private final HistoryPanel view;
    private List<Sale> sales = new ArrayList<>();

    public HistoryController(HistoryPanel view) {
        this.view = view;
        view.setController(this);
        refresh();
    }

    public void refresh() {
        BigDecimal today;
        try {
            sales = SaleDao.listSales();
            today = SaleDao.todaysTotal();
        } catch (SQLException ex) {
            view.showError("Could not load sales:\n" + ex.getMessage());
            return;
        }
        view.showTodaysTotal(today);
        view.showSales(sales);
        view.clearItems();
    }

    public void selectSale(int row) {
        view.clearItems();
        if (row < 0) return;
        try {
            view.showItems(SaleDao.listItems(sales.get(row).id()));
        } catch (SQLException ex) {
            view.showError(ex.getMessage());
        }
    }
}
