// // import java.awt.*;
// // import javax.swing.*;
// // import javax.swing.table.*;
// // class BillPanel extends JPanel {
// //     public BillPanel(String customerName, String phone, String voucherNo, String dateStr, DefaultTableModel purchaseTableModel, double total, double paid, double balance) {
// //         setLayout(null);
// //         setPreferredSize(new Dimension(600, 700));
// //         setBackground(Color.WHITE);

// //         JLabel shopLabel = new JLabel("Zaib AUTOS", JLabel.CENTER);
// //         shopLabel.setFont(new Font("Arial", Font.BOLD, 22));
// //         shopLabel.setBounds(0, 10, 600, 30);
// //         add(shopLabel);

// //         JLabel addrLabel = new JLabel("Ghotki City Gt. Road | Ph: 0715825524 | Mob: 03002841006", JLabel.CENTER);
// //         addrLabel.setBounds(0, 40, 600, 20);
// //         add(addrLabel);

// //         JLabel dateVoucher = new JLabel("Date: " + dateStr + "       Voucher#: " + voucherNo);
// //         dateVoucher.setBounds(20, 70, 400, 20);
// //         add(dateVoucher);

// //         JLabel customerInfo = new JLabel("Customer: " + customerName + " | Contact: " + phone);
// //         customerInfo.setBounds(20, 90, 500, 20);
// //         add(customerInfo);

// //         // Table
// //         JTable table = new JTable(purchaseTableModel);
// //         JScrollPane scroll = new JScrollPane(table);
// //         scroll.setBounds(20, 120, 560, 400);
// //         add(scroll);

// //         JLabel totalLabel = new JLabel("Total: " + String.format("%.2f", total));
// //         totalLabel.setBounds(400, 540, 200, 20);
// //         totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
// //         add(totalLabel);

// //         JLabel paidLabel = new JLabel("Paid: " + String.format("%.2f", paid));
// //         paidLabel.setBounds(400, 560, 200, 20);
// //         paidLabel.setHorizontalAlignment(SwingConstants.RIGHT);
// //         add(paidLabel);

// //         JLabel balanceLabel = new JLabel("Balance: " + String.format("%.2f", balance));
// //         balanceLabel.setBounds(400, 580, 200, 20);
// //         balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
// //         add(balanceLabel);

// //         JLabel words = new JLabel("In Words: " + convertToWords((int) total) + " Only");
// //         words.setBounds(20, 620, 560, 20);
// //         add(words);

// //         JLabel thanks = new JLabel("Thank you for your business!", JLabel.CENTER);
// //         thanks.setBounds(0, 680, 600, 30);
// //         thanks.setFont(new Font("Arial", Font.BOLD, 14));
// //         add(thanks);
 
// //     }
// //     private String convertToWords(int number) {
// //         String[] units = {
// //             "", "One", "Two", "Three", "Four", "Five",
// //             "Six", "Seven", "Eight", "Nine", "Ten", "Eleven",
// //             "Twelve", "Thirteen", "Fourteen", "Fifteen",
// //             "Sixteen", "Seventeen", "Eighteen", "Nineteen"
// //         };
// //         String[] tens = {
// //             "", "", "Twenty", "Thirty", "Forty", "Fifty",
// //             "Sixty", "Seventy", "Eighty", "Ninety"
// //         };
    
// //         if (number == 0) return "Zero";
    
// //         String words = "";
// //         if (number >= 1000) {
// //             words += units[number / 1000] + " Thousand ";
// //             number %= 1000;
// //         }
// //         if (number >= 100) {
// //             words += units[number / 100] + " Hundred ";
// //             number %= 100;
// //         }
// //         if (number >= 20) {
// //             words += tens[number / 10] + " ";
// //             number %= 10;
// //         }
// //         if (number > 0) {
// //             words += units[number] + " ";
// //         }
    
// //         return words.trim();
// //     }
    
// // }
// import java.awt.*;
// import javax.swing.*;
// import javax.swing.table.*;
// import java.text.SimpleDateFormat;
// import java.util.Date;

// class BillPanel extends JPanel {
//     public BillPanel(String customerName, String phone, String voucherNo, String dateStr, 
//                     DefaultTableModel purchaseTableModel, double total, double paid, double balance) {
//         setLayout(null);
//         setPreferredSize(new Dimension(600, 800));
//         setBackground(Color.WHITE);

//         // Shop header
//         JLabel shopLabel = new JLabel("LS AUTOS", JLabel.CENTER);
//         shopLabel.setFont(new Font("Arial", Font.BOLD, 24));
//         shopLabel.setBounds(0, 10, 600, 30);
//         add(shopLabel);

//         JLabel addrLabel = new JLabel("RACE COURSE ROAD SUNKUR", JLabel.CENTER);
//         addrLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//         addrLabel.setBounds(0, 40, 600, 20);
//         add(addrLabel);

