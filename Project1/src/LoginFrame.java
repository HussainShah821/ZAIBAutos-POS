import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        // Set frame properties
        setTitle("Zaib Autos - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximized by default
        setMinimumSize(new Dimension(1024, 768)); // Minimum size
        setLocationRelativeTo(null);
        setLayout(null);

        // Load background image
        ImageIcon backgroundIcon = new ImageIcon("resources/images/background.jpg");
        JLabel backgroundLabel = new JLabel(backgroundIcon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundLabel.setBounds(0, 0, getWidth(), getHeight());

        // Create top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);
        topPanel.setBackground(new Color(200, 200, 200));
        topPanel.setBounds(0, 0, getWidth(), 80);

        // Create Admin Button
        JButton adminButton = new RoundedButton("Admin");
        styleButton(adminButton);
        adminButton.setBounds(getWidth() - 180, 20, 90, 35);
        adminButton.addActionListener(e -> {
            setVisible(false);
            new AdminLogin();
        });

        // Create Staff Button
        JButton staffButton = new RoundedButton("Login");
        styleButton(staffButton);
        staffButton.setBounds(getWidth() - 290, 20, 90, 35);
        staffButton.addActionListener(e -> {
            setVisible(false);
            new MainDashboard();
        });

        // Create Label
        JLabel namelabel = new JLabel("ZAIB AUTOS");
        namelabel.setFont(new Font("Francois One", Font.BOLD, 48));
        namelabel.setForeground(Color.BLACK);
        namelabel.setBounds(30, 8, 400, 60);

        // Add buttons and label to top panel
        topPanel.add(adminButton);
        topPanel.add(staffButton);
        topPanel.add(namelabel);

        // Add components to frame
        add(topPanel);
        add(backgroundLabel);

        // Handle window resizing to keep components responsive
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                backgroundLabel.setBounds(0, 0, getWidth(), getHeight());
                topPanel.setBounds(0, 0, getWidth(), 80);
                adminButton.setBounds(getWidth() - 180, 20, 90, 35);
                staffButton.setBounds(getWidth() - 290, 20, 90, 35);
            }
        });

        // Make frame visible
        setVisible(true);
    }
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180)); // Steel blue
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

}

