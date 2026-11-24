package pos.view;

import pos.controller.SalesController;
import pos.model.CartItem;
import pos.model.Product;
import pos.util.Money;

import java.awt.Font;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * The cashier screen (view): product list, cart, cash field and receipt.
 * SalesController owns the cart and does the checkout.
 * The layout is edited in the NetBeans GUI Builder (SalesPanel.form).
 */
public class SalesPanel extends javax.swing.JPanel {

    private SalesController controller;
    private final DefaultTableModel productModel;
    private final DefaultTableModel cartModel;

    public SalesPanel() {
        initComponents();
        productModel = (DefaultTableModel) productTable.getModel();
        cartModel = (DefaultTableModel) cartTable.getModel();

        // Listeners the GUI Builder cannot attach (selection mode and text-change events)
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchField.getDocument().addDocumentListener(simpleListener(() -> {
            if (controller != null) controller.refresh();
        }));
        cashField.getDocument().addDocumentListener(simpleListener(() -> {
            if (controller != null) controller.cashChanged(cashField.getText());
        }));
    }

    public void setController(SalesController controller) {
        this.controller = controller;
    }

    // ---------- inputs ----------
    public String getSearchText() { return searchField.getText(); }
    public String getCashText() { return cashField.getText(); }

    // ---------- display ----------
    public void showProducts(List<Product> products) {
        productModel.setRowCount(0);
        for (Product p : products) {
            productModel.addRow(new Object[]{p.barcode() == null ? "" : p.barcode(), p.name(),
                    Money.format(p.price()), p.stock()});
        }
    }

    public void showCart(List<CartItem> items, BigDecimal total) {
        cartModel.setRowCount(0);
        for (CartItem ci : items) {
            cartModel.addRow(new Object[]{ci.getProduct().name(), Money.format(ci.getProduct().price()),
                    ci.getQuantity(), Money.format(ci.subtotal())});
        }
        totalLabel.setText("Total: " + Money.format(total));
    }

    /** Positive or zero = change to give back, negative = cash is short. */
    public void showChange(BigDecimal change) {
        changeLabel.setText(change.signum() >= 0
                ? "Change: " + Money.format(change)
                : "Short by: " + Money.format(change.negate()));
    }

    public void selectCartRow(int row) {
        cartTable.setRowSelectionInterval(row, row);
    }

    public void clearBarcode() {
        barcodeField.setText("");
        barcodeField.requestFocusInWindow();
    }

    public void clearCash() {
        cashField.setText("");
    }

    public void showReceipt(int saleId, String cashier, List<CartItem> items, BigDecimal total, BigDecimal cash) {
        StringBuilder sb = new StringBuilder();
        sb.append("        SCHOOL STORE\n");
        sb.append("  Receipt #").append(saleId).append("\n");
        sb.append("  Cashier: ").append(cashier).append("\n");
        sb.append("--------------------------------\n");
        for (CartItem ci : items) {
            sb.append(String.format("%-18s%s%n", ci.getProduct().name(), ""));
            sb.append(String.format("  %d x %-10s %10s%n", ci.getQuantity(),
                    Money.format(ci.getProduct().price()), Money.format(ci.subtotal())));
        }
        sb.append("--------------------------------\n");
        sb.append(String.format("%-14s %16s%n", "TOTAL", Money.format(total)));
        sb.append(String.format("%-14s %16s%n", "CASH", Money.format(cash)));
        sb.append(String.format("%-14s %16s%n", "CHANGE", Money.format(cash.subtract(total))));
        sb.append("\n     Thank you!\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Sale complete", JOptionPane.PLAIN_MESSAGE);
    }

    public void showError(String message) {
        Ui.error(this, message);
    }

    public void showInfo(String message) {
        Ui.info(this, message);
    }

    private static DocumentListener simpleListener(Runnable r) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { r.run(); }
            public void removeUpdate(DocumentEvent e) { r.run(); }
            public void changedUpdate(DocumentEvent e) { r.run(); }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        productSide = new javax.swing.JPanel();
        searchLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        barcodeLabel = new javax.swing.JLabel();
        barcodeField = new javax.swing.JTextField();
        productScroll = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        cartSide = new javax.swing.JPanel();
        cartLabel = new javax.swing.JLabel();
        cartScroll = new javax.swing.JScrollPane();
        cartTable = new javax.swing.JTable();
        plusButton = new javax.swing.JButton();
        minusButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        totalLabel = new javax.swing.JLabel();
        cashLabel = new javax.swing.JLabel();
        cashField = new javax.swing.JTextField();
        changeLabel = new javax.swing.JLabel();
        checkoutButton = new javax.swing.JButton();

        splitPane.setResizeWeight(0.5);