//         // Estimate section
//         JLabel estimateLabel = new JLabel("ESTIMATE", JLabel.CENTER);
//         estimateLabel.setFont(new Font("Arial", Font.BOLD, 16));
//         estimateLabel.setBounds(0, 70, 600, 20);
//         add(estimateLabel);

//         // Customer info
//         JLabel customerInfoLabel = new JLabel("Customer Name: " + customerName);
//         customerInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
//         customerInfoLabel.setBounds(20, 100, 400, 20);
//         add(customerInfoLabel);

//         JLabel addressLabel = new JLabel("Address: " + (customerName.contains("GHOTKI") ? "GHOTKI" : ""));
//         addressLabel.setBounds(20, 120, 400, 20);
//         add(addressLabel);

//         JLabel cityLabel = new JLabel("City: GHOTKI");
//         cityLabel.setBounds(20, 140, 400, 20);
//         add(cityLabel);

//         JLabel contactLabel = new JLabel("Contact Inf.");
//         contactLabel.setBounds(20, 160, 400, 20);
//         add(contactLabel);

//         // Divider line
//         JSeparator separator = new JSeparator();
//         separator.setBounds(20, 180, 560, 2);
//         add(separator);

//         // Site Item Dates header
//         JLabel siteItemsLabel = new JLabel("Site Item Dates:");
//         siteItemsLabel.setFont(new Font("Arial", Font.BOLD, 12));
//         siteItemsLabel.setBounds(20, 190, 200, 20);
//         add(siteItemsLabel);

//         // Table with custom model to match your example
//         DefaultTableModel billTableModel = new DefaultTableModel(
//             new String[]{"", "10 and 1", "Type", "Gry", "Horn", "EDM", "Rate", "Total"}, 0) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return false;
//             }
//         };

//         // Copy data from purchase table to bill table format
//         for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
//             billTableModel.addRow(new Object[]{
//                 i + 1,
//                 purchaseTableModel.getValueAt(i, 1), // Item
//                 purchaseTableModel.getValueAt(i, 3), // Type
//                 purchaseTableModel.getValueAt(i, 2), // Brand (as Gry)
//                 purchaseTableModel.getValueAt(i, 5), // Quantity (as Horn)
//                 purchaseTableModel.getValueAt(i, 4), // UOM (as EDM)
//                 purchaseTableModel.getValueAt(i, 6), // Price (as Rate)
//                 purchaseTableModel.getValueAt(i, 7)  // Total
//             });
//         }

//         JTable table = new JTable(billTableModel);
//         table.setFont(new Font("Arial", Font.PLAIN, 12));
//         table.setRowHeight(25);
//         table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
//         // Set column widths
//         table.getColumnModel().getColumn(0).setPreferredWidth(30);  // S.No
//         table.getColumnModel().getColumn(1).setPreferredWidth(150); // Item
//         table.getColumnModel().getColumn(2).setPreferredWidth(50);  // Type
//         table.getColumnModel().getColumn(3).setPreferredWidth(50);  // Gry
//         table.getColumnModel().getColumn(4).setPreferredWidth(50);  // Horn
//         table.getColumnModel().getColumn(5).setPreferredWidth(50);  // EDM
//         table.getColumnModel().getColumn(6).setPreferredWidth(50);  // Rate
//         table.getColumnModel().getColumn(7).setPreferredWidth(70); // Total

//         JScrollPane scroll = new JScrollPane(table);
//         scroll.setBounds(20, 210, 560, billTableModel.getRowCount() * 25 + 25);
//         add(scroll);

//         // Transporter section
//         JSeparator separator2 = new JSeparator();
//         separator2.setBounds(20, scroll.getY() + scroll.getHeight() + 10, 560, 2);
//         add(separator2);

//         JLabel transporterLabel = new JLabel("Transporter");
//         transporterLabel.setFont(new Font("Arial", Font.BOLD, 12));
//         transporterLabel.setBounds(20, separator2.getY() + 15, 200, 20);
//         add(transporterLabel);

//         JLabel deliverDateLabel = new JLabel("Deliver Date: " + new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
//         deliverDateLabel.setBounds(20, transporterLabel.getY() + 25, 200, 20);
//         add(deliverDateLabel);

//         JLabel deliverToLabel = new JLabel("Deliver To: ");
//         deliverToLabel.setBounds(20, deliverDateLabel.getY() + 25, 200, 20);
//         add(deliverToLabel);

//         // Totals section
//         JSeparator separator3 = new JSeparator();
//         separator3.setBounds(20, deliverToLabel.getY() + 30, 560, 2);
//         add(separator3);

//         JLabel totalLabel = new JLabel("Total: " + String.format("%.3f", total));
//         totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
//         totalLabel.setBounds(450, separator3.getY() + 10, 130, 20);
//         totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//         add(totalLabel);

