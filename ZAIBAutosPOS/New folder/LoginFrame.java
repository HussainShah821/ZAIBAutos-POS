import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        // Set frame properties
        setTitle("Zaib Autos - Login");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Load background image
        ImageIcon backgroundIcon = new ImageIcon("background.jpg");
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, 1366, 768);

        // Create top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);
        topPanel.setBounds(0, 0, 1366, 80);
        topPanel.setBackground(new Color(200, 200, 200));

        // Create Admin Button
        JButton adminButton = new RoundedButton("Admin");
        styleButton(adminButton);
        adminButton.setBounds(1180, 20, 90, 35);
        adminButton.addActionListener(e -> {
            setVisible(false);
            new AdminLogin();
        });

        // Create Staff Button
        JButton staffButton = new RoundedButton("Login");
        styleButton(staffButton);
        staffButton.setBounds(1050, 20, 90, 35);
        staffButton.addActionListener(e ->{ 
           setVisible(false);
            new SalesPoint();
    }
        );

        // Create Label
        JLabel namelabel = new JLabel("ZAIB AUTOS");
        namelabel.setBounds(10, 20, 400, 60);
        namelabel.setFont(new Font("Francois One", Font.BOLD, 48));
        namelabel.setForeground(Color.BLACK);

        // Add buttons and label to top panel
        topPanel.add(adminButton);
        topPanel.add(staffButton);
        topPanel.add(namelabel);

        // Add components to frame
        add(topPanel);
        add(backgroundLabel);

        // Make frame visible
        setVisible(true);
    }

    // Button styling method
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);

        // Mouse Listener to Repaint Button on Hover & Click
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(50, 50, 50)); // Darker on hover
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.BLACK); // Back to black
                button.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(30, 30, 30)); // Even darker when clicked
                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(Color.BLACK);
                button.repaint();
            }
        });
    }

    // Custom Rounded Button Class
    static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
