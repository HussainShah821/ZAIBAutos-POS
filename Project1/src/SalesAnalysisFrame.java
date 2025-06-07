import javax.swing.*;
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
import java.util.List;

public class SalesAnalysisFrame extends JFrame {
    private JTable salesTable;
    private DefaultTableModel salesModel;
    private JTextField dateField;
    private JLabel dailyLabel, monthlyLabel, yearlyLabel;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PK"));
    private Border defaultBorder, errorBorder;
    private static int nextSaleId = 1;
    private static List<DataManager.Sales> dummySales = new ArrayList<>(); // Dummy data storage

    public SalesAnalysisFrame() {
        setTitle("Zaib Autos - Sales Analysis");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(true);

        // Define borders for validation feedback
        defaultBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        errorBorder = BorderFactory.createLineBorder(Color.RED, 1);

        // Top Panel: Date Selection and Actions
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        dateField = new JTextField("dd-MM-yyyy", 12);
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        dateField.setForeground(Color.GRAY);
        dateField.setBorder(defaultBorder);
        dateField.setToolTipText("Enter date in dd-MM-yyyy format (e.g., 21-05-2025) or double-click for today");
        // HCI: Placeholder behavior
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
        // HCI: Double-click to fill current date
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
        JButton addEntryBtn = createStyledButton("Add Entry");
        JButton clearBtn = createStyledButton("Clear Data");
        JButton exportBtn = createStyledButton("Export Report");

        pickDateBtn.addActionListener(e -> showDatePicker());
        addEntryBtn.addActionListener(e -> showAddEntryDialog());
        clearBtn.addActionListener(e -> clearData());
        exportBtn.addActionListener(e -> exportReport());

        topPanel.add(new JLabel("Analysis Date:"));
        topPanel.add(dateField);
        topPanel.add(pickDateBtn);
        topPanel.add(addEntryBtn);
        topPanel.add(clearBtn);
        topPanel.add(exportBtn);
        add(topPanel, BorderLayout.NORTH);

        // Center: Sales Table
        salesModel = new DefaultTableModel(new String[]{"Date", "Sales", "Expenses", "Profit", "Profit %"}, 0) {
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

        // Right-align numeric columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        for (int i = 1; i < salesTable.getColumnCount(); i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Sales Records"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom: Summary Panel
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

        // Load sample data
        loadSampleData();
        updateSummary();
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
            // Prevent future dates
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
        // HCI: Double-click to fill current date
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

    private void showAddEntryDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField salesField = new JTextField();
        salesField.setFont(new Font("Arial", Font.PLAIN, 14));
        salesField.setToolTipText("Enter sales amount");
        JTextField expenseField = new JTextField();
        expenseField.setFont(new Font("Arial", Font.PLAIN, 14));
        expenseField.setToolTipText("Enter expenses amount");
        JTextField profitField = new JTextField();
        profitField.setFont(new Font("Arial", Font.PLAIN, 14));
        profitField.setEditable(false);
        profitField.setToolTipText("Calculated profit and profit percentage");

        // Auto-calculate profit
        KeyAdapter calculator = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    double sales = salesField.getText().isEmpty() ? 0 : Double.parseDouble(salesField.getText());
                    double expenses = expenseField.getText().isEmpty() ? 0 : Double.parseDouble(expenseField.getText());
                    double profit = sales - expenses;
                    double profitPercent = sales > 0 ? (profit / sales) * 100 : 0;
                    profitField.setText(String.format("%.2f (%.1f%%)", profit, profitPercent));
                } catch (NumberFormatException ex) {
                    profitField.setText("Invalid input");
                }
            }
        };

        salesField.addKeyListener(calculator);
        expenseField.addKeyListener(calculator);

        panel.add(new JLabel("Sales Amount:"));
        panel.add(salesField);
        panel.add(new JLabel("Expenses:"));
        panel.add(expenseField);
        panel.add(new JLabel("Profit:"));
        panel.add(profitField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Daily Entry",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double sales = Double.parseDouble(salesField.getText());
                double expenses = Double.parseDouble(expenseField.getText());
                if (sales < 0 || expenses < 0) {
                    JOptionPane.showMessageDialog(this, "Sales and expenses cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String date = dateField.getText();
                if (!validateDate(date, dateField)) {
                    JOptionPane.showMessageDialog(this, "Invalid or future date in analysis field. Use dd-MM-yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double profit = sales - expenses;
                double profitPercent = sales > 0 ? (profit / sales) * 100 : 0;

                // Use dummy sales list instead of DataManager
                dummySales.add(new DataManager.Sales(nextSaleId++, date, sales, expenses));
                salesModel.addRow(new Object[]{date, sales, expenses, profit, profitPercent});

                updateSummary();
                JOptionPane.showMessageDialog(this, "Entry added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for sales and expenses.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
        double dailySales = 0, dailyExpenses = 0;
        double monthlySales = 0, monthlyExpenses = 0;
        double yearlySales = 0, yearlyExpenses = 0;

        try {
            Date refDate = sdf.parse(selectedDate);
            Calendar refCal = Calendar.getInstance();
            refCal.setTime(refDate);

            // Use dummy sales instead of DataManager
            for (DataManager.Sales sale : dummySales) {
                Date entryDate = sdf.parse(sale.date);
                Calendar entryCal = Calendar.getInstance();
                entryCal.setTime(entryDate);

                // Daily
                if (sdf.format(refDate).equals(sdf.format(entryDate))) {
                    dailySales += sale.sales;
                    dailyExpenses += sale.expenses;
                }

                // Monthly
                if (refCal.get(Calendar.MONTH) == entryCal.get(Calendar.MONTH)
                        && refCal.get(Calendar.YEAR) == entryCal.get(Calendar.YEAR)) {
                    monthlySales += sale.sales;
                    monthlyExpenses += sale.expenses;
                }

                // Yearly
                if (refCal.get(Calendar.YEAR) == entryCal.get(Calendar.YEAR)) {
                    yearlySales += sale.sales;
                    yearlyExpenses += sale.expenses;
                }
            }

            // Calculate profits and percentages
            double dailyProfit = dailySales - dailyExpenses;
            double dailyProfitPercent = dailySales > 0 ? (dailyProfit / dailySales) * 100 : 0;

            double monthlyProfit = monthlySales - monthlyExpenses;
            double monthlyProfitPercent = monthlySales > 0 ? (monthlyProfit / monthlySales) * 100 : 0;

            double yearlyProfit = yearlySales - yearlyExpenses;
            double yearlyProfitPercent = yearlySales > 0 ? (yearlyProfit / yearlySales) * 100 : 0;

            // Update labels
            dailyLabel.setText(String.format("<html><b>Today:</b> Sales: %s | Expenses: %s<br>Profit: %s (%.1f%%)</html>",
                    currencyFormat.format(dailySales), currencyFormat.format(dailyExpenses),
                    currencyFormat.format(dailyProfit), dailyProfitPercent));

            monthlyLabel.setText(String.format("<html><b>This Month:</b> Sales: %s | Expenses: %s<br>Profit: %s (%.1f%%)</html>",
                    currencyFormat.format(monthlySales), currencyFormat.format(monthlyExpenses),
                    currencyFormat.format(monthlyProfit), monthlyProfitPercent));

            yearlyLabel.setText(String.format("<html><b>This Year:</b> Sales: %s | Expenses: %s<br>Profit: %s (%.1f%%)</html>",
                    currencyFormat.format(yearlySales), currencyFormat.format(yearlyExpenses),
                    currencyFormat.format(yearlyProfit), yearlyProfitPercent));

        } catch (ParseException e) {
            dailyLabel.setText("Today: Error parsing date");
            monthlyLabel.setText("This Month: Error parsing date");
            yearlyLabel.setText("This Year: Error parsing date");
        }
    }

    private void clearData() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all sales data?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dummySales.clear(); // Use dummy sales list instead of DataManager
            salesModel.setRowCount(0);

            updateSummary();
            JOptionPane.showMessageDialog(this, "Sales data cleared successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            writer.write("BT /F1 10 Tf 50 " + yPos + " Td (  Date  Sales  Expenses  Profit  Profit %) Tj ET\n");
            yPos -= 20;

            for (int i = 0; i < salesModel.getRowCount(); i++) {
                String date = salesModel.getValueAt(i, 0).toString();
                String sales = currencyFormat.format(salesModel.getValueAt(i, 1)).replace("PKR", "").replace(",", "");
                String expenses = currencyFormat.format(salesModel.getValueAt(i, 2)).replace("PKR", "").replace(",", "");
                String profit = currencyFormat.format(salesModel.getValueAt(i, 3)).replace("PKR", "").replace(",", "");
                String profitPercent = String.format("%.1f%%", salesModel.getValueAt(i, 4));
                String line = String.format("%s  %s  %s  %s  %s", date, sales, expenses, profit, profitPercent);
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

    private void loadSampleData() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // May 2025 sample data
        cal.set(2025, Calendar.MAY, 1);
        for (int i = 1; i <= 15; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            double sales = 1000 + (Math.random() * 2000);
            double expenses = 300 + (Math.random() * 700);
            double profit = sales - expenses;
            double profitPercent = sales > 0 ? (profit / sales) * 100 : 0;

            dummySales.add(new DataManager.Sales(nextSaleId++, sdf.format(cal.getTime()), sales, expenses));
            salesModel.addRow(new Object[]{sdf.format(cal.getTime()), sales, expenses, profit, profitPercent});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalesAnalysisFrame());
    }
}