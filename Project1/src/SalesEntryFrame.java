import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SalesEntryFrame extends JFrame {
    private DefaultTableModel saleTableModel;
    private JTable saleTable;
    private JComboBox<String> productCombo;
    private JTextField quantityField, priceField;
    private Border defaultBorder, errorBorder;
    private double totalAmount = 0;
    private static int nextSaleNo = 1;
    private Map<String, Integer> stockMap = new HashMap<>();
    private Map<String, Integer> soldQuantityMap = new HashMap<>();

    public SalesEntryFrame() {
        setTitle("Zaib Autos - Sales Entry");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Borders for validation
        defaultBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        errorBorder = BorderFactory.createLineBorder(Color.RED, 1);

        // Initialize stock and sold quantity maps
        initializeStock();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Daily Sales Entry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.WEST);

        saleTableModel = new DefaultTableModel(new String[]{"S.No", "Product", "Brand", "Type", "UOM", "Quantity", "Price", "Total"}, 0);
        saleTable = new JTable(saleTableModel);
        saleTable.setFont(new Font("Arial", Font.PLAIN, 14));
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
        JButton backButton = createStyledButton("Back");

        addItemButton.addActionListener(e -> addSaleItem());
        removeItemButton.addActionListener(e -> removeSaleItem());
        returnItemButton.addActionListener(e -> returnItem());
        clearButton.addActionListener(e -> clearForm());
        saveButton.addActionListener(e -> saveSale());
        backButton.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        buttonPanel.add(addItemButton);
        buttonPanel.add(removeItemButton);
        buttonPanel.add(returnItemButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

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

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add Sale Item"));
        panel.setPreferredSize(new Dimension(250, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Product:"), gbc);

        productCombo = new JComboBox<>(new String[]{"Engine Oil", "Brake Pad", "Air Filter", "Spark Plug"});
        productCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        productCombo.setToolTipText("Select a product");

        gbc.gridx = 1;
        panel.add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Quantity:"), gbc);

        quantityField = new JTextField();
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        quantityField.setToolTipText("Enter quantity (positive number)");

        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Price (PKR):"), gbc);

        priceField = new JTextField("0");
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        priceField.setToolTipText("Enter price per unit (positive number)");

        gbc.gridx = 1;
        panel.add(priceField, gbc);

        JLabel stockLabel = new JLabel("Stock: 0");

        productCombo.addActionListener(e -> {
            String selectedProduct = (String) productCombo.getSelectedItem();
            stockLabel.setText("Stock: " + stockMap.getOrDefault(selectedProduct, 0));
        });

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(stockLabel, gbc);

        return panel;
    }

    private void initializeStock() {
        stockMap.put("Engine Oil", 100);
        stockMap.put("Brake Pad", 50);
        stockMap.put("Air Filter", 30);
        stockMap.put("Spark Plug", 20);
        soldQuantityMap.put("Engine Oil", 0);
        soldQuantityMap.put("Brake Pad", 0);
        soldQuantityMap.put("Air Filter", 0);
        soldQuantityMap.put("Spark Plug", 0);
    }

    private void addSaleItem() {
        String selectedProduct = (String) productCombo.getSelectedItem();
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
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

            int stock = stockMap.getOrDefault(selectedProduct, 0);
            if (quantity > stock) {
                quantityField.setBorder(errorBorder);
                JOptionPane.showMessageDialog(this, "Insufficient stock. Available: " + stock, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add dummy data for Brand, Type, UOM
            String brand = switch (selectedProduct) {
                case "Engine Oil" -> "Castrol";
                case "Brake Pad" -> "Bosch";
                case "Air Filter" -> "Mann";
                case "Spark Plug" -> "NGK";
                default -> "Unknown";
            };
            String type = switch (selectedProduct) {
                case "Engine Oil" -> "Oil";
                case "Brake Pad" -> "Pad";
                case "Air Filter" -> "Filter";
                case "Spark Plug" -> "Plug";
                default -> "Unknown";
            };
            String uom = switch (selectedProduct) {
                case "Engine Oil" -> "Liters";
                case "Brake Pad" -> "Pieces";
                case "Air Filter" -> "Pieces";
                case "Spark Plug" -> "Pieces";
                default -> "Unknown";
            };

            double total = quantity * price;
            totalAmount += total;
            saleTableModel.addRow(new Object[]{
                    saleTableModel.getRowCount() + 1,
                    selectedProduct,
                    brand,
                    type,
                    uom,
                    quantity,
                    price,
                    total
            });

            stockMap.put(selectedProduct, stock - quantity);
            soldQuantityMap.put(selectedProduct, soldQuantityMap.getOrDefault(selectedProduct, 0) + quantity);
            quantityField.setBorder(defaultBorder);
            priceField.setBorder(defaultBorder);
            quantityField.setText("");
            priceField.setText("0");

        } catch (NumberFormatException ex) {
            quantityField.setBorder(errorBorder);
            priceField.setBorder(errorBorder);
            JOptionPane.showMessageDialog(this, "Invalid quantity or price. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSaleItem() {
        int selectedRow = saleTable.getSelectedRow();
        if (selectedRow != -1) {
            double itemTotal = (double) saleTableModel.getValueAt(selectedRow, 7); // Total column
            int quantity = (int) saleTableModel.getValueAt(selectedRow, 5); // Quantity column
            String productName = (String) saleTableModel.getValueAt(selectedRow, 1); // Product column
            totalAmount -= itemTotal;
            saleTableModel.removeRow(selectedRow);
            for (int i = 0; i < saleTableModel.getRowCount(); i++) {
                saleTableModel.setValueAt(i + 1, i, 0);
            }
            int currentStock = stockMap.getOrDefault(productName, 0);
            stockMap.put(productName, currentStock + quantity);
            int currentSold = soldQuantityMap.getOrDefault(productName, 0) - quantity;
            soldQuantityMap.put(productName, currentSold > 0 ? currentSold : 0);
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

        JComboBox<String> returnProductCombo = new JComboBox<>(new String[]{"Engine Oil", "Brake Pad", "Air Filter", "Spark Plug"});
        returnProductCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        dialog.add(returnProductCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Quantity:"), gbc);

        JTextField returnQuantityField = new JTextField();
        returnQuantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        dialog.add(returnQuantityField, gbc);

        JButton confirmButton = createStyledButton("Confirm");
        JButton cancelButton = createStyledButton("Cancel");

        confirmButton.addActionListener(e -> {
            String selectedProduct = (String) returnProductCombo.getSelectedItem();
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int returnQuantity = Integer.parseInt(returnQuantityField.getText().trim());
                if (returnQuantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Return quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int totalSold = soldQuantityMap.getOrDefault(selectedProduct, 0);
                if (returnQuantity > totalSold) {
                    JOptionPane.showMessageDialog(dialog, "Cannot return more than sold. Total sold: " + totalSold, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Add stock and update sold quantity
                int currentStock = stockMap.getOrDefault(selectedProduct, 0);
                stockMap.put(selectedProduct, currentStock + returnQuantity);
                int currentSold = soldQuantityMap.getOrDefault(selectedProduct, 0) - returnQuantity;
                soldQuantityMap.put(selectedProduct, currentSold > 0 ? currentSold : 0);

                // Add negative row to table
                String brand = switch (selectedProduct) {
                    case "Engine Oil" -> "Castrol";
                    case "Brake Pad" -> "Bosch";
                    case "Air Filter" -> "Mann";
                    case "Spark Plug" -> "NGK";
                    default -> "Unknown";
                };
                String type = switch (selectedProduct) {
                    case "Engine Oil" -> "Oil";
                    case "Brake Pad" -> "Pad";
                    case "Air Filter" -> "Filter";
                    case "Spark Plug" -> "Plug";
                    default -> "Unknown";
                };
                String uom = switch (selectedProduct) {
                    case "Engine Oil" -> "Liters";
                    case "Brake Pad" -> "Pieces";
                    case "Air Filter" -> "Pieces";
                    case "Spark Plug" -> "Pieces";
                    default -> "Unknown";
                };
                double price = 0; // Placeholder, should fetch actual price if needed
                double total = -returnQuantity * price; // Negative total for return
                saleTableModel.addRow(new Object[]{
                        saleTableModel.getRowCount() + 1,
                        selectedProduct,
                        brand,
                        type,
                        uom,
                        -returnQuantity, // Negative quantity for return
                        price,
                        total
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
        quantityField.setBorder(defaultBorder);
        priceField.setBorder(defaultBorder);
    }

    private void saveSale() {
        if (saleTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items added to the sale.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String saleDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        JOptionPane.showMessageDialog(this, "Sale saved successfully (database saving skipped)!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(SalesEntryFrame::new);
    }
}