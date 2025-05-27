import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class CustomerLedgerFrame extends JFrame {
    private JTable customerTable, ledgerTable;
    private DefaultTableModel customerTableModel, ledgerTableModel;
    private JTextField searchField;
    private JPanel mainPanel, detailPanel;
    private JLabel customerNameLabel, phoneLabel, amountPaidLabel, amountRemainingLabel;
    private int selectedCustomerRow = -1;
    private ArrayList<Product> inventoryProducts;
    private int customerCount = 0;

    private static class Product {
        int id;
        String name;
        String brand;
        double price;
        String type;
        String uom;

        Product(int id, String name, String brand, double price, String type, String uom) {
            this.id = id;
            this.name = name;
            this.brand = brand;
            this.price = price;
            this.type = type;
            this.uom = uom;
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

        // Initialize dummy inventory data
        inventoryProducts = new ArrayList<>();
        inventoryProducts.add(new Product(1, "Oil Filter", "Bosch", 1500.0, "Filter", "Unit"));
        inventoryProducts.add(new Product(2, "Brake Pads", "Toyota", 3000.0, "Brake", "Set"));
        inventoryProducts.add(new Product(3, "Air Filter", "Honda", 1200.0, "Filter", "Unit"));

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

        // Customize the table header font for customerTable
        JTableHeader customerTableHeader = customerTable.getTableHeader();
        customerTableHeader.setFont(new Font("Arial", Font.BOLD, 16)); // Increased header font size

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
        JButton backButton = createStyledButton("Back");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
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
        backButton.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && customerTable.getSelectedRow() != -1) {
                    selectedCustomerRow = customerTable.convertRowIndexToModel(customerTable.getSelectedRow());
                    showCustomerDetailPanel();
                }
            }
        });

        loadDummyData();
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

    private void sortCustomerToTop(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            resetCustomerTableOrder();
            return;
        }

        List<Vector<Object>> matched = new ArrayList<>();
        List<Vector<Object>> unmatched = new ArrayList<>();

        for (int i = 0; i < customerTableModel.getRowCount(); i++) {
            @SuppressWarnings("unchecked")
            Vector<Object> row = (Vector<Object>) customerTableModel.getDataVector().get(i);
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
        customerCount = customerTableModel.getRowCount();
    }

    private void resetCustomerTableOrder() {
        for (int i = 0; i < customerTableModel.getRowCount(); i++) {
            customerTableModel.setValueAt(i + 1, i, 0);
        }
    }

    private double calculateTotalSale(int customerId) {
        List<List<Object>> ledgerEntries = DataManager.getInstance().getCustomerLedger(customerId);
        double totalSales = 0.0;
        for (List<Object> entry : ledgerEntries) {
            totalSales += (Double) entry.get(7); // Total column
        }
        return totalSales;
    }

    private void updateTotalSale(int row, int customerId) {
        double totalSales = calculateTotalSale(customerId);
        customerTableModel.setValueAt(totalSales, row, 5); // Update Total Sale column
    }

    private void loadDummyData() {
        customerTableModel.setRowCount(0);
        Object[][] dummyCustomers = {
                {++customerCount, "Ali Khan", "500-1234567", 5000.0, 2000.0, 3000.0},
                {++customerCount, "Sara Ahmed", "500-7654321", 3000.0, 500.0, 3000.0},
                {++customerCount, "Bilal Raza", "500-9876543", 7000.0, 0.0, 1200.0}
        };
        for (Object[] customer : dummyCustomers) {
            customerTableModel.addRow(customer);
        }
    }

    private void addCustomer() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField amountPaidField = new JTextField();
        JTextField amountRemainingField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Amount Paid:")); panel.add(amountPaidField);
        panel.add(new JLabel("Amount Remaining:")); panel.add(amountRemainingField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                double amountPaid = Double.parseDouble(amountPaidField.getText());
                double amountRemaining = Double.parseDouble(amountRemainingField.getText());

                if (name.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name and phone are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                /*
                // Database insertion
                String query = "INSERT INTO Customers (name, phone, amount_paid, amount_remaining) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, phone);
                    pstmt.setDouble(3, amountPaid);
                    pstmt.setDouble(4, amountRemaining);
                    pstmt.executeUpdate();
                }
                */

                customerCount++;
                customerTableModel.addRow(new Object[]{customerCount, name, phone, amountPaid, amountRemaining, 0.0});
                resetCustomerTableOrder();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for amounts", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateCustomer() {
        int selectedRow = customerTable.convertRowIndexToModel(customerTable.getSelectedRow());
        if (selectedRow != -1) {
            String currentPaid = customerTableModel.getValueAt(selectedRow, 3).toString();

            JTextField amountPaidField = new JTextField(currentPaid);

            JPanel panel = new JPanel(new GridLayout(1, 2));
            panel.add(new JLabel("Amount Paid:"));
            panel.add(amountPaidField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Credit", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double amountPaid = Double.parseDouble(amountPaidField.getText());
                    if (amountPaid < 0) {
                        JOptionPane.showMessageDialog(this, "Amount paid cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Calculate total sales from ledger entries
                    int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
                    double totalSales = calculateTotalSale(customerId);

                    // Calculate new remaining amount
                    double amountRemaining = totalSales - amountPaid;
                    if (amountRemaining < 0) {
                        JOptionPane.showMessageDialog(this, "Amount paid exceeds total sales. Remaining amount cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    /*
                    // Database update
                    String query = "UPDATE Customers SET amount_paid = ?, amount_remaining = ? WHERE customer_id = ?";
                    try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
                        pstmt.setDouble(1, amountPaid);
                        pstmt.setDouble(2, amountRemaining);
                        pstmt.setInt(3, customerId);
                        pstmt.executeUpdate();
                    }
                    */

                    // Update table model
                    customerTableModel.setValueAt(amountPaid, selectedRow, 3);
                    customerTableModel.setValueAt(amountRemaining, selectedRow, 4);
                    updateTotalSale(selectedRow, customerId);

                    // Update detail panel labels if visible
                    if (detailPanel != null && selectedCustomerRow == selectedRow) {
                        amountPaidLabel.setText("Amount Paid: " + amountPaid);
                        amountRemainingLabel.setText("Amount Remaining: " + amountRemaining);
                    }

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for amount paid", "Error", JOptionPane.ERROR_MESSAGE);
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
                /*
                // Database deletion
                String query = "DELETE FROM Customers WHERE customer_id = ?";
                try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
                    pstmt.setInt(1, (int) customerTableModel.getValueAt(selectedRow, 0));
                    pstmt.executeUpdate();
                }
                */

                customerTableModel.removeRow(selectedRow);
                resetCustomerTableOrder();
                customerCount = customerTableModel.getRowCount();
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

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBackground(Color.WHITE);
        customerNameLabel = new JLabel("Customer: " + customerTableModel.getValueAt(selectedCustomerRow, 1));
        customerNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        phoneLabel = new JLabel("Phone: " + customerTableModel.getValueAt(selectedCustomerRow, 2));
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        amountPaidLabel = new JLabel("Amount Paid: " + customerTableModel.getValueAt(selectedCustomerRow, 3));
        amountPaidLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        amountRemainingLabel = new JLabel("Amount Remaining: " + customerTableModel.getValueAt(selectedCustomerRow, 4));
        amountRemainingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoPanel.add(customerNameLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(amountPaidLabel);
        infoPanel.add(amountRemainingLabel);
        detailPanel.add(infoPanel, BorderLayout.NORTH);

        // Custom DefaultTableModel to prevent direct editing
        ledgerTableModel = new DefaultTableModel(new String[]{"Date", "Item", "Brand", "Type", "UOM", "Qty", "Price", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing
            }
        };
        ledgerTable = new JTable(ledgerTableModel);
        ledgerTable.setFont(new Font("Arial", Font.PLAIN, 16));
        ledgerTable.setRowHeight(30);

        // Customize the table header font for ledgerTable
        JTableHeader ledgerTableHeader = ledgerTable.getTableHeader();
        ledgerTableHeader.setFont(new Font("Arial", Font.BOLD, 16)); // Increased header font size

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

        // Load existing ledger entries for the selected customer
        int customerId = (int) customerTableModel.getValueAt(selectedCustomerRow, 0);
        List<List<Object>> ledgerEntries = DataManager.getInstance().getCustomerLedger(customerId);
        for (List<Object> entry : ledgerEntries) {
            ledgerTableModel.addRow(new Object[]{
                    entry.get(0), // Date
                    entry.get(1), // Item
                    entry.get(2), // Brand
                    entry.get(3), // Type
                    entry.get(4), // UOM
                    entry.get(5), // Qty
                    entry.get(6), // Price
                    entry.get(7)  // Total
            });
        }
    }

    private void addPurchase() {
        JTextField searchField = new JTextField();
        JComboBox<Product> productComboBox = new JComboBox<>();
        JTextField qtyField = new JTextField();

        List<Product> filteredList = new ArrayList<>(inventoryProducts);
        for (Product product : filteredList) {
            productComboBox.addItem(product);
        }

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filterItems() {
                SwingUtilities.invokeLater(() -> {
                    String input = searchField.getText().toLowerCase();
                    productComboBox.removeAllItems();
                    for (Product product : filteredList) {
                        if (product.toString().toLowerCase().contains(input)) {
                            productComboBox.addItem(product);
                        }
                    }
                    productComboBox.showPopup();
                });
            }

            @Override
            public void insertUpdate(DocumentEvent e) { filterItems(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterItems(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterItems(); }
        });

        productComboBox.setEditable(true);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Search Product:"), gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        searchField.setPreferredSize(new Dimension(200, 30));
        panel.add(searchField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(productComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        qtyField.setPreferredSize(new Dimension(200, 30));
        panel.add(qtyField, gbc);

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(this, "Add Purchase");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        Integer result = (Integer) optionPane.getValue();
        if (result != null && result == JOptionPane.OK_OPTION) {
            try {
                Product selectedProduct = (Product) productComboBox.getSelectedItem();
                if (selectedProduct == null) {
                    JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int qty = Integer.parseInt(qtyField.getText().trim());
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price = selectedProduct.price;
                double total = qty * price;
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                ledgerTableModel.addRow(new Object[]{date, selectedProduct.name, selectedProduct.brand,
                        selectedProduct.type, selectedProduct.uom, qty, price, total});

                double currentRemaining = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 4).toString());
                double updatedRemaining = currentRemaining + total;
                customerTableModel.setValueAt(updatedRemaining, selectedCustomerRow, 4);
                amountRemainingLabel.setText("Amount Remaining: " + updatedRemaining);

                // Update total sale for this customer
                int customerId = (int) customerTableModel.getValueAt(selectedCustomerRow, 0);
                DataManager.getInstance().addLedgerEntry(customerId, date, selectedProduct.name, selectedProduct.brand,
                        selectedProduct.type, selectedProduct.uom, qty, price, total);
                updateTotalSale(selectedCustomerRow, customerId);

                /*
                // Database insertion
                String query = "INSERT INTO CustomerLedgers (customer_id, date, item, brand, type, uom, qty, price, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
                    pstmt.setInt(1, customerId);
                    pstmt.setString(2, date);
                    pstmt.setString(3, selectedProduct.name);
                    pstmt.setString(4, selectedProduct.brand);
                    pstmt.setString(5, selectedProduct.type);
                    pstmt.setString(6, selectedProduct.uom);
                    pstmt.setInt(7, qty);
                    pstmt.setDouble(8, price);
                    pstmt.setDouble(9, total);
                    pstmt.executeUpdate();

                    // Update customer balance
                    String updateQuery = "UPDATE Customers SET amount_remaining = ? WHERE customer_id = ?";
                    try (PreparedStatement updatePstmt = db.getConnection().prepareStatement(updateQuery)) {
                        updatePstmt.setDouble(1, updatedRemaining);
                        updatePstmt.setInt(2, customerId);
                        updatePstmt.executeUpdate();
                    }
                }
                */

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePurchase() {
        int selectedRow = ledgerTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedRow = ledgerTable.convertRowIndexToModel(selectedRow);
            double totalToRemove = (Double) ledgerTableModel.getValueAt(selectedRow, 7); // Total column
            String date = (String) ledgerTableModel.getValueAt(selectedRow, 0);
            String item = (String) ledgerTableModel.getValueAt(selectedRow, 1);
            ledgerTableModel.removeRow(selectedRow);

            // Update amount remaining
            double currentRemaining = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 4).toString());
            double updatedRemaining = currentRemaining - totalToRemove;
            customerTableModel.setValueAt(updatedRemaining, selectedCustomerRow, 4);
            amountRemainingLabel.setText("Amount Remaining: " + updatedRemaining);

            // Update total sale
            int customerId = (int) customerTableModel.getValueAt(selectedCustomerRow, 0);
            updateTotalSale(selectedCustomerRow, customerId);

            // Update DataManager ledger (remove the entry)
            List<List<Object>> ledgerEntries = DataManager.getInstance().getCustomerLedger(customerId);
            ledgerEntries.removeIf(entry -> entry.get(0).equals(date) && entry.get(1).equals(item) && (Double) entry.get(7) == totalToRemove);

            /*
            // Database deletion (example logic, adjust as per your schema)
            String query = "DELETE FROM CustomerLedgers WHERE customer_id = ? AND date = ? AND item = ? AND total = ?";
            try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
                pstmt.setInt(1, customerId);
                pstmt.setString(2, date);
                pstmt.setString(3, item);
                pstmt.setDouble(4, totalToRemove);
                pstmt.executeUpdate();

                // Update customer balance
                String updateQuery = "UPDATE Customers SET amount_remaining = ? WHERE customer_id = ?";
                try (PreparedStatement updatePstmt = db.getConnection().prepareStatement(updateQuery)) {
                    updatePstmt.setDouble(1, updatedRemaining);
                    updatePstmt.setInt(2, customerId);
                    updatePstmt.executeUpdate();
                }
            }
            */
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

        String amountPaidStr = JOptionPane.showInputDialog(this, "Total Sales = " + totalSales + "\nEnter amount paid:");
        if (amountPaidStr == null || amountPaidStr.trim().isEmpty()) return;

        try {
            double paid = Double.parseDouble(amountPaidStr);
            if (paid < 0) {
                JOptionPane.showMessageDialog(this, "Amount paid cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double remaining = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 4).toString()) - paid;
            customerTableModel.setValueAt(remaining, selectedCustomerRow, 4);
            amountRemainingLabel.setText("Amount Remaining: " + remaining);

            // Update amount paid in the table
            double currentPaid = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 3).toString());
            customerTableModel.setValueAt(currentPaid + paid, selectedCustomerRow, 3);

            // Update total sale
            int customerId = (int) customerTableModel.getValueAt(selectedCustomerRow, 0);
            updateTotalSale(selectedCustomerRow, customerId);

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

            /*
            // Database update
            String query = "UPDATE Customers SET amount_remaining = ?, amount_paid = ? WHERE customer_id = ?";
            try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
                pstmt.setDouble(1, remaining);
                pstmt.setDouble(2, currentPaid + paid);
                pstmt.setInt(3, customerId);
                pstmt.executeUpdate();
            }
            */

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount paid. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerLedgerFrame::new);
    }
}
