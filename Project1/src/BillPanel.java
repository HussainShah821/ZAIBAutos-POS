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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Shop Header
        JLabel shopLabel = new JLabel("ZAIB-AUTOS", JLabel.CENTER);
        shopLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        shopLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(shopLabel);

        JLabel addrLabel = new JLabel("GT.ROAD GHOTKI", JLabel.CENTER);
        addrLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        addrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(addrLabel);

        JLabel estimateLabel = new JLabel("ESTIMATE", JLabel.CENTER);
        estimateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        estimateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(estimateLabel);

        add(Box.createVerticalStrut(10));

        // Customer and Bill Info
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(new JLabel("Customer Name:"));
        customerNameLabel = new JLabel(customerName);
        infoPanel.add(customerNameLabel);
        infoPanel.add(new JLabel("Voucher No:"));
        voucherLabel = new JLabel(voucherNo);
        infoPanel.add(voucherLabel);
        infoPanel.add(new JLabel("Contact No:"));
        contactLabel = new JLabel(phone);
        infoPanel.add(contactLabel);
        infoPanel.add(new JLabel("Date:"));
        dateLabel = new JLabel(dateStr);
        infoPanel.add(dateLabel);
        add(infoPanel);

        add(Box.createVerticalStrut(10));

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
        billTable = new JTable(billTableModel);
        billTable.setFont(new Font("Arial", Font.PLAIN, 12));
        billTable.setRowHeight(25);
        billTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        billTable.setShowGrid(true);
        billTable.setGridColor(Color.LIGHT_GRAY);
        JScrollPane scroll = new JScrollPane(billTable);
        scroll.setPreferredSize(new Dimension(600, billTableModel.getRowCount() * 25 + 25));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll);

        add(Box.createVerticalStrut(10));

        // Payment Details
        JPanel paymentPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.add(new JLabel("Total Amount:"));
        totalLabel = new JLabel(String.format("%.2f", total));
        paymentPanel.add(totalLabel);
        paymentPanel.add(new JLabel("Amount Paid:"));
        paidLabel = new JLabel(String.format("%.2f", paid));
        paymentPanel.add(paidLabel);
        paymentPanel.add(new JLabel("Balance:"));
        balanceLabel = new JLabel(String.format("%.2f", balance));
        paymentPanel.add(balanceLabel);
        paymentPanel.add(new JLabel("Amount in Words:"));
        amountWordsLabel = new JLabel(convertToWords(total));
        paymentPanel.add(amountWordsLabel);
        add(paymentPanel);

        add(Box.createVerticalStrut(10));

        // Thank You
        JLabel thanksLabel = new JLabel("Thank you for your business!", JLabel.CENTER);
        thanksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        thanksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(thanksLabel);

        add(Box.createVerticalStrut(10));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton printButton = new JButton("Print Bill");
        printButton.addActionListener(e -> printBill());
        buttonPanel.add(printButton);

        JButton exportPdfButton = new JButton("Export to PDF");
        exportPdfButton.addActionListener(e -> exportToPDF());
        buttonPanel.add(exportPdfButton);

        add(buttonPanel);

        // Adjust panel height dynamically
        int totalHeight = 150 + (billTableModel.getRowCount() * 25 + 25) + 150 + 50; // Header + table + payment + buttons
        setPreferredSize(new Dimension(600, totalHeight));
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
                writer.write("BT /F1 12 Tf 50 750 Td (ZAIB-AUTOS) Tj ET\n");
                writer.write("BT /F1 10 Tf 50 730 Td (GT.ROAD GHOTKI) Tj ET\n");
                writer.write("BT /F1 12 Tf 50 710 Td (ESTIMATE) Tj ET\n");
                writer.write("BT /F1 10 Tf 50 690 Td (Customer Name: " + customerNameLabel.getText() + ") Tj ET\n");
                writer.write("BT /F1 10 Tf 50 670 Td (Contact No: " + contactLabel.getText() + ") Tj ET\n");
                writer.write("BT /F1 10 Tf 50 650 Td (Voucher No: " + voucherLabel.getText() + ") Tj ET\n");
                writer.write("BT /F1 10 Tf 50 630 Td (Date: " + dateLabel.getText() + ") Tj ET\n");
                int yPos = 610;
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Items Purchased:) Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (  Item Name  Brand  Type  Qty  UOM  Rate  Total) Tj ET\n");
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
                    writer.write("BT /F1 10 Tf 50 " + yPos + " Td (" + line + ") Tj ET\n");
                    yPos -= 20;
                }
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Payment Details:) Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Total Amount: " + totalLabel.getText() + ") Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Amount Paid: " + paidLabel.getText() + ") Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Balance: " + balanceLabel.getText() + ") Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Amount in Words: " + amountWordsLabel.getText() + ") Tj ET\n");
                yPos -= 20;
                writer.write("BT /F1 10 Tf 50 " + yPos + " Td (Thank you for your business!) Tj ET\n");
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