        searchLabel.setText("Search:");

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        barcodeLabel.setText("Barcode:");

        barcodeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barcodeFieldActionPerformed(evt);
            }
        });

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Barcode", "Name", "Price", "Stock"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productTable.setRowHeight(24);
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productTableMouseClicked(evt);
            }
        });
        productScroll.setViewportView(productTable);

        addButton.setText("Add to cart");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout productSideLayout = new javax.swing.GroupLayout(productSide);
        productSide.setLayout(productSideLayout);
        productSideLayout.setHorizontalGroup(
            productSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productSideLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(productSideLayout.createSequentialGroup()
                        .addGroup(productSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchLabel)
                            .addComponent(barcodeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(productSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchField)
                            .addComponent(barcodeField)))
                    .addComponent(productScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        productSideLayout.setVerticalGroup(
            productSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productSideLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(productSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(barcodeLabel)
                    .addComponent(barcodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(productScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addButton)
                .addContainerGap())
        );

        splitPane.setLeftComponent(productSide);

        cartLabel.setText("Cart");

        cartTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Price", "Qty", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        cartTable.setRowHeight(24);
        cartScroll.setViewportView(cartTable);

        plusButton.setText("+");
        plusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusButtonActionPerformed(evt);
            }
        });

        minusButton.setText("−");
        minusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minusButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        totalLabel.setFont(totalLabel.getFont().deriveFont(totalLabel.getFont().getStyle() | java.awt.Font.BOLD, 22));
        totalLabel.setText("Total: ₱0.00");

        cashLabel.setText("Cash given:");

        changeLabel.setFont(changeLabel.getFont().deriveFont((float)16));
        changeLabel.setText("Change: ₱0.00");

        checkoutButton.setFont(checkoutButton.getFont().deriveFont(checkoutButton.getFont().getStyle() | java.awt.Font.BOLD, 16));
        checkoutButton.setText("CHECKOUT");
        checkoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkoutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cartSideLayout = new javax.swing.GroupLayout(cartSide);
        cartSide.setLayout(cartSideLayout);
        cartSideLayout.setHorizontalGroup(
            cartSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartSideLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cartSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cartLabel)
                    .addComponent(cartScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addGroup(cartSideLayout.createSequentialGroup()
                        .addComponent(plusButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minusButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearButton))
                    .addComponent(totalLabel)
                    .addGroup(cartSideLayout.createSequentialGroup()
                        .addComponent(cashLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cashField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(changeLabel)
                    .addComponent(checkoutButton))
                .addContainerGap())
        );
        cartSideLayout.setVerticalGroup(
            cartSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cartSideLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cartLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cartScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cartSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(plusButton)
                    .addComponent(minusButton)
                    .addComponent(removeButton)
                    .addComponent(clearButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cartSideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cashLabel)
                    .addComponent(cashField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(changeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkoutButton)
                .addContainerGap())
        );

        splitPane.setRightComponent(cartSide);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1060, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void plusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusButtonActionPerformed
        controller.changeQuantity(cartTable.getSelectedRow(), 1);
    }//GEN-LAST:event_plusButtonActionPerformed

    private void minusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusButtonActionPerformed
        controller.changeQuantity(cartTable.getSelectedRow(), -1);
    }//GEN-LAST:event_minusButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        controller.removeItem(cartTable.getSelectedRow());
    }//GEN-LAST:event_removeButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        controller.clearCart();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void checkoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkoutButtonActionPerformed
        controller.checkout(cashField.getText());
    }//GEN-LAST:event_checkoutButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        controller.addSelected(productTable.getSelectedRow());
    }//GEN-LAST:event_addButtonActionPerformed

    private void productTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productTableMouseClicked
        if (evt.getClickCount() == 2) controller.addSelected(productTable.getSelectedRow());
    }//GEN-LAST:event_productTableMouseClicked

    private void barcodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barcodeFieldActionPerformed
        controller.addByBarcode(barcodeField.getText());
    }//GEN-LAST:event_barcodeFieldActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField barcodeField;
    private javax.swing.JLabel barcodeLabel;
    private javax.swing.JLabel cartLabel;
    private javax.swing.JScrollPane cartScroll;
    private javax.swing.JPanel cartSide;
    private javax.swing.JTable cartTable;
    private javax.swing.JTextField cashField;
    private javax.swing.JLabel cashLabel;
    private javax.swing.JLabel changeLabel;
    private javax.swing.JButton checkoutButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton minusButton;
    private javax.swing.JButton plusButton;
    private javax.swing.JScrollPane productScroll;
    private javax.swing.JPanel productSide;
    private javax.swing.JTable productTable;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JLabel totalLabel;
    // End of variables declaration//GEN-END:variables
}
