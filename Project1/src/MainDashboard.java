import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDashboard extends JFrame {
    private JPanel drawerPanel;
    private boolean isDrawerOpen = false;
    private JButton menuButton;
    private JLabel background;
    private JLabel title;
    private int drawerWidth = 250;
    private int screenWidth;
    private int screenHeight;

    public MainDashboard() {
        setTitle("Main Dashboard");
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
        addDrawerButton("Inventory Management", 200, e -> {
            new InventoryFrame();
            closeDrawer();
        });

        addDrawerButton("Customer Ledger", 250, e -> {
            new CustomerLedgerFrame();
            closeDrawer();
        });

        addDrawerButton("Supplier Ledger", 300, e -> {
            new SupplierLedgerFrame();
            closeDrawer();
        });

        addDrawerButton("Sales Analysis", 350, e -> {
            new SalesAnalysisFrame();
            closeDrawer();
        });

        addDrawerButton("Logout", 400, e -> {
            new AdminLogin();

        });

        addDrawerButton("Exit", 450, e -> System.exit(0));

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

        setVisible(true);
    }

    private void updateLayout() {
        // Resize background
        background.setIcon(new ImageIcon(new ImageIcon("resources/images/background.jpg")
                .getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH)));
        background.setBounds(0, 0, screenWidth, screenHeight);

        // Resize drawer
        drawerPanel.setBounds(isDrawerOpen ? 0 : -drawerWidth, 0, drawerWidth, screenHeight);

        // Resize title
        title.setBounds(isDrawerOpen ? drawerWidth + 20 : 20, 20,
                screenWidth - (isDrawerOpen ? drawerWidth + 40 : 40), 50);

        // Menu button
        menuButton.setBounds(10, 10, 40, 40);
    }

    private void addDrawerButton(String text, int y, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setBounds(20, y, 200, 40);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(action);
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
