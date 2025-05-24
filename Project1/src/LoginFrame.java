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
        RoundedButton signUpButton = new RoundedButton("Sign Up");
        styleButton(signUpButton);
        signUpButton.setBounds(getWidth() - 290, 20, 90, 35);
        signUpButton.addActionListener(e -> {
            // Prompt for secret code
            String inputCode = JOptionPane.showInputDialog(this, "Enter the secret code:", "Secret Code", JOptionPane.PLAIN_MESSAGE);
            if (inputCode != null && inputCode.equals(SECRET_CODE)) {
                SwingUtilities.invokeLater(this::showSignUpDialog); // Ensure dialog is shown on EDT
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
        topPanel.add(signUpButton);
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
                signUpButton.setBounds(getWidth() - 290, 20, 90, 35);
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

    // Show Sign Up dialog
    private void showSignUpDialog() {
        JDialog signUpDialog = new JDialog(this, "Sign Up", true);
        signUpDialog.setSize(400, 300);
        signUpDialog.setLocationRelativeTo(this);
        signUpDialog.setLayout(null);
        signUpDialog.setBackground(new Color(255, 255, 255, 220));

        // Sign Up form components
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setBounds(50, 30, 100, 25);
        signUpDialog.add(usernameLabel);

        RoundedTextField usernameField = new RoundedTextField(20, 20);
        usernameField.setBounds(150, 30, 200, 35);
        signUpDialog.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setBounds(50, 70, 100, 25);
        signUpDialog.add(passwordLabel);

        RoundedPasswordField passwordField = new RoundedPasswordField(20, 20);
        passwordField.setBounds(150, 70, 200, 35);
        signUpDialog.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        confirmPasswordLabel.setBounds(50, 110, 150, 25);
        signUpDialog.add(confirmPasswordLabel);

        RoundedPasswordField confirmPasswordField = new RoundedPasswordField(20, 20);
        confirmPasswordField.setBounds(150, 110, 200, 35);
        signUpDialog.add(confirmPasswordField);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setBounds(50, 150, 100, 25);
        signUpDialog.add(nameLabel);

        RoundedTextField nameField = new RoundedTextField(20, 20);
        nameField.setBounds(150, 150, 200, 35);
        signUpDialog.add(nameField);

        RoundedButton submitButton = new RoundedButton("Submit");
        submitButton.setBounds(150, 200, 100, 35);
        signUpDialog.add(submitButton);

        submitButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
            String fullName = nameField.getText().trim();

            // Input validation
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(signUpDialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(signUpDialog, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (username.length() < 3 || password.length() < 6) {
                JOptionPane.showMessageDialog(signUpDialog, "Username must be at least 3 characters and password at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Database storage logic (commented out as per request)
            /*
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zaibautos", "root", "");
                String sql = "INSERT INTO users (username, password, full_name) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password); // In production, hash the password
                stmt.setString(3, fullName);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(signUpDialog, "Sign Up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    signUpDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(signUpDialog, "Sign Up failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(signUpDialog, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            */

            // Placeholder for successful sign-up
            JOptionPane.showMessageDialog(signUpDialog, "Sign Up successful (database not configured).", "Success", JOptionPane.INFORMATION_MESSAGE);
            signUpDialog.dispose();
        });

        // Make dialog visible
        signUpDialog.setVisible(true);
    }
}