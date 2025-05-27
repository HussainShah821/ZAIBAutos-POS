import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.print.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class BillPanel extends JPanel implements Printable {
    private JLabel customerNameLabel, contactLabel, voucherLabel, dateLabel, totalLabel, paidLabel, balanceLabel, amountWordsLabel;
    private JTable billTable;
    private DefaultTableModel purchaseTableModel;
    private double total, paid, balance;

    public BillPanel(String customerName, String phone, String voucherNo, String dateStr,
                     DefaultTableModel purchaseTableModel, double total, double paid, double balance) {
        this.purchaseTableModel = purchaseTableModel;
        this.total = total;
        this.paid = paid;
        this.balance = balance;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(249, 250, 251)); // Soft off-white background (#F9FAFB)
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Shop Header
        JLabel shopLabel = new JLabel("ZAIB-AUTOS", JLabel.CENTER);
        shopLabel.setFont(new Font("Roboto", Font.BOLD, 28)); // Modern font, larger size
        shopLabel.setForeground(new Color(70,130,180)); // Deep teal (#0D9488)
        shopLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(shopLabel);

        JLabel addrLabel = new JLabel("GT.ROAD GHOTKI", JLabel.CENTER);
        addrLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        addrLabel.setForeground(new Color(31, 41, 55)); // Dark gray (#1F2937)
        addrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(addrLabel);

        JLabel estimateLabel = new JLabel("ESTIMATE", JLabel.CENTER);
        estimateLabel.setFont(new Font("Roboto", Font.BOLD, 20));
        estimateLabel.setForeground(new Color(70,130,180)); // Deep teal (#0D9488)
        estimateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(estimateLabel);

        add(Box.createVerticalStrut(20)); // Increased spacing

        // Customer and Bill Info
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 8));
        infoPanel.setBackground(new Color(249, 250, 251)); // Match background
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel customerNamePrompt = new JLabel("Customer Name:");
        customerNamePrompt.setFont(new Font("Roboto", Font.PLAIN, 14));
        customerNamePrompt.setForeground(new Color(31, 41, 55));
        infoPanel.add(customerNamePrompt);
        customerNameLabel = new JLabel(customerName);
        customerNameLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        customerNameLabel.setForeground(new Color(31, 41, 55));
        infoPanel.add(customerNameLabel);
        JLabel voucherPrompt = new JLabel("Voucher No:");
        voucherPrompt.setFont(new Font("Roboto", Font.PLAIN, 14));
        voucherPrompt.setForeground(new Color(31, 41, 55));
        infoPanel.add(voucherPrompt);
        voucherLabel = new JLabel(voucherNo);
        voucherLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        voucherLabel.setForeground(new Color(31, 41, 55));
        infoPanel.add(voucherLabel);
        JLabel contactPrompt = new JLabel("Contact No:");
        contactPrompt.setFont(new Font("Roboto", Font.PLAIN, 14));
        contactPrompt.setForeground(new Color(31, 41, 55));
        infoPanel.add(contactPrompt);
        contactLabel = new JLabel(phone);
        contactLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        contactLabel.setForeground(new Color(31, 41, 55));
        infoPanel.add(contactLabel);
        JLabel datePrompt = new JLabel("Date:");
        datePrompt.setFont(new Font("Roboto", Font.PLAIN, 14));
        datePrompt.setForeground(new Color(31, 41, 55));
        infoPanel.add(datePrompt);
        dateLabel = new JLabel(dateStr);
        dateLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(31, 41, 55));
        infoPanel.add(dateLabel);
        add(infoPanel);

        add(Box.createVerticalStrut(20)); // Increased spacing

        // Purchase Table
        DefaultTableModel billTableModel = new DefaultTableModel(
                new String[]{"", "Item Name", "Brand", "Type", "Qty", "UOM", "Rate", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
            billTableModel.addRow(new Object[]{
                    (Object) (i + 1),
                    purchaseTableModel.getValueAt(i, 1),
                    purchaseTableModel.getValueAt(i, 3),
                    purchaseTableModel.getValueAt(i, 2),
                    purchaseTableModel.getValueAt(i, 5),
                    purchaseTableModel.getValueAt(i, 4),
                    purchaseTableModel.getValueAt(i, 6),
                    purchaseTableModel.getValueAt(i, 7)
            });
        }
        billTable = new JTable(billTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(249, 250, 251) : new Color(243, 244, 246)); // Alternating rows (#F3F4F6)
                }
                return c;
            }
        };
        billTable.setFont(new Font("Roboto", Font.PLAIN, 14)); // Increased font size
        billTable.setRowHeight(30); // Increased row height
        JTableHeader tableHeader = billTable.getTableHeader();
        tableHeader.setFont(new Font("Roboto", Font.BOLD, 14));
        tableHeader.setBackground(new Color(70,130,180)); // Deep teal header
        tableHeader.setForeground(new Color(255, 255, 255)); // White text on header
        billTable.setShowGrid(true);
        billTable.setGridColor(new Color(209, 213, 219)); // Lighter gray grid (#D1D5DB)
        JScrollPane scroll = new JScrollPane(billTable);
        scroll.setPreferredSize(new Dimension(650, billTableModel.getRowCount() * 30 + 30));
        scroll.getViewport().setBackground(new Color(249, 250, 251));
        add(scroll);

        add(Box.createVerticalStrut(20)); // Increased spacing

        // Payment Details
        JPanel paymentPanel = new JPanel(new GridLayout(4, 2, 10, 8));
        paymentPanel.setBackground(new Color(249, 250, 251));
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel totalPrompt = new JLabel("Total Amount:");
        totalPrompt.setFont(new Font("Roboto", Font.PLAIN, 14));
        totalPrompt.setForeground(new Color(31, 41, 55));
        paymentPanel.add(totalPrompt);
        totalLabel = new JLabel(String.format("%.2f", total));
        totalLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        totalLabel.setForeground(new Color(31, 41, 55));
        paymentPanel.add(totalLabel);
        JLabel paidPrompt = new JLabel("Amount Paid:");
        paidPrompt.setFont(new Font("Roboto", Font.PLAIN, 14));
        paidPrompt.setForeground(new Color(31, 41, 55));
        paymentPanel.add(paidPrompt);
        paidLabel = new JLabel(String.format("%.2f", paid));
        paidLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        paidLabel.setForeground(new Color(31, 41, 55));
        paymentPanel.add(paidLabel);
        JLabel balancePrompt = new JLabel("Balance:");
        balancePrompt.setFont(new Font("Roboto", Font.PLAIN, 14));
        balancePrompt.setForeground(new Color(31, 41, 55));
        paymentPanel.add(balancePrompt);
        balanceLabel = new JLabel(String.format("%.2f", balance));
        balanceLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        balanceLabel.setForeground(new Color(31, 41, 55));
        paymentPanel.add(balanceLabel);
        JLabel amountWordsPrompt = new JLabel("Amount in Words:");
        amountWordsPrompt.setFont(new Font("Roboto", Font.PLAIN, 14));
        amountWordsPrompt.setForeground(new Color(31, 41, 55));
        paymentPanel.add(amountWordsPrompt);
        amountWordsLabel = new JLabel(convertToWords(total));
        amountWordsLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        amountWordsLabel.setForeground(new Color(31, 41, 55));
        paymentPanel.add(amountWordsLabel);
        add(paymentPanel);

        add(Box.createVerticalStrut(20)); // Increased spacing

        // Thank You
        JLabel thanksLabel = new JLabel("Thank you for your business!", JLabel.CENTER);
        thanksLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        thanksLabel.setForeground(new Color(70,130,180)); // Deep teal
        thanksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(thanksLabel);

        add(Box.createVerticalStrut(20)); // Increased spacing

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(249, 250, 251));
        JButton printButton = new JButton("Print Bill");
        printButton.setFont(new Font("Roboto", Font.BOLD, 14));
        printButton.setBackground(new Color(70,130,180)); // Deep teal
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        printButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        printButton.addActionListener(e -> printBill());
        buttonPanel.add(printButton);

        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.setFont(new Font("Roboto", Font.BOLD, 14));
        exportPdfButton.setBackground(new Color(70,130,180)); 
        exportPdfButton.setForeground(Color.WHITE);
        exportPdfButton.setFocusPainted(false);
        exportPdfButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        exportPdfButton.addActionListener(e -> exportToPDF());
        buttonPanel.add(exportPdfButton);

        add(buttonPanel);

        // Adjust panel height dynamically
        int totalHeight = 180 + (billTableModel.getRowCount() * 30 + 30) + 180 + 60; // Header + table + payment + buttons
        setPreferredSize(new Dimension(700, totalHeight)); // Increased width to 700px
    }

    private String convertToWords(double number) {
        int rupees = (int) number;
        int paise = (int) ((number - rupees) * 100);
        String result = rupees + " Rupees";
        if (paise > 0) {
            result += " and " + paise + " Paise";
        }
        return result + " Only";
    }

    private void printBill() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        job.setJobName("ZAIB-AUTOS Bill");

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Print error: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        double scale = Math.min(pageFormat.getImageableWidth() / getWidth(),
                pageFormat.getImageableHeight() / getHeight());
        g2d.scale(scale, scale);
        printAll(g2d);
        return PAGE_EXISTS;
    }

    private void exportToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Bill as PDF");
        fileChooser.setSelectedFile(new File("ZaibAutos_Bill_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf"));
        int selection = fileChooser.showSaveDialog(this);

        if (selection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write a basic PDF structure (text-only, no external libraries)
                writer.write("%PDF-1.4\n");
                writer.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
                writer.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
                writer.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> >> >> /MediaBox [0 0 612 792] /Contents 4 0 R >>\nendobj\n");
                writer.write("4 0 obj\n<< /Length 5 0 R >>\nstream\n");
                writer.write("BT /F1 14 Tf 50 750 Td (ZAIB-AUTOS) Tj ET\n");
                writer.write("BT /F1 12 Tf 50 730 Td (GT.ROAD GHOTKI) Tj ET\n");
                writer.write("BT /F1 14 Tf 50 710 Td (ESTIMATE) Tj ET\n");
                writer.write("BT /F1 12 Tf 50 690 Td (Customer Name: " + customerNameLabel.getText() + ") Tj ET\n");
                writer.write("BT /F1 12 Tf 50 670 Td (Contact No: " + contactLabel.getText() + ") Tj ET\n");
                writer.write("BT /F1 12 Tf 50 650 Td (Voucher No: " + voucherLabel.getText() + ") Tj ET\n");
                writer.write("BT /F1 12 Tf 50 630 Td (Date: " + dateLabel.getText() + ") Tj ET\n");
                int yPos = 610;
                writer.write("BT /F1 12 Tf 50 " + yPos + " Td (Items Purchased:) Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 12 Tf 50 " + yPos + " Td (  Item Name  Brand  Type  Qty  UOM  Rate  Total) Tj ET\n");
                yPos -= 20;
                for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
                    String line = String.format("%d  %s  %s  %s  %s  %s  %.2f  %.2f",
                            (i + 1),
                            purchaseTableModel.getValueAt(i, 1).toString(),
                            purchaseTableModel.getValueAt(i, 2).toString(),
                            purchaseTableModel.getValueAt(i, 3).toString(),
                            purchaseTableModel.getValueAt(i, 5).toString(),
                            purchaseTableModel.getValueAt(i, 4).toString(),
                            Double.parseDouble(purchaseTableModel.getValueAt(i, 6).toString()),
                            Double.parseDouble(purchaseTableModel.getValueAt(i, 7).toString()));
                    writer.write("BT /F1 12 Tf 50 " + yPos + " Td (" + line + ") Tj ET\n");
                    yPos -= 20;
                }
                writer.write("BT /F1 12 Tf 50 " + yPos + " Td (Payment Details:) Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 12 Tf 50 " + yPos + " Td (Total Amount: " + totalLabel.getText() + ") Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 12 Tf 50 " + yPos + " Td (Amount Paid: " + paidLabel.getText() + ") Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 12 Tf 50 " + yPos + " Td (Balance: " + balanceLabel.getText() + ") Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 12 Tf 50 " + yPos + " Td (Amount in Words: " + amountWordsLabel.getText() + ") Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 12 Tf 50 " + yPos + " Td (Thank you for your business!) Tj ET\n");
                writer.write("endstream\nendobj\n");
                writer.write("5 0 obj\n" + (yPos + 1000) + "\nendobj\n");
                writer.write("trailer\n<< /Root 1 0 R >>\n%%EOF\n");

                JOptionPane.showMessageDialog(this, "PDF exported successfully to " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}