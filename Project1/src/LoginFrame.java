import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import java.sql.*; // For database operations (commented out)

public class LoginFrame extends JFrame {
    // Secret code for sign-up (set to "Asjal" as specified)
    private static final String SECRET_CODE = "Asjal";

    public LoginFrame() {
        // Set frame properties
        setTitle("Zaib Autos - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 768));
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

        // Create Login Button
        RoundedButton loginButton = new RoundedButton("Login");
        styleButton(loginButton);
        loginButton.setBounds(getWidth() - 180, 20, 90, 35);
        loginButton.addActionListener(e -> {
            setVisible(false);
            new AdminLogin();
        });

        // Create Sign Up Button
        RoundedButton dailySales = new RoundedButton("Sales");
        styleButton(dailySales);
        dailySales.setBounds(getWidth() - 290, 20, 90, 35);
        dailySales.addActionListener(e -> {
            // Prompt for secret code
            String inputCode = JOptionPane.showInputDialog(this, "Enter the secret code:", "Secret Code", JOptionPane.PLAIN_MESSAGE);
            if (inputCode != null && inputCode.equals(SECRET_CODE)) {
                SwingUtilities.invokeLater(()-> new SalesEntryFrame());
            } else {
                JOptionPane.showMessageDialog(this, "Invalid secret code!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Create Label
        JLabel nameLabel = new JLabel("ZAIB AUTOS");
        nameLabel.setFont(new Font("Francois One", Font.BOLD, 48));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBounds(30, 8, 400, 60);

        // Add components to top panel
        topPanel.add(loginButton);
        topPanel.add(dailySales);
        topPanel.add(nameLabel);

        // Add components to frame
        add(topPanel);
        add(backgroundLabel);

        // Handle window resizing to keep components responsive
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                backgroundLabel.setBounds(0, 0, getWidth(), getHeight());
                topPanel.setBounds(0, 0, getWidth(), 80);
                loginButton.setBounds(getWidth() - 180, 20, 90, 35);
                dailySales.setBounds(getWidth() - 290, 20, 90, 35);
            }
        });

        // Make frame visible
        setVisible(true);
    }

    // Style buttons consistently
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


}