import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceGenerator extends JFrame {
    private JTextField customerNameField, phoneField, fromDateField, toDateField;
    private JTextField totalDiffField, totalChargesField, totalAssetsField;
    private JButton printButton;
    private DefaultTableModel ledgerModel;

    public InvoiceGenerator(DefaultTableModel ledgerModel, String supplierName, String phone, Date startDate, Date endDate) {
        this.ledgerModel = ledgerModel;
        initializeUI(ledgerModel, supplierName, phone, startDate, endDate);
    }

    private void initializeUI(DefaultTableModel ledgerModel, String supplierName, String phone, Date startDate, Date endDate) {
        setTitle("ZAIB-AUTOS Invoice Generator");
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

        // Customer Information Panel (Uneditable)
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new GridLayout(4, 2, 10, 10));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Supplier Information"));
        customerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        customerNameField = new JTextField(supplierName);
        customerNameField.setEditable(false);
        phoneField = new JTextField(phone);
        phoneField.setEditable(false);
        fromDateField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(startDate));
        fromDateField.setEditable(false);
        toDateField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(endDate));
        toDateField.setEditable(false);

        customerPanel.add(new JLabel("Supplier:"));
        customerPanel.add(customerNameField);
        customerPanel.add(new JLabel("Phone:"));
        customerPanel.add(phoneField);
        customerPanel.add(new JLabel("From Date:"));
        customerPanel.add(fromDateField);
        customerPanel.add(new JLabel("To Date:"));
        customerPanel.add(toDateField);

        // Invoice Summary Panel (Uneditable)
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(3, 2, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Invoice Summary"));
        summaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Calculate initial totals
        double totalDebit = 0.0;
        double totalCredit = 0.0;
        for (int i = 0; i < ledgerModel.getRowCount(); i++) {
            try {
                totalDebit += Double.parseDouble(ledgerModel.getValueAt(i, 3).toString());
                totalCredit += Double.parseDouble(ledgerModel.getValueAt(i, 4).toString());
            } catch (NumberFormatException e) {
                // Handle invalid numbers
            }
        }
        double remainingBalance = totalDebit - totalCredit;

        totalAssetsField = new JTextField(String.format("%,.2f", totalDebit));
        totalAssetsField.setEditable(false);
        totalChargesField = new JTextField(String.format("%,.2f", totalCredit));
        totalChargesField.setEditable(false);
        totalDiffField = new JTextField(String.format("%,.2f", remainingBalance));
        totalDiffField.setEditable(false);

        summaryPanel.add(new JLabel("Total Debit (PKR):"));
        summaryPanel.add(totalAssetsField);
        summaryPanel.add(new JLabel("Total Credit (PKR):"));
        summaryPanel.add(totalChargesField);
        summaryPanel.add(new JLabel("Total Remaining Balance (PKR):"));
        summaryPanel.add(totalDiffField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        printButton = new JButton("Print Invoice");

        printButton.addActionListener(e -> printInvoice());

        buttonPanel.add(printButton);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(customerPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(buttonPanel);

        add(contentPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private void calculateTotals(DefaultTableModel ledgerModel) {
        try {
            double totalDebit = 0.0;
            double totalCredit = 0.0;

            for (int i = 0; i < ledgerModel.getRowCount(); i++) {
                try {
                    totalDebit += Double.parseDouble(ledgerModel.getValueAt(i, 3).toString());
                    totalCredit += Double.parseDouble(ledgerModel.getValueAt(i, 4).toString());
                } catch (NumberFormatException e) {
                    // Handle invalid numbers
                }
            }

            double remainingBalance = totalDebit - totalCredit;
            totalDiffField.setText(String.format("%,.2f", remainingBalance));
            totalChargesField.setText(String.format("%,.2f", totalCredit));
            totalAssetsField.setText(String.format("%,.2f", totalDebit));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in data", "Error", JOptionPane.ERROR_MESSAGE);
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