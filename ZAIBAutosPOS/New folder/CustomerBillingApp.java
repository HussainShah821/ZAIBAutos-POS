import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;

public class CustomerBillingApp extends JFrame {
    private JPanel drawerPanel, mainPanel, ledgerPanel;
    private JTable customerTable, billTable;
    private DefaultTableModel customerTableModel, billTableModel;
    private JTextField customerNameField, customerPhoneField;
    private JTextField totalPriceField, amountPaidField, remainingBalanceField;
    private JDialog billDialog;
    
    public CustomerBillingApp() {
        setTitle("Customer Billing System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Drawer Panel
        drawerPanel = new JPanel();
        drawerPanel.setLayout(new BorderLayout());
        drawerPanel.setBackground(Color.LIGHT_GRAY);
        drawerPanel.setBounds(-150, 0, 150, getHeight());
        
        JButton openLedgerButton = new JButton("Open Ledger");
        openLedgerButton.addActionListener(e -> showLedgerPanel());
        drawerPanel.add(openLedgerButton, BorderLayout.CENTER);

        // Mouse Hover for Drawer
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getX() < 20) {
                    drawerPanel.setBounds(0, 0, 150, getHeight());
                } else if (e.getX() > 150) {
                    drawerPanel.setBounds(-150, 0, 150, getHeight());
                }
            }
        });

        // Main Panel
        mainPanel = new JPanel(null);
        mainPanel.setBounds(0, 0, getWidth(), getHeight());
        
        // Customer Ledger Panel (Hidden by Default)
        ledgerPanel = new JPanel(new BorderLayout());
        ledgerPanel.setBounds(160, 50, 600, 300);
        ledgerPanel.setVisible(false);
        
        customerTableModel = new DefaultTableModel(new String[]{"Customer Name", "Phone"}, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = customerTable.getSelectedRow();
                if (row != -1) {
                    String name = (String) customerTableModel.getValueAt(row, 0);
                    String phone = (String) customerTableModel.getValueAt(row, 1);
                    openBillingWindow(name, phone);
                }
            }
        });

        ledgerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        
        JPanel ledgerInputPanel = new JPanel();
        ledgerInputPanel.setLayout(new GridLayout(1, 3));
        customerNameField = new JTextField();
        customerPhoneField = new JTextField();
        JButton addCustomerButton = new JButton("Add Customer");
        addCustomerButton.addActionListener(e -> addCustomer());
        
        ledgerInputPanel.add(new JLabel("Name:"));
        ledgerInputPanel.add(customerNameField);
        ledgerInputPanel.add(new JLabel("Phone:"));
        ledgerInputPanel.add(customerPhoneField);
        ledgerInputPanel.add(addCustomerButton);
        
        ledgerPanel.add(ledgerInputPanel, BorderLayout.SOUTH);
        
        add(drawerPanel);
        add(mainPanel);
        add(ledgerPanel);
        setVisible(true);
    }

    private void showLedgerPanel() {
        ledgerPanel.setVisible(!ledgerPanel.isVisible());
    }

    private void addCustomer() {
        String name = customerNameField.getText();
        String phone = customerPhoneField.getText();
        if (!name.isEmpty() && !phone.isEmpty()) {
            customerTableModel.addRow(new Object[]{name, phone});
            customerNameField.setText("");
            customerPhoneField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Enter Name and Phone!");
        }
    }

    private void openBillingWindow(String customerName, String customerPhone) {
        billDialog = new JDialog(this, "Create Bill - " + customerName, true);
        billDialog.setSize(600, 400);
        billDialog.setLayout(new BorderLayout());

        billTableModel = new DefaultTableModel(new String[]{"Product", "Quantity", "Price", "Total"}, 0);
        billTable = new JTable(billTableModel);

        JPanel billInputPanel = new JPanel(new GridLayout(1, 4));
        JTextField productField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JButton addItemButton = new JButton("Add Item");

        addItemButton.addActionListener(e -> {
            try {
                String product = productField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());
                double total = quantity * price;
                billTableModel.addRow(new Object[]{product, quantity, price, total});
                updateBillSummary();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(billDialog, "Invalid Input!");
            }
        });

        billInputPanel.add(new JLabel("Product:"));
        billInputPanel.add(productField);
        billInputPanel.add(new JLabel("Quantity:"));
        billInputPanel.add(quantityField);
        billInputPanel.add(new JLabel("Price:"));
        billInputPanel.add(priceField);
        billInputPanel.add(addItemButton);

        billDialog.add(new JScrollPane(billTable), BorderLayout.CENTER);
        billDialog.add(billInputPanel, BorderLayout.NORTH);

        JPanel billSummaryPanel = new JPanel(new GridLayout(1, 3));
        totalPriceField = new JTextField();
        amountPaidField = new JTextField();
        remainingBalanceField = new JTextField();
        
        amountPaidField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateRemainingBalance();
            }
        });

        billSummaryPanel.add(new JLabel("Total Price:"));
        billSummaryPanel.add(totalPriceField);
        billSummaryPanel.add(new JLabel("Amount Paid:"));
        billSummaryPanel.add(amountPaidField);
        billSummaryPanel.add(new JLabel("Remaining:"));
        billSummaryPanel.add(remainingBalanceField);

        JButton printButton = new JButton("Print Bill");
        printButton.addActionListener(e -> printBill());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(billSummaryPanel, BorderLayout.CENTER);
        bottomPanel.add(printButton, BorderLayout.SOUTH);

        billDialog.add(bottomPanel, BorderLayout.SOUTH);
        billDialog.setVisible(true);
    }

    private void updateBillSummary() {
        double totalPrice = 0;
        for (int i = 0; i < billTableModel.getRowCount(); i++) {
            totalPrice += (double) billTableModel.getValueAt(i, 3);
        }
        totalPriceField.setText(String.valueOf(totalPrice));
        updateRemainingBalance();
    }

    private void updateRemainingBalance() {
        try {
            double totalPrice = Double.parseDouble(totalPriceField.getText());
            double amountPaid = Double.parseDouble(amountPaidField.getText());
            double remaining = totalPrice - amountPaid;
            remainingBalanceField.setText(String.valueOf(remaining));
        } catch (NumberFormatException ignored) {
        }
    }

    private void printBill() {
        try {
            billTable.print();
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(billDialog, "Print Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerBillingApp::new);
    }
}