import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Date;
import java.util.List;

public class CustomerLedgerFrame extends JFrame {
    private JTable customerTable, ledgerTable;
    private DefaultTableModel customerTableModel, ledgerTableModel;
    private JTextField searchField;
    private JPanel mainPanel, detailPanel;
    private JLabel customerNameLabel, phoneLabel, amountPaidLabel, amountRemainingLabel, totalAmountLabel;
    private int selectedCustomerRow = -1;
    private List<Product> inventoryProducts;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/zaibautos";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Replace with your MySQL password

    private static class Product {
        int id;
        String name;
        String brand;
        String type;
        String uom;
        int stock;
        double price;

        Product(int id, String name, String brand, String type, String uom, int stock, double price) {
            this.id = id;
            this.name = name;
            this.brand = brand;
            this.type = type;
            this.uom = uom;
            this.stock = stock;
            this.price = price;
        }

        @Override
        public String toString() {
            return name + " (" + brand + ")";
        }
    }

    public CustomerLedgerFrame() {
        setTitle("Zaib Autos - Customer Ledger & Credit Management");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize products from a database
        inventoryProducts = new ArrayList<>();
        initializeProducts();

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Customer Ledger & Credit Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setToolTipText("Search by name or customer ID...");
        centerPanel.add(searchField, BorderLayout.NORTH);

        // Custom DefaultTableModel to prevent direct editing
        customerTableModel = new DefaultTableModel(new String[]{"Customer ID", "Name", "Phone", "Amount Paid", "Amount Remaining", "Total Sale"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing
            }
        };
        customerTable = new JTable(customerTableModel);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 16));
        customerTable.setRowHeight(30);

        JTableHeader customerTableHeader = customerTable.getTableHeader();
        customerTableHeader.setFont(new Font("Arial", Font.BOLD, 16));
        customerTable.setGridColor(new Color(200, 200, 200));
        customerTable.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton("Add Customer");
        JButton updateButton = createStyledButton("Update Credit");
        JButton deleteButton = createStyledButton("Delete Customer");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    resetCustomerTableOrder();
                } else {
                    sortCustomerToTop(text);
                }
            }
        });

        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());

        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && customerTable.getSelectedRow() != -1) {
                    selectedCustomerRow = customerTable.convertRowIndexToModel(customerTable.getSelectedRow());
                    showCustomerDetailPanel();
                }
            }
        });

        loadCustomers();
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 150, 200));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    private void initializeProducts() {
        inventoryProducts.clear(); // Clear the list to prevent duplicates
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT product_id, item_name, brand, type, uom, stock, 0.0 AS price FROM products")) {
            while (rs.next()) {
                inventoryProducts.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("item_name"),
                        rs.getString("brand"),
                        rs.getString("type"),
                        rs.getString("uom"),
                        rs.getInt("stock"),
                        rs.getDouble("price")
                ));
            }
            if (inventoryProducts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No products found in database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCustomers() {
        customerTableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT customer_id, name, phone, amount_paid, balance, " +
                     "(SELECT SUM(total) FROM customer_ledgers WHERE customer_id = customers.customer_id) AS total_sale " +
                     "FROM customers")) {
            while (rs.next()) {
                customerTableModel.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("balance"),
                        rs.getDouble("total_sale") != 0.0 ? rs.getDouble("total_sale") : 0.0
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sortCustomerToTop(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            resetCustomerTableOrder();
            return;
        }

        List<Vector<Object>> matched = new ArrayList<>();
        List<Vector<Object>> unmatched = new ArrayList<>();

        Vector<?> dataVector = customerTableModel.getDataVector();
        for (int i = 0; i < dataVector.size(); i++) {
            @SuppressWarnings("unchecked")
            Vector<Object> row = (Vector<Object>) dataVector.elementAt(i);
            String name = row.get(1).toString().toLowerCase();
            String id = row.get(0).toString().toLowerCase();
            if (name.contains(searchText.toLowerCase()) || id.contains(searchText.toLowerCase())) {
                matched.add(row);
            } else {
                unmatched.add(row);
            }
        }

        customerTableModel.setRowCount(0);
        int no = 1;
        for (Vector<Object> row : matched) {
            row.set(0, no++);
            customerTableModel.addRow(row);
        }
        for (Vector<Object> row : unmatched) {
            row.set(0, no++);
            customerTableModel.addRow(row);
        }
    }

    private void resetCustomerTableOrder() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT customer_id, name, phone, amount_paid, balance, " +
                     "(SELECT SUM(total) FROM customer_ledgers WHERE customer_id = customers.customer_id) AS total_sale " +
                     "FROM customers ORDER BY customer_id")) {
            customerTableModel.setRowCount(0);
            while (rs.next()) {
                customerTableModel.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("balance"),
                        rs.getDouble("total_sale") != 0.0 ? rs.getDouble("total_sale") : 0.0
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error resetting customer table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateTotalSale(int customerId) {
        double totalSales = 0.0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT SUM(total) AS total_sale FROM customer_ledgers WHERE customer_id = ?")) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalSales = rs.getDouble("total_sale") != 0.0 ? rs.getDouble("total_sale") : 0.0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error calculating total sale: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return totalSales;
    }

    private void updateTotalSale(int row, int customerId) {
        double totalSales = calculateTotalSale(customerId);
        customerTableModel.setValueAt(totalSales, row, 5);
    }

    private void addCustomer() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField amountPaidField = new JTextField("0.0");
        JTextField amountRemainingField = new JTextField("0.0");
        JTextField creditLimitField = new JTextField("0.0");

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Amount Paid:")); panel.add(amountPaidField);
        panel.add(new JLabel("Balance:")); panel.add(amountRemainingField);
        panel.add(new JLabel("Credit Limit:")); panel.add(creditLimitField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                double amountPaid = Double.parseDouble(amountPaidField.getText().trim());
                double balance = Double.parseDouble(amountRemainingField.getText().trim());
                double creditLimit = Double.parseDouble(creditLimitField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (amountPaid < 0 || balance < 0 || creditLimit < 0) {
                    JOptionPane.showMessageDialog(this, "Amounts cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(
                             "INSERT INTO customers (name, phone, amount_paid, balance, credit_limit) VALUES (?, ?, ?, ?, ?)",
                             Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, phone.isEmpty() ? null : phone);
                    pstmt.setDouble(3, amountPaid);
                    pstmt.setDouble(4, balance);
                    pstmt.setDouble(5, creditLimit);
                    pstmt.executeUpdate();

                    ResultSet rs = pstmt.getGeneratedKeys();
                    int customerId = 0;
                    if (rs.next()) {
                        customerId = rs.getInt(1);
                    }

                    customerTableModel.addRow(new Object[]{customerId, name, phone, amountPaid, balance, 0.0});
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for amounts.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateCustomer() {
        int selectedRow = customerTable.convertRowIndexToModel(customerTable.getSelectedRow());
        if (selectedRow != -1) {
            String currentPaid = customerTableModel.getValueAt(selectedRow, 3).toString();
            String currentRemaining = customerTableModel.getValueAt(selectedRow, 4).toString();

            JTextField amountPaidField = new JTextField(currentPaid);

            JPanel panel = new JPanel(new GridLayout(1, 2));
            panel.add(new JLabel("Amount Paid:"));
            panel.add(amountPaidField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Credit", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double amountPaid = Double.parseDouble(amountPaidField.getText().trim());
                    if (amountPaid < 0) {
                        JOptionPane.showMessageDialog(this, "Amount paid cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
                    double totalSales = calculateTotalSale(customerId);
                    double amountRemaining = totalSales - amountPaid;
                    if (amountRemaining < 0) {
                        JOptionPane.showMessageDialog(this, "Amount paid exceeds total sales. Remaining amount cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                         PreparedStatement pstmt = conn.prepareStatement(
                                 "UPDATE customers SET amount_paid = ?, balance = ? WHERE customer_id = ?")) {
                        pstmt.setDouble(1, amountPaid);
                        pstmt.setDouble(2, amountRemaining);
                        pstmt.setInt(3, customerId);
                        pstmt.executeUpdate();

                        customerTableModel.setValueAt(amountPaid, selectedRow, 3);
                        customerTableModel.setValueAt(amountRemaining, selectedRow, 4);
                        updateTotalSale(selectedRow, customerId);

                        if (detailPanel != null && selectedCustomerRow == selectedRow) {
                            amountPaidLabel.setText("Amount Paid: " + amountPaid);
                            amountRemainingLabel.setText("Amount Remaining: " + amountRemaining);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for amount paid.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.convertRowIndexToModel(customerTable.getSelectedRow());
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    conn.setAutoCommit(false);
                    try {
                        PreparedStatement pstmtLedger = conn.prepareStatement("DELETE FROM customer_ledgers WHERE customer_id = ?");
                        pstmtLedger.setInt(1, customerId);
                        pstmtLedger.executeUpdate();

                        PreparedStatement pstmtCustomer = conn.prepareStatement("DELETE FROM customers WHERE customer_id = ?");
                        pstmtCustomer.setInt(1, customerId);
                        pstmtCustomer.executeUpdate();

                        conn.commit();
                        customerTableModel.removeRow(selectedRow);
                    } catch (SQLException e) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Error deleting customer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCustomerDetailPanel() {
        if (detailPanel != null) {
            remove(detailPanel);
        }

        mainPanel.setVisible(false);

        detailPanel = new JPanel(new BorderLayout(10, 10));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailPanel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(5, 1));
        infoPanel.setBackground(Color.WHITE);
        customerNameLabel = new JLabel("Customer: " + customerTableModel.getValueAt(selectedCustomerRow, 1));
        customerNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        phoneLabel = new JLabel("Phone: " + customerTableModel.getValueAt(selectedCustomerRow, 2));
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        amountPaidLabel = new JLabel("Amount Paid: " + customerTableModel.getValueAt(selectedCustomerRow, 3));
        amountPaidLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        totalAmountLabel = new JLabel("Total Amount: " + customerTableModel.getValueAt(selectedCustomerRow, 5));
        totalAmountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        amountRemainingLabel = new JLabel("Amount Remaining: " + customerTableModel.getValueAt(selectedCustomerRow, 4));
        amountRemainingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoPanel.add(customerNameLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(amountPaidLabel);
        infoPanel.add(totalAmountLabel);
        infoPanel.add(amountRemainingLabel);
        detailPanel.add(infoPanel, BorderLayout.NORTH);

        ledgerTableModel = new DefaultTableModel(new String[]{"Date", "Item", "Brand", "Type", "UOM", "Qty", "Price", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ledgerTable = new JTable(ledgerTableModel);
        ledgerTable.setFont(new Font("Arial", Font.PLAIN, 16));
        ledgerTable.setRowHeight(30);

        JTableHeader ledgerTableHeader = ledgerTable.getTableHeader();
        ledgerTableHeader.setFont(new Font("Arial", Font.BOLD, 16));
        ledgerTable.setGridColor(new Color(200, 200, 200));
        ledgerTable.setShowGrid(true);
        JScrollPane ledgerScrollPane = new JScrollPane(ledgerTable);
        detailPanel.add(ledgerScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        JButton addPurchaseBtn = createStyledButton("Add Purchase");
        JButton deletePurchaseBtn = createStyledButton("Delete Purchase");
        JButton generateBillBtn = createStyledButton("Generate Bill");
        JButton backBtn = createStyledButton("Back");

        bottomPanel.add(addPurchaseBtn);
        bottomPanel.add(deletePurchaseBtn);
        bottomPanel.add(generateBillBtn);
        bottomPanel.add(backBtn);
        detailPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(detailPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        addPurchaseBtn.addActionListener(e -> addPurchase());
        deletePurchaseBtn.addActionListener(e -> deletePurchase());
        generateBillBtn.addActionListener(e -> generateBill());
        backBtn.addActionListener(e -> {
            remove(detailPanel);
            detailPanel = null;
            mainPanel.setVisible(true);
            revalidate();
            repaint();
        });

        int customerId = (int) customerTableModel.getValueAt(selectedCustomerRow, 0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT cl.date, cl.item_name, p.brand, cl.type, cl.uom, cl.qty, cl.price, cl.total " +
                             "FROM customer_ledgers cl JOIN products p ON cl.product_id = p.product_id " +
                             "WHERE cl.customer_id = ?")) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ledgerTableModel.addRow(new Object[]{
                        new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("date")),
                        rs.getString("item_name"),
                        rs.getString("brand"),
                        rs.getString("type"),
                        rs.getString("uom"),
                        rs.getInt("qty"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading ledger entries: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPurchase() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add Purchase"));
        panel.setPreferredSize(new Dimension(250, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Product selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Product:"), gbc);

        JComboBox<Product> productCombo = new JComboBox<>();
        for (Product product : inventoryProducts) {
            productCombo.addItem(product);
        }
        productCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productCombo.setToolTipText("Select a product");
        gbc.gridx = 1;
        panel.add(productCombo, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Quantity:"), gbc);

        JTextField quantityField = new JTextField();
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityField.setToolTipText("Enter quantity (positive number)");
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Price (PKR):"), gbc);

        JTextField priceField = new JTextField("0");
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceField.setToolTipText("Enter price per unit (positive number)");
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Date:"), gbc);

        JTextField dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setToolTipText("Enter date (dd-MM-yyyy), default is today");
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        // Stock label
        JLabel stockLabel = new JLabel("Stock: " + (productCombo.getSelectedItem() != null ? ((Product) productCombo.getSelectedItem()).stock : 0));
        productCombo.addActionListener(e -> {
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            stockLabel.setText("Stock: " + (selectedProduct != null ? selectedProduct.stock : 0));
        });
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(stockLabel, gbc);

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(this, "Add Purchase");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        Integer result = (Integer) optionPane.getValue();
        if (result != null && result == JOptionPane.OK_OPTION) {
            try {
                Product selectedProduct = (Product) productCombo.getSelectedItem();
                if (selectedProduct == null) {
                    JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int qty = Integer.parseInt(quantityField.getText().trim());
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (qty > selectedProduct.stock) {
                    JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + selectedProduct.stock, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String dateStr = dateField.getText().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                sdf.setLenient(false);
                Date parsedDate;
                try {
                    parsedDate = sdf.parse(dateStr);
                    if (parsedDate.after(new Date())) {
                        JOptionPane.showMessageDialog(this, "Date cannot be in the future.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy (e.g., 18-07-2025).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(parsedDate);

                double total = qty * price;
                int customerId = (int) customerTableModel.getValueAt(selectedCustomerRow, 0);
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    conn.setAutoCommit(false);
                    try {
                        PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO customer_ledgers (customer_id, product_id, date, item_name, type, uom, qty, price, total) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        pstmt.setInt(1, customerId);
                        pstmt.setInt(2, selectedProduct.id);
                        pstmt.setString(3, sqlDate);
                        pstmt.setString(4, selectedProduct.name);
                        pstmt.setString(5, selectedProduct.type);
                        pstmt.setString(6, selectedProduct.uom);
                        pstmt.setInt(7, qty);
                        pstmt.setDouble(8, price);
                        pstmt.setDouble(9, total);
                        pstmt.executeUpdate();

                        conn.commit();

                        ledgerTableModel.addRow(new Object[]{
                                dateStr,
                                selectedProduct.name,
                                selectedProduct.brand,
                                selectedProduct.type,
                                selectedProduct.uom,
                                qty,
                                price,
                                total
                        });

                        double currentRemaining = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 4).toString());
                        double updatedRemaining = currentRemaining + total;
                        customerTableModel.setValueAt(updatedRemaining, selectedCustomerRow, 4);
                        amountRemainingLabel.setText("Amount Remaining: " + updatedRemaining);

                        updateTotalSale(selectedCustomerRow, customerId);
                        initializeProducts(); // Refresh product stock
                    } catch (SQLException e) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Error adding purchase: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity or price. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePurchase() {
        int selectedRow = ledgerTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedRow = ledgerTable.convertRowIndexToModel(selectedRow);
            double totalToRemove = (Double) ledgerTableModel.getValueAt(selectedRow, 7);
            String date = (String) ledgerTableModel.getValueAt(selectedRow, 0);
            String item = (String) ledgerTableModel.getValueAt(selectedRow, 1);
            int customerId = (int) customerTableModel.getValueAt(selectedCustomerRow, 0);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                conn.setAutoCommit(false);
                try {
                    PreparedStatement pstmt = conn.prepareStatement(
                            "DELETE FROM customer_ledgers WHERE customer_id = ? AND date = ? AND item_name = ? AND total = ?");
                    pstmt.setInt(1, customerId);
                    pstmt.setString(2, new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd-MM-yyyy").parse(date)));
                    pstmt.setString(3, item);
                    pstmt.setDouble(4, totalToRemove);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        conn.commit();
                        ledgerTableModel.removeRow(selectedRow);
                        double currentRemaining = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 4).toString());
                        double updatedRemaining = currentRemaining - totalToRemove;
                        customerTableModel.setValueAt(updatedRemaining, selectedCustomerRow, 4);
                        amountRemainingLabel.setText("Amount Remaining: " + updatedRemaining);
                        updateTotalSale(selectedCustomerRow, customerId);
                        initializeProducts(); // Refresh product stock
                    } else {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Purchase not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException | ParseException e) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Error deleting purchase: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a purchase to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateBill() {
        double totalSales = 0;
        for (int i = 0; i < ledgerTableModel.getRowCount(); i++) {
            Object val = ledgerTableModel.getValueAt(i, 7);
            if (val != null) {
                totalSales += Double.parseDouble(val.toString());
            }
        }

        double paid = 0.0; // Default to 0 as no prompt for amount paid
        double currentRemaining = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 4).toString());
        double remaining = currentRemaining - paid;

        int customerId = (int) customerTableModel.getValueAt(selectedCustomerRow, 0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE customers SET amount_paid = amount_paid + ?, balance = balance - ? WHERE customer_id = ?")) {
            pstmt.setDouble(1, paid);
            pstmt.setDouble(2, paid);
            pstmt.setInt(3, customerId);
            pstmt.executeUpdate();

            double currentPaid = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 3).toString());
            customerTableModel.setValueAt(currentPaid + paid, selectedCustomerRow, 3);
            customerTableModel.setValueAt(remaining, selectedCustomerRow, 4);
            amountRemainingLabel.setText("Amount Remaining: " + remaining);
            amountPaidLabel.setText("Amount Paid: " + (currentPaid + paid));

            String customerName = customerTableModel.getValueAt(selectedCustomerRow, 1).toString();
            String phone = customerTableModel.getValueAt(selectedCustomerRow, 2).toString();
            String voucherNo = "INV-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String dateStr = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());

            JDialog billDialog = new JDialog(this, "Generated Bill", true);
            billDialog.setSize(650, 900);
            billDialog.setLocationRelativeTo(this);
            BillPanel billPanel = new BillPanel(customerName, phone, voucherNo, dateStr, ledgerTableModel, totalSales, paid, remaining);
            billDialog.add(billPanel);
            billDialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerLedgerFrame::new);
    }
}