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
        drawerPanel = new JPanel(null);
        drawerPanel.setBackground(new Color(50, 50, 50, 220));
        drawerPanel.setBounds(-drawerWidth, 0, drawerWidth, screenHeight);
        layeredPane.add(drawerPanel, Integer.valueOf(2));

        // Profile info
        JLabel profileLabel = new JLabel("Zaib Autos");
        profileLabel.setForeground(Color.WHITE);
        profileLabel.setFont(new Font("Arial", Font.BOLD, 20));
        profileLabel.setBounds(64, 60, 200, 30);
        drawerPanel.add(profileLabel);

        JLabel emailLabel = new JLabel("Asjal Mehmood");
        emailLabel.setForeground(Color.LIGHT_GRAY);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        emailLabel.setBounds(50, 100, 200, 30);
        drawerPanel.add(emailLabel);

        // Drawer buttons
        int yOffset = 200;
        addDrawerButton("Inventory Management", yOffset, e -> {
            new InventoryFrame();
            closeDrawer();
        });
        addDrawerButton("Customer Ledger", yOffset += 50, e -> {
            new CustomerLedgerFrame();
            closeDrawer();
        });
        addDrawerButton("Supplier Ledger", yOffset += 50, e -> {
            new SupplierLedgerFrame();
            closeDrawer();
        });
        addDrawerButton("Sales Entry", yOffset += 50, e -> {
            new SalesEntryFrame();
            closeDrawer();
        });
        addDrawerButton("Expenditure Tracking", yOffset += 50, e -> {
            new ExpenditureTrackingFrame();
            closeDrawer();
        });
        addDrawerButton("Profit Entry", yOffset += 50, e -> {
            new ProfitEntryFrame();
            closeDrawer();
        });
        addDrawerButton("Customer Credit", yOffset += 50, e -> {
            new CustomerCreditFrame();
            closeDrawer();
        });
        addDrawerButton("Sales Analysis", yOffset += 50, e -> {
            new SalesAnalysisFrame();
            closeDrawer();
        });
        addDrawerButton("Logout", yOffset += 50, e -> {
            new AdminLogin();
            dispose();
        });
        addDrawerButton("Exit", yOffset += 50, e -> System.exit(0));

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

    private void addDrawerButton(String text, int y, ActionListener action) {
        JButton button = new JButton(text);
        button.setBounds(20, y, 200, 40);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(action);
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
        drawerPanel.add(button);
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