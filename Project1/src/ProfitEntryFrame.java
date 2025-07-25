import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProfitEntryFrame extends JFrame {
    private DefaultTableModel profitTableModel;
    private JTable profitTable;
    private JTextField dateField, amountField, remarksField;
    private Border defaultBorder, errorBorder;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/zaibautos";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Replace with your MySQL password

    public ProfitEntryFrame() {
        setTitle("Zaib Autos - Profit Entry");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Define borders for validation feedback
        defaultBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        errorBorder = BorderFactory.createLineBorder(Color.RED, 1);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Profit Entry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.WEST);

        profitTableModel = new DefaultTableModel(new String[]{
                "Profit ID", "Date", "Amount", "Remarks"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing of table cells
            }
        };
        profitTable = new JTable(profitTableModel);
        profitTable.setFont(new Font("Arial", Font.PLAIN, 14));
        profitTable.setRowHeight(25);
        profitTable.setGridColor(new Color(200, 200, 200));
        profitTable.setShowGrid(true);
        // Hide the Profit ID column
        profitTable.getColumnModel().getColumn(0).setMinWidth(0);
        profitTable.getColumnModel().getColumn(0).setMaxWidth(0);
        profitTable.getColumnModel().getColumn(0).setWidth(0);
        JScrollPane scrollPane = new JScrollPane(profitTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Populate table with existing profits
        loadProfitData();

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setToolTipText(text);
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
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Date:"));
        dateField = new JTextField("dd-MM-yyyy");
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        dateField.setForeground(Color.GRAY);
        dateField.setBorder(defaultBorder);
        dateField.setToolTipText("Enter date in dd-MM-yyyy format (e.g., 21-05-2025) or double-click for today");
        dateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dateField.getText().equals("dd-MM-yyyy")) {
                    dateField.setText("");
                    dateField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (dateField.getText().isEmpty()) {
                    dateField.setText("dd-MM-yyyy");
                    dateField.setForeground(Color.GRAY);
                }
            }
        });
        dateField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    dateField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                    dateField.setForeground(Color.BLACK);
                    dateField.setBorder(defaultBorder);
                }
            }
        });
        panel.add(dateField);

        panel.add(new JLabel("Profit Amount:"));
        amountField = new JTextField("0");
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField.setToolTipText("Enter the profit amount (non-negative)");
        amountField.setBorder(defaultBorder);
        panel.add(amountField);

        panel.add(new JLabel("Remarks:"));
        remarksField = new JTextField();
        remarksField.setFont(new Font("Arial", Font.PLAIN, 14));
        remarksField.setToolTipText("Optional notes about this profit entry");
        remarksField.setBorder(defaultBorder);
        panel.add(remarksField);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addProfitButton = createStyledButton("Add Profit");
        JButton deleteProfitButton = createStyledButton("Delete Profit");
        JButton clearButton = createStyledButton("Clear");
        JButton dailyReportButton = createStyledButton("Daily Report");
        JButton monthlyReportButton = createStyledButton("Monthly Report");
        JButton quarterlyReportButton = createStyledButton("Quarterly Report");
        JButton yearlyReportButton = createStyledButton("Yearly Report");

        addProfitButton.addActionListener(e -> addProfit());
        deleteProfitButton.addActionListener(e -> deleteProfit());
        clearButton.addActionListener(e -> clearForm());
        dailyReportButton.addActionListener(e -> generateDailyReport());
        monthlyReportButton.addActionListener(e -> generateMonthlyReport());
        quarterlyReportButton.addActionListener(e -> generateQuarterlyReport());
        yearlyReportButton.addActionListener(e -> generateYearlyReport());

        buttonPanel.add(addProfitButton);
        buttonPanel.add(deleteProfitButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(dailyReportButton);
        buttonPanel.add(monthlyReportButton);
        buttonPanel.add(quarterlyReportButton);
        buttonPanel.add(yearlyReportButton);

        return buttonPanel;
    }

    private boolean validateDate(String dateStr, JTextField field) {
        if (dateStr.equals("dd-MM-yyyy") || dateStr.isEmpty()) {
            field.setBorder(errorBorder);
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            if (date.after(new Date())) {
                field.setBorder(errorBorder);
                return false;
            }
            field.setBorder(defaultBorder);
            return true;
        } catch (ParseException e) {
            field.setBorder(errorBorder);
            return false;
        }
    }

    private void loadProfitData() {
        profitTableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT profit_id, profit_date, amount, remarks FROM profits ORDER BY profit_date DESC")) {
            while (rs.next()) {
                profitTableModel.addRow(new Object[]{
                        rs.getInt("profit_id"), // Store profit_id
                        new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                        rs.getBigDecimal("amount"),
                        rs.getString("remarks")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading profit data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProfit() {
        try {
            String dateStr = dateField.getText().trim();
            if (!validateDate(dateStr, dateField)) {
                JOptionPane.showMessageDialog(this, "Invalid or future date. Use dd-MM-yyyy (e.g., 21-05-2025).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount < 0) {
                amountField.setBorder(errorBorder);
                JOptionPane.showMessageDialog(this, "Profit amount cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            amountField.setBorder(defaultBorder);
            String remarks = remarksField.getText().trim();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date parsedDate = sdf.parse(dateStr);
            String sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(parsedDate);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO profits (profit_date, amount, remarks) VALUES (?, ?, ?)",
                         Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, sqlDate);
                pstmt.setBigDecimal(2, BigDecimal.valueOf(amount));
                pstmt.setString(3, remarks.isEmpty() ? null : remarks);
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                int newId = rs.next() ? rs.getInt(1) : 0;

                profitTableModel.addRow(new Object[]{
                        newId, // Store new profit_id
                        dateStr,
                        amount,
                        remarks
                });

                clearForm();
                JOptionPane.showMessageDialog(this, "Profit added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding profit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            amountField.setBorder(errorBorder);
            JOptionPane.showMessageDialog(this, "Invalid profit amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            dateField.setBorder(errorBorder);
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProfit() {
        int selectedRow = profitTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this profit entry?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int profitId = (int) profitTableModel.getValueAt(selectedRow, 0); // Get profit_id from first column
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM profits WHERE profit_id = ?")) {
                    pstmt.setInt(1, profitId);
                    pstmt.executeUpdate();
                    profitTableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Profit deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting profit: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a profit entry to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        dateField.setText("dd-MM-yyyy");
        dateField.setForeground(Color.GRAY);
        dateField.setBorder(defaultBorder);
        amountField.setText("0");
        amountField.setBorder(defaultBorder);
        remarksField.setText("");
        remarksField.setBorder(defaultBorder);
    }

    private void generateDailyReport() {
        JTextField reportDateField = new JTextField("dd-MM-yyyy");
        reportDateField.setFont(new Font("Arial", Font.PLAIN, 14));
        reportDateField.setForeground(Color.GRAY);
        reportDateField.setBorder(defaultBorder);
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
                    reportDateField.setBorder(defaultBorder);
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Select Date:"));
        panel.add(reportDateField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Daily Profit Report", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedDate = reportDateField.getText().trim();
        if (!validateDate(selectedDate, reportDateField)) {
            JOptionPane.showMessageDialog(this, "Invalid or future date. Use dd-MM-yyyy (e.g., 21-05-2025).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date parsedDate = sdf.parse(selectedDate);
            String sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(parsedDate);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT profit_id, profit_date, amount, remarks FROM profits WHERE profit_date = ?")) {
                pstmt.setString(1, sqlDate);
                ResultSet rs = pstmt.executeQuery();

                double totalProfit = 0;
                StringBuilder report = new StringBuilder();
                report.append("Daily Profit Report (").append(selectedDate).append(")\n\n");

                while (rs.next()) {
                    double amount = rs.getBigDecimal("amount").doubleValue();
                    totalProfit += amount;
                    report.append(String.format("ID: PRF-%d, Date: %s, Amount: %.2f, Remarks: %s\n",
                            rs.getInt("profit_id"),
                            new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                            amount,
                            rs.getString("remarks") != null ? rs.getString("remarks") : ""));
                }
                report.append(String.format("\nTotal Profit: %.2f", totalProfit));

                JTextArea reportArea = new JTextArea(report.toString());
                reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
                reportArea.setEditable(false);
                JScrollPane reportScroll = new JScrollPane(reportArea);
                reportScroll.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(this, reportScroll, "Daily Profit Report", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error generating daily report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-MM-yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateMonthlyReport() {
        ArrayList<String> monthYears = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int pastYears = 0;
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
        monthCombo.setToolTipText("Select a month and year for the profit report");
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Select Month:"));
        panel.add(monthCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Monthly Profit Report", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedMonthYear = (String) monthCombo.getSelectedItem();
        String[] parts = selectedMonthYear.split(" ");
        String monthName = parts[0];
        int year = Integer.parseInt(parts[1]);

        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        int monthNumber = 0;
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equals(monthName)) {
                monthNumber = i + 1;
                break;
            }
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT profit_id, profit_date, amount, remarks FROM profits WHERE YEAR(profit_date) = ? AND MONTH(profit_date) = ?")) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, monthNumber);
            ResultSet rs = pstmt.executeQuery();

            double totalProfit = 0;
            StringBuilder report = new StringBuilder();
            report.append("Monthly Profit Report (").append(selectedMonthYear).append(")\n\n");

            while (rs.next()) {
                double amount = rs.getBigDecimal("amount").doubleValue();
                totalProfit += amount;
                report.append(String.format("ID: PRF-%d, Date: %s, Amount: %.2f, Remarks: %s\n",
                        rs.getInt("profit_id"),
                        new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                        amount,
                        rs.getString("remarks") != null ? rs.getString("remarks") : ""));
            }
            report.append(String.format("\nTotal Profit: %.2f", totalProfit));

            JTextArea reportArea = new JTextArea(report.toString());
            reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
            reportArea.setEditable(false);
            JScrollPane reportScroll = new JScrollPane(reportArea);
            reportScroll.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(this, reportScroll, "Monthly Profit Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating monthly report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

        int result = JOptionPane.showConfirmDialog(this, panel, "Quarterly Profit Report", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedHalfYear = (String) halfYearCombo.getSelectedItem();
        String[] parts = selectedHalfYear.split(" ");
        String half = parts[0];
        int year = Integer.parseInt(parts[2]);
        int startMonth = half.equals("1st") ? 1 : 7;
        int endMonth = half.equals("1st") ? 6 : 12;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT profit_id, profit_date, amount, remarks FROM profits WHERE YEAR(profit_date) = ? AND MONTH(profit_date) BETWEEN ? AND ?")) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, startMonth);
            pstmt.setInt(3, endMonth);
            ResultSet rs = pstmt.executeQuery();

            double totalProfit = 0;
            StringBuilder report = new StringBuilder();
            report.append("Quarterly Profit Report (").append(selectedHalfYear).append(")\n\n");

            while (rs.next()) {
                double amount = rs.getBigDecimal("amount").doubleValue();
                totalProfit += amount;
                report.append(String.format("ID: PRF-%d, Date: %s, Amount: %.2f, Remarks: %s\n",
                        rs.getInt("profit_id"),
                        new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                        amount,
                        rs.getString("remarks") != null ? rs.getString("remarks") : ""));
            }
            report.append(String.format("\nTotal Profit: %.2f", totalProfit));

            JTextArea reportArea = new JTextArea(report.toString());
            reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
            reportArea.setEditable(false);
            JScrollPane reportScroll = new JScrollPane(reportArea);
            reportScroll.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(this, reportScroll, "Quarterly Profit Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating quarterly report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateYearlyReport() {
        ArrayList<String> years = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int pastYears = 1;
        int futureYears = 30;

        for (int year = currentYear - pastYears; year <= currentYear + futureYears; year++) {
            years.add(String.valueOf(year));
        }

        JComboBox<String> yearCombo = new JComboBox<>(years.toArray(new String[0]));
        yearCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JLabel("Select Year:"));
        panel.add(yearCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Yearly Profit Report", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedYear = (String) yearCombo.getSelectedItem();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT profit_id, profit_date, amount, remarks FROM profits WHERE YEAR(profit_date) = ?")) {
            pstmt.setInt(1, Integer.parseInt(selectedYear));
            ResultSet rs = pstmt.executeQuery();

            double totalProfit = 0;
            StringBuilder report = new StringBuilder();
            report.append("Yearly Profit Report (").append(selectedYear).append(")\n\n");

            while (rs.next()) {
                double amount = rs.getBigDecimal("amount").doubleValue();
                totalProfit += amount;
                report.append(String.format("ID: PRF-%d, Date: %s, Amount: %.2f, Remarks: %s\n",
                        rs.getInt("profit_id"),
                        new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                        amount,
                        rs.getString("remarks") != null ? rs.getString("remarks") : ""));
            }
            report.append(String.format("\nTotal Profit: %.2f", totalProfit));

            JTextArea reportArea = new JTextArea(report.toString());
            reportArea.setFont(new Font("Arial", Font.PLAIN, 14));
            reportArea.setEditable(false);
            JScrollPane reportScroll = new JScrollPane(reportArea);
            reportScroll.setPreferredSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(this, reportScroll, "Yearly Profit Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating yearly report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProfitEntryFrame::new);
    }
}