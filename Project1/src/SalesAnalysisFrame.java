import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

public class SalesAnalysisFrame extends JFrame {
    private JTable salesTable;
    private DefaultTableModel salesModel;
    private JTextField dateField;
    private JLabel dailyLabel, monthlyLabel, yearlyLabel;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public SalesAnalysisFrame() {
        setTitle("Sales Analysis");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(true);

        // Top Panel: Date Selection and Actions
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        dateField = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()), 12);
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));

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

        // Center: Sales Table with improved styling
        salesModel = new DefaultTableModel(new String[]{"Date", "Sales", "Expenses", "Profit", "Profit %"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
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

        // Bottom: Summary Panel with better visualization
        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        dailyLabel = createSummaryLabel("Today");
        monthlyLabel = createSummaryLabel("This Month");
        yearlyLabel = createSummaryLabel("This Year");

        summaryPanel.add(dailyLabel);
        summaryPanel.add(monthlyLabel);
        summaryPanel.add(yearlyLabel);
        add(summaryPanel, BorderLayout.SOUTH);

        // Load sample data for demonstration
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

    private void showDatePicker() {
        String input = JOptionPane.showInputDialog(this, "Enter Date (dd-MM-yyyy):", dateField.getText());
        if (isValidDate(input)) {
            dateField.setText(input);
            updateSummary();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Date Format! Use dd-MM-yyyy", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddEntryDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField salesField = new JTextField();
        JTextField expenseField = new JTextField();
        JTextField profitField = new JTextField();
        profitField.setEditable(false);

        // Add listeners for auto-calculation
        KeyAdapter calculator = new KeyAdapter() {
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

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Add Daily Entry",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double sales = Double.parseDouble(salesField.getText());
                double expenses = Double.parseDouble(expenseField.getText());
                double profit = sales - expenses;
                double profitPercent = sales > 0 ? (profit / sales) * 100 : 0;
                String date = dateField.getText();

                salesModel.addRow(new Object[]{
                        date,
                        sales,
                        expenses,
                        profit,
                        profitPercent
                });

                updateSummary();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for sales and expenses.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateSummary() {
        String selectedDate = dateField.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        double dailySales = 0, dailyExpenses = 0;
        double monthlySales = 0, monthlyExpenses = 0;
        double yearlySales = 0, yearlyExpenses = 0;

        try {
            Date refDate = sdf.parse(selectedDate);
            Calendar refCal = Calendar.getInstance();
            refCal.setTime(refDate);

            for (int i = 0; i < salesModel.getRowCount(); i++) {
                Date entryDate = sdf.parse(salesModel.getValueAt(i, 0).toString());
                Calendar entryCal = Calendar.getInstance();
                entryCal.setTime(entryDate);

                double sale = (double) salesModel.getValueAt(i, 1);
                double expense = (double) salesModel.getValueAt(i, 2);

                // Daily
                if (sdf.format(refDate).equals(sdf.format(entryDate))) {
                    dailySales += sale;
                    dailyExpenses += expense;
                }

                // Monthly
                if (refCal.get(Calendar.MONTH) == entryCal.get(Calendar.MONTH)
                        && refCal.get(Calendar.YEAR) == entryCal.get(Calendar.YEAR)) {
                    monthlySales += sale;
                    monthlyExpenses += expense;
                }

                // Yearly
                if (refCal.get(Calendar.YEAR) == entryCal.get(Calendar.YEAR)) {
                    yearlySales += sale;
                    yearlyExpenses += expense;
                }
            }

            // Calculate profits and percentages
            double dailyProfit = dailySales - dailyExpenses;
            double dailyProfitPercent = dailySales > 0 ? (dailyProfit / dailySales) * 100 : 0;

            double monthlyProfit = monthlySales - monthlyExpenses;
            double monthlyProfitPercent = monthlySales > 0 ? (monthlyProfit / monthlySales) * 100 : 0;

            double yearlyProfit = yearlySales - yearlyExpenses;
            double yearlyProfitPercent = yearlySales > 0 ? (yearlyProfit / yearlySales) * 100 : 0;

            // Update labels with formatted currency values
            dailyLabel.setText(String.format("<html><b>Today:</b> Sales: %s | Expenses: %s<br>Profit: %s (%.1f%%)</html>",
                    currencyFormat.format(dailySales),
                    currencyFormat.format(dailyExpenses),
                    currencyFormat.format(dailyProfit),
                    dailyProfitPercent));

            monthlyLabel.setText(String.format("<html><b>This Month:</b> Sales: %s | Expenses: %s<br>Profit: %s (%.1f%%)</html>",
                    currencyFormat.format(monthlySales),
                    currencyFormat.format(monthlyExpenses),
                    currencyFormat.format(monthlyProfit),
                    monthlyProfitPercent));

            yearlyLabel.setText(String.format("<html><b>This Year:</b> Sales: %s | Expenses: %s<br>Profit: %s (%.1f%%)</html>",
                    currencyFormat.format(yearlySales),
                    currencyFormat.format(yearlyExpenses),
                    currencyFormat.format(yearlyProfit),
                    yearlyProfitPercent));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void clearData() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Clear all sales data?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            salesModel.setRowCount(0);
            updateSummary();
        }
    }

    private void exportReport() {
        JOptionPane.showMessageDialog(this,
                "Export functionality would be implemented here",
                "Export Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadSampleData() {
        // Add some sample data for demonstration
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // Current month data
        for (int i = 1; i <= 15; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            double sales = 1000 + (Math.random() * 2000);
            double expenses = 300 + (Math.random() * 700);
            double profit = sales - expenses;
            double profitPercent = (profit / sales) * 100;

            salesModel.addRow(new Object[]{
                    sdf.format(cal.getTime()),
                    sales,
                    expenses,
                    profit,
                    profitPercent
            });
        }
    }

    private boolean isValidDate(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            sdf.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SalesAnalysisFrame frame = new SalesAnalysisFrame();
            frame.setVisible(true);
        });
    }
}