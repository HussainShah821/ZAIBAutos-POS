class BillPanel extends JPanel {
    public BillPanel(String customerName, String phone, String voucherNo, String dateStr, DefaultTableModel purchaseTableModel, double total, double paid, double balance) {
        setLayout(null);
        setPreferredSize(new Dimension(600, 800));
        setBackground(Color.WHITE);

        JLabel shopLabel = new JLabel("LS AUTOS", JLabel.CENTER);
        shopLabel.setFont(new Font("Arial", Font.BOLD, 22));
        shopLabel.setBounds(0, 10, 600, 30);
        add(shopLabel);

        JLabel addrLabel = new JLabel("Race Course Road, Sukkur | Ph: 0715825524 | Mob: 03002841006", JLabel.CENTER);
        addrLabel.setBounds(0, 40, 600, 20);
        add(addrLabel);

        JLabel dateVoucher = new JLabel("Date: " + dateStr + "       Voucher#: " + voucherNo);
        dateVoucher.setBounds(20, 70, 400, 20);
        add(dateVoucher);

        JLabel customerInfo = new JLabel("Customer: " + customerName + " | Contact: " + phone);
        customerInfo.setBounds(20, 90, 500, 20);
        add(customerInfo);

        // Table
        JTable table = new JTable(purchaseTableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 120, 560, 400);
        add(scroll);

        JLabel totalLabel = new JLabel("Total: " + String.format("%.2f", total));
        totalLabel.setBounds(400, 540, 200, 20);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(totalLabel);

        JLabel paidLabel = new JLabel("Paid: " + String.format("%.2f", paid));
        paidLabel.setBounds(400, 560, 200, 20);
        paidLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(paidLabel);

        JLabel balanceLabel = new JLabel("Balance: " + String.format("%.2f", balance));
        balanceLabel.setBounds(400, 580, 200, 20);
        balanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(balanceLabel);

        JLabel words = new JLabel("In Words: " + convertToWords((int) total) + " Only");
        words.setBounds(20, 620, 560, 20);
        add(words);

        JLabel thanks = new JLabel("Thank you for your business!", JLabel.CENTER);
        thanks.setBounds(0, 680, 600, 30);
        thanks.setFont(new Font("Arial", Font.BOLD, 14));
        add(thanks);
    }
}
