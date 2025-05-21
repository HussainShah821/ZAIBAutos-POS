import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomerLedgerFrame extends JFrame {
    private JTable customerTable, purchaseTable;
    private DefaultTableModel customerTableModel, purchaseTableModel;
    private JTextField searchField;
    private JPanel mainPanel, detailPanel;
    private JLabel customerNameLabel, phoneLabel, balanceLabel;
    private int selectedCustomerRow = -1;

    public CustomerLedgerFrame() {
        setTitle("Customer Ledger");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Customer Ledger");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        searchField = new JTextField();
        searchField.setToolTipText("Search by name or phone...");
        centerPanel.add(searchField, BorderLayout.NORTH);

        customerTableModel = new DefaultTableModel(new String[]{"Name", "Phone No", "Balance"}, 0);
        customerTable = new JTable(customerTableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Add Customer");
        JButton deleteButton = new JButton("Delete Customer");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Listeners
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                filterCustomerTable(text);
            }
        });

        addButton.addActionListener(_ -> addCustomer());
        deleteButton.addActionListener(_ -> deleteCustomer());

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

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            customerTableModel.addRow(new Object[]{nameField.getText(), phoneField.getText(), "0"});
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

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        customerNameLabel = new JLabel("Customer: " + customerTableModel.getValueAt(selectedCustomerRow, 0));
        phoneLabel = new JLabel("Phone: " + customerTableModel.getValueAt(selectedCustomerRow, 1));
        balanceLabel = new JLabel("Balance: " + customerTableModel.getValueAt(selectedCustomerRow, 2));
        infoPanel.add(customerNameLabel);
        infoPanel.add(phoneLabel);
        infoPanel.add(balanceLabel);
        detailPanel.add(infoPanel, BorderLayout.NORTH);

        purchaseTableModel = new DefaultTableModel(new String[]{"Date", "Item", "Brand", "Type", "UOM", "Qty", "Price", "Total"}, 0);
        purchaseTable = new JTable(purchaseTableModel);
        JScrollPane purchaseScrollPane = new JScrollPane(purchaseTable);
        detailPanel.add(purchaseScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addPurchaseBtn = new JButton("Add Purchase");
        JButton generateBillBtn = new JButton("Generate Bill");
        JButton backBtn = new JButton("Back");

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
            mainPanel.setVisible(true);
            revalidate();
            repaint();
        });
    }

    private void addPurchase() {
        JTextField itemField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField uomField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField priceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Item:")); panel.add(itemField);
        panel.add(new JLabel("Brand:")); panel.add(brandField);
        panel.add(new JLabel("Type:")); panel.add(typeField);
        panel.add(new JLabel("UOM:")); panel.add(uomField);
        panel.add(new JLabel("Quantity:")); panel.add(qtyField);
        panel.add(new JLabel("Price:")); panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Purchase", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                double price = Double.parseDouble(priceField.getText());
                double total = qty * price;
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                purchaseTableModel.addRow(new Object[]{
                        date, itemField.getText(), brandField.getText(), typeField.getText(),
                        uomField.getText(), qty, price, total
                });

                double currentBalance = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 2).toString());
                double updatedBalance = currentBalance + total;
                customerTableModel.setValueAt(updatedBalance, selectedCustomerRow, 2);
                balanceLabel.setText("Balance: " + updatedBalance);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
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
            double balance = total - paid;

            double currentBalance = Double.parseDouble(customerTableModel.getValueAt(selectedCustomerRow, 2).toString());
            double newBalance = currentBalance - paid;
            customerTableModel.setValueAt(newBalance, selectedCustomerRow, 2);
            balanceLabel.setText("Balance: " + newBalance);

            String customerName = customerTableModel.getValueAt(selectedCustomerRow, 0).toString();
            String phone = customerTableModel.getValueAt(selectedCustomerRow, 1).toString();
            String voucherNo = "INV-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String dateStr = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());

            BillPanel billPanel = new BillPanel(customerName, phone, voucherNo, dateStr,
                    purchaseTableModel, total, paid, balance);

            JScrollPane scrollPane = new JScrollPane(billPanel);
            scrollPane.setPreferredSize(new Dimension(650, 700));

            JOptionPane.showMessageDialog(this, scrollPane, "Generated Bill", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid amount paid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerLedgerFrame::new);
    }
}
