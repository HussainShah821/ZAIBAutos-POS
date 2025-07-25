import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class InventoryFrame extends JFrame {
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private DatabaseConnection dbConnection;

    public InventoryFrame() {
        // Initialize database connection
        dbConnection = DatabaseConnection.getInstance("root", "root");
        if (dbConnection.getConnection() == null) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database. Please check your connection.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setTitle("Inventory Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Inventory Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Center panel (search + table)
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        centerPanel.add(searchField, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(new String[]{"Item No", "Item Name", "Brand", "Type", "UOM", "Quantity", "Product ID"}, 0);
        productTable = new JTable(productTableModel);
        productTable.removeColumn(productTable.getColumn("Product ID")); // Hide Product ID column
        JScrollPane scrollPane = new JScrollPane(productTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Add Product");
        JButton updateButton = new JButton("Update Product");
        JButton deleteButton = new JButton("Delete Product");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);

        // Load initial product data
        loadProducts();

        // Listeners
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    loadProducts();
                } else {
                    searchProducts(text);
                }
            }
        });

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        setVisible(true);
    }

    private void loadProducts() {
        productTableModel.setRowCount(0);
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT product_id, item_name, brand, type, uom, stock FROM products")) {
            int itemNo = 1;
            while (rs.next()) {
                productTableModel.addRow(new Object[]{
                        itemNo++,
                        rs.getString("item_name"),
                        rs.getString("brand") != null ? rs.getString("brand") : "",
                        rs.getString("type") != null ? rs.getString("type") : "",
                        rs.getString("uom") != null ? rs.getString("uom") : "",
                        rs.getInt("stock"),
                        rs.getInt("product_id")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error loading products: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProducts(String productName) {
        productTableModel.setRowCount(0);
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT product_id, item_name, brand, type, uom, stock FROM products WHERE item_name LIKE ?")) {
            pstmt.setString(1, "%" + productName + "%");
            ResultSet rs = pstmt.executeQuery();
            int itemNo = 1;
            while (rs.next()) {
                productTableModel.addRow(new Object[]{
                        itemNo++,
                        rs.getString("item_name"),
                        rs.getString("brand") != null ? rs.getString("brand") : "",
                        rs.getString("type") != null ? rs.getString("type") : "",
                        rs.getString("uom") != null ? rs.getString("uom") : "",
                        rs.getInt("stock"),
                        rs.getInt("product_id")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
            JOptionPane.showMessageDialog(this, "Error searching products: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProduct() {
        JTextField itemNameField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField uomField = new JTextField();
        JTextField quantityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Item Name:")); panel.add(itemNameField);
        panel.add(new JLabel("Brand:")); panel.add(brandField);
        panel.add(new JLabel("Type:")); panel.add(typeField);
        panel.add(new JLabel("UOM:")); panel.add(uomField);
        panel.add(new JLabel("Quantity:")); panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText().trim();
            String brand = brandField.getText().trim();
            String type = typeField.getText().trim();
            String uom = uomField.getText().trim();
            String qty = quantityField.getText().trim();

            // Validate inputs
            if (itemName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Item Name is required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!qty.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO products (item_name, brand, type, uom, stock) VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setString(1, itemName);
                pstmt.setString(2, brand.isEmpty() ? null : brand);
                pstmt.setString(3, type.isEmpty() ? null : type);
                pstmt.setString(4, uom.isEmpty() ? null : uom);
                pstmt.setInt(5, Integer.parseInt(qty));
                pstmt.executeUpdate();
                loadProducts(); // Refresh table
            } catch (SQLException e) {
                System.err.println("Error adding product: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
                JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            int productId = (int) productTableModel.getValueAt(selectedRow, 6); // Hidden Product ID column
            String currentQuantity = productTableModel.getValueAt(selectedRow, 5).toString();
            String newQuantity = JOptionPane.showInputDialog(this, "Enter new quantity:", currentQuantity);
            if (newQuantity != null && !newQuantity.trim().isEmpty()) {
                if (!newQuantity.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try (Connection conn = dbConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                             "UPDATE products SET stock = ? WHERE product_id = ?")) {
                    pstmt.setInt(1, Integer.parseInt(newQuantity));
                    pstmt.setInt(2, productId);
                    pstmt.executeUpdate();
                    loadProducts(); // Refresh table
                } catch (SQLException e) {
                    System.err.println("Error updating product: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
                    JOptionPane.showMessageDialog(this, "Error updating product: " + e.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int productId = (int) productTableModel.getValueAt(selectedRow, 6); // Hidden Product ID column
        String productName = productTableModel.getValueAt(selectedRow, 1).toString();

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Count related records
                PreparedStatement countStmt = conn.prepareStatement(
                        "SELECT (SELECT COUNT(*) FROM sales WHERE product_id = ?) AS sales_count, " +
                                "(SELECT COUNT(*) FROM customer_ledgers WHERE product_id = ?) AS ledger_count");
                countStmt.setInt(1, productId);
                countStmt.setInt(2, productId);
                ResultSet rs = countStmt.executeQuery();
                int salesCount = 0, ledgerCount = 0;
                if (rs.next()) {
                    salesCount = rs.getInt("sales_count");
                    ledgerCount = rs.getInt("ledger_count");
                }
                System.out.println("DEBUG: Product ID " + productId + " (" + productName + ") has " + salesCount + " sales and " + ledgerCount + " ledger records");

                // Confirm deletion
                String confirmMessage = String.format("Delete product '%s'? This will also delete:\n- %d sales record(s)\n- %d customer ledger record(s)",
                        productName, salesCount, ledgerCount);
                int confirm = JOptionPane.showConfirmDialog(this, confirmMessage, "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    conn.rollback();
                    return;
                }

                // Delete from sales
                PreparedStatement deleteSalesStmt = conn.prepareStatement("DELETE FROM sales WHERE product_id = ?");
                deleteSalesStmt.setInt(1, productId);
                int salesRows = deleteSalesStmt.executeUpdate();
                System.out.println("DEBUG: Deleted " + salesRows + " sales records for Product ID " + productId);

                // Delete from customer_ledgers
                PreparedStatement deleteLedgerStmt = conn.prepareStatement("DELETE FROM customer_ledgers WHERE product_id = ?");
                deleteLedgerStmt.setInt(1, productId);
                int ledgerRows = deleteLedgerStmt.executeUpdate();
                System.out.println("DEBUG: Deleted " + ledgerRows + " customer ledger records for Product ID " + productId);

                // Delete from products
                PreparedStatement deleteProductStmt = conn.prepareStatement("DELETE FROM products WHERE product_id = ?");
                deleteProductStmt.setInt(1, productId);
                int productRows = deleteProductStmt.executeUpdate();
                System.out.println("DEBUG: Deleted " + productRows + " product record for Product ID " + productId);

                if (productRows != 1) {
                    throw new SQLException("Failed to delete product: Expected 1 row affected, got " + productRows);
                }

                conn.commit();
                System.out.println("DEBUG: Transaction committed successfully");
                JOptionPane.showMessageDialog(this, "Product '" + productName + "' and related records deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts(); // Refresh table
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("DEBUG: Transaction rolled back due to error: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
                JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG: Database connection error: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]");
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage() + " [SQLState: " + e.getSQLState() + "]",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InventoryFrame::new);
    }
}