import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainDashboard extends JFrame {
    private JPanel drawerPanel;
    private boolean isDrawerOpen = false;
    private JButton menuButton;
    private JLabel background;
    private int drawerWidth = 250;
    private int screenWidth;
    private int screenHeight;

    public MainDashboard() {
        setTitle("Zaib Autos - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;

        // Layered pane
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.setLayout(null);

        // Background image
        background = new JLabel();
        background.setIcon(new ImageIcon(new ImageIcon("resources/images/background.jpg")
                .getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH)));
        background.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(background, Integer.valueOf(1));

        // Drawer panel
        drawerPanel = new JPanel();
        drawerPanel.setLayout(new BoxLayout(drawerPanel, BoxLayout.Y_AXIS));
        drawerPanel.setBackground(new Color(44, 62, 80)); // #2C3E50
        drawerPanel.setBounds(-drawerWidth, 0, drawerWidth, screenHeight);
        layeredPane.add(drawerPanel, Integer.valueOf(2));

        // Profile info
        JLabel profileLabel = new JLabel("Zaib Autos");
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        profileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
        drawerPanel.add(profileLabel);

        JLabel emailLabel = new JLabel("Asjal Mehmood");
        emailLabel.setForeground(Color.LIGHT_GRAY);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        drawerPanel.add(emailLabel);

        // Drawer buttons in the recommended order
        addDrawerButton("Dashboard", e -> {
            // Already on Dashboard, just close drawer
            closeDrawer();
        });
        addDrawerButton("Inventory Management", e -> {
            new InventoryFrame();
            closeDrawer();
        });
        addDrawerButton("Sales Entry", e -> {
            new SalesEntryFrame();
            closeDrawer();
        });
        addDrawerButton("Customer Ledger", e -> {
            new CustomerLedgerFrame();
            closeDrawer();
        });
        addDrawerButton("Supplier Ledger", e -> {
            new SupplierLedgerFrame();
            closeDrawer();
        });
        addDrawerButton("Customer Credit", e -> {
            new CustomerCreditFrame();
            closeDrawer();
        });
        addDrawerButton("Expenditure Tracking", e -> {
            new ExpenditureTrackingFrame();
            closeDrawer();
        });
        addDrawerButton("Profit Entry", e -> {
            new ProfitEntryFrame();
            closeDrawer();
        });
        addDrawerButton("Sales Analysis", e -> {
            new SalesAnalysisFrame();
            closeDrawer();
        });
        addDrawerButton("Logout", e -> {
            new AdminLogin();
            dispose();
        }, true); // Critical action
        addDrawerButton("Exit", e -> System.exit(0), true); // Critical action

        // Menu button
        ImageIcon menuIcon = new ImageIcon(
                new ImageIcon("resources/images/mainmenu.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        menuButton = new JButton(menuIcon);
        menuButton.setBounds(10, 10, 40, 40);
        styleMenuButton(menuButton);
        menuButton.addActionListener(e -> toggleDrawer());
        layeredPane.add(menuButton, Integer.valueOf(3));

        // Resize listener
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                screenWidth = getWidth();
                screenHeight = getHeight();
                updateLayout();
            }
        });

        // Mouse click to close drawer
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (isDrawerOpen && e.getX() > drawerWidth) {
                    closeDrawer();
                }
            }
        });

        // Placeholder database fetch (commented out, for future dashboard data)
        /*
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
            String sql = "SELECT COUNT(*) as total_sales FROM sales WHERE DATE(sale_date) = CURDATE()";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Example: Display daily sales count on dashboard
                // JLabel salesLabel = new JLabel("Today's Sales: " + rs.getInt("total_sales"));
                // salesLabel.setBounds(100, 100, 200, 30);
                // layeredPane.add(salesLabel, Integer.valueOf(2));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        */

        setVisible(true);
    }

    private void updateLayout() {
        // Resize background
        background.setIcon(new ImageIcon(new ImageIcon("resources/images/background.jpg")
                .getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH)));
        background.setBounds(0, 0, screenWidth, screenHeight);

        // Resize drawer
        drawerPanel.setBounds(isDrawerOpen ? 0 : -drawerWidth, 0, drawerWidth, screenHeight);
    }

    private void addDrawerButton(String text, ActionListener action) {
        addDrawerButton(text, action, false);
    }

    private void addDrawerButton(String text, ActionListener action, boolean isCritical) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(isCritical ? new Color(231, 76, 60) : Color.WHITE); // #E74C3C for critical actions
        button.setBackground(new Color(44, 62, 80)); // #2C3E50
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(drawerWidth - 40, 40));
        button.addActionListener(action);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185)); // #2980B9
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(44, 62, 80)); // #2C3E50
            }
        });
        drawerPanel.add(button);
        drawerPanel.add(Box.createVerticalStrut(5)); // Spacing between buttons
    }

    private void styleMenuButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    private void toggleDrawer() {
        isDrawerOpen = !isDrawerOpen;
        drawerPanel.setBounds(isDrawerOpen ? 0 : -drawerWidth, 0, drawerWidth, screenHeight);
        updateLayout();
        revalidate();
        repaint();
    }

    private void closeDrawer() {
        isDrawerOpen = false;
        drawerPanel.setBounds(-drawerWidth, 0, drawerWidth, screenHeight);
        updateLayout();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainDashboard::new);
    }
}