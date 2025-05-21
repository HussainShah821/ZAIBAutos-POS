import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SupplierLedgerFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private JTable supplierTable, ledgerTable;
    private DefaultTableModel supplierModel, ledgerModel;
    private JLabel supplierNameLabel, supplierPhoneLabel;
    private int selectedSupplierRow = -1;

    public SupplierLedgerFrame() {
        setTitle("Supplier Ledger");
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
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return titleLabel;
    }

    private JPanel initWrapperPanel(JLabel title, JPanel contentPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(contentPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel supplierListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        supplierModel = new DefaultTableModel(new String[]{"Supplier Name", "Phone No"}, 0);
        supplierTable = new JTable(supplierModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim().toLowerCase();
                filterSuppliers(text);
            }
        });

        JButton addButton = new JButton("Add Supplier");
        JButton deleteButton = new JButton("Delete Supplier");

        addButton.addActionListener(e -> addSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(searchField, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        supplierNameLabel = new JLabel("Supplier: ");
        supplierPhoneLabel = new JLabel("Phone: ");
        supplierNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        supplierPhoneLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        infoPanel.add(supplierNameLabel);
        infoPanel.add(supplierPhoneLabel);

        ledgerModel = new DefaultTableModel(new String[]{
                "V No/Bill No", "Date", "Remarks", "Debit", "Credit", "Balance"
        }, 0);
        ledgerTable = new JTable(ledgerModel);
        JScrollPane tableScroll = new JScrollPane(ledgerTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addEntryButton = new JButton("Add Entry");
        JButton deleteEntryButton = new JButton("Delete Entry");
        JButton generateInvoiceButton = new JButton("Generate Invoice");
        JButton backButton = new JButton("Back to Suppliers");

        addEntryButton.addActionListener(e -> addSupplierEntry());
        deleteEntryButton.addActionListener(e -> deleteSupplierEntry());

        generateInvoiceButton.addActionListener(e -> {
            if (ledgerModel.getRowCount() > 0) {
                String supplierName = supplierNameLabel.getText().replace("Supplier: ", "");
                String phone = supplierPhoneLabel.getText().replace("Phone: ", "");
                InvoiceGenerator invoiceGenerator = new InvoiceGenerator(ledgerModel, supplierName, phone);
                invoiceGenerator.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "No entries to generate invoice", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "List"));

        buttonPanel.add(addEntryButton);
        buttonPanel.add(deleteEntryButton);
        buttonPanel.add(generateInvoiceButton);
        buttonPanel.add(backButton);

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showSupplierDetailPanel() {
        String name = supplierModel.getValueAt(selectedSupplierRow, 0).toString();
        String phone = supplierModel.getValueAt(selectedSupplierRow, 1).toString();

        supplierNameLabel.setText("Supplier: " + name);
        supplierPhoneLabel.setText("Phone: " + phone);

        ledgerModel.setRowCount(0);
        ledgerModel.addRow(new Object[]{"INV-001", new SimpleDateFormat("dd-MM-yyyy").format(new Date()), "Initial balance", "0", "1000", "1000"});
        ledgerModel.addRow(new Object[]{"INV-002", new SimpleDateFormat("dd-MM-yyyy").format(new Date()), "Purchase", "500", "0", "500"});

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
        panel.add(new JLabel("Supplier Name:")); panel.add(name);
        panel.add(new JLabel("Phone No:")); panel.add(phone);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            supplierModel.addRow(new Object[]{name.getText(), phone.getText()});
        }
    }

    private void deleteSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this supplier?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                supplierModel.removeRow(selectedRow);
            }
        }
    }

    private void addSupplierEntry() {
        JTextField billNo = new JTextField();
        JTextField date = new JTextField(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        JTextField remarks = new JTextField();
        JTextField debit = new JTextField("0");
        JTextField credit = new JTextField("0");
        JTextField balance = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("Bill No:")); panel.add(billNo);
        panel.add(new JLabel("Date:")); panel.add(date);
        panel.add(new JLabel("Remarks:")); panel.add(remarks);
        panel.add(new JLabel("Debit:")); panel.add(debit);
        panel.add(new JLabel("Credit:")); panel.add(credit);
        panel.add(new JLabel("Balance:")); panel.add(balance);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier Entry", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            ledgerModel.addRow(new Object[]{
                    billNo.getText(),
                    date.getText(),
                    remarks.getText(),
                    debit.getText(),
                    credit.getText(),
                    balance.getText()
            });
        }
    }

    private void deleteSupplierEntry() {
        int selected = ledgerTable.getSelectedRow();
        if (selected != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected entry?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ledgerModel.removeRow(selected);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupplierLedgerFrame::new);
    }
}
