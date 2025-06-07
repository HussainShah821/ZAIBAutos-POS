import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SalesEntryFrame extends JFrame {
    private DefaultTableModel saleTableModel;
    private JTable saleTable;
    private JComboBox<String> productCombo;
    private JTextField quantityField, priceField, dateField;
    private Border defaultBorder, errorBorder;
    private double totalAmount = 0;
    private static int nextSaleNo = 1;
    private Map<String, ProductDetails> productMap = new HashMap<>();
    // private DatabaseConnection db;

    // Helper class to store product details (excluding price)
    private static class ProductDetails {
        String name, company, type, uom;
        int quantity;

        ProductDetails(String name, String company, String type, String uom, int quantity) {
            this.name = name;
            this.company = company;
            this.type = type;
            this.uom = uom;
            this.quantity = quantity;
        }
    }

    public SalesEntryFrame() {
        // db = new DatabaseConnection();
        setTitle("Zaib Autos - Sales Entry");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Borders for validation
        defaultBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        errorBorder = BorderFactory.createLineBorder(Color.RED, 1);

        // Initialize product details
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
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


        buttonPanel.add(addItemButton);
        buttonPanel.add(removeItemButton);
        buttonPanel.add(returnItemButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(viewDailySalesButton);
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
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add Sale Item"));
        panel.setPreferredSize(new Dimension(250, 180)); // Increased height for date field

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

        JLabel stockLabel = new JLabel("Stock: 0");
        productCombo.addActionListener(e -> {
            String selectedProduct = (String) productCombo.getSelectedItem();
            ProductDetails details = productMap.get(selectedProduct);
            stockLabel.setText("Stock: " + (details != null ? details.quantity : 0));
        });

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(stockLabel, gbc);

        return panel;
    }

    private void initializeProducts() {
        productMap.put("Engine Oil (Castrol)", new ProductDetails("Engine Oil", "Castrol", "Oil", "Liters", 100));
        productMap.put("Brake Pad (Bosch)", new ProductDetails("Brake Pad", "Bosch", "Pad", "Pieces", 50));
        productMap.put("Air Filter (Mann)", new ProductDetails("Air Filter", "Mann", "Filter", "Pieces", 30));
        productMap.put("Spark Plug (NGK)", new ProductDetails("Spark Plug", "NGK", "Plug", "Pieces", 20));

        // Fetch products from database (commented out)
        // try {
        //     PreparedStatement pstmt = db.getConnection().prepareStatement("SELECT * FROM Products");
        //     ResultSet rs = pstmt.executeQuery();
        //     while (rs.next()) {
        //         String name = rs.getString("name");
        //         String company = rs.getString("company");
        //         String displayName = name + " (" + company + ")";
        //         productMap.put(displayName, new ProductDetails(
        //             name,
        //             company,
        //             rs.getString("type"),
        //             rs.getString("uom"),
        //             rs.getInt("quantity")
        //         ));
        //     }
        // } catch (SQLException e) {
        //     e.printStackTrace();
        //     JOptionPane.showMessageDialog(this, "Error loading products");
        // }
    }

    private void addSaleItem() {
        String selectedProductDisplay = (String) productCombo.getSelectedItem();
        if (selectedProductDisplay == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProductDetails product = productMap.get(selectedProductDisplay);
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            String date = dateField.getText().trim();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            sdf.parse(date); // Validate date format, will throw ParseException if invalid

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

            if (quantity > product.quantity) {
                quantityField.setBorder(errorBorder);
                JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + product.quantity, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double total = quantity * price;
            totalAmount += total;
            saleTableModel.addRow(new Object[]{
                    saleTableModel.getRowCount() + 1,
                    product.name,
                    product.company,
                    product.type,
                    product.uom,
                    quantity,
                    price,
                    total,
                    date
            });

            product.quantity -= quantity;
            quantityField.setBorder(defaultBorder);
            priceField.setBorder(defaultBorder);
            dateField.setBorder(defaultBorder);
            quantityField.setText("");
            priceField.setText("0");
            dateField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date())); // Reset to current date

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
            String company = (String) saleTableModel.getValueAt(selectedRow, 2);
            String displayName = productName + " (" + company + ")";

            totalAmount -= itemTotal;
            saleTableModel.removeRow(selectedRow);
            for (int i = 0; i < saleTableModel.getRowCount(); i++) {
                saleTableModel.setValueAt(i + 1, i, 0);
            }

            ProductDetails product = productMap.get(displayName);
            if (product != null) {
                product.quantity += quantity;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnItem() {
        JDialog dialog = new JDialog(this, "Return Item", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.WHITE);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Product:"), gbc);

        JComboBox<String> returnProductCombo = new JComboBox<>(productMap.keySet().toArray(new String[0]));
        returnProductCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        dialog.add(returnProductCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Quantity:"), gbc);

        JTextField returnQuantityField = new JTextField();
        returnQuantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        dialog.add(returnQuantityField, gbc);

        JButton confirmButton = createStyledButton("Confirm");
        JButton cancelButton = createStyledButton("Cancel");

        confirmButton.addActionListener(e -> {
            String selectedProductDisplay = (String) returnProductCombo.getSelectedItem();
            if (selectedProductDisplay == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ProductDetails product = productMap.get(selectedProductDisplay);
            try {
                int returnQuantity = Integer.parseInt(returnQuantityField.getText().trim());
                if (returnQuantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Return quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                product.quantity += returnQuantity;
                double price = 0;
                double total = 0;
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                saleTableModel.addRow(new Object[]{
                        saleTableModel.getRowCount() + 1,
                        product.name,
                        product.company,
                        product.type,
                        product.uom,
                        -returnQuantity,
                        price,
                        total,
                        date
                });

                JOptionPane.showMessageDialog(dialog, "Item returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid quantity. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0; gbc.gridy = 2;
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
    }

    private void saveSale() {
        if (saleTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items added to the sale.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save to database (commented out)
        // try {
        //     for (int i = 0; i < saleTableModel.getRowCount(); i++) {
        //         String productName = (String) saleTableModel.getValueAt(i, 1);
        //         String company = (String) saleTableModel.getValueAt(i, 2);
        //         String displayName = productName + " (" + company + ")";
        //         int quantity = (int) saleTableModel.getValueAt(i, 5);
        //         double price = (double) saleTableModel.getValueAt(i, 6);
        //         double total = (double) saleTableModel.getValueAt(i, 7);
        //         String saleDate = (String) saleTableModel.getValueAt(i, 8);

        //         // Find product_id
        //         int productId = -1;
        //         PreparedStatement pstmt = db.getConnection().prepareStatement("SELECT product_id FROM Products WHERE name = ? AND company = ?");
        //         pstmt.setString(1, productName);
        //         pstmt.setString(2, company);
        //         ResultSet rs = pstmt.executeQuery();
        //         if (rs.next()) {
        //             productId = rs.getInt("product_id");
        //         }

        //         // Insert sale
        //         pstmt = db.getConnection().prepareStatement("INSERT INTO Sales (sale_no, product_id, quantity, price, total, sale_date) VALUES (?, ?, ?, ?, ?, ?)");
        //         pstmt.setInt(1, nextSaleNo);
        //         pstmt.setInt(2, productId);
        //         pstmt.setInt(3, quantity);
        //         pstmt.setDouble(4, price);
        //         pstmt.setDouble(5, total);
        //         pstmt.setString(6, saleDate);
        //         pstmt.executeUpdate();

        //         // Update product stock
        //         pstmt = db.getConnection().prepareStatement("UPDATE Products SET quantity = quantity - ? WHERE product_id = ?");
        //         pstmt.setInt(1, quantity);
        //         pstmt.setInt(2, productId);
        //         pstmt.executeUpdate();
        //     }
        //     nextSaleNo++;
        // } catch (SQLException e) {
        //     e.printStackTrace();
        //     JOptionPane.showMessageDialog(this, "Error saving sale");
        //     return;
        // }

        String saleDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        JOptionPane.showMessageDialog(this, "Sale saved successfully (database saving skipped)!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
    }

    private void viewDailySales() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());
        DefaultTableModel dailyModel = new DefaultTableModel(new String[]{"S.No", "Product", "Company", "Type", "UOM", "Quantity", "Price", "Total", "Date"}, 0);

        for (int i = 0; i < saleTableModel.getRowCount(); i++) {
            String entryDate = (String) saleTableModel.getValueAt(i, 8);
            if (entryDate.equals(currentDate)) {
                dailyModel.addRow(new Object[]{
                        dailyModel.getRowCount() + 1,
                        saleTableModel.getValueAt(i, 1),
                        saleTableModel.getValueAt(i, 2),
                        saleTableModel.getValueAt(i, 3),
                        saleTableModel.getValueAt(i, 4),
                        saleTableModel.getValueAt(i, 5),
                        saleTableModel.getValueAt(i, 6),
                        saleTableModel.getValueAt(i, 7),
                        saleTableModel.getValueAt(i, 8)
                });
            }
        }

        if (dailyModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No sales recorded for today (" + currentDate + ").", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTable dailyTable = new JTable(dailyModel);
        dailyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dailyTable.setRowHeight(25);
        dailyTable.setGridColor(new Color(200, 200, 200));
        dailyTable.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(dailyTable);
        JOptionPane.showMessageDialog(this, scrollPane, "Daily Sales - " + currentDate, JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SalesEntryFrame::new);
    }
}