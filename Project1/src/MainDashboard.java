import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainDashboard extends JFrame {
    private JPanel drawerPanel;
    private boolean isDrawerOpen = true;
    private JButton openButton;
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

        // Layered pane for main frame
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.setLayout(null);

        // Background image
        background = new JLabel();
        background.setIcon(new ImageIcon(new ImageIcon("resources/images/background.jpg")
                .getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH)));
        background.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(background, Integer.valueOf(1));

        // Drawer panel with BoxLayout for content
        drawerPanel = new JPanel();
        drawerPanel.setLayout(new BoxLayout(drawerPanel, BoxLayout.Y_AXIS));
        drawerPanel.setBackground(new Color(44, 62, 80)); // #2C3E50
        drawerPanel.setBounds(0, 0, drawerWidth, screenHeight); // Start open
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
        addDrawerButton(drawerPanel, "Sales Entry", e -> {
            new SalesEntryFrame();
            // Removed closeDrawer() to keep drawer open
        });

        addDrawerButton(drawerPanel, "Profit Entry", e -> {
            new ProfitEntryFrame();
            // Removed closeDrawer() to keep drawer open
        });

        addDrawerButton(drawerPanel, "Inventory Management", e -> {
            new InventoryFrame();
            // Removed closeDrawer() to keep drawer open
        });

        addDrawerButton(drawerPanel, "Customer Ledger", e -> {
            new CustomerLedgerFrame();
            // Removed closeDrawer() to keep drawer open
        });
        addDrawerButton(drawerPanel, "Supplier Ledger", e -> {
            new SupplierLedgerFrame();
            // Removed closeDrawer() to keep drawer open
        });

        addDrawerButton(drawerPanel, "Expenditure Tracking", e -> {
            new ExpenditureTrackingFrame();
            // Removed closeDrawer() to keep drawer open
        });

        addDrawerButton(drawerPanel, "Sales Analysis", e -> {
            new SalesAnalysisFrame();
            // Removed closeDrawer() to keep drawer open
        });
        addDrawerButton(drawerPanel, "Logout", e -> {
            new AdminLogin();
            dispose();

        }, true); // Critical action
        addDrawerButton(drawerPanel, "Exit", e -> System.exit(0), true); // Critical action


        // Open button (top-left, pointing right, initially hidden)
        openButton = new JButton("→");
        openButton.setFont(new Font("Arial", Font.BOLD, 18));
        openButton.setForeground(Color.WHITE);
        openButton.setBackground(new Color(44, 62, 80)); // #2C3E50
        openButton.setOpaque(true); // Ensure background is painted
        openButton.setFocusPainted(false);
        openButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        openButton.setBounds(-40, 10, 40, 40); // Start off-screen
        openButton.setVisible(true); // Explicitly set visible
        openButton.addActionListener(e -> openDrawer());
        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                openButton.setBackground(new Color(41, 128, 185)); // #2980B9
            }
            @Override
            public void mouseExited(MouseEvent e) {
                openButton.setBackground(new Color(44, 62, 80)); // #2C3E50
            }
        });
        layeredPane.add(openButton, Integer.valueOf(3));

        // Force initial layout update
        updateLayout();

        // Ensure repaint after frame is visible
        SwingUtilities.invokeLater(() -> {
            layeredPane.revalidate();
            layeredPane.repaint();
        });

        // Resize listener
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                screenWidth = getWidth();
                screenHeight = getHeight();
                updateLayout();
            }
        });

        // Mouse click to close drawer (optional, can be removed if buttons suffice)
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (isDrawerOpen && e.getX() > drawerWidth) {
                    closeDrawer();
                }
            }
        });

        setVisible(true);
    }

    private void updateLayout() {
        // Resize background
        background.setIcon(new ImageIcon(new ImageIcon("resources/images/background.jpg")
                .getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH)));
        background.setBounds(0, 0, screenWidth, screenHeight);

        // Resize drawer and buttons
        drawerPanel.setBounds(isDrawerOpen ? 0 : -drawerWidth, 0, drawerWidth, screenHeight);
        openButton.setBounds(isDrawerOpen ? -40 : 10, 10, 40, 40);

        revalidate();
        repaint();
    }

    private void addDrawerButton(JPanel panel, String text, ActionListener action) {
        addDrawerButton(panel, text, action, false);
    }

    private void addDrawerButton(JPanel panel, String text, ActionListener action, boolean isCritical) {
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
        panel.add(button);
        panel.add(Box.createVerticalStrut(5)); // Spacing between buttons
    }

    private void closeDrawer() {
        isDrawerOpen = false;
        updateLayout();
    }

    private void openDrawer() {
        isDrawerOpen = true;
        updateLayout();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainDashboard::new);
    }
}