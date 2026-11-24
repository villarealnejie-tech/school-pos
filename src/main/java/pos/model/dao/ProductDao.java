package pos.model.dao;

import pos.model.Category;
import pos.model.Product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class ProductDao {

    private ProductDao() {
    }

    private static final String SELECT
            = "SELECT p.id, p.barcode, p.name, IFNULL(p.category_id, 0) AS cid, IFNULL(c.name, '') AS cname, "
            + "p.price, p.stock FROM products p LEFT JOIN categories c ON c.id = p.category_id ";

    private static Product map(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("id"), rs.getString("barcode"), rs.getString("name"),
                rs.getInt("cid"), rs.getString("cname"), rs.getBigDecimal("price"), rs.getInt("stock"));
    }

    /**
     * Active products whose name or barcode contains the text (empty text =
     * all).
     */
    public static List<Product> search(String text) throws SQLException {
        String sql = SELECT + "WHERE p.active = 1 AND (p.name LIKE ? OR p.barcode LIKE ?) ORDER BY p.name";
        List<Product> list = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + (text == null ? "" : text.trim()) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public static Product findByBarcode(String barcode) throws SQLException {
        String sql = SELECT + "WHERE p.active = 1 AND p.barcode = ?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, barcode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public static List<Category> categories() throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT id, name FROM categories ORDER BY name")) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }

    public static void addCategory(String name) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("INSERT INTO categories(name) VALUES(?)")) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
        }
    }

    private static void bind(PreparedStatement ps, String barcode, String name, int categoryId,
            BigDecimal price, int stock) throws SQLException {
        if (barcode == null || barcode.isBlank()) {
            ps.setNull(1, Types.VARCHAR);
        } else {
            ps.setString(1, barcode.trim());
        }
        ps.setString(2, name.trim());
        if (categoryId <= 0) {
            ps.setNull(3, Types.INTEGER);
        } else {
            ps.setInt(3, categoryId);
        }
        ps.setBigDecimal(4, price);
        ps.setInt(5, stock);
    }

    public static void insert(String barcode, String name, int categoryId, BigDecimal price, int stock)
            throws SQLException {
        String sql = "INSERT INTO products(barcode, name, category_id, price, stock) VALUES(?,?,?,?,?)";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, barcode, name, categoryId, price, stock);
            ps.executeUpdate();
        }
    }

    public static void update(int id, String barcode, String name, int categoryId, BigDecimal price, int stock)
            throws SQLException {
        String sql = "UPDATE products SET barcode=?, name=?, category_id=?, price=?, stock=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, barcode, name, categoryId, price, stock);
            ps.setInt(6, id);
            ps.executeUpdate();
        }
    }

    /**
     * Soft delete: hides the product but keeps old sales intact.
     */
    public static void delete(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE products SET active = 0 WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
