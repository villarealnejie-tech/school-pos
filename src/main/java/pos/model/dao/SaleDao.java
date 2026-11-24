package pos.model.dao;

import pos.model.CartItem;
import pos.model.Product;
import pos.model.Sale;
import pos.model.SaleItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SaleDao {
    private SaleDao() {}

    /**
     * Saves a sale and deducts stock in ONE transaction.
     * If anything fails (e.g. not enough stock) everything is rolled back.
     *
     * @return the new sale id
     */
    public static int checkout(int userId, List<CartItem> items, BigDecimal cash) throws SQLException {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : items) total = total.add(ci.subtotal());
        BigDecimal change = cash.subtract(total);

        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                int saleId;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO sales(user_id, total, cash_given, change_amount) VALUES(?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setBigDecimal(2, total);
                    ps.setBigDecimal(3, cash);
                    ps.setBigDecimal(4, change);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        saleId = keys.getInt(1);
                    }
                }

                try (PreparedStatement item = c.prepareStatement(
                        "INSERT INTO sale_items(sale_id, product_id, product_name, quantity, unit_price, subtotal) "
                      + "VALUES(?,?,?,?,?,?)");
                     PreparedStatement stock = c.prepareStatement(
                        "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?")) {
                    for (CartItem ci : items) {
                        Product p = ci.getProduct();
                        stock.setInt(1, ci.getQuantity());
                        stock.setInt(2, p.id());
                        stock.setInt(3, ci.getQuantity());
                        if (stock.executeUpdate() == 0) {
                            throw new SQLException("Not enough stock for \"" + p.name() + "\".");
                        }
                        item.setInt(1, saleId);
                        item.setInt(2, p.id());
                        item.setString(3, p.name());
                        item.setInt(4, ci.getQuantity());
                        item.setBigDecimal(5, p.price());
                        item.setBigDecimal(6, ci.subtotal());
                        item.executeUpdate();
                    }
                }
                c.commit();
                return saleId;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** The latest 500 sales, newest first. */
    public static List<Sale> listSales() throws SQLException {
        String sql = "SELECT s.id, s.sale_date, u.full_name, s.total, s.cash_given, s.change_amount "
                   + "FROM sales s JOIN users u ON u.id = s.user_id ORDER BY s.id DESC LIMIT 500";
        List<Sale> rows = new ArrayList<>();
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Sale(rs.getInt(1), rs.getTimestamp(2), rs.getString(3),
                        rs.getBigDecimal(4), rs.getBigDecimal(5), rs.getBigDecimal(6)));
            }
        }
        return rows;
    }

    /** The items of one sale. */
    public static List<SaleItem> listItems(int saleId) throws SQLException {
        String sql = "SELECT product_name, quantity, unit_price, subtotal FROM sale_items WHERE sale_id = ?";
        List<SaleItem> rows = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new SaleItem(rs.getString(1), rs.getInt(2),
                            rs.getBigDecimal(3), rs.getBigDecimal(4)));
                }
            }
        }
        return rows;
    }

    public static BigDecimal todaysTotal() throws SQLException {
        String sql = "SELECT IFNULL(SUM(total), 0) FROM sales WHERE DATE(sale_date) = CURDATE()";
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getBigDecimal(1);
        }
    }
}
