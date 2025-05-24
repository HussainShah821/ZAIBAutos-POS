import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class SupplierLedgerFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private JTable supplierTable, ledgerTable;
    private DefaultTableModel supplierModel, ledgerModel;
    private JLabel supplierNameLabel, supplierPhoneLabel;
    private int selectedSupplierRow = -1;
    private static int nextBillNo = 1;

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
        JButton backButton = createStyledButton("Back");

        addButton.addActionListener(e -> addSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
        backButton.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(searchField, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
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

        /*
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
            String sql = "SELECT supplier_name, phone FROM suppliers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            supplierModel.setRowCount(0);
            while (rs.next()) {
                supplierModel.addRow(new Object[]{
                    rs.getString("supplier_name"),
                    rs.getString("phone")
                });
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        */

        return panel;
    }

    private JPanel supplierDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
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
                "V No/Bill No", "Date", "Remarks", "Debit", "Credit", "Balance"
        }, 0);
        ledgerTable = new JTable(ledgerModel);
        ledgerTable.setFont(new Font("Arial", Font.PLAIN, 14));
        ledgerTable.setRowHeight(25);
        ledgerTable.setGridColor(new Color(200, 200, 200));
        ledgerTable.setShowGrid(true);
        JScrollPane tableScroll = new JScrollPane(ledgerTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addEntryButton = createStyledButton("Add Entry");
        JButton deleteEntryButton = createStyledButton("Delete Entry");
        JButton generateInvoiceButton = createStyledButton("Generate Invoice");
        JButton backButton = createStyledButton("Back to Suppliers");

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
        DataManager dataManager = DataManager.getInstance();
        DataManager.Supplier supplier = dataManager.getSuppliers().get(selectedSupplierRow);
        supplierNameLabel.setText("Supplier: " + supplier.name);
        supplierPhoneLabel.setText("Phone: " + supplier.phone);

        ledgerModel.setRowCount(0);
        ledgerModel.addRow(new Object[]{"SUP-001", new SimpleDateFormat("dd-MM-yyyy").format(new Date()), "Initial balance", "0", "1000", "1000"});
        ledgerModel.addRow(new Object[]{"SUP-002", new SimpleDateFormat("dd-MM-yyyy").format(new Date()), "Purchase", "500", "0", "500"});

        /*
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
            String sql = "SELECT bill_no, entry_date, remarks, debit, credit, balance " +
                         "FROM supplier_ledger WHERE supplier_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, supplier.id);
            ResultSet rs = stmt.executeQuery();
            ledgerModel.setRowCount(0);
            while (rs.next()) {
                ledgerModel.addRow(new Object[]{
                    rs.getString("bill_no"),
                    new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("entry_date")),
                    rs.getString("remarks"),
                    rs.getDouble("debit"),
                    rs.getDouble("credit"),
                    rs.getDouble("balance")
                });
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        */

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

            /*
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
                String sql = "INSERT INTO suppliers (supplier_name, phone) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, supplierName);
                stmt.setString(2, supplierPhone);
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            */
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

                /*
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
                    String sql = "DELETE FROM suppliers WHERE supplier_name = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, supplierModel.getValueAt(selectedRow, 0).toString());
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                */
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
        JTextField balance = new JTextField("0");

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
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
        panel.add(new JLabel("Balance:"));
        panel.add(balance);

        /*
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
            String sql = "SELECT MAX(entry_id) + 1 AS next_id FROM supplier_ledger";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int nextId = rs.next() ? rs.getInt("next_id") : 1;
            billNo.setText(String.format("SUP-%03d", nextId));
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        */

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Supplier Entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double debitVal = Double.parseDouble(debit.getText().trim());
                double creditVal = Double.parseDouble(credit.getText().trim());
                double balanceVal = Double.parseDouble(balance.getText().trim());
                if (debitVal < 0 || creditVal < 0 || balanceVal < 0) {
                    JOptionPane.showMessageDialog(this, "Debit, Credit, and Balance must be non-negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ledgerModel.addRow(new Object[]{
                        billNo.getText(),
                        date.getText(),
                        remarks.getText(),
                        debitVal,
                        creditVal,
                        balanceVal
                });

                /*
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
                    String sql = "INSERT INTO supplier_ledger (supplier_id, bill_no, entry_date, remarks, debit, credit, balance) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    DataManager dataManager = DataManager.getInstance();
                    stmt.setInt(1, dataManager.getSuppliers().get(selectedSupplierRow).id);
                    stmt.setString(2, billNo.getText());
                    stmt.setString(3, date.getText());
                    stmt.setString(4, remarks.getText());
                    stmt.setDouble(5, debitVal);
                    stmt.setDouble(6, creditVal);
                    stmt.setDouble(7, balanceVal);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                */
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for Debit, Credit, or Balance.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSupplierEntry() {
        int selected = ledgerTable.getSelectedRow();
        if (selected != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected entry?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String billNo = ledgerModel.getValueAt(selected, 0).toString();

                /*
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
                    String sql = "DELETE FROM supplier_ledger WHERE bill_no = ? AND supplier_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    DataManager dataManager = DataManager.getInstance();
                    stmt.setString(1, billNo);
                    stmt.setInt(2, dataManager.getSuppliers().get(selectedSupplierRow).id);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                })": {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                */

                ledgerModel.removeRow(selected);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupplierLedgerFrame::new);
    }
}