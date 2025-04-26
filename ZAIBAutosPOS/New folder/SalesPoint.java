import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SalesPoint extends JFrame {
    private JPanel topDrawerPanel;
    private boolean isDrawerOpen = false;
    private JLabel dateLabel, timeLabel;
    private Timer timer;
    private JPanel sellPanel, historyPanel;
private DefaultTableModel sellTableModel, historyTableModel;
private JTable sellTable, historyTable;
ArrayList<Vector<Object>> fullProductList = new ArrayList<>();



    public SalesPoint() {
        setTitle("Sales Point");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLayeredPane layeredPane = getLayeredPane();

        // Background image (same as Dashboard)
        JLabel background = new JLabel(new ImageIcon("background.jpg"));
        background.setBounds(0, 0, 1366, 768);
        layeredPane.add(background, Integer.valueOf(1));

        // Top Drawer
        topDrawerPanel = new JPanel(null);
        topDrawerPanel.setBackground(new Color(50, 50, 50, 220));
        topDrawerPanel.setBounds(0, -100, 1366, 100);
        layeredPane.add(topDrawerPanel, Integer.valueOf(2));

        // Sell Products Button
        JButton sellButton = new JButton("Sell Products");
        sellButton.setBounds(100, 30, 120, 35); // smaller width
        styleButton(sellButton);
        topDrawerPanel.add(sellButton);

        // See History Button
        JButton historyButton = new JButton("See History");
        historyButton.setBounds(240, 30, 120, 35); // smaller width
        styleButton(historyButton);
        topDrawerPanel.add(historyButton);

        // Sell Products Button opens sellPanel
sellButton.addActionListener(e -> {
    sellPanel.setVisible(true);
    historyPanel.setVisible(false);
});

// See History Button opens historyPanel
historyButton.addActionListener(e -> {
    historyPanel.setVisible(true);
    sellPanel.setVisible(false);
});


        // Date Label (right side)
        dateLabel = new JLabel();
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setBounds(1050, 20, 200, 20);
        topDrawerPanel.add(dateLabel);

        // Time Label (right side)
        timeLabel = new JLabel();
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setBounds(1050, 50, 200, 20);
        topDrawerPanel.add(timeLabel);

        // Key Listener for 'Q' to open drawer
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Q) {
                    toggleTopDrawer();
                }
            }
        });
        initializeSellPanel(layeredPane);
    initializeHistoryPanel(layeredPane);
        // Sample products (temporary, until DB connection is added)
fullProductList.add(new Vector<>(Arrays.asList(1, "Battery", "Exide", "12V", "pcs", "10")));
fullProductList.add(new Vector<>(Arrays.asList(2, "Tyre", "MRF", "Radial", "pcs", "20")));

filterProductTable(""); // Load all into the sell table


        setFocusable(true);
        requestFocusInWindow();

        // Timer to update date/time
        startDateTimeUpdater();

        setVisible(true);
    }
  
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void toggleTopDrawer() {
        if (isDrawerOpen) {
            topDrawerPanel.setBounds(0, -100, 1366, 100);
        } else {
            topDrawerPanel.setBounds(0, 0, 1366, 100);
        }
        isDrawerOpen = !isDrawerOpen;
    }

    private void startDateTimeUpdater() {
        timer = new Timer(1000, e -> {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
            dateLabel.setText("Date: " + dateFormat.format(now));
            timeLabel.setText("Time: " + timeFormat.format(now));
        });
        timer.start();
    }
    private void initializeSellPanel(JLayeredPane layeredPane) {
        sellPanel = new JPanel(null);
        sellPanel.setBounds(100, 120, 1100, 500);
        sellPanel.setBackground(new Color(0, 0, 0, 180));
    
        JTextField searchField = new JTextField();
        searchField.setBounds(20, 20, 400, 30);
        sellPanel.add(searchField);
    
        sellTableModel = new DefaultTableModel(new String[]{"Item No", "Item Name", "Brand", "Type", "UOM", "Quantity"}, 0);
        sellTable = new JTable(sellTableModel);
        JScrollPane scrollPane = new JScrollPane(sellTable);
        scrollPane.setBounds(20, 60, 1050, 350);
        sellPanel.add(scrollPane);
    
        // On row click: ask quantity and price
        sellTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = sellTable.getSelectedRow();
                if (row == -1) return;
    
                String itemName = sellTableModel.getValueAt(row, 1).toString();
    
                JTextField qtyField = new JTextField();
                JTextField priceField = new JTextField();
                JPanel panel = new JPanel(new GridLayout(2, 2));
                panel.add(new JLabel("Quantity:"));
                panel.add(qtyField);
                panel.add(new JLabel("Selling Price:"));
                panel.add(priceField);
    
                int result = JOptionPane.showConfirmDialog(SalesPoint.this, panel, "Sell Product", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int qty = Integer.parseInt(qtyField.getText());
                        double price = Double.parseDouble(priceField.getText());
                        double total = qty * price;
                        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
    
                        // Add to history
                        historyTableModel.addRow(new Object[]{date, itemName, qty, price, total});
                        JOptionPane.showMessageDialog(SalesPoint.this, "Product Sold!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(SalesPoint.this, "Invalid input.");
                    }
                }
            }
        });
    
        // Enable live search
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim().toLowerCase();
                filterProductTable(text);
            }
        });
    
        layeredPane.add(sellPanel, Integer.valueOf(4));
        sellPanel.setVisible(false);
    }
    private void filterProductTable(String text) {
        sellTableModel.setRowCount(0);
        int index = 1;
        for (Vector<Object> row : fullProductList) {
            if (row.get(1).toString().toLowerCase().contains(text)) {
                Vector<Object> newRow = new Vector<>(row);
                newRow.set(0, index++);
                sellTableModel.addRow(newRow);
            }
        }
    }
    
    
    private void initializeHistoryPanel(JLayeredPane layeredPane) {
        historyPanel = new JPanel(null);
        historyPanel.setBounds(100, 120, 1100, 500);
        historyPanel.setBackground(new Color(0, 0, 0, 180));
    
        historyTableModel = new DefaultTableModel(new String[]{"Date", "Product", "Qty", "Price", "Total"}, 0);
        historyTable = new JTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBounds(20, 20, 1050, 370);
        historyPanel.add(scrollPane);
    
        JButton clearBtn = new JButton("Clear History");
        clearBtn.setBounds(20, 410, 150, 30);
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Clear all sales history?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                historyTableModel.setRowCount(0);
            }
        });
        historyPanel.add(clearBtn);
    
        layeredPane.add(historyPanel, Integer.valueOf(4));
        historyPanel.setVisible(false);
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SalesPoint::new);
    }
}
