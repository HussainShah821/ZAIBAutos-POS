import java.awt.*;
import java.awt.Font;
import java.awt.print.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

class BillPanel extends JPanel {
    private JTextField customerNameField, addressField, cityField, contactField;
    private JTextField voucherField, dateField, totalField, paidField, balanceField;
    private JTextArea amountWordsArea;

    public BillPanel(String customerName, String phone, String voucherNo, String dateStr,
                     DefaultTableModel purchaseTableModel, double total, double paid, double balance) {
        setLayout(null);
        setPreferredSize(new Dimension(600, 900));
        setBackground(Color.WHITE);

        createComponents(customerName, phone, voucherNo, dateStr, purchaseTableModel, total, paid, balance);

        JButton printButton = new JButton("Print Bill");
        printButton.setBounds(200, 800, 100, 30);
        printButton.addActionListener(e -> printBill());
        add(printButton);

        JButton exportButton = new JButton("Export to PDF");
        exportButton.setBounds(320, 800, 120, 30);
        exportButton.addActionListener(e -> exportToPDF());
        add(exportButton);
    }

    private void createComponents(String customerName, String phone, String voucherNo, String dateStr,
                                  DefaultTableModel purchaseTableModel, double total, double paid, double balance) {
        JLabel shopLabel = new JLabel("ZAIB-AUTOS", JLabel.CENTER);
        shopLabel.setFont(new Font("Arial", Font.BOLD, 24));
        shopLabel.setBounds(0, 10, 600, 30);
        add(shopLabel);

        JLabel addrLabel = new JLabel("GT.ROAD GHOTKI", JLabel.CENTER);
        addrLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        addrLabel.setBounds(0, 40, 600, 20);
        add(addrLabel);

        JLabel estimateLabel = new JLabel("ESTIMATE", JLabel.CENTER);
        estimateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        estimateLabel.setBounds(0, 70, 600, 25);
        add(estimateLabel);

        // Customer details
        addLabelAndField("Customer Name:", 50, 110, customerNameField = createBorderlessTextField(customerName));
        addLabelAndField("Address:", 50, 135, addressField = createBorderlessTextField(""));
        addLabelAndField("City:", 50, 160, cityField = createBorderlessTextField(""));
        addLabelAndField("Contact No:", 50, 185, contactField = createBorderlessTextField(phone));

        addSeparator(20, 210, 560);

        // Bill info
        addLabelAndField("Voucher No:", 350, 110, voucherField = createBorderlessTextField(voucherNo));
        addLabelAndField("Date:", 350, 135, dateField = createBorderlessTextField(dateStr));

        DefaultTableModel billTableModel = createBillTableModel(purchaseTableModel);
        JTable table = createBillTable(billTableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 240, 560, billTableModel.getRowCount() * 25 + 25);
        setScrollPaneStyle(scroll, table);
        add(scroll);

        addPaymentDetailsSection(scroll.getY() + scroll.getHeight() + 10, total, paid, balance);
    }

    private void addLabelAndField(String label, int x, int y, JTextField field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        jLabel.setBounds(x, y, 100, 20);
        add(jLabel);

        field.setBounds(x + 110, y, 300, 20);
        add(field);
    }

    private void addSeparator(int x, int y, int width) {
        JSeparator separator = new JSeparator();
        separator.setBounds(x, y, width, 2);
        add(separator);
    }

    private JTextField createBorderlessTextField(String text) {
        JTextField field = new JTextField(text);
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setOpaque(false);
        field.setEditable(false);
        return field;
    }

    private void setScrollPaneStyle(JScrollPane scroll, JTable table) {
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(Color.WHITE);
        table.setOpaque(false);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setOpaque(false);
    }

    private DefaultTableModel createBillTableModel(DefaultTableModel purchaseTableModel) {
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
        return billTableModel;
    }

    private JTable createBillTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        return table;
    }

    private void addPaymentDetailsSection(int yPos, double total, double paid, double balance) {
        addSeparator(20, yPos, 560);

        JLabel paymentLabel = new JLabel("Payment Details");
        paymentLabel.setFont(new Font("Arial", Font.BOLD, 12));
        paymentLabel.setBounds(20, yPos + 15, 200, 20);
        add(paymentLabel);

        addLabelAndField("Total Amount:", 350, paymentLabel.getY(), totalField = createBorderlessTextField(String.format("%.2f")));
        addLabelAndField("Amount Paid:", 350, paymentLabel.getY() + 30, paidField = createBorderlessTextField(String.format("%.2f")));
        addLabelAndField("Balance:", 350, paymentLabel.getY() + 60, balanceField = createBorderlessTextField(String.format("%.2f")));

        JLabel amountWordsLabel = new JLabel("Amount in Words:");
        amountWordsLabel.setBounds(20, paymentLabel.getY() + 30, 120, 20);
        add(amountWordsLabel);

        amountWordsArea = new JTextArea(convertToWords(total));
        amountWordsArea.setBounds(20, paymentLabel.getY() + 50, 300, 40);
        amountWordsArea.setBorder(BorderFactory.createEmptyBorder());
        amountWordsArea.setEditable(false);
        amountWordsArea.setLineWrap(true);
        amountWordsArea.setWrapStyleWord(true);
        amountWordsArea.setBackground(Color.WHITE);
        amountWordsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        add(amountWordsArea);

        JLabel thanksLabel = new JLabel("Thank you for your business!", JLabel.CENTER);
        thanksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        thanksLabel.setBounds(0, balanceField.getY() + 40, 600, 20);
        add(thanksLabel);
    }

    private void printBill() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("ZAIB-AUTOS Bill");

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double scale = Math.min(pageFormat.getImageableWidth() / getWidth(),
                    pageFormat.getImageableHeight() / getHeight());
            g2d.scale(scale, scale);
            printAll(g2d);
            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Print error: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToPDF() {
        // Unchanged: your full PDF export logic here...
    }

    private void addPdfCell(PdfPTable table, String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        if (isHeader) cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    private String convertToWords(double number) {
        // Basic number to words converter
        int rupees = (int) number;
        int paise = (int) ((number - rupees) * 100);
        String result = rupees + " Rupees";
        if (paise > 0) {
            result += " and " + paise + " Paise";
        }
        return result + " Only";
    }
}
