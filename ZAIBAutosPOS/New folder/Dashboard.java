import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
// JFreeChart imports
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

// SwingX imports
import org.jdesktop.swingx.JXDatePicker;

// Other necessary imports
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.text.ParseException;
import java.text.DateFormatSymbols;

public class Dashboard extends JFrame {
    private JPanel drawerPanel, inventoryPanel, ledgerPanel, customerDetailPanel;
    private boolean isDrawerOpen = false;
    private JButton menuButton;
    private JTable productTable, customerTable, purchaseTable;
    private DefaultTableModel productTableModel, customerTableModel, purchaseTableModel;
    private List<String> productNames = new ArrayList<String>();
    private int itemCount = 0;
    private JLabel customerNameLabel, phoneLabel, balanceLabel;
    private JPanel supplierLedgerPanel;
private JTable supplierTable;
private DefaultTableModel supplierTableModel;
private JPanel supplierListPanel, supplierDetailPanel;
private JTable supplierListTable;
private DefaultTableModel supplierListTableModel;
private JLabel supplierNameLabel, supplierPhoneLabel;
private JPanel salesAnalysisPanel;
private JTable dailyEntriesTable;
private DefaultTableModel dailyEntriesModel;
private JLabel dailyTotalLabel, monthlyTotalLabel, yearlyTotalLabel;
private JLabel dailyProfitLabel, monthlyProfitLabel, yearlyProfitLabel;
private JFreeChart salesChart, profitChart;
private ChartPanel salesChartPanel, profitChartPanel;
private JXDatePicker datePicker;

    public Dashboard() {
        setTitle("Inventory Management");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background
        JLayeredPane layeredPane = getLayeredPane();
        JLabel background = new JLabel(new ImageIcon("background.jpg"));
        background.setBounds(0, 0, 1366, 768);
        layeredPane.add(background, Integer.valueOf(1));

        // Drawer panel
        drawerPanel = new JPanel(null);
        drawerPanel.setBackground(new Color(50, 50, 50, 220));
        drawerPanel.setBounds(-250, 0, 250, 768);
        layeredPane.add(drawerPanel, Integer.valueOf(2));

        JLabel profileLabel = new JLabel("Asjal Mehmood");
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setFont(new Font("Arial", Font.BOLD, 16));
        profileLabel.setBounds(80, 20, 200, 30);
        drawerPanel.add(profileLabel);

        JLabel emailLabel = new JLabel("ZAIB_AUTOS");
        emailLabel.setForeground(Color.LIGHT_GRAY);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setBounds(50, 50, 200, 30);
        drawerPanel.add(emailLabel);

      

        JButton homeButton = addDrawerButton("Home", 150);
        homeButton.addActionListener(e -> {
            closeDrawer();
            showHome();
        });
        
        JButton inventoryButton = addDrawerButton("Update Inventory", 200);
        inventoryButton.addActionListener(e -> {
            closeDrawer();
            showInventoryPanel();
        });
        
        JButton ledgerButton = addDrawerButton("Customer Ledger", 250);
        ledgerButton.addActionListener(e -> {
            closeDrawer();
            showLedgerPanel();
        });
        
        JButton supplierButton = addDrawerButton("Supplier Ledger", 300);
        supplierButton.addActionListener(e -> {
            closeDrawer();
            showSupplierLedgerPanel();
        });
        JButton salesReport=addDrawerButton("Sales ",350);
        salesReport.addActionListener(e -> {
           initializeSalesAnalysisPanel();
        });
        
        JButton logoutButton = addDrawerButton("Logout", 400);
        logoutButton.addActionListener(e ->{
            // new AdminLogin();
        });
        drawerPanel.add(logoutButton);
        

        menuButton = new JButton("☰");
        menuButton.setBounds(10, 10, 40, 40);
        menuButton.setFont(new Font("Arial", Font.BOLD, 20));
        menuButton.setBackground(Color.GRAY);
        menuButton.setForeground(Color.WHITE);
        menuButton.setBorderPainted(false);
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> toggleDrawer());
        layeredPane.add(menuButton, Integer.valueOf(3));

        // Panels
        initializeInventoryPanel();
        initializeLedgerPanel();
        initializeCustomerDetailPanel();
        initializeSupplierLedgerPanel();
        initializeSupplierDetailPanel(); 

