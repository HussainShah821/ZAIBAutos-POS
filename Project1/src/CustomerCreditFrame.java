import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CustomerCreditFrame extends JFrame {
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private List<String> customerNames = new ArrayList<>();
    private int customerCount = 0;
    // private DatabaseConnection db;

    public CustomerCreditFrame() {
        // db = new DatabaseConnection();
        setTitle("Customer Credit Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Customer Credit Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Center panel (search + table)
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        centerPanel.add(searchField, BorderLayout.NORTH);

        customerTableModel = new DefaultTableModel(new String[]{"Customer ID", "Name", "Amount Paid", "Amount Remaining", "Credit Limit"}, 0);
        customerTable = new JTable(customerTableModel);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Add Customer");
        JButton updateButton = new JButton("Update Credit");
        JButton deleteButton = new JButton("Delete Customer");
        styleButton(addButton);
        styleButton(updateButton);
        styleButton(deleteButton);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel);

        // Listeners
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

        loadDummyData();
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 105, 217));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255));
            }
        });
    }

    private void sortCustomerToTop(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            resetCustomerTableOrder();
            return;
        }

        List<Vector<Object>> matched = new ArrayList<>();
        List<Vector<Object>> unmatched = new ArrayList<>();

        for (int i = 0; i < customerTableModel.getRowCount(); i++) {
            @SuppressWarnings("unchecked")
            Vector<Object> row = (Vector<Object>) customerTableModel.getDataVector().get(i);
            if (row.get(1).toString().toLowerCase().contains(customerName.toLowerCase())) {
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

    private void loadDummyData() {
        customerTableModel.setRowCount(0);
        // Dummy data
        Object[][] dummyCustomers = {
                {++customerCount, "Ali Khan", 5000.0, 2000.0, 10000.0},
                {++customerCount, "Sara Ahmed", 3000.0, 500.0, 8000.0},
                {++customerCount, "Bilal Raza", 7000.0, 0.0, 15000.0}
        };
        for (Object[] customer : dummyCustomers) {
            customerTableModel.addRow(customer);
            customerNames.add(customer[1].toString());
        }
    }

    private void addCustomer() {
        JTextField nameField = new JTextField();
        JTextField amountPaidField = new JTextField();
        JTextField amountRemainingField = new JTextField();
        JTextField creditLimitField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Amount Paid:")); panel.add(amountPaidField);
        panel.add(new JLabel("Amount Remaining:")); panel.add(amountRemainingField);
        panel.add(new JLabel("Credit Limit:")); panel.add(creditLimitField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                double amountPaid = Double.parseDouble(amountPaidField.getText());
                double amountRemaining = Double.parseDouble(amountRemainingField.getText());
                double creditLimit = Double.parseDouble(creditLimitField.getText());

                // String query = "INSERT INTO Customers (name, amount_paid, amount_remaining, credit_limit) VALUES (?, ?, ?, ?)";
                // PreparedStatement pstmt = db.getConnection().prepareStatement(query);
                // pstmt.setString(1, name);
                // pstmt.setDouble(2, amountPaid);
                // pstmt.setDouble(3, amountRemaining);
                // pstmt.setDouble(4, creditLimit);
                // pstmt.executeUpdate();

                customerCount++;
                customerTableModel.addRow(new Object[]{customerCount, name, amountPaid, amountRemaining, creditLimit});
                customerNames.add(name);
                resetCustomerTableOrder();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for amounts");
            }
        }
    }

    private void updateCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
            String currentPaid = customerTableModel.getValueAt(selectedRow, 2).toString();
            String currentRemaining = customerTableModel.getValueAt(selectedRow, 3).toString();

            JTextField amountPaidField = new JTextField(currentPaid);
            JTextField amountRemainingField = new JTextField(currentRemaining);

            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Amount Paid:")); panel.add(amountPaidField);
            panel.add(new JLabel("Amount Remaining:")); panel.add(amountRemainingField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Credit", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double amountPaid = Double.parseDouble(amountPaidField.getText());
                    double amountRemaining = Double.parseDouble(amountRemainingField.getText());

                    // String query = "UPDATE Customers SET amount_paid = ?, amount_remaining = ? WHERE customer_id = ?";
                    // PreparedStatement pstmt = db.getConnection().prepareStatement(query);
                    // pstmt.setDouble(1, amountPaid);
                    // pstmt.setDouble(2, amountRemaining);
                    // pstmt.setInt(3, getCustomerIdFromDB(customerId));
                    // pstmt.executeUpdate();

                    customerTableModel.setValueAt(amountPaid, selectedRow, 2);
                    customerTableModel.setValueAt(amountRemaining, selectedRow, 3);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers for amounts");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // String query = "DELETE FROM Customers WHERE customer_id = ?";
                // PreparedStatement pstmt = db.getConnection().prepareStatement(query);
                // pstmt.setInt(1, getCustomerIdFromDB(customerId));
                // pstmt.executeUpdate();

                String customerName = customerTableModel.getValueAt(selectedRow, 1).toString();
                customerTableModel.removeRow(selectedRow);
                customerNames.remove(customerName);
                resetCustomerTableOrder();
                customerCount = customerTableModel.getRowCount();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // private int getCustomerIdFromDB(int displayId) throws SQLException {
    //     String query = "SELECT customer_id FROM Customers LIMIT 1 OFFSET ?";
    //     PreparedStatement pstmt = db.getConnection().prepareStatement(query);
    //     pstmt.setInt(1, displayId - 1);
    //     ResultSet rs = pstmt.executeQuery();
    //     if (rs.next()) {
    //         return rs.getInt("customer_id");
    //     }
    //     return -1;
    // }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerCreditFrame::new);
    }
}