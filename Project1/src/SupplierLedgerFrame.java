
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.util.GregorianCalendar;
import java.util.Properties;

public class SupplierLedgerFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private JTable supplierTable, ledgerTable;
    private DefaultTableModel supplierModel, ledgerModel;
    private JLabel supplierNameLabel, supplierPhoneLabel;
    private int selectedSupplierRow = -1;
    private static int nextBillNo = 1;
    // New fields for totals
    private JTextField totalDebitField, totalCreditField, totalRemainingBalanceField;

    public SupplierLedgerFrame() {
        setTitle("Zaib Autos - Supplier Ledger");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(initWrapperPanel(initTitleLabel("Supplier List"), supplierListPanel()), "List");
        mainPanel.add(initWrapperPanel(initTitleLabel("Supplier Ledger Details"), supplierDetailPanel()), "Detail");

        add(mainPanel, BorderLayout.CENTER);
        cardLayout.show(mainPanel, "List");

        setVisible(true);
    }

    private JLabel initTitleLabel(String text) {
        JLabel titleLabel = new JLabel(text);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return titleLabel;
    }

    private JPanel initWrapperPanel(JLabel title, JPanel contentPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);
        return wrapper;
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

    private JPanel supplierListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        supplierModel = new DefaultTableModel(new String[]{"Supplier Name", "Phone No"}, 0);
        supplierTable = new JTable(supplierModel);
        supplierTable.setFont(new Font("Arial", Font.PLAIN, 14));
        supplierTable.setRowHeight(25);
        supplierTable.setGridColor(new Color(200, 200, 200));
        supplierTable.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(supplierTable);

        DataManager dataManager = DataManager.getInstance();
        for (DataManager.Supplier supplier : dataManager.getSuppliers()) {
            supplierModel.addRow(new Object[]{supplier.name, supplier.phone});
        }

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search by name or phone...");
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim().toLowerCase();
                filterSuppliers(text);
            }
        });

        JButton addButton = createStyledButton("Add Supplier");
        JButton deleteButton = createStyledButton("Delete Supplier");

        addButton.addActionListener(e -> addSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(searchField, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        supplierTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (supplierTable.getSelectedRow() != -1) {
                    selectedSupplierRow = supplierTable.getSelectedRow();
                    showSupplierDetailPanel();
                }
            }
        });

        return panel;
    }

    private JPanel supplierDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 0, 10, 10));
        panel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        supplierNameLabel = new JLabel("Supplier: ");
        supplierPhoneLabel = new JLabel("Phone: ");
        supplierNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        supplierPhoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoPanel.add(supplierNameLabel);
        infoPanel.add(supplierPhoneLabel);

        ledgerModel = new DefaultTableModel(new String[]{
                "V No/Bill No", "Date", "Remarks", "Debit", "Credit", "Remaining Balance"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ledgerTable = new JTable(ledgerModel);
        ledgerTable.setFont(new Font("Arial", Font.PLAIN, 14));
        ledgerTable.setRowHeight(25);
        ledgerTable.setGridColor(new Color(200, 200, 200));
        ledgerTable.setShowGrid(true);
        JScrollPane tableScroll = new JScrollPane(ledgerTable);

        JPanel totalsPanel = new JPanel(new GridBagLayout());
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel totalDebitLabel = new JLabel("Total Debit:");
        totalDebitLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalsPanel.add(totalDebitLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        totalDebitField = new JTextField("0.00");
        totalDebitField.setFont(new Font("Arial", Font.PLAIN, 14));
        totalDebitField.setEditable(false);
        totalDebitField.setHorizontalAlignment(JTextField.RIGHT);
        totalDebitField.setMinimumSize(new Dimension(120, 25));
        totalDebitField.setPreferredSize(new Dimension(120, 25));
        totalsPanel.add(totalDebitField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel totalCreditLabel = new JLabel("Total Credit:");
        totalCreditLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalsPanel.add(totalCreditLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        totalCreditField = new JTextField("0.00");
        totalCreditField.setFont(new Font("Arial", Font.PLAIN, 14));
        totalCreditField.setEditable(false);
        totalCreditField.setHorizontalAlignment(JTextField.RIGHT);
        totalCreditField.setMinimumSize(new Dimension(120, 25));
        totalCreditField.setPreferredSize(new Dimension(120, 25));
        totalsPanel.add(totalCreditField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel totalRemainingLabel = new JLabel("Total Remaining:");
        totalRemainingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalsPanel.add(totalRemainingLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        totalRemainingBalanceField = new JTextField("0.00");
        totalRemainingBalanceField.setFont(new Font("Arial", Font.PLAIN, 14));
        totalRemainingBalanceField.setEditable(false);
        totalRemainingBalanceField.setHorizontalAlignment(JTextField.RIGHT);
        totalRemainingBalanceField.setMinimumSize(new Dimension(120, 25));
        totalRemainingBalanceField.setPreferredSize(new Dimension(120, 25));
        totalsPanel.add(totalRemainingBalanceField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton addEntryButton = createStyledButton("Add Entry");
        JButton updateEntryButton = createStyledButton("Update Credit/Debit");
        JButton deleteEntryButton = createStyledButton("Delete Entry");
        JButton generateInvoiceButton = createStyledButton("Generate Invoice");
        JButton backButton = createStyledButton("Back to Suppliers");

        addEntryButton.addActionListener(e -> addSupplierEntry());
        updateEntryButton.addActionListener(e -> updateSupplierEntry());
        deleteEntryButton.addActionListener(e -> deleteSupplierEntry());
        generateInvoiceButton.addActionListener(e -> {
            if (ledgerModel.getRowCount() > 0) {
                showDateRangeDialog(ledgerModel);
            } else {
                JOptionPane.showMessageDialog(this, "No entries to generate invoice", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "List"));

        buttonPanel.add(addEntryButton);
        buttonPanel.add(updateEntryButton);
        buttonPanel.add(deleteEntryButton);
        buttonPanel.add(generateInvoiceButton);
        buttonPanel.add(backButton);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(totalsPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showDateRangeDialog(DefaultTableModel ledgerModel) {
        JDialog dateDialog = new JDialog(this, "Select Date Range", true);
        dateDialog.setSize(450, 200); // Increased height for better spacing
        dateDialog.setLocationRelativeTo(this);

        // Use GridBagLayout for flexible spacing
        dateDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around all components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize date pickers
        UtilDateModel startModel = new UtilDateModel();
        UtilDateModel endModel = new UtilDateModel();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JUNE, 7, 20, 14); // June 07, 2025, 08:14 PM PKT (current time)
        endModel.setValue(calendar.getTime());

        Properties dateProps = new Properties();
        dateProps.put("text.today", "Today");
        dateProps.put("text.month", "Month");
        dateProps.put("text.year", "Year");

        JDatePanelImpl startDatePanel = new JDatePanelImpl(startModel, dateProps);
        JDatePanelImpl endDatePanel = new JDatePanelImpl(endModel, dateProps);

        JDatePickerImpl startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
        JDatePickerImpl endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());

        // Add labels and pickers with spacing
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        dateDialog.add(new JLabel("Start Date:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        dateDialog.add(startDatePicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dateDialog.add(new JLabel("End Date:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dateDialog.add(endDatePicker, gbc);

        // Add buttons with spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Horizontal and vertical gaps
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        okButton.addActionListener(e -> {
            Object startValue = startDatePicker.getModel().getValue();
            Object endValue = endDatePicker.getModel().getValue();
            Date startDate = startValue != null ? (startValue instanceof GregorianCalendar ? ((GregorianCalendar) startValue).getTime() : (Date) startValue) : null;
            Date endDate = endValue != null ? (endValue instanceof GregorianCalendar ? ((GregorianCalendar) endValue).getTime() : (Date) endValue) : null;

            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(dateDialog, "Please select both dates.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(dateDialog, "Start date cannot be after end date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Start Date: " + new SimpleDateFormat("dd-MM-yyyy").format(startDate));
            System.out.println("End Date: " + new SimpleDateFormat("dd-MM-yyyy").format(endDate));
            DefaultTableModel filteredModel = filterLedgerByDateRange(ledgerModel, startDate, endDate);
            String supplierName = supplierNameLabel.getText().replace("Supplier: ", "");
            String phone = supplierPhoneLabel.getText().replace("Phone: ", "");
            InvoiceGenerator invoiceGenerator = new InvoiceGenerator(filteredModel, supplierName, phone, startDate, endDate); // Updated line
            invoiceGenerator.setVisible(true);
            invoiceGenerator.toFront();
            invoiceGenerator.requestFocus();
            invoiceGenerator.setAlwaysOnTop(true); // Optional: Forces the window to stay on top
            dateDialog.dispose();
        });

        cancelButton.addActionListener(e -> dateDialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across both columns
        gbc.anchor = GridBagConstraints.CENTER;
        dateDialog.add(buttonPanel, gbc);

        dateDialog.setVisible(true);
    }

    private DefaultTableModel filterLedgerByDateRange(DefaultTableModel ledgerModel, Date startDate, Date endDate) {
        DefaultTableModel filteredModel = new DefaultTableModel(new String[]{
                "V No/Bill No", "Date", "Remarks", "Debit", "Credit", "Remaining Balance"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        for (int i = 0; i < ledgerModel.getRowCount(); i++) {
            try {
                String dateStr = ledgerModel.getValueAt(i, 1).toString();
                Date entryDate = dateFormat.parse(dateStr);

                if (!entryDate.before(startDate) && !entryDate.after(endDate)) {
                    filteredModel.addRow(new Object[]{
                            ledgerModel.getValueAt(i, 0),
                            ledgerModel.getValueAt(i, 1),
                            ledgerModel.getValueAt(i, 2),
                            ledgerModel.getValueAt(i, 3),
                            ledgerModel.getValueAt(i, 4),
                            ledgerModel.getValueAt(i, 5)
                    });
                }
            } catch (Exception e) {
                // Skip entries with invalid dates
            }
        }

        return filteredModel;
    }

    private void updateTotals() {
        double totalDebit = 0.0;
        double totalCredit = 0.0;
        double totalRemainingBalance = 0.0;
        for (int i = 0; i < ledgerModel.getRowCount(); i++) {
            totalDebit += (Double) ledgerModel.getValueAt(i, 3);
            totalCredit += (Double) ledgerModel.getValueAt(i, 4);
            totalRemainingBalance += (Double) ledgerModel.getValueAt(i, 5);
        }
        totalDebitField.setText(String.format("%.2f", totalDebit));
        totalCreditField.setText(String.format("%.2f", totalCredit));
        totalRemainingBalanceField.setText(String.format("%.2f", totalRemainingBalance));
    }

    private void showSupplierDetailPanel() {
        DataManager dataManager = DataManager.getInstance();
        DataManager.Supplier supplier = dataManager.getSuppliers().get(selectedSupplierRow);
        supplierNameLabel.setText("Supplier: " + supplier.name);
        supplierPhoneLabel.setText("Phone: " + supplier.phone);

        ledgerModel.setRowCount(0);

        Object[][] dummyData = {
                {"SUP-001", "01-06-2025", "Initial balance", 0.0, 1000.0, -1000.0},
                {"SUP-002", "02-06-2025", "Purchase", 500.0, 0.0, -1500.0},
                {"SUP-003", "03-06-2025", "Payment", 0.0, 300.0, -1200.0},
                {"SUP-004", "04-06-2025", "Purchase", 700.0, 0.0, -1900.0},
                {"SUP-005", "05-06-2025", "Payment", 0.0, 400.0, -1500.0},
                {"SUP-006", "06-06-2025", "Purchase", 600.0, 0.0, -2100.0},
                {"SUP-007", "07-06-2025", "Payment", 0.0, 500.0, -1600.0}
        };

        for (Object[] row : dummyData) {
            double debit = (Double) row[3];
            double credit = (Double) row[4];
            double remainingBalance = (Double) row[5]; // Using pre-calculated remaining balance
            ledgerModel.addRow(new Object[]{row[0], row[1], row[2], debit, credit, remainingBalance});
        }

        updateTotals();
        cardLayout.show(mainPanel, "Detail");
    }

    private void filterSuppliers(String text) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(supplierModel);
        supplierTable.setRowSorter(sorter);
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void addSupplier() {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Supplier Name:"));
        panel.add(name);
        panel.add(new JLabel("Phone No:"));
        panel.add(phone);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String supplierName = name.getText().trim();
            String supplierPhone = phone.getText().trim();
            if (supplierName.isEmpty() || supplierPhone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and phone are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            DataManager dataManager = DataManager.getInstance();
            DataManager.Supplier newSupplier = new DataManager.Supplier(dataManager.getSuppliers().size() + 1, supplierName, supplierPhone);
            dataManager.addSupplier(newSupplier);
            supplierModel.addRow(new Object[]{supplierName, supplierPhone});
        }
    }

    private void deleteSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this supplier?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DataManager dataManager = DataManager.getInstance();
                dataManager.getSuppliers().remove(selectedRow);
                supplierModel.removeRow(selectedRow);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSupplierEntry() {
        String billNoStr = String.format("SUP-%03d", nextBillNo++);
        JTextField billNo = new JTextField(billNoStr);
        billNo.setEditable(false);
        JTextField date = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        JTextField remarks = new JTextField();
        JTextField debit = new JTextField("0");
        JTextField credit = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Bill No:"));
        panel.add(billNo);
        panel.add(new JLabel("Date:"));
        panel.add(date);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarks);
        panel.add(new JLabel("Debit:"));
        panel.add(debit);
        panel.add(new JLabel("Credit:"));
        panel.add(credit);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double debitVal = Double.parseDouble(debit.getText().trim());
                double creditVal = Double.parseDouble(credit.getText().trim());
                if (debitVal < 0 || creditVal < 0) {
                    JOptionPane.showMessageDialog(this, "Debit and Credit must be non-negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double remainingBalance = debitVal - creditVal;
                ledgerModel.addRow(new Object[]{
                        billNo.getText(),
                        date.getText(),
                        remarks.getText(),
                        debitVal,
                        creditVal,
                        remainingBalance
                });

                updateTotals();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for Debit or Credit.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateSupplierEntry() {
        int selected = ledgerTable.getSelectedRow();
        if (selected != -1) {
            double currentDebit = (Double) ledgerModel.getValueAt(selected, 3);
            double currentCredit = (Double) ledgerModel.getValueAt(selected, 4);

            JTextField debit = new JTextField(String.valueOf(currentDebit));
            JTextField credit = new JTextField(String.valueOf(currentCredit));

            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.add(new JLabel("Debit:"));
            panel.add(debit);
            panel.add(new JLabel("Credit:"));
            panel.add(credit);

            int result = JOptionPane.showConfirmDialog(this, panel, "Update Credit/Debit", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    double debitVal = Double.parseDouble(debit.getText().trim());
                    double creditVal = Double.parseDouble(credit.getText().trim());
                    if (debitVal < 0 || creditVal < 0) {
                        JOptionPane.showMessageDialog(this, "Debit and Credit must be non-negative.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double remainingBalance = debitVal - creditVal;
                    ledgerModel.setValueAt(debitVal, selected, 3);
                    ledgerModel.setValueAt(creditVal, selected, 4);
                    ledgerModel.setValueAt(remainingBalance, selected, 5);

                    updateTotals();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number format for Debit or Credit.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSupplierEntry() {
        int selected = ledgerTable.getSelectedRow();
        if (selected != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected entry?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String billNo = ledgerModel.getValueAt(selected, 0).toString();
                ledgerModel.removeRow(selected);
                updateTotals();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupplierLedgerFrame::new);
    }
}

// Formatter for JDatePicker
class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public Object stringToValue(String text) throws java.text.ParseException {
        return dateFormat.parse(text);
    }

    @Override
    public String valueToString(Object value) throws java.text.ParseException {
        if (value == null) return "";
        if (value instanceof java.util.GregorianCalendar) {
            return dateFormat.format(((GregorianCalendar) value).getTime());
        } else if (value instanceof java.util.Date) {
            return dateFormat.format((Date) value);
        }
        return "";
    }
}