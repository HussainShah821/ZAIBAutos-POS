import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class InvoiceGenerator extends JFrame {
    private JTextField customerNameField, addressField, cityField;
    private JTextField vendorField, dateField, testField, degreeCardField, tplField;
    private JTextField totalDiffField, totalChargesField, totalAssetsField, featuresField;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JButton printButton, calculateButton;

    public InvoiceGenerator(DefaultTableModel ledgerModel, String supplierName, String phone) {
        initializeUI(ledgerModel, supplierName, phone);
    }

    private void initializeUI(DefaultTableModel ledgerModel, String supplierName, String phone) {
        setTitle("ZAIB-AUTOS Invoice Generator");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel (Centered)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("ZAIB-AUTOS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel addressLabel = new JLabel("GT ROAD GHOTKI");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(addressLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Customer Information Panel (Editable)
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new GridLayout(3, 2, 10, 10));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Supplier Information"));
        customerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        customerNameField = new JTextField(supplierName);
        addressField = new JTextField("GT ROAD GHOTKI");
        cityField = new JTextField("GHOTKI");

        customerPanel.add(new JLabel("Supplier:"));
        customerPanel.add(customerNameField);
        customerPanel.add(new JLabel("Phone:"));
        customerPanel.add(new JTextField(phone));
        customerPanel.add(new JLabel("Address:"));
        customerPanel.add(addressField);

        // Transaction Table - Use data from ledgerModel
        String[] columnNames = {"Bill No", "Date", "Remarks", "Debit (PKR)", "Credit (PKR)", "Balance (PKR)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        // Copy data from ledgerModel to our tableModel
        for (int i = 0; i < ledgerModel.getRowCount(); i++) {
            Vector<Object> row = new Vector<>();
            for (int j = 0; j < ledgerModel.getColumnCount(); j++) {
                row.add(ledgerModel.getValueAt(i, j));
            }
            tableModel.addRow(row);
        }

        transactionTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(transactionTable);

        // Rest of the code remains the same...
        // [Previous code for metaPanel, summaryPanel, buttons, etc.]

        // Invoice Meta Panel (Editable)
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new GridLayout(3, 2, 10, 10));
        metaPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        vendorField = new JTextField("01-20-2013");
        dateField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        testField = new JTextField();
        degreeCardField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        tplField = new JTextField();

        metaPanel.add(new JLabel("Vendor:"));
        metaPanel.add(vendorField);
        metaPanel.add(new JLabel("Date:"));
        metaPanel.add(dateField);
        metaPanel.add(new JLabel("Test #:"));
        metaPanel.add(testField);
        metaPanel.add(new JLabel("Degree Card:"));
        metaPanel.add(degreeCardField);
        metaPanel.add(new JLabel("TPL #:"));
        metaPanel.add(tplField);

        // Invoice Summary Panel (Editable)
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(4, 2, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Invoice Summary"));
        summaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calculate initial totals
        double totalDebit = 0;
        double totalCredit = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                totalDebit += Double.parseDouble(tableModel.getValueAt(i, 3).toString().replace(",", ""));
                totalCredit += Double.parseDouble(tableModel.getValueAt(i, 4).toString().replace(",", ""));
            } catch (NumberFormatException e) {
                // Handle invalid numbers
            }
        }
        double balance = totalDebit - totalCredit;

        totalDiffField = new JTextField(String.format("%,.2f", balance));
        totalChargesField = new JTextField(String.format("%,.2f", totalCredit));
        totalAssetsField = new JTextField(String.format("%,.2f", totalDebit));
        featuresField = new JTextField("Features and dates for RS: 99,000");

        summaryPanel.add(new JLabel("Total Difference (PKR):"));
        summaryPanel.add(totalDiffField);
        summaryPanel.add(new JLabel("Total Charges (PKR):"));
        summaryPanel.add(totalChargesField);
        summaryPanel.add(new JLabel("Total Assets (PKR):"));
        summaryPanel.add(totalAssetsField);
        summaryPanel.add(new JLabel("Features:"));
        summaryPanel.add(featuresField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        calculateButton = new JButton("Calculate Totals");
        printButton = new JButton("Print Invoice");

        calculateButton.addActionListener(e -> calculateTotals());
        printButton.addActionListener(e -> printInvoice());

        buttonPanel.add(calculateButton);
        buttonPanel.add(printButton);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(customerPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(tableScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(metaPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(buttonPanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    // Rest of the methods remain the same...
    private void calculateTotals() {
        try {
            double totalDebit = 0;
            double totalCredit = 0;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String debitStr = tableModel.getValueAt(i, 3).toString().replace(",", "");
                String creditStr = tableModel.getValueAt(i, 4).toString().replace(",", "");

                totalDebit += Double.parseDouble(debitStr);
                totalCredit += Double.parseDouble(creditStr);
            }

            double balance = totalDebit - totalCredit;
            totalDiffField.setText(String.format("%,.2f", balance));
            totalChargesField.setText(String.format("%,.2f", totalCredit));
            totalAssetsField.setText(String.format("%,.2f", totalDebit));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in table", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printInvoice() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("ZAIB-AUTOS Invoice");

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Print the main content panel directly
                getContentPane().printAll(g2d);

                return Printable.PAGE_EXISTS;
            }
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Print error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