//         JLabel discountLabel = new JLabel("Total Discount: 0");
//         discountLabel.setBounds(450, totalLabel.getY() + 25, 130, 20);
//         discountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//         add(discountLabel);

//         JLabel chargesLabel = new JLabel("Total Charges: 0");
//         chargesLabel.setBounds(450, discountLabel.getY() + 25, 130, 20);
//         chargesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//         add(chargesLabel);

//         JLabel grandTotalLabel = new JLabel("Grand Total: " + String.format("%.3f", total));
//         grandTotalLabel.setFont(new Font("Arial", Font.BOLD, 14));
//         grandTotalLabel.setBounds(450, chargesLabel.getY() + 25, 130, 25);
//         grandTotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//         add(grandTotalLabel);

//         // Footer
//         JLabel thanksLabel = new JLabel("Thank you for your business!", JLabel.CENTER);
//         thanksLabel.setFont(new Font("Arial", Font.BOLD, 14));
//         thanksLabel.setBounds(0, grandTotalLabel.getY() + 40, 600, 20);
//         add(thanksLabel);
//     }
// }
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BillPanel extends JPanel {
    public BillPanel(String customerName, String phone, String voucherNo, String dateStr, 
                    DefaultTableModel purchaseTableModel, double total, double paid, double balance) {
        setLayout(null);
        setPreferredSize(new Dimension(600, 900)); // Increased height to accommodate all elements
        setBackground(Color.WHITE);

        // Shop header - Centered and bold
        JLabel shopLabel = new JLabel("LS AUTOS", JLabel.CENTER);
        shopLabel.setFont(new Font("Arial", Font.BOLD, 24));
        shopLabel.setBounds(0, 10, 600, 30);
        add(shopLabel);

        // Address - Centered below shop name
        JLabel addrLabel = new JLabel("RACE COURSE ROAD SUNKUR", JLabel.CENTER);
        addrLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        addrLabel.setBounds(0, 40, 600, 20);
        add(addrLabel);

        // Estimate heading - Centered and bold
        JLabel estimateLabel = new JLabel("ESTIMATE", JLabel.CENTER);
        estimateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        estimateLabel.setBounds(0, 70, 600, 25);
        add(estimateLabel);

        // Customer information section
        JLabel customerNameLabel = new JLabel("Customer Name: " + customerName);
        customerNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        customerNameLabel.setBounds(50, 110, 500, 20);
        add(customerNameLabel);

        JLabel addressLabel = new JLabel("Address: GHOTKI");
        addressLabel.setBounds(50, 135, 500, 20);
        add(addressLabel);

        JLabel cityLabel = new JLabel("City: GHOTKI");
        cityLabel.setBounds(50, 160, 500, 20);
        add(cityLabel);

        JLabel contactLabel = new JLabel("Contact Inf.");
        contactLabel.setBounds(50, 185, 500, 20);
        add(contactLabel);

        // Divider line
        JSeparator separator = new JSeparator();
        separator.setBounds(20, 210, 560, 2);
        add(separator);

        // Site Item Dates heading
        JLabel siteItemsLabel = new JLabel("Site Item Dates:");
        siteItemsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        siteItemsLabel.setBounds(20, 220, 200, 20);
        add(siteItemsLabel);

        // Create table with exact column headers from the image
        DefaultTableModel billTableModel = new DefaultTableModel(
            new String[]{"", "10 and 1", "Type", "Gry", "Horn", "EDM", "Rate", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Copy data from purchase table to bill table format
        for (int i = 0; i < purchaseTableModel.getRowCount(); i++) {
            billTableModel.addRow(new Object[]{
                i + 1, // Serial number
                purchaseTableModel.getValueAt(i, 1), // Item name (10 and 1)
                purchaseTableModel.getValueAt(i, 3), // Type
                purchaseTableModel.getValueAt(i, 2), // Brand (as Gry)
                purchaseTableModel.getValueAt(i, 5), // Quantity (as Horn)
                purchaseTableModel.getValueAt(i, 4), // UOM (as EDM)
                purchaseTableModel.getValueAt(i, 6), // Price (as Rate)
                purchaseTableModel.getValueAt(i, 7)  // Total
            });
        }

        // Configure table appearance
        JTable table = new JTable(billTableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set exact column widths to match the image
        table.getColumnModel().getColumn(0).setPreferredWidth(30);  // Serial number
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // 10 and 1 (Item)
        table.getColumnModel().getColumn(2).setPreferredWidth(60);  // Type
        table.getColumnModel().getColumn(3).setPreferredWidth(60);  // Gry
        table.getColumnModel().getColumn(4).setPreferredWidth(60);  // Horn
        table.getColumnModel().getColumn(5).setPreferredWidth(60);  // EDM
        table.getColumnModel().getColumn(6).setPreferredWidth(60);  // Rate
        table.getColumnModel().getColumn(7).setPreferredWidth(80);  // Total
       // Add this after creating your table in BillPanel
table.getTableHeader().setBackground(Color.WHITE);
table.getTableHeader().setForeground(Color.BLACK);
table.setShowGrid(true);
table.setGridColor(Color.LIGHT_GRAY);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 240, 560, billTableModel.getRowCount() * 25 + 25);
        
         // After creating the JScrollPane
scroll.getViewport().setOpaque(false);
scroll.setOpaque(false);
scroll.getViewport().setBackground(Color.WHITE);
table.setOpaque(false);
((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
    .setOpaque(false);
    add(scroll);
        // Transporter section
        JSeparator separator2 = new JSeparator();
        separator2.setBounds(20, scroll.getY() + scroll.getHeight() + 10, 560, 2);
        add(separator2);

        JLabel transporterLabel = new JLabel("Transporter");
        transporterLabel.setFont(new Font("Arial", Font.BOLD, 12));
        transporterLabel.setBounds(20, separator2.getY() + 15, 200, 20);
        add(transporterLabel);

        JLabel deliverDateLabel = new JLabel("Deliver Date: " + new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        deliverDateLabel.setBounds(20, transporterLabel.getY() + 25, 300, 20);
        add(deliverDateLabel);

        JLabel noOTLabel = new JLabel("No OT");
        noOTLabel.setBounds(20, deliverDateLabel.getY() + 25, 300, 20);
        add(noOTLabel);

        JLabel deliverToLabel = new JLabel("Deliver To: ");
        deliverToLabel.setBounds(20, noOTLabel.getY() + 25, 300, 20);
        add(deliverToLabel);

        // Totals section
        JSeparator separator3 = new JSeparator();
        separator3.setBounds(20, deliverToLabel.getY() + 30, 560, 2);
        add(separator3);

        JLabel toolsLabel = new JLabel("Tools");
        toolsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        toolsLabel.setBounds(20, separator3.getY() + 15, 200, 20);
        add(toolsLabel);

        JLabel totalDiscountLabel = new JLabel("Total Discount:");
        totalDiscountLabel.setBounds(350, toolsLabel.getY(), 100, 20);
        add(totalDiscountLabel);

        JLabel discountValueLabel = new JLabel("0");
        discountValueLabel.setBounds(470, toolsLabel.getY(), 100, 20);
        discountValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(discountValueLabel);

        JLabel totalChargesLabel = new JLabel("Total Charges:");
        totalChargesLabel.setBounds(350, toolsLabel.getY() + 25, 100, 20);
        add(totalChargesLabel);

        JLabel chargesValueLabel = new JLabel("0");
        chargesValueLabel.setBounds(470, toolsLabel.getY() + 25, 100, 20);
        chargesValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(chargesValueLabel);

        JLabel grandTollLabel = new JLabel("Grand Toll:");
        grandTollLabel.setBounds(350, toolsLabel.getY() + 50, 100, 20);
        add(grandTollLabel);

        JLabel grandTollValueLabel = new JLabel(String.format("%.3f", total));
        grandTollValueLabel.setBounds(470, toolsLabel.getY() + 50, 100, 20);
        grandTollValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(grandTollValueLabel);

        // Footer with thank you message
        JLabel thanksLabel = new JLabel("Thank you for your business!", JLabel.CENTER);
        thanksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        thanksLabel.setBounds(0, grandTollValueLabel.getY() + 40, 600, 20);
        add(thanksLabel);
    }
    private String convertToWords(double number) {
        if (number == 0) {
            return "Zero";
        }
        
        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        String[] teens = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", 
                         "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", 
                        "Sixty", "Seventy", "Eighty", "Ninety"};
        
        long num = (long) number;
        if (num < 0) {
            return "Minus " + convertToWords(-num);
        }
        
        if (num < 10) {
            return units[(int) num];
        }
        
        if (num < 20) {
            return teens[(int) (num - 10)];
        }
        
        if (num < 100) {
            return tens[(int) (num / 10)] + ((num % 10 != 0) ? " " + units[(int) (num % 10)] : "");
        }
        
        if (num < 1000) {
            return units[(int) (num / 100)] + " Hundred" + ((num % 100 != 0) ? " and " + convertToWords(num % 100) : "");
        }
        
        if (num < 100000) {
            return convertToWords(num / 1000) + " Thousand" + ((num % 1000 != 0) ? " " + convertToWords(num % 1000) : "");
        }
        
        if (num < 10000000) {
            return convertToWords(num / 100000) + " Lakh" + ((num % 100000 != 0) ? " " + convertToWords(num % 100000) : "");
        }
        
        return convertToWords(num / 10000000) + " Crore" + ((num % 10000000 != 0) ? " " + convertToWords(num % 10000000) : "");
    }
}