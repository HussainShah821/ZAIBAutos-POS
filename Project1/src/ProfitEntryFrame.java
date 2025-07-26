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
        setSize(1200, 750); // Match SalesEntryFrame
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
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Match SalesEntryFrame
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
        profitTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
        profitTable.setRowHeight(25);
        profitTable.setGridColor(new Color(200, 200, 200));
        profitTable.setShowGrid(true);
        // Hide the Profit ID column
        profitTable.getColumnModel().getColumn(0).setMinWidth(0);
        profitTable.getColumnModel().getColumn(0).setMaxWidth(0);
        profitTable.getColumnModel().getColumn(0).setWidth(0);
        JScrollPane scrollPane = new JScrollPane(profitTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Profit Entries")); // Match SalesEntryFrame
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Match SalesEntryFrame
        button.setBackground(new Color(0, 111, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 69, 217));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 111, 255));
            }
        });
        return button;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Match SalesEntryFrame
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

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add Profit Entry")); // Match SalesEntryFrame
        panel.setPreferredSize(new Dimension(300, 180)); // Match SalesEntryFrame

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Date:"), gbc);
        dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        dateField.setPreferredSize(new Dimension(150, 25));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
        dateField.setBorder(defaultBorder);
        dateField.setToolTipText("Enter date (dd-MM-yyyy), default is today");
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Profit Amount:"), gbc);
        amountField = new JTextField("0");
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
        amountField.setToolTipText("Enter the profit amount (non-negative)");
        amountField.setBorder(defaultBorder);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Remarks:"), gbc);
        remarksField = new JTextField();
        remarksField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
        remarksField.setToolTipText("Optional notes about this profit entry");
        remarksField.setBorder(defaultBorder);
        gbc.gridx = 1;
        panel.add(remarksField, gbc);

        return panel;
    }

    private boolean validateDate(String dateStr, JTextField field) {
        if (dateStr.isEmpty()) {
            field.setBorder(errorBorder);
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            // Ensure date is on or after July 1, 2025
            Calendar minDate = Calendar.getInstance();
            minDate.set(2025, Calendar.JULY, 1, 0, 0, 0);
            minDate.set(Calendar.MILLISECOND, 0);
            if (date.before(minDate.getTime())) {
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
                JOptionPane.showMessageDialog(this, "Invalid date or date before 01-07-2025. Use dd-MM-yyyy (e.g., 01-07-2025).", "Error", JOptionPane.ERROR_MESSAGE);
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
        dateField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date())); // Match SalesEntryFrame
        dateField.setBorder(defaultBorder);
        amountField.setText("0");
        amountField.setBorder(defaultBorder);
        remarksField.setText("");
        remarksField.setBorder(defaultBorder);
    }

    private void generateDailyReport() {
        JDialog dialog = new JDialog(this, "Daily Profit Report", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.WHITE);
        dialog.setSize(250, 170); // Match SalesEntryFrame dialog
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Select Date:"), gbc);
        JTextField reportDateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        reportDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
        reportDateField.setToolTipText("Enter date (dd-MM-yyyy, on or after 01-07-2025)");
        gbc.gridx = 1;
        dialog.add(reportDateField, gbc);

        JButton okButton = createStyledButton("OK");
        JButton cancelButton = createStyledButton("Cancel");

        okButton.addActionListener(e -> {
            String selectedDate = reportDateField.getText().trim();
            if (!validateDate(selectedDate, reportDateField)) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or date before 01-07-2025. Use dd-MM-yyyy (e.g., 01-07-2025).", "Error", JOptionPane.ERROR_MESSAGE);
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

                    DefaultTableModel reportModel = new DefaultTableModel(new String[]{"Profit ID", "Date", "Amount", "Remarks"}, 0);
                    double totalProfit = 0;
                    while (rs.next()) {
                        double amount = rs.getBigDecimal("amount").doubleValue();
                        totalProfit += amount;
                        reportModel.addRow(new Object[]{
                                rs.getInt("profit_id"),
                                new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                                amount,
                                rs.getString("remarks") != null ? rs.getString("remarks") : ""
                        });
                    }
                    if (reportModel.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "No profits recorded for " + selectedDate + ".", "Info", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        return;
                    }
                    JTable reportTable = new JTable(reportModel);
                    reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
                    reportTable.setRowHeight(25);
                    reportTable.setGridColor(new Color(200, 200, 200));
                    reportTable.setShowGrid(true);
                    reportTable.getColumnModel().getColumn(0).setMinWidth(0);
                    reportTable.getColumnModel().getColumn(0).setMaxWidth(0);
                    reportTable.getColumnModel().getColumn(0).setWidth(0);
                    JScrollPane reportScroll = new JScrollPane(reportTable);
                    JOptionPane.showMessageDialog(this, reportScroll, "Daily Profit Report - " + selectedDate, JOptionPane.PLAIN_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error generating daily report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                dialog.dispose();
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use dd-MM-yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Match SalesEntryFrame dialog
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private void generateMonthlyReport() {
        ArrayList<String> monthYears = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 1); // Start from July 2025
        int endYear = 2025 + 30; // Up to 2055

        for (int year = 2025; year <= endYear; year++) {
            int startMonth = (year == 2025) ? Calendar.JULY : Calendar.JANUARY; // Start from July for 2025, January for others
            for (int month = startMonth; month <= Calendar.DECEMBER; month++) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
                monthYears.add(String.format("%s %d", monthName, year));
            }
        }

        JDialog dialog = new JDialog(this, "Monthly Profit Report", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.WHITE);
        dialog.setSize(250, 170); // Match SalesEntryFrame dialog
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Select Month:"), gbc);
        JComboBox<String> monthCombo = new JComboBox<>(monthYears.toArray(new String[0]));
        monthCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
        monthCombo.setToolTipText("Select a month and year for the profit report");
        gbc.gridx = 1;
        dialog.add(monthCombo, gbc);

        JButton okButton = createStyledButton("OK");
        JButton cancelButton = createStyledButton("Cancel");

        okButton.addActionListener(e -> {
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

                DefaultTableModel reportModel = new DefaultTableModel(new String[]{"Profit ID", "Date", "Amount", "Remarks"}, 0);
                double totalProfit = 0;
                while (rs.next()) {
                    double amount = rs.getBigDecimal("amount").doubleValue();
                    totalProfit += amount;
                    reportModel.addRow(new Object[]{
                            rs.getInt("profit_id"),
                            new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                            amount,
                            rs.getString("remarks") != null ? rs.getString("remarks") : ""
                    });
                }
                if (reportModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No profits recorded for " + selectedMonthYear + ".", "Info", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    return;
                }
                JTable reportTable = new JTable(reportModel);
                reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
                reportTable.setRowHeight(25);
                reportTable.setGridColor(new Color(200, 200, 200));
                reportTable.setShowGrid(true);
                reportTable.getColumnModel().getColumn(0).setMinWidth(0);
                reportTable.getColumnModel().getColumn(0).setMaxWidth(0);
                reportTable.getColumnModel().getColumn(0).setWidth(0);
                JScrollPane reportScroll = new JScrollPane(reportTable);
                JOptionPane.showMessageDialog(this, reportScroll, "Monthly Profit Report - " + selectedMonthYear, JOptionPane.PLAIN_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error generating monthly report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Match SalesEntryFrame dialog
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private void generateQuarterlyReport() {
        ArrayList<String> halfYears = new ArrayList<>();
        int startYear = 2025;
        int endYear = 2025 + 30; // Up to 2055

        for (int year = startYear; year <= endYear; year++) {
            if (year == 2025) {
                halfYears.add("2nd Half " + year); // Start from 2nd Half 2025
            } else {
                halfYears.add("1st Half " + year);
                halfYears.add("2nd Half " + year);
            }
        }

        JDialog dialog = new JDialog(this, "Quarterly Profit Report", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.WHITE);
        dialog.setSize(250, 170); // Match SalesEntryFrame dialog
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Select Half-Year:"), gbc);
        JComboBox<String> halfYearCombo = new JComboBox<>(halfYears.toArray(new String[0]));
        halfYearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
        halfYearCombo.setToolTipText("Select a half-year for the profit report");
        gbc.gridx = 1;
        dialog.add(halfYearCombo, gbc);

        JButton okButton = createStyledButton("OK");
        JButton cancelButton = createStyledButton("Cancel");

        okButton.addActionListener(e -> {
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

                DefaultTableModel reportModel = new DefaultTableModel(new String[]{"Profit ID", "Date", "Amount", "Remarks"}, 0);
                double totalProfit = 0;
                while (rs.next()) {
                    double amount = rs.getBigDecimal("amount").doubleValue();
                    totalProfit += amount;
                    reportModel.addRow(new Object[]{
                            rs.getInt("profit_id"),
                            new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                            amount,
                            rs.getString("remarks") != null ? rs.getString("remarks") : ""
                    });
                }
                if (reportModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No profits recorded for " + selectedHalfYear + ".", "Info", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    return;
                }
                JTable reportTable = new JTable(reportModel);
                reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
                reportTable.setRowHeight(25);
                reportTable.setGridColor(new Color(200, 200, 200));
                reportTable.setShowGrid(true);
                reportTable.getColumnModel().getColumn(0).setMinWidth(0);
                reportTable.getColumnModel().getColumn(0).setMaxWidth(0);
                reportTable.getColumnModel().getColumn(0).setWidth(0);
                JScrollPane reportScroll = new JScrollPane(reportTable);
                JOptionPane.showMessageDialog(this, reportScroll, "Quarterly Profit Report - " + selectedHalfYear, JOptionPane.PLAIN_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error generating quarterly report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Match SalesEntryFrame dialog
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private void generateYearlyReport() {
        ArrayList<String> years = new ArrayList<>();
        int startYear = 2025;
        int endYear = 2025 + 30; // Up to 2055

        for (int year = startYear; year <= endYear; year++) {
            years.add(String.valueOf(year));
        }

        JDialog dialog = new JDialog(this, "Yearly Profit Report", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setBackground(Color.WHITE);
        dialog.setSize(250, 170); // Match SalesEntryFrame dialog
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Select Year:"), gbc);
        JComboBox yearCombo = new JComboBox<>(years.toArray(new String[0]));
        yearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
        yearCombo.setToolTipText("Select a year for the profit report");
        gbc.gridx = 1;
        dialog.add(yearCombo, gbc);

        JButton okButton = createStyledButton("OK");
        JButton cancelButton = createStyledButton("Cancel");

        okButton.addActionListener(e -> {
            String selectedYear = (String) yearCombo.getSelectedItem();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT profit_id, profit_date, amount, remarks FROM profits WHERE YEAR(profit_date) = ?")) {
                pstmt.setInt(1, Integer.parseInt(selectedYear));
                ResultSet rs = pstmt.executeQuery();

                DefaultTableModel reportModel = new DefaultTableModel(new String[]{"Profit ID", "Date", "Amount", "Remarks"}, 0);
                double totalProfit = 0;
                while (rs.next()) {
                    double amount = rs.getBigDecimal("amount").doubleValue();
                    totalProfit += amount;
                    reportModel.addRow(new Object[]{
                            rs.getInt("profit_id"),
                            new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("profit_date")),
                            amount,
                            rs.getString("remarks") != null ? rs.getString("remarks") : ""
                    });
                }
                if (reportModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No profits recorded for " + selectedYear + ".", "Info", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    return;
                }
                JTable reportTable = new JTable(reportModel);
                reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Match SalesEntryFrame
                reportTable.setRowHeight(25);
                reportTable.setGridColor(new Color(200, 200, 200));
                reportTable.setShowGrid(true);
                reportTable.getColumnModel().getColumn(0).setMinWidth(0);
                reportTable.getColumnModel().getColumn(0).setMaxWidth(0);
                reportTable.getColumnModel().getColumn(0).setWidth(0);
                JScrollPane reportScroll = new JScrollPane(reportTable);
                JOptionPane.showMessageDialog(this, reportScroll, "Yearly Profit Report - " + selectedYear, JOptionPane.PLAIN_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error generating yearly report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Match SalesEntryFrame dialog
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProfitEntryFrame::new);
    }
}