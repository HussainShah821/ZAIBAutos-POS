import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class SalesEntryFrame extends JFrame {
    private DefaultTableModel saleTableModel;
    private JTable saleTable;
    private JComboBox<String> productCombo;
    private JTextField quantityField, priceField, dateField;
    private JLabel stockLabel;
    private Border defaultBorder, errorBorder;
    private double totalAmount = 0;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/zaibautos";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Replace with your MySQL password
    private Map<String, Product> productMap = new HashMap<>();

    private static class Product {
        int productId;
        String itemName, brand, type, uom;
        int stock;

        Product(int productId, String itemName, String brand, String type, String uom, int stock) {
            this.productId = productId;
            this.itemName = itemName;
            this.brand = brand;
            this.type = type;
            this.uom = uom;
            this.stock = stock;
        }
    }

    public SalesEntryFrame() {
        setTitle("Zaib Autos - Sales Entry");
        setSize(1200, 750); // Increased by 10% from 800x600
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Borders for validation
        defaultBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        errorBorder = BorderFactory.createLineBorder(Color.RED, 1);

        // Initialize product details from database
        initializeProducts();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Daily Sales Entry");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.WEST);

        saleTableModel = new DefaultTableModel(new String[]{"S.No", "Product", "Company", "Type", "UOM", "Quantity", "Price", "Total", "Date"}, 0);
        saleTable = new JTable(saleTableModel);
        saleTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        saleTable.setRowHeight(25);
        saleTable.setGridColor(new Color(200, 200, 200));
        saleTable.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(saleTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Sale Items"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new RoundBorder(10)); // Rounded corners with 10px radius
        button.setOpaque(true); // Needed for background color
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 105, 217));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 123, 255));
            }
        });
        return button;
    }

    // Custom rounded border class
    private static class RoundBorder implements Border {
        private int radius;

        RoundBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(5, 15, 5, 15); // Match original padding
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(96, 96, 96)); // Grey border
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center-aligned buttons
        panel.setBackground(Color.WHITE);
        JButton addItemButton = createStyledButton("Add Item");
        JButton removeItemButton = createStyledButton("Remove Item");
        JButton returnItemButton = createStyledButton("Return Item");
        JButton clearButton = createStyledButton("Clear");
        JButton saveButton = createStyledButton("Save Sale");
        JButton viewDailySalesButton = createStyledButton("View Daily Sales");

        addItemButton.addActionListener(e -> addSaleItem());
        removeItemButton.addActionListener(e -> removeSaleItem());
        returnItemButton.addActionListener(e -> returnItem());
        clearButton.addActionListener(e -> clearForm());
        saveButton.addActionListener(e -> saveSale());
        viewDailySalesButton.addActionListener(e -> viewDailySales());

        panel.add(addItemButton);
        panel.add(removeItemButton);
        panel.add(returnItemButton);
        panel.add(clearButton);
        panel.add(saveButton);
        panel.add(viewDailySalesButton);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add Sale Item"));
        panel.setPreferredSize(new Dimension(300, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Product:"), gbc);

        String[] productDisplayNames = productMap.keySet().toArray(new String[0]);
        productCombo = new JComboBox<>(productDisplayNames);
        productCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productCombo.setToolTipText("Select a product");
        gbc.gridx = 1;
        panel.add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Quantity:"), gbc);

        quantityField = new JTextField();
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityField.setToolTipText("Enter quantity (positive number)");
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Price (PKR):"), gbc);

        priceField = new JTextField("0");
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceField.setToolTipText("Enter price per unit (positive number)");
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Date:"), gbc);

        dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setToolTipText("Enter date (dd-MM-yyyy), default is today");
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        stockLabel = new JLabel("Stock: 0");
        productCombo.addActionListener(e -> {
            String selectedProduct = (String) productCombo.getSelectedItem();
            Product product = productMap.get(selectedProduct);
            stockLabel.setText("Stock: " + (product != null ? product.stock : 0));
        });
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(stockLabel, gbc);

        return panel;
    }

    private void initializeProducts() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT product_id, item_name, brand, type, uom, stock FROM products")) {
            productMap.clear(); // Clear existing map to avoid duplicates
            while (rs.next()) {
                String displayName = rs.getString("item_name") + " (" + rs.getString("brand") + ")";
                productMap.put(displayName, new Product(
                        rs.getInt("product_id"),
                        rs.getString("item_name"),
                        rs.getString("brand"),
                        rs.getString("type"),
                        rs.getString("uom"),
                        rs.getInt("stock")
                ));
            }
            if (productMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No products found in database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSaleItem() {
        String selectedProductDisplay = (String) productCombo.getSelectedItem();
        if (selectedProductDisplay == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = productMap.get(selectedProductDisplay);
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            String date = dateField.getText().trim();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            sdf.parse(date);

            if (quantity <= 0) {
                quantityField.setBorder(errorBorder);
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (price < 0) {
                priceField.setBorder(errorBorder);
                JOptionPane.showMessageDialog(this, "Price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (quantity > product.stock) {
                quantityField.setBorder(errorBorder);
                JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + product.stock, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update local stock
            product.stock -= quantity;
            double total = quantity * price;
            totalAmount += total;
            saleTableModel.addRow(new Object[]{
                    saleTableModel.getRowCount() + 1,
                    product.itemName,
                    product.brand,
                    product.type,
                    product.uom,
                    quantity,
                    price,
                    total,
                    date
            });

            quantityField.setBorder(defaultBorder);
            priceField.setBorder(defaultBorder);
            dateField.setBorder(defaultBorder);
            quantityField.setText("");
            priceField.setText("0");
            dateField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            stockLabel.setText("Stock: " + product.stock);
        } catch (NumberFormatException ex) {
            quantityField.setBorder(errorBorder);
            priceField.setBorder(errorBorder);
            JOptionPane.showMessageDialog(this, "Invalid quantity or price. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            dateField.setBorder(errorBorder);
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy (e.g., 07-06-2025).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSaleItem() {
        int selectedRow = saleTable.getSelectedRow();
        if (selectedRow != -1) {
            double itemTotal = (double) saleTableModel.getValueAt(selectedRow, 7);
            int quantity = (int) saleTableModel.getValueAt(selectedRow, 5);
            String productName = (String) saleTableModel.getValueAt(selectedRow, 1);
            String brand = (String) saleTableModel.getValueAt(selectedRow, 2);
            String displayName = productName + " (" + brand + ")";

            totalAmount -= itemTotal;
            saleTableModel.removeRow(selectedRow);
            for (int i = 0; i < saleTableModel.getRowCount(); i++) {
                saleTableModel.setValueAt(i + 1, i, 0);
            }

            Product product = productMap.get(displayName);
            if (product != null) {
                product.stock += quantity; // Restore local stock
                stockLabel.setText("Stock: " + product.stock);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnItem() {
        JDialog dialog = new JDialog(this, "Return Item", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.WHITE);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Item Name:"), gbc);
        JComboBox<String> itemCombo = new JComboBox<>(productCombo.getModel());
        itemCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        dialog.add(itemCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Quantity:"), gbc);
        JTextField quantityField = new JTextField();
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityField.setToolTipText("Enter quantity to return (positive number)");
        gbc.gridx = 1;
        dialog.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Price per Unit (PKR):"), gbc);
        JTextField priceField = new JTextField("0");
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceField.setToolTipText("Enter price per unit (positive number)");
        gbc.gridx = 1;
        dialog.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Date:"), gbc);
        JTextField dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setToolTipText("Enter return date (dd-MM-yyyy), default is today");
        gbc.gridx = 1;
        dialog.add(dateField, gbc);

        JButton confirmButton = createStyledButton("Confirm");
        JButton cancelButton = createStyledButton("Cancel");

        confirmButton.addActionListener(e -> {
            String itemName = (String) itemCombo.getSelectedItem();
            String quantityText = quantityField.getText().trim();
            String priceText = priceField.getText().trim();
            String dateText = dateField.getText().trim();
            if (itemName == null || quantityText.isEmpty() || priceText.isEmpty() || dateText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Product product = productMap.get(itemName);
            if (product == null) {
                JOptionPane.showMessageDialog(dialog, "Invalid item selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int quantity = Integer.parseInt(quantityText);
                double price = Double.parseDouble(priceText);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                sdf.setLenient(false);
                Date parsedDate = sdf.parse(dateText);
                String sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(parsedDate);

                if (quantity <= 0) {
                    quantityField.setBorder(errorBorder);
                    JOptionPane.showMessageDialog(dialog, "Quantity must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (price < 0) {
                    priceField.setBorder(errorBorder);
                    JOptionPane.showMessageDialog(dialog, "Price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(dialog,
                        String.format("Return %d units of %s (%s) at PKR %.2f per unit on %s?",
                                quantity, product.itemName, product.brand, price, dateText),
                        "Confirm Return", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    conn.setAutoCommit(false);
                    try {
                        // Lock the product row to prevent concurrent modifications
                        PreparedStatement checkProduct = conn.prepareStatement(
                                "SELECT stock FROM products WHERE product_id = ? FOR UPDATE");
                        checkProduct.setInt(1, product.productId);
                        ResultSet rs = checkProduct.executeQuery();
                        int currentStock;
                        if (rs.next()) {
                            currentStock = rs.getInt("stock");
                            System.out.println("DEBUG: Product ID " + product.productId + " (" + product.itemName + ") current stock: " + currentStock);
                        } else {
                            throw new SQLException("Product not found in database: " + product.itemName);
                        }

                        // Insert negative sales record
                        PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO sales (product_id, product_name, brand, type, uom, quantity, price, total, date) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        pstmt.setInt(1, product.productId);
                        pstmt.setString(2, product.itemName);
                        pstmt.setString(3, product.brand);
                        pstmt.setString(4, product.type);
                        pstmt.setString(5, product.uom);
                        pstmt.setInt(6, quantity);
                        pstmt.setBigDecimal(7, BigDecimal.valueOf(price));
                        pstmt.setBigDecimal(8, BigDecimal.valueOf(quantity * price * -1));
                        pstmt.setString(9, sqlDate);
                        int salesRows = pstmt.executeUpdate();
                        System.out.println("DEBUG: Sales record inserted, rows affected: " + salesRows);

                        // Update stock in products table
                        PreparedStatement updateStock = conn.prepareStatement(
                                "UPDATE products SET stock = stock + ? WHERE product_id = ?");
                        updateStock.setInt(1, quantity);
                        updateStock.setInt(2, product.productId);
                        int rowsAffected = updateStock.executeUpdate();
                        System.out.println("DEBUG: Stock update attempted, rows affected: " + rowsAffected);
                        if (rowsAffected != 1) {
                            throw new SQLException("Failed to update stock for product: " + product.itemName + " (Expected 1 row affected, got " + rowsAffected + ")");
                        }

                        // Verify stock after update but before commit
                        rs = checkProduct.executeQuery();
                        if (rs.next()) {
                            int preCommitStock = rs.getInt("stock");
                            System.out.println("DEBUG: Pre-commit stock for Product ID " + product.productId + ": " + preCommitStock);
                            if (preCommitStock != currentStock + quantity) {
                                System.err.println("DEBUG WARNING: Pre-commit stock inconsistency. Expected " + (currentStock + quantity) + ", got " + preCommitStock);
                            }
                        }

                        conn.commit();
                        System.out.println("DEBUG: Transaction committed successfully");

                        // Verify stock after commit
                        PreparedStatement postCommitCheck = conn.prepareStatement(
                                "SELECT stock FROM products WHERE product_id = ?");
                        postCommitCheck.setInt(1, product.productId);
                        rs = postCommitCheck.executeQuery();
                        if (rs.next()) {
                            int finalStock = rs.getInt("stock");
                            System.out.println("DEBUG: Post-commit stock for Product ID " + product.productId + ": " + finalStock);
                            if (finalStock != currentStock + quantity) {
                                System.err.println("DEBUG ERROR: Post-commit stock inconsistency. Expected " + (currentStock + quantity) + ", got " + finalStock);
                                JOptionPane.showMessageDialog(dialog, "Stock update failed: Inconsistent stock value after commit.", "Error", JOptionPane.ERROR_MESSAGE);
                                conn.rollback();
                                return;
                            }
                        } else {
                            throw new SQLException("Product not found after commit: " + product.itemName);
                        }

                        JOptionPane.showMessageDialog(dialog,
                                String.format("Returned %d units of %s successfully! New stock: %d",
                                        quantity, product.itemName, currentStock + quantity),
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh productMap and update stockLabel
                        initializeProducts();
                        String selectedProduct = (String) productCombo.getSelectedItem();
                        Product updatedProduct = productMap.get(selectedProduct);
                        stockLabel.setText("Stock: " + (updatedProduct != null ? updatedProduct.stock : 0));
                        System.out.println("DEBUG: stockLabel updated to: " + (updatedProduct != null ? updatedProduct.stock : 0));
                    } catch (SQLException ex) {
                        conn.rollback();
                        System.err.println("DEBUG: Transaction rolled back due to error: " + ex.getMessage());
                        JOptionPane.showMessageDialog(dialog, "Error processing return: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    System.err.println("DEBUG: Database connection error: " + ex.getMessage());
                    JOptionPane.showMessageDialog(dialog, "Error connecting to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                quantityField.setBorder(errorBorder);
                priceField.setBorder(errorBorder);
                JOptionPane.showMessageDialog(dialog, "Invalid quantity or price. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException ex) {
                dateField.setBorder(errorBorder);
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use dd-MM-yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private void clearForm() {
        saleTableModel.setRowCount(0);
        totalAmount = 0;
        quantityField.setText("");
        priceField.setText("0");
        dateField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        quantityField.setBorder(defaultBorder);
        priceField.setBorder(defaultBorder);
        dateField.setBorder(defaultBorder);
        initializeProducts(); // Reset local stock
        String selectedProduct = (String) productCombo.getSelectedItem();
        Product product = productMap.get(selectedProduct);
        stockLabel.setText("Stock: " + (product != null ? product.stock : 0));
    }

    private void saveSale() {
        if (saleTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items added to the sale.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate stock availability in database
        Map<Integer, Integer> productQuantities = new HashMap<>();
        for (int i = 0; i < saleTableModel.getRowCount(); i++) {
            String productName = (String) saleTableModel.getValueAt(i, 1);
            String brand = (String) saleTableModel.getValueAt(i, 2);
            String displayName = productName + " (" + brand + ")";
            int quantity = (int) saleTableModel.getValueAt(i, 5);

            Product product = productMap.get(displayName);
            if (product == null) {
                JOptionPane.showMessageDialog(this, "Product not found: " + displayName, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            productQuantities.merge(product.productId, quantity, Integer::sum);
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check stock in database
            for (Map.Entry<Integer, Integer> entry : productQuantities.entrySet()) {
                PreparedStatement checkStock = conn.prepareStatement(
                        "SELECT stock FROM products WHERE product_id = ?");
                checkStock.setInt(1, entry.getKey());
                ResultSet rs = checkStock.executeQuery();
                if (rs.next()) {
                    int currentStock = rs.getInt("stock");
                    if (entry.getValue() > currentStock) {
                        JOptionPane.showMessageDialog(this,
                                "Insufficient stock for product ID " + entry.getKey() + ". Available: " + currentStock,
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Product ID " + entry.getKey() + " not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < saleTableModel.getRowCount(); i++) {
                    String productName = (String) saleTableModel.getValueAt(i, 1);
                    String brand = (String) saleTableModel.getValueAt(i, 2);
                    String displayName = productName + " (" + brand + ")";
                    int quantity = (int) saleTableModel.getValueAt(i, 5);
                    double price = (double) saleTableModel.getValueAt(i, 6);
                    double total = (double) saleTableModel.getValueAt(i, 7);
                    String dateStr = (String) saleTableModel.getValueAt(i, 8);

                    Product product = productMap.get(displayName);
                    if (product == null) {
                        throw new SQLException("Product not found: " + displayName);
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date parsedDate = sdf.parse(dateStr);
                    String sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(parsedDate);

                    // Insert sale record
                    PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO sales (product_id, product_name, brand, type, uom, quantity, price, total, date) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    pstmt.setInt(1, product.productId);
                    pstmt.setString(2, product.itemName);
                    pstmt.setString(3, product.brand);
                    pstmt.setString(4, product.type);
                    pstmt.setString(5, product.uom);
                    pstmt.setInt(6, quantity);
                    pstmt.setBigDecimal(7, BigDecimal.valueOf(price));
                    pstmt.setBigDecimal(8, BigDecimal.valueOf(total));
                    pstmt.setString(9, sqlDate);
                    pstmt.executeUpdate();

                    // Update stock in database
                    PreparedStatement updateStock = conn.prepareStatement(
                            "UPDATE products SET stock = stock - ? WHERE product_id = ?");
                    updateStock.setInt(1, quantity);
                    updateStock.setInt(2, product.productId);
                    int rowsAffected = updateStock.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Product not found in database: " + product.itemName);
                    }
                }
                conn.commit();
                JOptionPane.showMessageDialog(this, "Sale saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                initializeProducts();
            } catch (SQLException | ParseException ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Error saving sale: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewDailySales() {
        JTextField dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setToolTipText("Enter date (dd-MM-yyyy)");
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Select Date:"));
        panel.add(dateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "View Daily Sales", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedDate = dateField.getText().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            Date parsedDate = sdf.parse(selectedDate);
            String sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(parsedDate);

            DefaultTableModel dailyModel = new DefaultTableModel(new String[]{"Product", "Company", "Type", "UOM", "Quantity", "Price", "Total", "Date"}, 0);
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT product_name, brand, type, uom, quantity, price, total, date FROM sales WHERE date = ?")) {
                pstmt.setString(1, sqlDate);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    dailyModel.addRow(new Object[]{
                            rs.getString("product_name"),
                            rs.getString("brand"),
                            rs.getString("type"),
                            rs.getString("uom"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("price"),
                            rs.getBigDecimal("total"),
                            new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("date"))
                    });
                }
                if (dailyModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No sales recorded for " + selectedDate + ".", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                JTable dailyTable = new JTable(dailyModel);
                dailyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                dailyTable.setRowHeight(25);
                dailyTable.setGridColor(new Color(200, 200, 200));
                dailyTable.setShowGrid(true);
                JScrollPane scrollPane = new JScrollPane(dailyTable);
                JOptionPane.showMessageDialog(this, scrollPane, "Daily Sales - " + selectedDate, JOptionPane.PLAIN_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading daily sales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SalesEntryFrame::new);
    }
}