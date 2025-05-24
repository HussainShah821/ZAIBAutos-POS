import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Vector;

public class InventoryFrame extends JFrame {
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private List<String> productNames = new ArrayList<>();
    private int itemCount = 0;

    public InventoryFrame() {
        setTitle("Inventory Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Inventory Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Center panel (search + table)
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        centerPanel.add(searchField, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(new String[]{"Item No", "Item Name", "Brand", "Type", "UOM", "Quantity"}, 0);
        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Add Product");
        JButton updateButton = new JButton("Update Product");
        JButton deleteButton = new JButton("Delete Product");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);

        // Listeners
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    resetProductTableOrder();
                } else {
                    sortProductToTop(text);
                }
            }
        });

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        setVisible(true);
    }

    private void sortProductToTop(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            resetProductTableOrder();
            return;
        }

        List<Vector<Object>> matched = new ArrayList<>();
        List<Vector<Object>> unmatched = new ArrayList<>();

        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            @SuppressWarnings("unchecked")
            Vector<Object> row = (Vector<Object>) productTableModel.getDataVector().get(i);
            if (row.get(1).toString().toLowerCase().contains(productName.toLowerCase())) {
                matched.add(row);
            } else {
                unmatched.add(row);
            }
        }

        productTableModel.setRowCount(0);
        int no = 1;
        for (Vector<Object> row : matched) {
            row.set(0, no++);
            productTableModel.addRow(row);
        }
        for (Vector<Object> row : unmatched) {
            row.set(0, no++);
            productTableModel.addRow(row);
        }
        itemCount = productTableModel.getRowCount();
    }

    private void resetProductTableOrder() {
        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            productTableModel.setValueAt(i + 1, i, 0);
        }
    }

    private void addProduct() {
        JTextField itemNameField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField uomField = new JTextField();
        JTextField quantityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Item Name:")); panel.add(itemNameField);
        panel.add(new JLabel("Brand:")); panel.add(brandField);
        panel.add(new JLabel("Type:")); panel.add(typeField);
        panel.add(new JLabel("UOM:")); panel.add(uomField);
        panel.add(new JLabel("Quantity:")); panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText();
            String brand = brandField.getText();
            String type = typeField.getText();
            String uom = uomField.getText();
            String qty = quantityField.getText();

            itemCount++;
            productTableModel.addRow(new Object[]{
                    itemCount, itemName, brand, type, uom, qty
            });
            productNames.add(itemName);

            for (int i = 0; i < productTableModel.getRowCount(); i++) {
                productTableModel.setValueAt(i + 1, i, 0);
            }
            itemCount = productTableModel.getRowCount(); // sync count
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            String currentQuantity = productTableModel.getValueAt(selectedRow, 5).toString();
            String newQuantity = JOptionPane.showInputDialog(this, "Enter new quantity:", currentQuantity);
            if (newQuantity != null && !newQuantity.trim().isEmpty()) {
                productTableModel.setValueAt(newQuantity, selectedRow, 5);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            String productName = productTableModel.getValueAt(selectedRow, 1).toString();
            productTableModel.removeRow(selectedRow);
            productNames.remove(productName);

            for (int i = 0; i < productTableModel.getRowCount(); i++) {
                productTableModel.setValueAt(i + 1, i, 0);
            }
            itemCount = productTableModel.getRowCount();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InventoryFrame::new);
    }
}
