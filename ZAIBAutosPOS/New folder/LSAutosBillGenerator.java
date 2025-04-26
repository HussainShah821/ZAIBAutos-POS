import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LSAutosBillGenerator extends JFrame {
    private JTextField customerNameField, addressField, cityField, contactField;
    private JTextField voucherField, dateField;
    private JTextField deliverDateField, deliverToField;
    private JTable itemsTable;
    private JButton generateButton, clearButton;

    public LSAutosBillGenerator() {
        setTitle("LS Autos Bill Generator");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("# LS AUTOS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel addressLabel = new JLabel("RACE COURSE ROAD SUKKUR", JLabel.CENTER);
        addressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel estimateLabel = new JLabel("## Estimate", JLabel.CENTER);
        estimateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        estimateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(addressLabel);
        headerPanel.add(estimateLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Customer details panel
        JPanel customerPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        
        customerPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField();
        customerPanel.add(customerNameField);
        
        customerPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        customerPanel.add(addressField);
        
        customerPanel.add(new JLabel("City:"));
        cityField = new JTextField();
        customerPanel.add(cityField);
        
        customerPanel.add(new JLabel("Contact Info:"));
        contactField = new JTextField();
        customerPanel.add(contactField);
        
        mainPanel.add(customerPanel, BorderLayout.CENTER);

        // Voucher and date panel
        JPanel voucherPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        voucherPanel.setBorder(BorderFactory.createTitledBorder("Voucher Information"));
        
        voucherPanel.add(new JLabel("Voucher #:"));
        voucherField = new JTextField();
        voucherPanel.add(voucherField);
        
        voucherPanel.add(new JLabel("Date:"));
        dateField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        voucherPanel.add(dateField);
        
        mainPanel.add(voucherPanel, BorderLayout.AFTER_LINE_ENDS);

        // Items table
        String[] columnNames = {"Std", "Item Desc", "B mnd", "Type", "Qty", "Uom", "Chk", "Rate", "Total"};
        Object[][] data = {
                {1, "", "", "", "", "", "", "", ""},
                {2, "", "", "", "", "", "", "", ""},
                {3, "", "", "", "", "", "", "", ""},
                {4, "", "", "", "", "", "", "", ""}
        };
        
        itemsTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Items"));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // Transporter panel
        JPanel transporterPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        transporterPanel.setBorder(BorderFactory.createTitledBorder("Transporter"));
        
        transporterPanel.add(new JLabel("Deliver Date:"));
        deliverDateField = new JTextField(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        transporterPanel.add(deliverDateField);
        
        transporterPanel.add(new JLabel("Deliver To:"));
        deliverToField = new JTextField();
        transporterPanel.add(deliverToField);
        
        mainPanel.add(transporterPanel, BorderLayout.AFTER_LAST_LINE);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        generateButton = new JButton("Generate Bill");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBill();
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
        
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
    }

    private void generateBill() {
        // Calculate totals
        double grandTotal = 0;
        for (int i = 0; i < itemsTable.getRowCount(); i++) {
            try {
                int qty = Integer.parseInt(itemsTable.getValueAt(i, 4).toString());
                double rate = Double.parseDouble(itemsTable.getValueAt(i, 7).toString());
                double total = qty * rate;
                itemsTable.setValueAt(total, i, 8);
                grandTotal += total;
            } catch (NumberFormatException e) {
                // Skip if values are not numbers
            }
        }

        // Create bill preview
        StringBuilder billText = new StringBuilder();
        billText.append("# LS AUTOS\n");
        billText.append("RACE COURSE ROAD SUKKUR\n\n");
        billText.append("## Estimate\n");
        billText.append("Customer Name: ").append(customerNameField.getText()).append("\n");
        billText.append("Address: ").append(addressField.getText()).append("\n");
        billText.append("City: ").append(cityField.getText()).append("\n");
        billText.append("Contact Info: ").append(contactField.getText()).append("\n\n");
        billText.append("Voucher #: ").append(voucherField.getText()).append("\n");
        billText.append("Date: ").append(dateField.getText()).append("\n\n");
        
        billText.append("Items:\n");
        for (int i = 0; i < itemsTable.getRowCount(); i++) {
            if (!itemsTable.getValueAt(i, 1).toString().isEmpty()) {
                billText.append(itemsTable.getValueAt(i, 0)).append("\t");
                billText.append(itemsTable.getValueAt(i, 1)).append("\t");
                billText.append(itemsTable.getValueAt(i, 2)).append("\t");
                billText.append(itemsTable.getValueAt(i, 3)).append("\t");
                billText.append(itemsTable.getValueAt(i, 4)).append("\t");
                billText.append(itemsTable.getValueAt(i, 5)).append("\t");
                billText.append(itemsTable.getValueAt(i, 6)).append("\t");
                billText.append(itemsTable.getValueAt(i, 7)).append("\t");
                billText.append(itemsTable.getValueAt(i, 8)).append("\n");
            }
        }
        
        billText.append("\nTransporter:\n");
        billText.append("Deliver Date: ").append(deliverDateField.getText()).append("\n");
        billText.append("Deliver To: ").append(deliverToField.getText()).append("\n\n");
        
        billText.append("Grand Total: ").append(grandTotal).append("\n");
        billText.append(NumberToWords.convert((int) grandTotal)).append("\n");

        // Show preview
        JTextArea previewArea = new JTextArea(billText.toString());
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Bill Preview", JOptionPane.INFORMATION_MESSAGE);
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
                new LSAutosBillGenerator().setVisible(true);
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