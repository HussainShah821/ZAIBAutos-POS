import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

public class ExpenditureTrackingFrame extends JFrame {
    private DefaultTableModel expenseTableModel;
    private JTable expenseTable;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/zaibautos";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public ExpenditureTrackingFrame() {
        setTitle("Zaib Autos - Expenditure Tracking");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 5));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Expenditure Tracking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputTablePanel = new JPanel(new GridBagLayout());
        inputTablePanel.setBackground(Color.WHITE);

        JPanel formPanel = createFormPanel();
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                BorderFactory.createLineBorder(new Color(200, 200, 200))
        ));
        formPanel.setPreferredSize(new Dimension(230, 400));
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.gridx = 0;
        gbcForm.gridy = 0;
        gbcForm.weightx = 0.4;
        gbcForm.fill = GridBagConstraints.BOTH;
        inputTablePanel.add(formPanel, gbcForm);

        expenseTableModel = new DefaultTableModel(new String[]{
                "Date", "Description", "Amount", "Payment Type"
        }, 0);
        expenseTable = new JTable(expenseTableModel);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        expenseTable.setRowHeight(25);
        expenseTable.setGridColor(new Color(230, 230, 230));
        expenseTable.setShowGrid(true);
        expenseTable.setIntercellSpacing(new Dimension(1, 1));
        expenseTable.setSelectionBackground(new Color(0, 120, 215));
        expenseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        expenseTable.getTableHeader().setBackground(new Color(240, 240, 240));
        JScrollPane tableScrollPane = new JScrollPane(expenseTable);
        tableScrollPane.setPreferredSize(new Dimension(500, 400));
        GridBagConstraints gbcTable = new GridBagConstraints();
        gbcTable.gridx = 1;
        gbcTable.gridy = 0;
        gbcTable.weightx = 0.6;
        gbcTable.fill = GridBagConstraints.BOTH;
        inputTablePanel.add(tableScrollPane, gbcTable);

        mainPanel.add(inputTablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        loadExpenses();

        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadExpenses() {
        expenseTableModel.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT expenditure_date, description, amount, payment_type FROM expenditures")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            while (rs.next()) {
                expenseTableModel.addRow(new Object[]{
                        sdf.format(rs.getDate("expenditure_date")),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("payment_type")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 100, 200), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setPreferredSize(new Dimension(120, 30));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 140, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 120, 215));
            }
        });
        return button;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(dateLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        JTextField dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateField.setPreferredSize(new Dimension(120, 25));
        panel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(descriptionLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JTextField descriptionField = new JTextField();
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionField.setPreferredSize(new Dimension(120, 25));
        panel.add(descriptionField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(amountLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JTextField amountField = new JTextField("0");
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        amountField.setPreferredSize(new Dimension(120, 25));
        panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel paymentTypeLabel = new JLabel("Payment Type:");
        paymentTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(paymentTypeLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        JComboBox<String> paymentTypeCombo = new JComboBox<>(new String[]{"Cash", "Jazzcash", "Bank"});
        paymentTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paymentTypeCombo.setPreferredSize(new Dimension(120, 25));
        panel.add(paymentTypeCombo, gbc);

        return panel;
    }

    private void addExpense() {
        JTextField dateField = (JTextField) ((JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(0)).getComponent(1);
        JTextField descriptionField = (JTextField) ((JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(0)).getComponent(3);
        JTextField amountField = (JTextField) ((JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(0)).getComponent(5);
        JComboBox<String> paymentTypeCombo = (JComboBox<String>) ((JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(0)).getComponent(7);

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
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            Date parsedDate;
            try {
                parsedDate = sdf.parse(date);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO expenditures (expenditure_date, description, amount, payment_type) VALUES (?, ?, ?, ?)",
                         Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setDate(1, new java.sql.Date(parsedDate.getTime()));
                pstmt.setString(2, description);
                pstmt.setDouble(3, amount);
                pstmt.setString(4, (String) paymentTypeCombo.getSelectedItem());
                pstmt.executeUpdate();

                expenseTableModel.addRow(new Object[]{
                        date,
                        description,
                        amount,
                        (String) paymentTypeCombo.getSelectedItem()
                });

                clearForm();
                JOptionPane.showMessageDialog(this, "Expense added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                             "DELETE FROM expenditures WHERE expenditure_date = ? AND description = ? AND amount = ? AND payment_type = ?")) {
                    String dateStr = (String) expenseTableModel.getValueAt(selectedRow, 0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date parsedDate = sdf.parse(dateStr);
                    pstmt.setDate(1, new java.sql.Date(parsedDate.getTime()));
                    pstmt.setString(2, (String) expenseTableModel.getValueAt(selectedRow, 1));
                    pstmt.setDouble(3, (Double) expenseTableModel.getValueAt(selectedRow, 2));
                    pstmt.setString(4, (String) expenseTableModel.getValueAt(selectedRow, 3));
                    pstmt.executeUpdate();

                    expenseTableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Expense deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting expense: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Error parsing date: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        JTextField dateField = (JTextField) ((JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(0)).getComponent(1);
        JTextField descriptionField = (JTextField) ((JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(0)).getComponent(3);
        JTextField amountField = (JTextField) ((JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(0)).getComponent(5);
        JComboBox<String> paymentTypeCombo = (JComboBox<String>) ((JPanel) ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponent(0)).getComponent(7);

        dateField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        descriptionField.setText("");
        amountField.setText("0");
        paymentTypeCombo.setSelectedIndex(0);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Add");
        JButton deleteButton = createStyledButton("Delete");
        JButton clearButton = createStyledButton("Clear");
        JButton dailyReportButton = createStyledButton("Daily Report");
        JButton monthlyReportButton = createStyledButton("Monthly Report");
        JButton quarterlyReportButton = createStyledButton("Quarterly Report");

        addButton.addActionListener(e -> addExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        clearButton.addActionListener(e -> clearForm());
        dailyReportButton.addActionListener(e -> generateDailyReport());
        monthlyReportButton.addActionListener(e -> generateMonthlyReport());
        quarterlyReportButton.addActionListener(e -> generateQuarterlyReport());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(dailyReportButton);
        buttonPanel.add(monthlyReportButton);
        buttonPanel.add(quarterlyReportButton);

        return buttonPanel;
    }

    private void generateDailyReport() {
        JTextField reportDateField = new JTextField("dd-MM-yyyy");
        reportDateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reportDateField.setForeground(Color.GRAY);
        reportDateField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        reportDateField.setToolTipText("Enter date in dd-MM-yyyy format (e.g., 21-05-2025) or double-click for today");
        reportDateField.setPreferredSize(new Dimension(150, 25));
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
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.add(new JLabel("Select Date:"));
        panel.add(reportDateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Daily Expenditure Report", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedDate = reportDateField.getText().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        Date parsedDate;
        try {
            parsedDate = sdf.parse(selectedDate);
            if (parsedDate.after(new Date())) {
                JOptionPane.showMessageDialog(this, "Cannot select a future date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy (e.g., 21-05-2025).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalExpenditure = 0;
        StringBuilder report = new StringBuilder();
        report.append("Daily Expenditure Report (").append(selectedDate).append(")\n\n");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT expenditure_date, description, amount, payment_type FROM expenditures WHERE expenditure_date = ?")) {
            pstmt.setDate(1, new java.sql.Date(parsedDate.getTime()));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double amount = rs.getDouble("amount");
                totalExpenditure += amount;
                report.append(String.format("Date: %s, Amount: %.2f, Payment: %s, Desc: %s\n",
                        sdf.format(rs.getDate("expenditure_date")),
                        amount,
                        rs.getString("payment_type"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        report.append(String.format("\nTotal Expenditure: %.2f", totalExpenditure));

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
        monthCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        monthCombo.setPreferredSize(new Dimension(200, 25));
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
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

        double totalExpenditure = 0;
        StringBuilder report = new StringBuilder();
        report.append("Monthly Expenditure Report (").append(selectedMonthYear).append(")\n\n");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT expenditure_date, description, amount, payment_type FROM expenditures WHERE YEAR(expenditure_date) = ? AND MONTH(expenditure_date) = ?")) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, monthNumber);
            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            while (rs.next()) {
                double amount = rs.getDouble("amount");
                totalExpenditure += amount;
                report.append(String.format("Date: %s, Amount: %.2f, Payment: %s, Desc: %s\n",
                        sdf.format(rs.getDate("expenditure_date")),
                        amount,
                        rs.getString("payment_type"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        report.append(String.format("\nTotal Expenditure: %.2f", totalExpenditure));

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
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
        halfYearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        halfYearCombo.setPreferredSize(new Dimension(200, 25));
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
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

        double totalExpenditure = 0;
        StringBuilder report = new StringBuilder();
        report.append("Quarterly expenditure Report (").append(selectedHalfYear).append(")\n\n");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT expenditure_date, description, amount, payment_type FROM expenditures WHERE YEAR(expenditure_date) = ? AND MONTH(expenditure_date) BETWEEN ? AND ?")) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, startMonth);
            pstmt.setInt(3, endMonth);
            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            while (rs.next()) {
                double amount = rs.getDouble("amount");
                totalExpenditure += amount;
                report.append(String.format("Date: %s, Amount: %.2f, Payment: %s, Desc: %s\n",
                        sdf.format(rs.getDate("expenditure_date")),
                        amount,
                        rs.getString("payment_type"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        report.append(String.format("\nTotal Expenditure: %.2f", totalExpenditure));

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reportArea.setEditable(false);
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, reportScroll, "Quarterly Expenditure Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenditureTrackingFrame::new);
    }
}