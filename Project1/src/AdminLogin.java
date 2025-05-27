import org.w3c.dom.Text;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

class RoundedTextField extends JTextField {
    private final int radius;

    public RoundedTextField(int columns, int radius) {
        super(columns);
        this.radius = radius;
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 220));
        setFont(new Font("Arial", Font.PLAIN, 16));
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(getBackground());
        g2.fill(shape);
        g2.setClip(shape);
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(180, 180, 180));
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        g2.dispose();
    }
}

class RoundedPasswordField extends JPasswordField {
    private final int radius;

    public RoundedPasswordField(int columns, int radius) {
        super(columns);
        this.radius = radius;
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 220));
        setFont(new Font("Arial", Font.PLAIN, 16));
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setEchoChar('*');

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(getBackground());
        g2.fill(shape);
        g2.setClip(shape);
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(180, 180, 180));
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        g2.dispose();
    }
}

class RoundedButton extends JButton {
    private final Color normalColor = new Color(30, 30, 30);
    private final Color hoverColor = new Color(50, 50, 50);
    private final Color clickColor = new Color(20, 20, 20);
    private Color currentColor = normalColor;

    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = normalColor;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentColor = clickColor;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    public void paintBorder(Graphics g) {
        // no border
    }
}

public class AdminLogin extends JFrame {
    private String userName = "admin";
    private String password = "admin";
    private final String mpin = "123";

    public AdminLogin() {
        setTitle("Admin Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);

        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);
        layeredPane.setLayout(null);

        JLabel background = createBackground();
        layeredPane.add(background, Integer.valueOf(0));

        JPanel topPanel = createTopPanel();
        layeredPane.add(topPanel, Integer.valueOf(1));

        JPanel loginPanel = createLoginPanel();
        layeredPane.add(loginPanel, Integer.valueOf(2));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                background.setSize(getWidth(), getHeight());
                topPanel.setSize(getWidth(), 80);
                loginPanel.setBounds((getWidth() - 500) / 2, 200, 500, 400);
            }
        });

        setVisible(true);
    }

    private JLabel createBackground() {
        ImageIcon bgImage = new ImageIcon("resources/images/background.jpg");
        JLabel label = new JLabel(bgImage) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        label.setBounds(0, 0, getWidth(), getHeight());
        return label;
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(200, 200, 200));
        panel.setBounds(0, 0, getWidth(), 80);

        JLabel title = new JLabel("ZAIB AUTOS");
        title.setFont(new Font("Francois One", Font.BOLD, 48));
        title.setForeground(Color.BLACK);
        title.setBounds(20, 10, 400, 60);

        panel.add(title);
        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(255, 255, 255, 220));
        panel.setBounds((getWidth() - 500) / 2, 200, 500, 400);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        userLabel.setBounds(50, 40, 150, 25);
        panel.add(userLabel);

        RoundedTextField userField = new RoundedTextField(20, 30);
        userField.setBounds(50, 70, 400, 40);
        panel.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passLabel.setBounds(50, 130, 150, 25);
        panel.add(passLabel);

        RoundedPasswordField passwordField = new RoundedPasswordField(20, 30);
        passwordField.setBounds(50, 160, 400, 40);
        panel.add(passwordField);

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        showPassword.setForeground(Color.BLACK);
        showPassword.setBounds(55, 205, 150, 25);
        showPassword.setBackground(new Color(255, 255, 255, 0)); // Transparent background
        showPassword.setFocusPainted(false);
        showPassword.setFocusable(false);
        showPassword.setOpaque(false);
        showPassword.setRolloverEnabled(false);
        showPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

// Improve accessibility with a tooltip
        showPassword.setToolTipText("Toggle password visibility");

// Event listener to show/hide password
        showPassword.addActionListener(e -> {
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '*');
        });
        panel.add(showPassword);

        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setBounds(50, 240, 180, 45);
        loginButton.addActionListener(e -> login(userField, passwordField));
        panel.add(loginButton);

        RoundedButton backButton = new RoundedButton("Back");
        backButton.setBounds(270, 240, 180, 45);
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame(); // Your LoginFrame class
        });
        panel.add(backButton);

        RoundedButton forgotButton = new RoundedButton("Forgot Password");
        forgotButton.setBounds(150, 310, 200, 45);
        forgotButton.addActionListener(e -> resetPassword());
        panel.add(forgotButton);

        return panel;
    }

    private void login(JTextField userField, JPasswordField passwordField) {
        if (userField.getText().equals(userName) && new String(passwordField.getPassword()).equals(password)) {
            new MainDashboard(); // Your Dashboard class
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetPassword() {
        String input = JOptionPane.showInputDialog(this, "Enter your PIN:");
        if (input != null && input.equals(mpin)) {
            String newUser = JOptionPane.showInputDialog(this, "New Username:");
            String newPass = JOptionPane.showInputDialog(this, "New Password:");
            if (newUser != null && !newUser.isEmpty() && newPass != null && !newPass.isEmpty()) {
                userName = newUser;
                password = newPass;
                JOptionPane.showMessageDialog(this, "Username and Password updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect PIN!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLogin::new);
}
}