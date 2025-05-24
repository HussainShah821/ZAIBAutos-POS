import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class CustomerLedgerFrame extends JFrame {
    private JTable customerTable, purchaseTable;
    private DefaultTableModel customerTableModel, purchaseTableModel;
    private JTextField searchField;
    private JPanel mainPanel, detailPanel;
    private JLabel customerNameLabel, phoneLabel, balanceLabel;
    private int selectedCustomerRow = -1;
    private ArrayList<Product> inventoryProducts;

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
        setTitle("Zaib Autos - Customer Ledger");
        setSize(1000, 600);
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

        JLabel titleLabel = new JLabel("Customer Ledger");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setToolTipText("Search by name or phone...");
        centerPanel.add(searchField, BorderLayout.NORTH);

        customerTableModel = new DefaultTableModel(new String[]{"Name", "Phone No", "Balance"}, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 14));
        customerTable.setRowHeight(25);
        customerTable.setGridColor(new Color(200, 200, 200));
        customerTable.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton("Add Customer");
        JButton deleteButton = createStyledButton("Delete Customer");
        JButton backButton = createStyledButton("Back");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                filterCustomerTable(text);
            }
        });

        addButton.addActionListener(e -> addCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        backButton.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && customerTable.getSelectedRow() != -1) {
                    selectedCustomerRow = customerTable.getSelectedRow();
                    showCustomerDetailPanel();
                }
            }
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
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

    private void filterCustomerTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(customerTableModel);
        customerTable.setRowSorter(sorter);
        if (searchText.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void addCustomer() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and phone are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            customerTableModel.addRow(new Object[]{name, phone, "0"});
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                customerTableModel.removeRow(selectedRow);
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

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBackground(Color.WHITE);
        customerNameLabel = new JLabel("Customer: " + customerTableModel.getValueAt(selectedCustomerRow, 0));
        customerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        phoneLabel = new JLabel("Phone: " + customerTableModel.getValueAt(selectedCustomerRow, 1));
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        balanceLabel = new JLabel("Balance: " + customerTableModel.getValueAt(selectedCustomerRow, 2));
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(customerNameLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(balanceLabel);
        detailPanel.add(infoPanel, BorderLayout.NORTH);

        purchaseTableModel = new DefaultTableModel(new String[]{"Date", "Item", "Brand", "Type", "UOM", "Qty", "Price", "Total"}, 0);
        purchaseTable = new JTable(purchaseTableModel);
        purchaseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        purchaseTable.setRowHeight(25);
        purchaseTable.setGridColor(new Color(200, 200, 200));
        purchaseTable.setShowGrid(true);
        JScrollPane purchaseScrollPane = new JScrollPane(purchaseTable);
        detailPanel.add(purchaseScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        JButton addPurchaseBtn = createStyledButton("Add Purchase");
        JButton generateBillBtn = createStyledButton("Generate Bill");
        JButton backBtn = createStyledButton("Back");

        bottomPanel.add(addPurchaseBtn);
        bottomPanel.add(generateBillBtn);
        bottomPanel.add(backBtn);
        detailPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(detailPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        addPurchaseBtn.addActionListener(e -> addPurchase());
        generateBillBtn.addActionListener(e -> generateBill());
        backBtn.addActionListener(e -> {
            remove(detailPanel);
            detailPanel = null;
            mainPanel.setVisible(true);
            revalidate();
            repaint();
        });
    }

    private void addPurchase() {
        // Create components
        JTextField searchField = new JTextField();
        JComboBox<Product> productComboBox = new JComboBox<>();
        JTextField qtyField = new JTextField();

        // Backup list for filtering
        List<Product> filteredList = new ArrayList<>(inventoryProducts);

        // Add all products initially
        for (Product product : filteredList) {
            productComboBox.addItem(product);
        }

        // Set up search functionality
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

        // Make combo box editable for better user experience
        productComboBox.setEditable(true);

        // Create panel layout with GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Search field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Search Product:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        searchField.setPreferredSize(new Dimension(200, 30));
        panel.add(searchField, gbc);

        // Product combo box
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        productComboBox.setPreferredSize(new Dimension(200, 30));
        panel.add(new JLabel("Select Product:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(productComboBox, gbc);

        // Quantity field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        qtyField.setPreferredSize(new Dimension(200, 30));
        panel.add(qtyField, gbc);

        // Show input dialog
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

                purchaseTableModel.addRow(new Object[]{
                        date, selectedProduct.name, selectedProduct.brand,
                        selectedProduct.type, selectedProduct.uom, qty, price, total
                });

                double currentBalance = Double.parseDouble(
                        customerTableModel.getValueAt(selectedCustomerRow, 2).toString());
                double updatedBalance = currentBalance + total;
                customerTableModel.setValueAt(updatedBalance, selectedCustomerRow, 2);
                balanceLabel.setText("Balance: " + updatedBalance);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void generateBill() {
        double total = 0;
        for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
            Object val = purchaseTableModel.getValueAt(i, 7);
            if (val != null) {
                total += Double.parseDouble(val.toString());
            }
        }

        String amountPaidStr = JOptionPane.showInputDialog(this, "Total = " + total + "\nEnter amount paid:");
        if (amountPaidStr == null || amountPaidStr.trim().isEmpty()) return;

        try {
            double paid = Double.parseDouble(amountPaidStr);
            if (paid < 0) {
                JOptionPane.showMessageDialog(this, "Amount paid cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double balance = total - paid;
            double currentBalance = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 2).toString());
            double newBalance = currentBalance - paid;
            customerTableModel.setValueAt(newBalance, selectedCustomerRow, 2);
            balanceLabel.setText("Balance: " + newBalance);

            String customerName = customerTableModel.getValueAt(selectedCustomerRow, 0).toString();
            String phone = customerTableModel.getValueAt(selectedCustomerRow, 1).toString();
            String voucherNo = "INV-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String dateStr = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());

            JDialog billDialog = new JDialog(this, "Generated Bill", true);
            billDialog.setSize(650, 900);
            billDialog.setLocationRelativeTo(this);

            BillPanel billPanel = new BillPanel(customerName, phone, voucherNo, dateStr, purchaseTableModel, total, paid, balance);
            billDialog.add(billPanel);
            billDialog.setVisible(true);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount paid. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerLedgerFrame::new);
    }
}