        addClickListener();
        setVisible(true);
    }
    private void showHome() {
        inventoryPanel.setVisible(false);
        ledgerPanel.setVisible(false);
        customerDetailPanel.setVisible(false);
        supplierLedgerPanel.setVisible(false);
    }
    

    private void toggleDrawer() {
        if (isDrawerOpen) {
            closeDrawer();
        } else {
            drawerPanel.setBounds(0, 0, 250, 768);
            isDrawerOpen = true;
        }
    }

    private void closeDrawer() {
        drawerPanel.setBounds(-250, 0, 250, 768);
        isDrawerOpen = false;
    }

    private void addClickListener() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (isDrawerOpen && e.getX() > 250) {
                    closeDrawer();
                }
            }
        });
    }

    private JButton addDrawerButton(String text, int yPosition) {
        JButton button = new JButton(text);
        button.setBounds(20, yPosition, 200, 40);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        drawerPanel.add(button);
        return button;
    }
    private void initializeInventoryPanel() {
        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(null);
        inventoryPanel.setBounds(260, 100, 900, 500);
        inventoryPanel.setBackground(new Color(0, 0, 0, 200));

        // Search Bar - JTextField instead of JComboBox
        JTextField searchField = new JTextField();
        searchField.setBounds(20, 20, 400, 30);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    resetProductTableOrder();
                } else {
                    sortProductToTop(text);
                }
            }
        });
        inventoryPanel.add(searchField);

        // Product table
        productTableModel = new DefaultTableModel(new String[]{"Item No", "Item Name", "Brand", "Type", "UOM", "Quantity"}, 0);
        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBounds(20, 60, 860, 300);
        inventoryPanel.add(scrollPane);

        // Buttons
        JButton addButton = new JButton("Add Product");
        addButton.setBounds(20, 380, 140, 30);
        addButton.addActionListener(e -> addProduct());
        inventoryPanel.add(addButton);

        JButton updateButton = new JButton("Update Product");
        updateButton.setBounds(180, 380, 140, 30);
        updateButton.addActionListener(e -> updateProduct());
        inventoryPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Product");
        deleteButton.setBounds(340, 380, 140, 30);
        deleteButton.addActionListener(e -> deleteProduct());
        inventoryPanel.add(deleteButton);

        getLayeredPane().add(inventoryPanel, Integer.valueOf(4));
        inventoryPanel.setVisible(false);
    }

    private void sortProductToTop(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            resetProductTableOrder();
            return;
        }
    
        List<Vector<Object>> matched = new ArrayList<Vector<Object>>();
        List<Vector<Object>> unmatched = new ArrayList<Vector<Object>>();
    
        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            @SuppressWarnings("unchecked")
            Vector<Object> row = (Vector<Object>) productTableModel.getDataVector().get(i);
            if (row.get(1).toString().toLowerCase().contains(productName.toLowerCase())) {
                matched.add(row);
            } else {
                unmatched.add(row);
            }
        }
    
        productTableModel.setRowCount(0);
        int no = 1;
        for (Vector<Object> row : matched) {
            row.set(0, no++);
            productTableModel.addRow(row);
        }
        for (Vector<Object> row : unmatched) {
            row.set(0, no++);
            productTableModel.addRow(row);
        }
        itemCount = productTableModel.getRowCount();
    }

    private void resetProductTableOrder() {
        // Just reloads in current order, but resets numbering
        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            productTableModel.setValueAt(i + 1, i, 0);
        }
    }
    private void addProduct() {
        JTextField itemNameField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField uomField = new JTextField();
        JTextField quantityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel("Brand:"));
        panel.add(brandField);
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("UOM:"));
        panel.add(uomField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText();
            String brand = brandField.getText();
            String type = typeField.getText();
            String uom = uomField.getText();
            String qty = quantityField.getText();

            itemCount++;
            productTableModel.addRow(new Object[]{
                itemCount, itemName, brand, type, uom, qty
            });
            productNames.add(itemName);

            // Reassign item numbers
            for (int i = 0; i < productTableModel.getRowCount(); i++) {
                productTableModel.setValueAt(i + 1, i, 0);
            }
            itemCount = productTableModel.getRowCount(); // sync count
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            String currentQuantity = productTableModel.getValueAt(selectedRow, 5).toString();
            String newQuantity = JOptionPane.showInputDialog(this, "Enter new quantity:", currentQuantity);
            if (newQuantity != null && !newQuantity.trim().isEmpty()) {
                productTableModel.setValueAt(newQuantity, selectedRow, 5);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            String productName = productTableModel.getValueAt(selectedRow, 1).toString();
            productTableModel.removeRow(selectedRow);
            productNames.remove(productName);

            // Reassign item numbers
            for (int i = 0; i < productTableModel.getRowCount(); i++) {
                productTableModel.setValueAt(i + 1, i, 0);
            }
            itemCount = productTableModel.getRowCount();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // private void initializeLedgerPanel() {
    //     ledgerPanel = new JPanel();
    //     ledgerPanel.setLayout(null);
    //     ledgerPanel.setBounds(260, 100, 900, 500);
    //     ledgerPanel.setBackground(new Color(0, 0, 0, 200));

    //     customerTableModel = new DefaultTableModel(new String[]{"Name", "Phone No", "Balance"}, 0) {
    //         @Override
    //         public boolean isCellEditable(int row, int column) {
    //             return false;
    //         }
    //     };
       
        
    //     customerTable = new JTable(customerTableModel);
    //     customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //     // customerTable.addMouseListener(new MouseAdapter() {
    //     //     public void mouseClicked(MouseEvent e) {
    //     //         if (e.getClickCount() == 1) {
    //     //             showCustomerDetailPanel();
    //     //         }
    //     //     }
    //     // });
    //     customerTable.addMouseListener(new MouseAdapter() {
    //         public void mouseClicked(MouseEvent e) {
    //             int row = customerTable.getSelectedRow();
    //             if (row == -1) return;
        
    //             // if (e.getClickCount() == 2) {
    //             //     int confirm = JOptionPane.showConfirmDialog(Dashboard.this,
    //             //             "Are you sure you want to delete this customer?",
    //             //             "Delete Customer", JOptionPane.YES_NO_OPTION);
    //             //     if (confirm == JOptionPane.YES_OPTION) {
    //             //         customerTableModel.removeRow(row);
    //             //     }
    //             // } 
    //             else if (e.getClickCount() == 2) {
    //                 showCustomerDetailPanel();
    //             }
    //         }
    //     });

        

    //     JScrollPane scrollPane = new JScrollPane(customerTable);
    //     scrollPane.setBounds(20, 20, 860, 400);
    //     ledgerPanel.add(scrollPane);

    //     // JButton addCustomerButton = new JButton("Add Customer");
    //     // addCustomerButton.setBounds(20, 430, 140, 30);
    //     // addCustomerButton.addActionListener(e -> addCustomer());
    //     // ledgerPanel.add(addCustomerButton);
    //     JButton addCustomerButton = new JButton("Add Customer");
    //     addCustomerButton.setBounds(20, 430, 140, 30);
    //     addCustomerButton.addActionListener(e -> addCustomer());
    //     ledgerPanel.add(addCustomerButton);
        
    //     JButton deleteCustomerButton = new JButton("Delete Customer");
    //     deleteCustomerButton.setBounds(180, 430, 140, 30);
    //     deleteCustomerButton.addActionListener(e -> deleteCustomer());
    //     ledgerPanel.add(deleteCustomerButton);

    //     getLayeredPane().add(ledgerPanel, Integer.valueOf(4));
    //     ledgerPanel.setVisible(false);
    // }
    private void sortCustomerToTop(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            resetCustomerTableOrder();
            return;
        }
    
        List<Vector<Object>> matched = new ArrayList<Vector<Object>>();
        List<Vector<Object>> unmatched = new ArrayList<Vector<Object>>();
    
        for (int i = 0; i < customerTableModel.getRowCount(); i++) {
            @SuppressWarnings("unchecked")
            Vector<Object> row = (Vector<Object>) customerTableModel.getDataVector().get(i);
            if (row.get(0).toString().toLowerCase().contains(searchText.toLowerCase()) || 
                row.get(1).toString().toLowerCase().contains(searchText.toLowerCase())) {
                matched.add(row);
            } else {
                unmatched.add(row);
            }
        }
    
        customerTableModel.setRowCount(0);
        for (Vector<Object> row : matched) {
            customerTableModel.addRow(row);
        }
        for (Vector<Object> row : unmatched) {
            customerTableModel.addRow(row);
        }
    }
    
    private void resetCustomerTableOrder() {
        // No need to reset numbers as customer table doesn't have item numbers
        // Just leave as is since we're not reordering, just filtering
    }
   private void initializeLedgerPanel() {
    ledgerPanel = new JPanel();
    ledgerPanel.setLayout(null);
    ledgerPanel.setBounds(260, 100, 900, 500);
    ledgerPanel.setBackground(new Color(0, 0, 0, 200));

    // Add search field for customers
    JTextField customerSearchField = new JTextField();
    customerSearchField.setBounds(20, 20, 400, 30);
    customerSearchField.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
            String text = customerSearchField.getText().trim();
            if (text.isEmpty()) {
                resetCustomerTableOrder();
            } else {
                sortCustomerToTop(text);
            }
        }
    });
    ledgerPanel.add(customerSearchField);

    customerTableModel = new DefaultTableModel(new String[]{"Name", "Phone No", "Balance"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    customerTable = new JTable(customerTableModel);
    customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    customerTable.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            int row = customerTable.getSelectedRow();
            if (row == -1) return;
            else if (e.getClickCount() == 2) {
                showCustomerDetailPanel();
            }
        }
    });

    JScrollPane scrollPane = new JScrollPane(customerTable);
    scrollPane.setBounds(20, 60, 860, 340); // Adjusted y position to make space for search field
    ledgerPanel.add(scrollPane);

    JButton addCustomerButton = new JButton("Add Customer");
    addCustomerButton.setBounds(20, 420, 140, 30); // Adjusted y position
    addCustomerButton.addActionListener(e -> addCustomer());
    ledgerPanel.add(addCustomerButton);
    
    JButton deleteCustomerButton = new JButton("Delete Customer");
    deleteCustomerButton.setBounds(180, 420, 140, 30); // Adjusted y position
    deleteCustomerButton.addActionListener(e -> deleteCustomer());
    ledgerPanel.add(deleteCustomerButton);

    getLayeredPane().add(ledgerPanel, Integer.valueOf(4));
    ledgerPanel.setVisible(false);
}
    private void initializeCustomerDetailPanel() {
        customerDetailPanel = new JPanel();
        customerDetailPanel.setLayout(null);
        customerDetailPanel.setBounds(260, 100, 900, 500);
        customerDetailPanel.setBackground(new Color(0, 0, 0, 200));

        customerNameLabel = new JLabel("Customer: ");
        customerNameLabel.setForeground(Color.WHITE);
        customerNameLabel.setBounds(20, 20, 400, 25);
        customerDetailPanel.add(customerNameLabel);

        phoneLabel = new JLabel("Phone: ");
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setBounds(20, 50, 400, 25);
        customerDetailPanel.add(phoneLabel);

        balanceLabel = new JLabel("Balance: ");
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setBounds(20, 80, 400, 25);
        customerDetailPanel.add(balanceLabel);

        purchaseTableModel = new DefaultTableModel(new String[]{"Date", "Item", "Brand", "Type", "UOM", "Quantity", "Price", "Total"}, 0);
        purchaseTable = new JTable(purchaseTableModel);
        JScrollPane purchaseScroll = new JScrollPane(purchaseTable);
        purchaseScroll.setBounds(20, 120, 860, 250);
        customerDetailPanel.add(purchaseScroll);

        JButton addPurchaseButton = new JButton("Add Purchase");
        addPurchaseButton.setBounds(20, 380, 140, 30);
        addPurchaseButton.addActionListener(e -> addPurchase());
        customerDetailPanel.add(addPurchaseButton);

        JButton generateBillButton = new JButton("Generate Bill");
        generateBillButton.setBounds(180, 380, 140, 30);
        generateBillButton.addActionListener(e -> generateBill());
        customerDetailPanel.add(generateBillButton);

        JButton backButton = new JButton("Back to Customer Ledger");
        backButton.setBounds(740, 380, 140, 30);
        backButton.addActionListener(e -> {
            customerDetailPanel.setVisible(false);
            ledgerPanel.setVisible(true);
        });
        customerDetailPanel.add(backButton);

        getLayeredPane().add(customerDetailPanel, Integer.valueOf(4));
        customerDetailPanel.setVisible(false);
    }

    private void showInventoryPanel() {
        ledgerPanel.setVisible(false);
        customerDetailPanel.setVisible(false);
        inventoryPanel.setVisible(true);
    }

    private void showLedgerPanel() {
        inventoryPanel.setVisible(false);
        customerDetailPanel.setVisible(false);
        ledgerPanel.setVisible(true);
    }

    private void showCustomerDetailPanel() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) return;

        purchaseTableModel.setRowCount(0);

        customerNameLabel.setText("Customer: " + customerTableModel.getValueAt(selectedRow, 0));
        phoneLabel.setText("Phone: " + customerTableModel.getValueAt(selectedRow, 1));
        balanceLabel.setText("Balance: " + customerTableModel.getValueAt(selectedRow, 2));

        inventoryPanel.setVisible(false);
        ledgerPanel.setVisible(false);
        customerDetailPanel.setVisible(true);
    }
    private void addCustomer() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone No:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            customerTableModel.addRow(new Object[]{
                nameField.getText(),
                phoneField.getText(),
                "0"
            });
        }
    }
    // private void showSupplierLedgerPanel() {
    //     inventoryPanel.setVisible(false);
    //     ledgerPanel.setVisible(false);
    //     customerDetailPanel.setVisible(false);
    //     supplierDetailPanel.setVisible(false);
    //     supplierLedgerPanel.setVisible(true);
    // }
    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this customer?",
                    "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                customerTableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void addPurchase() {
        try {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a customer first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            JTextField itemField = new JTextField();
            JTextField brandField = new JTextField();
            JTextField typeField = new JTextField();
            JTextField uomField = new JTextField();
            JTextField quantityField = new JTextField();
            JTextField priceField = new JTextField();
    
            JPanel panel = new JPanel(new GridLayout(6, 2));
            panel.add(new JLabel("Item:")); panel.add(itemField);
            panel.add(new JLabel("Brand:")); panel.add(brandField);
            panel.add(new JLabel("Type:")); panel.add(typeField);
            panel.add(new JLabel("UOM:")); panel.add(uomField);
            panel.add(new JLabel("Quantity:")); panel.add(quantityField);
            panel.add(new JLabel("Price:")); panel.add(priceField);
    
            int result = JOptionPane.showConfirmDialog(this, panel, "Add Purchase", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String itemName = itemField.getText();
                String brand = brandField.getText();
                String type = typeField.getText();
                String uom = uomField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                double total = quantity * price;
    
                boolean itemExists = false;
                for (int i = 0; i < productTableModel.getRowCount(); i++) {
                    if (productTableModel.getValueAt(i, 1).equals(itemName)) {
                        itemExists = true;
                        int availableQty = Integer.parseInt(productTableModel.getValueAt(i, 5).toString());
                        if (quantity > availableQty) {
                            JOptionPane.showMessageDialog(this, "Not enough quantity in inventory!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        } else {
                            productTableModel.setValueAt(availableQty - quantity, i, 5);
                        }
                        break;
                    }
                }
    
                if (!itemExists) {
                    JOptionPane.showMessageDialog(this, "Item not found in inventory!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                purchaseTableModel.addRow(new Object[]{
                    date, itemName, brand, type, uom, quantity, price, total
                });
    
                double currentBalance = Double.parseDouble(customerTableModel.getValueAt(selectedRow, 2).toString());
                customerTableModel.setValueAt(currentBalance + total, selectedRow, 2);
                balanceLabel.setText("Balance: " + (currentBalance + total));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // private void generateBill() {
    //     int selectedRow = customerTable.getSelectedRow();
    //     if (selectedRow == -1) return;
    
    //     String customerName = customerTableModel.getValueAt(selectedRow, 0).toString();
    //     String phone = customerTableModel.getValueAt(selectedRow, 1).toString();
    //     double totalAmount = 0;
    
    //     // Calculate total amount from the purchase table
    //     for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
    //         Object totalObj = purchaseTableModel.getValueAt(i, 7); // Total is at column 7
    //         if (totalObj != null) {
    //             try {
    //                 totalAmount += Double.parseDouble(totalObj.toString());
    //             } catch (NumberFormatException e) {
    //                 // Handle error if needed
    //             }
    //         }
    //     }
    
    //     String amountPaidStr = JOptionPane.showInputDialog(this, "Enter Amount Paid:", "Payment", JOptionPane.PLAIN_MESSAGE);
    //     if (amountPaidStr == null || amountPaidStr.trim().isEmpty()) return;
        
    //     try {
    //         double amountPaid = Double.parseDouble(amountPaidStr);
    //         double balance = totalAmount - amountPaid;
    //         String dateStr = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
    //         String voucherNo = "SI-" + (new Random().nextInt(9000) + 1000);
    
    //         // Create a copy of the purchase table model
    //         DefaultTableModel modelCopy = new DefaultTableModel(
    //             new String[]{"Date", "Item", "Brand", "Type", "UOM", "Qty", "Price", "Total"}, 0
    //         );
            
    //         for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
    //             Vector<Object> rowData = new Vector<>();
    //             for (int j = 0; j < purchaseTableModel.getColumnCount(); j++) {
    //                 rowData.add(purchaseTableModel.getValueAt(i, j));
    //             }
    //             modelCopy.addRow(rowData);
    //         }
    
    //         // Create the bill panel with the new format
    //         BillPanel billPanel = new BillPanel(
    //             customerName, 
    //             phone, 
    //             voucherNo, 
    //             dateStr, 
    //             modelCopy, 
    //             totalAmount, 
    //             amountPaid, 
    //             balance
    //         );
    
    //         // Show preview dialog
    //         JScrollPane scrollPane = new JScrollPane(billPanel);
    //         scrollPane.setPreferredSize(new Dimension(650, 700));
            
    //         int result = JOptionPane.showConfirmDialog(
    //             this, 
    //             scrollPane, 
    //             "Preview Invoice", 
    //             JOptionPane.YES_NO_OPTION,
    //             JOptionPane.PLAIN_MESSAGE
    //         );
            
    //         if (result == JOptionPane.YES_OPTION) {
    //             // Print the bill panel
    //             printPanel(billPanel);
                
    //             // Update customer balance if payment was made
    //             if (amountPaid > 0) {
    //                 double currentBalance = Double.parseDouble(customerTableModel.getValueAt(selectedRow, 2).toString());
    //                 customerTableModel.setValueAt(currentBalance - amountPaid, selectedRow, 2);
    //                 balanceLabel.setText("Balance: " + (currentBalance - amountPaid));
    //             }
    //         }
    //     } catch (NumberFormatException e) {
    //         JOptionPane.showMessageDialog(this, "Please enter a valid number for amount paid", "Error", JOptionPane.ERROR_MESSAGE);
    //     }
    // }
    private void generateBill() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String customerName = customerTableModel.getValueAt(selectedRow, 0).toString();
        String phone = customerTableModel.getValueAt(selectedRow, 1).toString();
        double totalAmount = 0;
    
        // Calculate total amount from purchases
        for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
            try {
                Object totalObj = purchaseTableModel.getValueAt(i, 7);
                if (totalObj != null) {
                    totalAmount += Double.parseDouble(totalObj.toString());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error calculating total: " + e.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    
        // Get payment amount
        String amountPaidStr = JOptionPane.showInputDialog(this, 
            "Enter Amount Paid (Total: " + totalAmount + "):", 
            "Payment", JOptionPane.PLAIN_MESSAGE);
        
        if (amountPaidStr == null || amountPaidStr.trim().isEmpty()) return;
    
        try {
            double amountPaid = Double.parseDouble(amountPaidStr);
            double balance = totalAmount - amountPaid;
            String dateStr = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
            String voucherNo = "SI-" + (new Random().nextInt(9000) + 1000);
    
            // Create table model copy for the bill
            DefaultTableModel billTableModel = new DefaultTableModel(
                new String[]{"", "10 and 1", "Type", "Gry", "Horn", "EDM", "Rate", "Total"}, 0);
            
            for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
                billTableModel.addRow(new Object[]{
                    i + 1,
                    purchaseTableModel.getValueAt(i, 1), // Item
                    purchaseTableModel.getValueAt(i, 3), // Type
                    purchaseTableModel.getValueAt(i, 2), // Brand (as Gry)
                    purchaseTableModel.getValueAt(i, 5), // Quantity (as Horn)
                    purchaseTableModel.getValueAt(i, 4), // UOM (as EDM)
                    purchaseTableModel.getValueAt(i, 6), // Price (as Rate)
                    purchaseTableModel.getValueAt(i, 7)  // Total
                });
            }
    
            // Create the bill panel
            BillPanel billPanel = new BillPanel(
    customerName, 
    phone, 
    voucherNo, 
    dateStr, 
    purchaseTableModel,
    totalAmount, 
    amountPaid, 
    balance
);
    
            // Show preview dialog
            JScrollPane scrollPane = new JScrollPane(billPanel);
            scrollPane.setPreferredSize(new Dimension(650, 700));
            
            int result = JOptionPane.showConfirmDialog(
                this, scrollPane, "Preview Bill", 
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // Print the bill
                printPanel(billPanel);
                
                // Update customer balance if payment was made
                if (amountPaid > 0) {
                    double currentBalance = Double.parseDouble(
                        customerTableModel.getValueAt(selectedRow, 2).toString());
                    customerTableModel.setValueAt(currentBalance - amountPaid, selectedRow, 2);
                    balanceLabel.setText("Balance: " + (currentBalance - amountPaid));
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for amount paid", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // private void saveBillAsPDF(String customerName, String content) {
    //     try {
    //         PrinterJob job = PrinterJob.getPrinterJob();
    //         job.setJobName("Invoice for " + customerName);

    //         job.setPrintable((graphics, pageFormat, pageIndex) -> {
    //             if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

    //             Graphics2D g2d = (Graphics2D) graphics;
    //             g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    //             g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));

    //             int y = 50;
    //             for (String line : content.split("\n")) {
    //                 g2d.drawString(line, 50, y);
    //                 y += g2d.getFontMetrics().getHeight();
    //             }
    //             return Printable.PAGE_EXISTS;
    //         });

    //         if (job.printDialog()) {
    //             job.print();
    //             JOptionPane.showMessageDialog(this, "Bill sent to printer successfully!", "Print Success", JOptionPane.INFORMATION_MESSAGE);
    //         }
    //     } catch (PrinterException e) {
    //         JOptionPane.showMessageDialog(this, "Print Error: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
    //     }
       
    // }
    private void printPanel(JPanel panel) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Print Bill");
        
        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                
                // Print the panel
                panel.printAll(g2d);
                
                return Printable.PAGE_EXISTS;
            }
        });
        
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Print failed: " + e.getMessage());
            }
        }
    }

    
    private void initializeSupplierLedgerPanel() {
        supplierLedgerPanel = new JPanel();
        supplierLedgerPanel.setLayout(null);
        supplierLedgerPanel.setBounds(260, 100, 900, 500);
        supplierLedgerPanel.setBackground(new Color(0, 0, 0, 200));
    
        // Search field for suppliers
        JTextField supplierSearchField = new JTextField();
        supplierSearchField.setBounds(20, 20, 400, 30);
        supplierSearchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = supplierSearchField.getText().trim();
                if (text.isEmpty()) {
                    resetSupplierTableOrder();
                } else {
                    sortSupplierToTop(text);
                }
            }
        });
        supplierLedgerPanel.add(supplierSearchField);
    
        supplierListTableModel = new DefaultTableModel(new String[]{"Supplier Name", "Phone No"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        supplierListTable = new JTable(supplierListTableModel);
        supplierListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierListTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = supplierListTable.getSelectedRow();
                if (row == -1) return;
                else if (e.getClickCount() == 2) {
                    showSupplierDetailPanel();
                }
            }
        });
    
        JScrollPane scrollPane = new JScrollPane(supplierListTable);
        scrollPane.setBounds(20, 60, 860, 340);
        supplierLedgerPanel.add(scrollPane);
    
        JButton addSupplierButton = new JButton("Add Supplier");
        addSupplierButton.setBounds(20, 420, 140, 30);
        addSupplierButton.addActionListener(e -> addSupplier());
        supplierLedgerPanel.add(addSupplierButton);
        
        JButton deleteSupplierButton = new JButton("Delete Supplier");
        deleteSupplierButton.setBounds(180, 420, 140, 30);
        deleteSupplierButton.addActionListener(e -> deleteSupplier());
        supplierLedgerPanel.add(deleteSupplierButton);
    
        getLayeredPane().add(supplierLedgerPanel, Integer.valueOf(4));
        supplierLedgerPanel.setVisible(false);
    }
    private void initializeSupplierDetailPanel() {
        supplierDetailPanel = new JPanel();
        supplierDetailPanel.setLayout(null);
        supplierDetailPanel.setBounds(260, 100, 900, 500);
        supplierDetailPanel.setBackground(new Color(0, 0, 0, 200));
    
        supplierNameLabel = new JLabel("Supplier: ");
        supplierNameLabel.setForeground(Color.WHITE);
        supplierNameLabel.setBounds(20, 20, 400, 25);
        supplierDetailPanel.add(supplierNameLabel);
    
        supplierPhoneLabel = new JLabel("Phone: ");
        supplierPhoneLabel.setForeground(Color.WHITE);
        supplierPhoneLabel.setBounds(20, 50, 400, 25);
        supplierDetailPanel.add(supplierPhoneLabel);
    
        supplierTableModel = new DefaultTableModel(new String[]{
            "V No/Bill No", "Date", "Remarks", "Debit", "Credit", "Balance"
        }, 0);
        supplierTable = new JTable(supplierTableModel);
        
        JScrollPane supplierScroll = new JScrollPane(supplierTable);
        supplierScroll.setBounds(20, 90, 860, 280);
        supplierDetailPanel.add(supplierScroll);
    
        JButton addEntryButton = new JButton("Add Entry");
        addEntryButton.setBounds(20, 380, 140, 30);
        addEntryButton.addActionListener(e -> addSupplierEntry());
        supplierDetailPanel.add(addEntryButton);
    
        JButton deleteEntryButton = new JButton("Delete Entry");
        deleteEntryButton.setBounds(180, 380, 140, 30);
        deleteEntryButton.addActionListener(e -> deleteSupplierEntry());
        supplierDetailPanel.add(deleteEntryButton);
    
        JButton exportButton = new JButton("Export to PDF");
        exportButton.setBounds(340, 380, 140, 30);
        exportButton.addActionListener(e -> exportSupplierLedgerAsPDF());
        supplierDetailPanel.add(exportButton);
    
        JButton backButton = new JButton("Back to Suppliers");
        backButton.setBounds(740, 380, 140, 30);
        backButton.addActionListener(e -> {
            supplierDetailPanel.setVisible(false);
            supplierLedgerPanel.setVisible(true);
        });
        supplierDetailPanel.add(backButton);
    
        getLayeredPane().add(supplierDetailPanel, Integer.valueOf(4));
        supplierDetailPanel.setVisible(false);
    }
    private void sortSupplierToTop(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            resetSupplierTableOrder();
            return;
        }
    
        List<Vector<Object>> matched = new ArrayList<Vector<Object>>();
        List<Vector<Object>> unmatched = new ArrayList<Vector<Object>>();
    
        for (int i = 0; i < supplierListTableModel.getRowCount(); i++) {
            @SuppressWarnings("unchecked")
            Vector<Object> row = (Vector<Object>) supplierListTableModel.getDataVector().get(i);
            if (row.get(0).toString().toLowerCase().contains(searchText.toLowerCase()) || 
                row.get(1).toString().toLowerCase().contains(searchText.toLowerCase())) {
                matched.add(row);
            } else {
                unmatched.add(row);
            }
        }
    
        supplierListTableModel.setRowCount(0);
        for (Vector<Object> row : matched) {
            supplierListTableModel.addRow(row);
        }
        for (Vector<Object> row : unmatched) {
            supplierListTableModel.addRow(row);
        }
    }
    
    private void resetSupplierTableOrder() {
        // No action needed as we're not maintaining order numbers
    }
    
    private void showSupplierDetailPanel() {
        int selectedRow = supplierListTable.getSelectedRow();
        if (selectedRow == -1) return;
    
        supplierTableModel.setRowCount(0);
    
        supplierNameLabel.setText("Supplier: " + supplierListTableModel.getValueAt(selectedRow, 0));
        supplierPhoneLabel.setText("Phone: " + supplierListTableModel.getValueAt(selectedRow, 1));
    
        supplierLedgerPanel.setVisible(false);
        supplierDetailPanel.setVisible(true);
    }
    
    private void addSupplier() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
    
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone No:"));
        panel.add(phoneField);
    
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            supplierListTableModel.addRow(new Object[]{
                nameField.getText(),
                phoneField.getText()
            });
        }
    }
    
    private void deleteSupplier() {
        int selectedRow = supplierListTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this supplier?",
                    "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                supplierListTableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a supplier to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void exportSupplierLedgerAsPDF() {
        StringBuilder content = new StringBuilder();
        content.append("SUPPLIER LEDGER\n");
        content.append("------------------------------\n");
    
        for (int i = 0; i < supplierTableModel.getRowCount(); i++) {
            for (int j = 0; j < supplierTableModel.getColumnCount(); j++) {
                content.append(supplierTableModel.getValueAt(i, j)).append("\t");
            }
            content.append("\n");
        }
    
        content.append("------------------------------\n");
    
        JTextArea textArea = new JTextArea(content.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
    
        int option = JOptionPane.showConfirmDialog(this, new JScrollPane(textArea),
                "Print or Export Supplier Ledger", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
    
        if (option == JOptionPane.YES_OPTION) {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("Supplier Ledger");
    
                job.setPrintable((graphics, pageFormat, pageIndex) -> {
                    if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
    
                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
    
                    int y = 50;
                    for (String line : content.toString().split("\n")) {
                        g2d.drawString(line, 50, y);
                        y += g2d.getFontMetrics().getHeight();
                    }
                    return Printable.PAGE_EXISTS;
                });
    
                if (job.printDialog()) {
                    job.print();
                    JOptionPane.showMessageDialog(this, "Supplier Ledger sent to printer successfully!", "Print Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Print Error: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void deleteSupplierEntry() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this entry?",
                    "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                supplierTableModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a supplier entry to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
        
    private void addSupplierEntry() {
        JTextField billNoField = new JTextField();
        JTextField dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        JTextField remarksField = new JTextField();
        JTextField debitField = new JTextField();
        JTextField creditField = new JTextField();
        JTextField balanceField = new JTextField();
    
        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("V No/Bill No:")); panel.add(billNoField);
        panel.add(new JLabel("Date:")); panel.add(dateField);
        panel.add(new JLabel("Remarks:")); panel.add(remarksField);
        panel.add(new JLabel("Debit:")); panel.add(debitField);
        panel.add(new JLabel("Credit:")); panel.add(creditField);
        panel.add(new JLabel("Balance:")); panel.add(balanceField);
    
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier Entry", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            supplierTableModel.addRow(new Object[]{
                billNoField.getText(),
                dateField.getText(),
                remarksField.getText(),
                debitField.getText(),
                creditField.getText(),
                balanceField.getText()
            });
        }
    }
    private void showSupplierLedgerPanel() {
        inventoryPanel.setVisible(false);
        ledgerPanel.setVisible(false);
        customerDetailPanel.setVisible(false);
        supplierLedgerPanel.setVisible(true);
    }
    private void initializeSalesAnalysisPanel() {
    salesAnalysisPanel = new JPanel();
    salesAnalysisPanel.setLayout(null);
    salesAnalysisPanel.setBounds(260, 100, 900, 500);
    salesAnalysisPanel.setBackground(new Color(0, 0, 0, 200));

    // Date Picker
    datePicker = new JXDatePicker(new Date());
    datePicker.setBounds(20, 20, 150, 25);
    datePicker.addActionListener(e -> updateSalesData());
    salesAnalysisPanel.add(datePicker);

    // Add Entry Button
    JButton addEntryButton = new JButton("Add Entry");
    addEntryButton.setBounds(180, 20, 120, 25);
    addEntryButton.addActionListener(e -> addDailyEntry());
    salesAnalysisPanel.add(addEntryButton);

    // Daily Entries Table
    dailyEntriesModel = new DefaultTableModel(new String[]{"Date", "Sales", "Expenses", "Profit"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    dailyEntriesTable = new JTable(dailyEntriesModel);
    JScrollPane scrollPane = new JScrollPane(dailyEntriesTable);
    scrollPane.setBounds(20, 60, 860, 150);
    salesAnalysisPanel.add(scrollPane);

    // Summary Labels
    JLabel summaryLabel = new JLabel("Summary:");
    summaryLabel.setForeground(Color.WHITE);
    summaryLabel.setBounds(20, 220, 100, 25);
    salesAnalysisPanel.add(summaryLabel);

    dailyTotalLabel = createSummaryLabel("Today: $0.00 | Profit: $0.00", 20, 250);
    monthlyTotalLabel = createSummaryLabel("This Month: $0.00 | Profit: $0.00", 20, 280);
    yearlyTotalLabel = createSummaryLabel("This Year: $0.00 | Profit: $0.00", 20, 310);

    // Initialize Charts
    createSalesChart();
    createProfitChart();
    
    salesChartPanel = new ChartPanel(salesChart);
    salesChartPanel.setBounds(250, 220, 300, 250);
    salesAnalysisPanel.add(salesChartPanel);

    profitChartPanel = new ChartPanel(profitChart);
    profitChartPanel.setBounds(570, 220, 300, 250);
    salesAnalysisPanel.add(profitChartPanel);

    getLayeredPane().add(salesAnalysisPanel, Integer.valueOf(4));
    salesAnalysisPanel.setVisible(false);
}

private JLabel createSummaryLabel(String text, int x, int y) {
    JLabel label = new JLabel(text);
    label.setForeground(Color.WHITE);
    label.setBounds(x, y, 400, 25);
    salesAnalysisPanel.add(label);
    return label;
}

// Add new daily entry
private void addDailyEntry() {
    JTextField salesField = new JTextField();
    JTextField expensesField = new JTextField();

    JPanel panel = new JPanel(new GridLayout(2, 2));
    panel.add(new JLabel("Total Sales:"));
    panel.add(salesField);
    panel.add(new JLabel("Total Expenses:"));
    panel.add(expensesField);

    int result = JOptionPane.showConfirmDialog(this, panel, "Add Daily Entry", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        try {
            double sales = Double.parseDouble(salesField.getText());
            double expenses = Double.parseDouble(expensesField.getText());
            double profit = sales - expenses;
            
            String dateStr = new SimpleDateFormat("dd-MM-yyyy").format(datePicker.getDate());
            dailyEntriesModel.addRow(new Object[]{
                dateStr, 
                String.format("$%.2f", sales),
                String.format("$%.2f", expenses),
                String.format("$%.2f", profit)
            });
            
            updateSalesData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// Update all sales data and charts
private void updateSalesData() {
    Date selectedDate = datePicker.getDate();
    Calendar cal = Calendar.getInstance();
    cal.setTime(selectedDate);
    
    // Calculate daily totals
    double dailySales = 0;
    double dailyExpenses = 0;
    String dateStr = new SimpleDateFormat("dd-MM-yyyy").format(selectedDate);
    
    for (int i = 0; i < dailyEntriesModel.getRowCount(); i++) {
        if (dailyEntriesModel.getValueAt(i, 0).equals(dateStr)) {
            dailySales += Double.parseDouble(dailyEntriesModel.getValueAt(i, 1).toString().replace("$", ""));
            dailyExpenses += Double.parseDouble(dailyEntriesModel.getValueAt(i, 2).toString().replace("$", ""));
        }
    }
    
    double dailyProfit = dailySales - dailyExpenses;
    dailyTotalLabel.setText(String.format("Today: $%.2f | Profit: $%.2f", dailySales, dailyProfit));
    
    // Calculate monthly totals
    double monthlySales = 0;
    double monthlyExpenses = 0;
    int currentMonth = cal.get(Calendar.MONTH);
    int currentYear = cal.get(Calendar.YEAR);
    
    for (int i = 0; i < dailyEntriesModel.getRowCount(); i++) {
        try {
            Date entryDate = new SimpleDateFormat("dd-MM-yyyy").parse(dailyEntriesModel.getValueAt(i, 0).toString());
            Calendar entryCal = Calendar.getInstance();
            entryCal.setTime(entryDate);
            
            if (entryCal.get(Calendar.MONTH) == currentMonth && entryCal.get(Calendar.YEAR) == currentYear) {
                monthlySales += Double.parseDouble(dailyEntriesModel.getValueAt(i, 1).toString().replace("$", ""));
                monthlyExpenses += Double.parseDouble(dailyEntriesModel.getValueAt(i, 2).toString().replace("$", ""));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    double monthlyProfit = monthlySales - monthlyExpenses;
    monthlyTotalLabel.setText(String.format("This Month: $%.2f | Profit: $%.2f", monthlySales, monthlyProfit));
    
    // Calculate yearly totals
    double yearlySales = 0;
    double yearlyExpenses = 0;
    
    for (int i = 0; i < dailyEntriesModel.getRowCount(); i++) {
        try {
            Date entryDate = new SimpleDateFormat("dd-MM-yyyy").parse(dailyEntriesModel.getValueAt(i, 0).toString());
            Calendar entryCal = Calendar.getInstance();
            entryCal.setTime(entryDate);
            
            if (entryCal.get(Calendar.YEAR) == currentYear) {
                yearlySales += Double.parseDouble(dailyEntriesModel.getValueAt(i, 1).toString().replace("$", ""));
                yearlyExpenses += Double.parseDouble(dailyEntriesModel.getValueAt(i, 2).toString().replace("$", ""));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    double yearlyProfit = yearlySales - yearlyExpenses;
    yearlyTotalLabel.setText(String.format("This Year: $%.2f | Profit: $%.2f", yearlySales, yearlyProfit));
    
    // Update charts
    updateCharts();
}

// Create and update charts
private void createSalesChart() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    // Sample data - will be updated in updateCharts()
    dataset.addValue(0, "Sales", "Jan");
    dataset.addValue(0, "Expenses", "Jan");
    
    salesChart = ChartFactory.createBarChart(
        "Sales vs Expenses", 
        "Month", 
        "Amount ($)", 
        dataset, 
        PlotOrientation.VERTICAL, 
        true, true, false);
    
    salesChart.setBackgroundPaint(new Color(0, 0, 0, 200));
    CategoryPlot plot = salesChart.getCategoryPlot();
    plot.setBackgroundPaint(new Color(0, 0, 0, 200));
    plot.setRangeGridlinePaint(Color.WHITE);
    plot.setDomainGridlinePaint(Color.WHITE);
}

private void createProfitChart() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    // Sample data - will be updated in updateCharts()
    dataset.addValue(0, "Profit", "Jan");
    
    profitChart = ChartFactory.createLineChart(
        "Profit Trend", 
        "Month", 
        "Amount ($)", 
        dataset, 
        PlotOrientation.VERTICAL, 
        true, true, false);
    
    profitChart.setBackgroundPaint(new Color(0, 0, 0, 200));
    CategoryPlot plot = profitChart.getCategoryPlot();
    plot.setBackgroundPaint(new Color(0, 0, 0, 200));
    plot.setRangeGridlinePaint(Color.WHITE);
    plot.setDomainGridlinePaint(Color.WHITE);
}

private void updateCharts() {
    DefaultCategoryDataset salesDataset = new DefaultCategoryDataset();
    DefaultCategoryDataset profitDataset = new DefaultCategoryDataset();
    
    // Group data by month
    Map<String, Double> monthlySales = new LinkedHashMap<>();
    Map<String, Double> monthlyExpenses = new LinkedHashMap<>();
    Map<String, Double> monthlyProfit = new LinkedHashMap<>();
    
    // Initialize all months
    String[] months = new DateFormatSymbols().getShortMonths();
    for (int i = 0; i < 12; i++) {
        monthlySales.put(months[i], 0.0);
        monthlyExpenses.put(months[i], 0.0);
        monthlyProfit.put(months[i], 0.0);
    }
    
    // Populate with actual data
    for (int i = 0; i < dailyEntriesModel.getRowCount(); i++) {
        try {
            Date entryDate = new SimpleDateFormat("dd-MM-yyyy").parse(dailyEntriesModel.getValueAt(i, 0).toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(entryDate);
            String month = months[cal.get(Calendar.MONTH)];
            
            double sales = Double.parseDouble(dailyEntriesModel.getValueAt(i, 1).toString().replace("$", ""));
            double expenses = Double.parseDouble(dailyEntriesModel.getValueAt(i, 2).toString().replace("$", ""));
            double profit = sales - expenses;
            
            monthlySales.put(month, monthlySales.get(month) + sales);
            monthlyExpenses.put(month, monthlyExpenses.get(month) + expenses);
            monthlyProfit.put(month, monthlyProfit.get(month) + profit);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    // Add data to datasets
    for (Map.Entry<String, Double> entry : monthlySales.entrySet()) {
        String month = entry.getKey();
        salesDataset.addValue(entry.getValue(), "Sales", month);
        salesDataset.addValue(monthlyExpenses.get(month), "Expenses", month);
        profitDataset.addValue(monthlyProfit.get(month), "Profit", month);
    }
    
    // Update charts
    ((CategoryPlot)salesChart.getPlot()).setDataset(salesDataset);
    ((CategoryPlot)profitChart.getPlot()).setDataset(profitDataset);
}


// Add this method to show the panel
private void showSalesAnalysisPanel() {
    inventoryPanel.setVisible(false);
    ledgerPanel.setVisible(false);
    customerDetailPanel.setVisible(false);
    supplierLedgerPanel.setVisible(false);
    supplierDetailPanel.setVisible(false);
    salesAnalysisPanel.setVisible(true);
    updateSalesData(); // Refresh data when shown
}
    
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard());
    }
}