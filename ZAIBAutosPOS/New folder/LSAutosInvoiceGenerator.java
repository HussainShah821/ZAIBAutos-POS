import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LSAutosInvoiceGenerator extends JFrame {
    private JTextField customerNameField, addressField, cityField, contactField;
    private JTextField voucherField, dateField;
    private JTextField deliverDateField, deliverToField;
    private JTable itemsTable;
    private JButton generateButton, clearButton;

    public LSAutosInvoiceGenerator() {
        setTitle("LS Autos Invoice Generator");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Header panel - matches the image exactly
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("# LS AUTOS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel addressLabel = new JLabel("RACE COURSE ROAD SUKKUR", JLabel.CENTER);
        addressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel estimateLabel = new JLabel("## Estimate", JLabel.CENTER);
        estimateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        estimateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(addressLabel);
        headerPanel.add(estimateLabel);
        
        mainPanel.add(headerPanel);

        // Customer details panel - matches the image layout
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
        customerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("Customer Name"));
        customerNameField = new JTextField(20);
        namePanel.add(customerNameField);
        
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addressPanel.add(new JLabel("Address   "));
        addressField = new JTextField(20);
        addressPanel.add(addressField);
        
        JPanel cityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cityPanel.add(new JLabel("City   "));
        cityField = new JTextField(10);
        cityPanel.add(cityField);
        cityPanel.add(new JLabel("Contact Inf."));
        contactField = new JTextField(15);
        cityPanel.add(contactField);
        
        customerPanel.add(namePanel);
        customerPanel.add(addressPanel);
        customerPanel.add(cityPanel);
        
        // Add divider line
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(650, 5));
        
        mainPanel.add(customerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(separator);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Contact info panel - matches the exact format from image
        JPanel contactInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contactInfoPanel.add(new JLabel("Pts:"));
        JTextField ptsField = new JTextField(10);
        contactInfoPanel.add(ptsField);
        contactInfoPanel.add(new JLabel("Mobile:"));
        JTextField mobileField = new JTextField(12);
        contactInfoPanel.add(mobileField);
        
        mainPanel.add(contactInfoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Voucher and date panel
        JPanel voucherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voucherPanel.add(new JLabel("Voucher #:"));
        voucherField = new JTextField(8);
        voucherPanel.add(voucherField);
        voucherPanel.add(new JLabel("Date:"));
        dateField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()), 12);
        voucherPanel.add(dateField);
        
        mainPanel.add(voucherPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Items table - matches the exact columns from the image
        String[] columnNames = {"Std", "Item Desc", "B mnd", "Type", "Qty", "Uom", "Chk", "Rate", "Total"};
        Object[][] data = {
                {1, "", "", "", "", "", "", "", ""},
                {2, "", "", "", "", "", "", "", ""},
                {3, "", "", "", "", "", "", "", ""},
                {4, "", "", "", "", "", "", "", ""}
        };
        
        itemsTable = new JTable(data, columnNames);
        itemsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(650, 150));
        
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Transporter panel - matches the image
        JPanel transporterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transporterPanel.add(new JLabel("Transporter"));
        
        JPanel deliverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deliverPanel.add(new JLabel("Deliver Date"));
        deliverDateField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()), 12);
        deliverPanel.add(deliverDateField);
        deliverPanel.add(new JLabel("No Of"));
        deliverPanel.add(new JLabel("Deliver To"));
        deliverToField = new JTextField(15);
        deliverPanel.add(deliverToField);
        
        mainPanel.add(transporterPanel);
        mainPanel.add(deliverPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Totals panel - matches the image exactly
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel totalsLabel = new JLabel("Totals");
        totalsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.add(new JLabel("Total"));
        JTextField totalField = new JTextField(10);
        totalPanel.add(totalField);
        
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        discountPanel.add(new JLabel("Total Discount"));
        JTextField discountField = new JTextField(10);
        discountField.setText("0");
        discountPanel.add(discountField);
        
        JPanel chargesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chargesPanel.add(new JLabel("Total Charges"));
        JTextField chargesField = new JTextField(10);
        chargesField.setText("0");
        chargesPanel.add(chargesField);
        
        JPanel grandTotalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        grandTotalPanel.add(new JLabel("Grand Total"));
        JTextField grandTotalField = new JTextField(10);
        grandTotalPanel.add(grandTotalField);
        
        totalsPanel.add(totalsLabel);
        totalsPanel.add(totalPanel);
        totalsPanel.add(discountPanel);
        totalsPanel.add(chargesPanel);
        totalsPanel.add(grandTotalPanel);
        
        mainPanel.add(totalsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        generateButton = new JButton("Generate Invoice");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateInvoice();
            }
        });
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        buttonPanel.add(generateButton);
        buttonPanel.add(clearButton);
        
        mainPanel.add(buttonPanel);
    }

    private void generateInvoice() {
        // Calculate totals
        double grandTotal = 0;
        for (int i = 0; i < itemsTable.getRowCount(); i++) {
            try {
                String qtyStr = itemsTable.getValueAt(i, 4).toString();
                String rateStr = itemsTable.getValueAt(i, 7).toString();
                
                if (!qtyStr.isEmpty() && !rateStr.isEmpty()) {
                    int qty = Integer.parseInt(qtyStr);
                    double rate = Double.parseDouble(rateStr);
                    double total = qty * rate;
                    itemsTable.setValueAt(total, i, 8);
                    grandTotal += total;
                }
            } catch (NumberFormatException e) {
                // Skip if values are not numbers
            }
        }

        // Create invoice preview
        StringBuilder invoiceText = new StringBuilder();
        invoiceText.append("# LS AUTOS\n");
        invoiceText.append("RACE COURSE ROAD SUKKUR\n\n");
        invoiceText.append("## Estimate\n");
        invoiceText.append("Customer Name: ").append(customerNameField.getText()).append("\n");
        invoiceText.append("Address:    ").append(addressField.getText()).append("\n");
        invoiceText.append("City:   ").append(cityField.getText()).append("    Contact Inf.\n");
        invoiceText.append("---\n");
        invoiceText.append("Pts: ").append("0716826524").append(",  "); // Hardcoded as in image
        invoiceText.append("Mobile: ").append("03002841000").append(".\n\n"); // Hardcoded as in image
        invoiceText.append("Voucher #: ").append(voucherField.getText()).append("\n");
        invoiceText.append("Date: ").append(dateField.getText()).append("\n\n");
        
        invoiceText.append("| Std | Item Desc | B mnd | Type | Qty | Uom | Chk | Rate | Total |\n");
        for (int i = 0; i < itemsTable.getRowCount(); i++) {
            if (!itemsTable.getValueAt(i, 1).toString().isEmpty()) {
                invoiceText.append("| ").append(itemsTable.getValueAt(i, 0)).append(" | ");
                invoiceText.append(itemsTable.getValueAt(i, 1)).append(" | ");
                invoiceText.append(itemsTable.getValueAt(i, 2)).append(" | ");
                invoiceText.append(itemsTable.getValueAt(i, 3)).append(" | ");
                invoiceText.append(itemsTable.getValueAt(i, 4)).append(" | ");
                invoiceText.append(itemsTable.getValueAt(i, 5)).append(" | ");
                invoiceText.append(itemsTable.getValueAt(i, 6)).append(" | ");
                invoiceText.append(itemsTable.getValueAt(i, 7)).append(" | ");
                invoiceText.append(itemsTable.getValueAt(i, 8)).append(" |\n");
            }
        }
        
        invoiceText.append("\n---\n");
        invoiceText.append("Transporter\n");
        invoiceText.append("Deliver Date: ").append(deliverDateField.getText()).append("\n");
        invoiceText.append("No Of\n");
        invoiceText.append("Deliver To: ").append(deliverToField.getText()).append("\n\n");
        
        invoiceText.append("---\n");
        invoiceText.append("Totals\n");
        invoiceText.append("Total Discount: 0\n");
        invoiceText.append("Total Charges: 0\n");
        invoiceText.append("Grand Total: ").append(grandTotal).append("\n");
        invoiceText.append(NumberToWords.convert((int) grandTotal)).append("\n");

        // Show preview
        JTextArea previewArea = new JTextArea(invoiceText.toString());
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Invoice Preview", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {
        customerNameField.setText("");
        addressField.setText("");
        cityField.setText("");
        contactField.setText("");
        voucherField.setText("");
        
        for (int i = 0; i < itemsTable.getRowCount(); i++) {
            for (int j = 1; j < itemsTable.getColumnCount(); j++) {
                itemsTable.setValueAt("", i, j);
            }
        }
        
        deliverToField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LSAutosInvoiceGenerator().setVisible(true);
            }
        });
    }
}

class NumberToWords {
    private static final String[] units = {
            "", "one", "two", "three", "four", "five", "six", "seven",
            "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    };

    private static final String[] tens = {
            "",        // 0
            "",       // 1
            "twenty",  // 2
            "thirty", // 3
            "forty",  // 4
            "fifty",  // 5
            "sixty",  // 6
            "seventy", // 7
            "eighty",  // 8
            "ninety"   // 9
    };

    public static String convert(int n) {
        if (n == 0) {
            return "zero";
        }
        return convertHelper(n).trim().toUpperCase();
    }

    private static String convertHelper(int n) {
        if (n < 20) {
            return units[n];
        }

        if (n < 100) {
            return tens[n / 10] + " " + units[n % 10];
        }

        if (n < 1000) {
            return units[n / 100] + " hundred " + convertHelper(n % 100);
        }

        if (n < 100000) {
            return convertHelper(n / 1000) + " thousand " + convertHelper(n % 1000);
        }

        if (n < 10000000) {
            return convertHelper(n / 100000) + " lakh " + convertHelper(n % 100000);
        }

        return convertHelper(n / 10000000) + " crore " + convertHelper(n % 10000000);
    }
}