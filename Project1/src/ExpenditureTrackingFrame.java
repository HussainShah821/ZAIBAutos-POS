import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

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

        JPanel buttonPanel = createButtonPanel();
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
                    JOptionPane.showMessageDialog(this, "Supplier payment recorded. Please check Supplier Ledger for " + expenditure.supplier.name, "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            nextExpenseId++;
            expenseIdField.setText(String.format("EXP-%03d", nextExpenseId));
            clearForm();

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

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addExpenseButton = createStyledButton("Add Expense");
        JButton deleteExpenseButton = createStyledButton("Delete Expense");
        JButton clearButton = createStyledButton("Clear");
        JButton dailyReportButton = createStyledButton("Daily Report");
        JButton monthlyReportButton = createStyledButton("Monthly Report");
        JButton quarterlyReportButton = createStyledButton("Quarterly Report");
        JButton backButton = createStyledButton("Back");

        addExpenseButton.addActionListener(e -> addExpense());
        deleteExpenseButton.addActionListener(e -> deleteExpense());
        clearButton.addActionListener(e -> clearForm());
        dailyReportButton.addActionListener(e -> generateDailyReport());
        monthlyReportButton.addActionListener(e -> generateMonthlyReport());
        quarterlyReportButton.addActionListener(e -> generateQuarterlyReport());
        backButton.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        buttonPanel.add(addExpenseButton);
        buttonPanel.add(deleteExpenseButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(dailyReportButton);
        buttonPanel.add(monthlyReportButton);
        buttonPanel.add(quarterlyReportButton);
        buttonPanel.add(backButton);

        return buttonPanel;
    }

    private void generateDailyReport() {
        JTextField reportDateField = new JTextField("dd-MM-yyyy");
        reportDateField.setFont(new Font("Arial", Font.PLAIN, 14));
        reportDateField.setForeground(Color.GRAY);
        reportDateField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        reportDateField.setToolTipText("Enter date in dd-MM-yyyy format (e.g., 21-05-2025) or double-click for today");
        reportDateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (reportDateField.getText().equals("dd-MM-yyyy")) {
                    reportDateField.setText("");
                    reportDateField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (reportDateField.getText().isEmpty()) {
                    reportDateField.setText("dd-MM-yyyy");
                    reportDateField.setForeground(Color.GRAY);
                }
            }
        });
        reportDateField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    reportDateField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                    reportDateField.setForeground(Color.BLACK);
                    reportDateField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Select Date:"));
        panel.add(reportDateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Daily Expenditure Report", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedDate = reportDateField.getText().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(selectedDate);
            if (date.after(new Date())) {
                JOptionPane.showMessageDialog(this, "Cannot select a future date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy (e.g., 21-05-2025).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DataManager dataManager = DataManager.getInstance();
        double totalExpenditure = 0;
        StringBuilder report = new StringBuilder();
        report.append("Daily Expenditure Report (").append(selectedDate).append(")\n\n");

        for (DataManager.Expenditure expenditure : dataManager.getExpenditures()) {
            if (expenditure.date.equals(selectedDate)) {
                totalExpenditure += expenditure.amount;
                report.append(String.format("ID: EXP-%d, Date: %s, Category: %s, Supplier: %s, Amount: %.2f, Payment: %s, Desc: %s\n",
                        expenditure.id, expenditure.date, expenditure.category,
                        expenditure.supplier != null ? expenditure.supplier.name : "N/A",
                        expenditure.amount, expenditure.paymentType, expenditure.description));
            }
        }
        report.append(String.format("\nTotal Expenditure: %.2f", totalExpenditure));

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
        reportArea.setEditable(false);
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, reportScroll, "Daily Expenditure Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateMonthlyReport() {
        ArrayList<String> monthYears = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int pastYears = 1;
        int futureYears = 30;

        for (int year = currentYear - pastYears; year <= currentYear + futureYears; year++) {
            for (int month = 1; month <= 12; month++) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month - 1);
                String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
                monthYears.add(String.format("%s %d", monthName, year));
            }
        }

        JComboBox<String> monthCombo = new JComboBox<>(monthYears.toArray(new String[0]));
        monthCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Select Month:"));
        panel.add(monthCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Monthly Expenditure Report", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedMonthYear = (String) monthCombo.getSelectedItem();
        String[] parts = selectedMonthYear.split(" ");
        String monthName = parts[0];
        int year = Integer.parseInt(parts[1]);

        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        int monthNumber = 0;
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(monthName)) {
                monthNumber = i + 1;
                break;
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        DataManager dataManager = DataManager.getInstance();
        double totalExpenditure = 0;
        StringBuilder report = new StringBuilder();
        report.append("Monthly Expenditure Report (").append(selectedMonthYear).append(")\n\n");

        for (DataManager.Expenditure expenditure : dataManager.getExpenditures()) {
            try {
                Date date = sdf.parse(expenditure.date);
                Calendar expenditureCal = Calendar.getInstance();
                expenditureCal.setTime(date);
                int expenditureMonth = expenditureCal.get(Calendar.MONTH) + 1;
                int expenditureYear = expenditureCal.get(Calendar.YEAR);
                if (expenditureYear == year && expenditureMonth == monthNumber) {
                    totalExpenditure += expenditure.amount;
                    report.append(String.format("ID: EXP-%d, Date: %s, Category: %s, Supplier: %s, Amount: %.2f, Payment: %s, Desc: %s\n",
                            expenditure.id, expenditure.date, expenditure.category,
                            expenditure.supplier != null ? expenditure.supplier.name : "N/A",
                            expenditure.amount, expenditure.paymentType, expenditure.description));
                }
            } catch (ParseException e) {
                // Skip invalid dates
            }
        }
        report.append(String.format("\nTotal Expenditure: %.2f", totalExpenditure));

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
        reportArea.setEditable(false);
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, reportScroll, "Monthly Expenditure Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateQuarterlyReport() {
        ArrayList<String> halfYears = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int pastYears = 1;
        int futureYears = 30;

        for (int year = currentYear - pastYears; year <= currentYear + futureYears; year++) {
            halfYears.add("1st Half " + year);
            halfYears.add("2nd Half " + year);
        }

        JComboBox<String> halfYearCombo = new JComboBox<>(halfYears.toArray(new String[0]));
        halfYearCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Select Half-Year:"));
        panel.add(halfYearCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Quarterly Expenditure Report", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedHalfYear = (String) halfYearCombo.getSelectedItem();
        String[] parts = selectedHalfYear.split(" ");
        String half = parts[0];
        int year = Integer.parseInt(parts[2]);
        int startMonth = half.equals("1st") ? 1 : 7;
        int endMonth = half.equals("1st") ? 6 : 12;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        DataManager dataManager = DataManager.getInstance();
        double totalExpenditure = 0;
        StringBuilder report = new StringBuilder();
        report.append("Quarterly Expenditure Report (").append(selectedHalfYear).append(")\n\n");

        for (DataManager.Expenditure expenditure : dataManager.getExpenditures()) {
            try {
                Date date = sdf.parse(expenditure.date);
                Calendar expenditureCal = Calendar.getInstance();
                expenditureCal.setTime(date);
                int expenditureMonth = expenditureCal.get(Calendar.MONTH) + 1;
                int expenditureYear = expenditureCal.get(Calendar.YEAR);
                if (expenditureYear == year && expenditureMonth >= startMonth && expenditureMonth <= endMonth) {
                    totalExpenditure += expenditure.amount;
                    report.append(String.format("ID: EXP-%d, Date: %s, Category: %s, Supplier: %s, Amount: %.2f, Payment: %s, Desc: %s\n",
                            expenditure.id, expenditure.date, expenditure.category,
                            expenditure.supplier != null ? expenditure.supplier.name : "N/A",
                            expenditure.amount, expenditure.paymentType, expenditure.description));
                }
            } catch (ParseException e) {
                // Skip invalid dates
            }
        }
        report.append(String.format("\nTotal Expenditure: %.2f", totalExpenditure));

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
        reportArea.setEditable(false);
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, reportScroll, "Quarterly Expenditure Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenditureTrackingFrame::new);
    }
}