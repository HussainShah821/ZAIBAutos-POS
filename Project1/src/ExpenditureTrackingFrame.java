import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class ExpenditureTrackingFrame extends JFrame {
    private DefaultTableModel expenseTableModel;
    private JTable expenseTable;
    private static int nextExpenseId = 1;

    public ExpenditureTrackingFrame() {
        setTitle("Zaib Autos - Expenditure Tracking");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Expenditure Tracking");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.WEST);

        expenseTableModel = new DefaultTableModel(new String[]{
                "Expense ID", "Date", "Category", "Supplier", "Description", "Amount", "Payment Type"
        }, 0);
        expenseTable = new JTable(expenseTableModel);
        expenseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        expenseTable.setRowHeight(25);
        expenseTable.setGridColor(new Color(200, 200, 200));
        expenseTable.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addExpenseButton = createStyledButton("Add Expense");
        JButton deleteExpenseButton = createStyledButton("Delete Expense");
        JButton clearButton = createStyledButton("Clear");
        JButton generateReportButton = createStyledButton("Generate Report");
        JButton backButton = createStyledButton("Back");

        addExpenseButton.addActionListener(e -> addExpense());
        deleteExpenseButton.addActionListener(e -> deleteExpense());
        clearButton.addActionListener(e -> clearForm());
        generateReportButton.addActionListener(e -> generateReport());
        backButton.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        buttonPanel.add(addExpenseButton);
        buttonPanel.add(deleteExpenseButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(generateReportButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Populate table with existing expenditures
        DataManager dataManager = DataManager.getInstance();
        for (DataManager.Expenditure expenditure : dataManager.getExpenditures()) {
            expenseTableModel.addRow(new Object[]{
                    "EXP-" + expenditure.id,
                    expenditure.date,
                    expenditure.category,
                    expenditure.supplier != null ? expenditure.supplier.name : "N/A",
                    expenditure.description,
                    expenditure.amount,
                    expenditure.paymentType
            });
        }

        /*
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
            String sql = "SELECT e.expense_id, e.expense_date, e.category, s.supplier_name, e.description, e.amount, e.payment_type " +
                         "FROM expenditures e LEFT JOIN suppliers s ON e.supplier_id = s.supplier_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            expenseTableModel.setRowCount(0);
            while (rs.next()) {
                expenseTableModel.addRow(new Object[]{
                    "EXP-" + rs.getInt("expense_id"),
                    rs.getString("expense_date"),
                    rs.getString("category"),
                    rs.getString("supplier_name") != null ? rs.getString("supplier_name") : "N/A",
                    rs.getString("description"),
                    rs.getDouble("amount"),
                    rs.getString("payment_type")
                });
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        */

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
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Expense ID:"));
        JTextField expenseIdField = new JTextField(String.format("EXP-%03d", nextExpenseId));
        expenseIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        expenseIdField.setEditable(false);
        panel.add(expenseIdField);

        panel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(dateField);

        panel.add(new JLabel("Category:"));
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Operational", "Supplier Payment", "Miscellaneous"});
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(categoryCombo);

        panel.add(new JLabel("Supplier:"));
        DataManager dataManager = DataManager.getInstance();
        JComboBox<DataManager.Supplier> supplierCombo = new JComboBox<>();
        supplierCombo.addItem(null); // Allow no supplier
        for (DataManager.Supplier supplier : dataManager.getSuppliers()) {
            supplierCombo.addItem(supplier);
        }
        supplierCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(supplierCombo);

        panel.add(new JLabel("Description:"));
        JTextField descriptionField = new JTextField();
        descriptionField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(descriptionField);

        panel.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField("0");
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(amountField);

        panel.add(new JLabel("Payment Type:"));
        JComboBox<String> paymentTypeCombo = new JComboBox<>(new String[]{"Cash", "Jazzcash", "Bank"});
        paymentTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(paymentTypeCombo);

        return panel;
    }

    private void addExpense() {
        JTextField expenseIdField = (JTextField) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(1);
        JTextField dateField = (JTextField) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(3);
        JComboBox<String> categoryCombo = (JComboBox<String>) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(5);
        JComboBox<DataManager.Supplier> supplierCombo = (JComboBox<DataManager.Supplier>) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(7);
        JTextField descriptionField = (JTextField) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(9);
        JTextField amountField = (JTextField) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(11);
        JComboBox<String> paymentTypeCombo = (JComboBox<String>) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(13);

        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Description is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String date = dateField.getText().trim();
            // Basic date validation
            try {
                new SimpleDateFormat("dd-MM-yyyy").parse(date);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DataManager dataManager = DataManager.getInstance();
            DataManager.Expenditure expenditure = new DataManager.Expenditure(
                    nextExpenseId,
                    date,
                    (String) categoryCombo.getSelectedItem(),
                    (DataManager.Supplier) supplierCombo.getSelectedItem(),
                    description,
                    amount,
                    (String) paymentTypeCombo.getSelectedItem()
            );
            dataManager.addExpenditure(expenditure);

            expenseTableModel.addRow(new Object[]{
                    expenseIdField.getText(),
                    expenditure.date,
                    expenditure.category,
                    expenditure.supplier != null ? expenditure.supplier.name : "N/A",
                    expenditure.description,
                    expenditure.amount,
                    expenditure.paymentType
            });

            // Update supplier ledger if supplier payment
            if (expenditure.supplier != null && expenditure.category.equals("Supplier Payment")) {
                // Simulate adding to SupplierLedgerFrame's ledger
                int supplierIndex = dataManager.getSuppliers().indexOf(expenditure.supplier);
                if (supplierIndex != -1) {
                    // Note: Cannot directly modify SupplierLedgerFrame's ledgerModel as it's instance-specific
                    // Instead, store in DataManager or notify SupplierLedgerFrame to refresh
                    // For simplicity, we'll add to a hypothetical shared ledger in DataManager (future enhancement)
                    // For now, show a message indicating the update
                    JOptionPane.showMessageDialog(this, "Supplier payment recorded. Please check Supplier Ledger for " + expenditure.supplier.name, "Info", JOptionPane.INFORMATION_MESSAGE);

                    /*
                    try {
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
                        String sql = "INSERT INTO supplier_ledger (supplier_id, bill_no, entry_date, remarks, debit, credit, balance) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, expenditure.supplier.id);
                        stmt.setString(2, "EXP-" + nextExpenseId);
                        stmt.setString(3, expenditure.date);
                        stmt.setString(4, "Payment for " + expenditure.description);
                        stmt.setDouble(5, 0);
                        stmt.setDouble(6, expenditure.amount);
                        stmt.setDouble(7, 0); // Balance calculation requires previous balance
                        stmt.executeUpdate();
                        stmt.close();
                        conn.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    */
                }
            }

            nextExpenseId++;
            expenseIdField.setText(String.format("EXP-%03d", nextExpenseId));
            clearForm();

            /*
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
                String sql = "INSERT INTO expenditures (expense_date, category, supplier_id, description, amount, payment_type) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, expenditure.date);
                stmt.setString(2, expenditure.category);
                stmt.setObject(3, expenditure.supplier != null ? expenditure.supplier.id : null);
                stmt.setString(4, expenditure.description);
                stmt.setDouble(5, expenditure.amount);
                stmt.setString(6, expenditure.paymentType);
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            */

            JOptionPane.showMessageDialog(this, "Expense added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DataManager dataManager = DataManager.getInstance();
                dataManager.removeExpenditure(dataManager.getExpenditures().get(selectedRow));
                expenseTableModel.removeRow(selectedRow);

                /*
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
                    String sql = "DELETE FROM expenditures WHERE expense_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    String expenseIdStr = (String) expenseTableModel.getValueAt(selectedRow, 0);
                    int expenseId = Integer.parseInt(expenseIdStr.replace("EXP-", ""));
                    stmt.setInt(1, expenseId);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                */

                JOptionPane.showMessageDialog(this, "Expense deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        JTextField dateField = (JTextField) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(3);
        JComboBox<String> categoryCombo = (JComboBox<String>) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(5);
        JComboBox<DataManager.Supplier> supplierCombo = (JComboBox<DataManager.Supplier>) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(7);
        JTextField descriptionField = (JTextField) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(9);
        JTextField amountField = (JTextField) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(11);
        JComboBox<String> paymentTypeCombo = (JComboBox<String>) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(13);

        dateField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        categoryCombo.setSelectedIndex(0);
        supplierCombo.setSelectedIndex(0);
        descriptionField.setText("");
        amountField.setText("0");
        paymentTypeCombo.setSelectedIndex(0);
    }

    private void generateReport() {
        DataManager dataManager = DataManager.getInstance();
        Map<String, Double> categoryTotals = new HashMap<>();
        double grandTotal = 0;

        for (DataManager.Expenditure expenditure : dataManager.getExpenditures()) {
            categoryTotals.merge(expenditure.category, expenditure.amount, Double::sum);
            grandTotal += expenditure.amount;
        }

        StringBuilder report = new StringBuilder();
        report.append("Expense Report (as of ").append(new SimpleDateFormat("dd-MM-yyyy").format(new Date())).append(")\n\n");
        report.append("Category Totals:\n");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            report.append(String.format("%s: %.2f\n", entry.getKey(), entry.getValue()));
        }
        report.append(String.format("\nGrand Total: %.2f", grandTotal));

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
        reportArea.setEditable(false);
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, reportScroll, "Expense Report", JOptionPane.INFORMATION_MESSAGE);

        // Future enhancement: Export to PDF similar to BillPanel
        /*
        try {
            // Use iText to generate PDF (similar to BillPanel)
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        */
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenditureTrackingFrame::new);
    }
}