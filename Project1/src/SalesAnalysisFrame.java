import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SalesAnalysisFrame extends JFrame {
    private JTable salesTable;
    private DefaultTableModel salesModel;
    private JTextField dateField;
    private JLabel dailyLabel, monthlyLabel, yearlyLabel;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PK"));
    private Border defaultBorder, errorBorder;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/zaibautos";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private Timer refreshTimer;

    public SalesAnalysisFrame() {
        setTitle("Zaib Autos - Sales Analysis");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(true);

        defaultBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        errorBorder = BorderFactory.createLineBorder(Color.RED, 1);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        dateField = new JTextField("dd-MM-yyyy", 12);
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

        JButton pickDateBtn = createStyledButton("Pick Date");
        JButton clearBtn = createStyledButton("Clear Data");
        JButton exportBtn = createStyledButton("Export Report");

        pickDateBtn.addActionListener(e -> showDatePicker());
        clearBtn.addActionListener(e -> clearData());
        exportBtn.addActionListener(e -> exportReport());

        topPanel.add(new JLabel("Analysis Date:"));
        topPanel.add(dateField);
        topPanel.add(pickDateBtn);
        topPanel.add(clearBtn);
        topPanel.add(exportBtn);
        add(topPanel, BorderLayout.NORTH);

        salesModel = new DefaultTableModel(new String[]{"Date", "Sales", "Expenses", "Profit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? String.class : Double.class;
            }
        };

        salesTable = new JTable(salesModel);
        salesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        salesTable.setRowHeight(25);
        salesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        salesTable.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        for (int i = 1; i < salesTable.getColumnCount(); i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Sales Records"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        summaryPanel.setBackground(Color.WHITE);

        dailyLabel = createSummaryLabel("Today");
        monthlyLabel = createSummaryLabel("This Month");
        yearlyLabel = createSummaryLabel("This Year");

        summaryPanel.add(dailyLabel);
        summaryPanel.add(monthlyLabel);
        summaryPanel.add(yearlyLabel);
        add(summaryPanel, BorderLayout.SOUTH);

        loadSalesData();
        updateSummary();

        // Start timer for real-time updates (every 5 seconds)
        refreshTimer = new Timer(5000, e -> {
            loadSalesData();
            updateSummary();
        });
        refreshTimer.start();

        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadSalesData() {
        salesModel.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT date, sales, expenses, profit FROM sales_analysis")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            while (rs.next()) {
                salesModel.addRow(new Object[]{
                        sdf.format(rs.getDate("date")),
                        rs.getDouble("sales"),
                        rs.getDouble("expenses"),
                        rs.getDouble("profit")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading sales data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private JLabel createSummaryLabel(String period) {
        JLabel label = new JLabel(period + ": Loading...", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        label.setOpaque(true);
        label.setBackground(new Color(240, 240, 240));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private boolean validateDate(String dateStr, JTextField field) {
        if (dateStr.equals("dd-MM-yyyy") || dateStr.isEmpty()) {
            if (field != null) field.setBorder(errorBorder);
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            if (date.after(new Date())) {
                if (field != null) field.setBorder(errorBorder);
                return false;
            }
            if (field != null) field.setBorder(defaultBorder);
            return true;
        } catch (ParseException e) {
            if (field != null) field.setBorder(errorBorder);
            return false;
        }
    }

    private void showDatePicker() {
        JTextField inputField = new JTextField(dateField.getText().equals("dd-MM-yyyy") ? "" : dateField.getText(), 12);
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setForeground(Color.BLACK);
        inputField.setBorder(defaultBorder);
        inputField.setToolTipText("Enter date in dd-MM-yyyy format (e.g., 21-05-2025)");
        inputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    inputField.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                    inputField.setForeground(Color.BLACK);
                    inputField.setBorder(defaultBorder);
                }
            }
        });

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Enter Date (dd-MM-yyyy):"));
        panel.add(inputField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Select Analysis Date",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String input = inputField.getText().trim();
            if (validateDate(input, inputField)) {
                dateField.setText(input);
                dateField.setForeground(Color.BLACK);
                dateField.setBorder(defaultBorder);
                updateSummary();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid or future date. Use dd-MM-yyyy (e.g., 21-05-2025).",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateSummary() {
        String selectedDate = dateField.getText();
        if (!validateDate(selectedDate, dateField)) {
            dailyLabel.setText("Today: Invalid date");
            monthlyLabel.setText("This Month: Invalid date");
            yearlyLabel.setText("This Year: Invalid date");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        double dailySales = 0, dailyExpenses = 0, dailyProfit = 0;
        double monthlySales = 0, monthlyExpenses = 0, monthlyProfit = 0;
        double yearlySales = 0, yearlyExpenses = 0, yearlyProfit = 0;

        try {
            Date refDate = sdf.parse(selectedDate);
            Calendar refCal = Calendar.getInstance();
            refCal.setTime(refDate);
            int refMonth = refCal.get(Calendar.MONTH) + 1;
            int refYear = refCal.get(Calendar.YEAR);

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT date, sales, expenses, profit FROM sales_analysis")) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Date entryDate = rs.getDate("date");
                    Calendar entryCal = Calendar.getInstance();
                    entryCal.setTime(entryDate);
                    int entryMonth = entryCal.get(Calendar.MONTH) + 1;
                    int entryYear = entryCal.get(Calendar.YEAR);

                    double sales = rs.getDouble("sales");
                    double expenses = rs.getDouble("expenses");
                    double profit = rs.getDouble("profit");

                    if (sdf.format(refDate).equals(sdf.format(entryDate))) {
                        dailySales += sales;
                        dailyExpenses += expenses;
                        dailyProfit += profit;
                    }

                    if (refMonth == entryMonth && refYear == entryYear) {
                        monthlySales += sales;
                        monthlyExpenses += expenses;
                        monthlyProfit += profit;
                    }

                    if (refYear == entryYear) {
                        yearlySales += sales;
                        yearlyExpenses += expenses;
                        yearlyProfit += profit;
                    }
                }
            }

            dailyLabel.setText(String.format("<html><b>Today:</b> Sales: %s | Expenses: %s | Profit: %s</html>",
                    currencyFormat.format(dailySales), currencyFormat.format(dailyExpenses),
                    currencyFormat.format(dailyProfit)));

            monthlyLabel.setText(String.format("<html><b>This Month:</b> Sales: %s | Expenses: %s | Profit: %s</html>",
                    currencyFormat.format(monthlySales), currencyFormat.format(monthlyExpenses),
                    currencyFormat.format(monthlyProfit)));

            yearlyLabel.setText(String.format("<html><b>This Year:</b> Sales: %s | Expenses: %s | Profit: %s</html>",
                    currencyFormat.format(yearlySales), currencyFormat.format(yearlyExpenses),
                    currencyFormat.format(yearlyProfit)));

        } catch (ParseException e) {
            dailyLabel.setText("Today: Error parsing date");
            monthlyLabel.setText("This Month: Error parsing date");
            yearlyLabel.setText("This Year: Error parsing date");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating summary: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearData() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all sales analysis data?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM sales_analysis")) {
                pstmt.executeUpdate();
                salesModel.setRowCount(0);
                updateSummary();
                JOptionPane.showMessageDialog(this, "Sales analysis data cleared successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error clearing data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sales Report PDF");
        String defaultFileName = "SalesReport_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        fileChooser.setSelectedFile(new File(defaultFileName));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
            }
            @Override
            public String getDescription() {
                return "PDF Files (*.pdf)";
            }
        });

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
            writer.write("%PDF-1.4\n");
            writer.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
            writer.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
            writer.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> >> >> /MediaBox [0 0 612 792] /Contents 4 0 R >>\nendobj\n");
            writer.write("4 0 obj\n<< /Length 5 0 R >>\nstream\n");

            writer.write("BT /F1 12 Tf 50 750 Td (ZAIB-AUTOS) Tj ET\n");
            writer.write("BT /F1 10 Tf 50 730 Td (GT.ROAD GHOTKI) Tj ET\n");
            writer.write("BT /F1 12 Tf 50 710 Td (SALES ANALYSIS REPORT) Tj ET\n");

            String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            writer.write("BT /F1 10 Tf 50 690 Td (Generated on: " + formattedDate + ") Tj ET\n");

            int yPos = 670;
            writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Sales Records:) Tj ET\n");
            yPos -= 20;
            writer.write("BT /F1 10 Tf 50 " + yPos + " Td (  Date  Sales  Expenses  Profit) Tj ET\n");
            yPos -= 20;

            for (int i = 0; i < salesModel.getRowCount(); i++) {
                String date = salesModel.getValueAt(i, 0).toString();
                String sales = currencyFormat.format(salesModel.getValueAt(i, 1)).replace("PKR", "").replace(",", "");
                String expenses = currencyFormat.format(salesModel.getValueAt(i, 2)).replace("PKR", "").replace(",", "");
                String profit = currencyFormat.format(salesModel.getValueAt(i, 3)).replace("PKR", "").replace(",", "");
                String line = String.format("%s  %s  %s  %s", date, sales, expenses, profit);
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (" + line + ") Tj ET\n");
                yPos -= 20;
            }

            yPos -= 20;
            writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Summary Statistics:) Tj ET\n");
            yPos -= 20;

            String dailyText = dailyLabel.getText()
                    .replaceAll("<html><b>Today:</b>", "Today:")
                    .replaceAll("<br>", " - ")
                    .replaceAll("</html>", "")
                    .replace("PKR", "")
                    .replace(",", "");
            writer.write("BT /F1 10 Tf 50 " + yPos + " Td (" + dailyText + ") Tj ET\n");
            yPos -= 20;

            String monthlyText = monthlyLabel.getText()
                    .replaceAll("<html><b>This Month:</b>", "This Month:")
                    .replaceAll("<br>", " - ")
                    .replaceAll("</html>", "")
                    .replace("PKR", "")
                    .replace(",", "");
            writer.write("BT /F1 10 Tf 50 " + yPos + " Td (" + monthlyText + ") Tj ET\n");
            yPos -= 20;

            String yearlyText = yearlyLabel.getText()
                    .replaceAll("<html><b>This Year:</b>", "This Year:")
                    .replaceAll("<br>", " - ")
                    .replaceAll("</html>", "")
                    .replace("PKR", "")
                    .replace(",", "");
            writer.write("BT /F1 10 Tf 50 " + yPos + " Td (" + yearlyText + ") Tj ET\n");

            writer.write("endstream\nendobj\n");
            writer.write("5 0 obj\n" + (yPos + 1000) + "\nendobj\n");
            writer.write("trailer\n<< /Root 1 0 R >>\n%%EOF\n");

            JOptionPane.showMessageDialog(this, "Report exported successfully to " + fileToSave.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesAnalysisFrame());
    }
}