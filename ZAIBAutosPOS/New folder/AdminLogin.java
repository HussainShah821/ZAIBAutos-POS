
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

class RoundedTextField extends JTextField {
    private int radius;

    public RoundedTextField(int columns, int radius) {
        super(columns);
        this.radius = radius;
        setOpaque(false); // Prevent default painting
        setBackground(new Color(255, 255, 255, 180));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clip text field to rounded shape
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));

        // Draw background in rounded shape
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g); // Let JTextField paint text
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground()); // Hide the black edge
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
    }
    
}


class RoundedPasswordField extends JPasswordField {
    private int radius;

    public RoundedPasswordField(int columns, int radius) {
        super(columns);
        this.radius = radius;
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 180));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clip the component to the rounded shape
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));

        // Draw the background
        // g2.setColor(getBackground());
        // g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
    }
}

class RoundedButton extends JButton {
    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 14));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Don’t paint any background, keep it transparent
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Optionally, draw a semi-transparent border
        // g2.setColor(new Color(0, 0, 0, 50));
        // g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Do nothing — disables borders that show on hover/focus
    }
}




class AdminLogin extends JFrame {
    String pasword="null";// add these variables in database as the user changes 
    //the pass and username with his mpin it will auto update in your database 
    String userName="null";
    String mpin ="123";
    AdminLogin() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setLayout(null);

        ImageIcon background = new ImageIcon("background.jpg");
        JLabel backImage = new JLabel(background);
        backImage.setBounds(0, 0, 1366, 768);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 1366, 768);
        layeredPane.add(backImage, Integer.valueOf(1));

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 1366, 80);
        panel.setBackground(new Color(200, 200, 200));

        JLabel text1 = new JLabel("ZAIB AUTOS");
        text1.setFont(new Font("Francois One", Font.BOLD, 48));
        text1.setBounds(20, 10, 400, 60);
        panel.add(text1);
        layeredPane.add(panel, Integer.valueOf(2));
       
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(null);
        bottomPanel.setBounds(400, 200, 500, 300);
        bottomPanel.setBackground(new Color(255, 255, 255, 180));

        JLabel userLabel = new JLabel("USERNAME:");
        userLabel.setFont(new Font("Francois One", Font.BOLD, 18));
        userLabel.setBounds(50, 30, 200, 30);
        bottomPanel.add(userLabel);

        RoundedTextField user = new RoundedTextField(20, 30);
        user.setBounds(50, 60, 400, 40);
        bottomPanel.add(user);

        JLabel passLabel = new JLabel("PASSWORD:");
        passLabel.setFont(new Font("Francois One", Font.BOLD, 18));
        passLabel.setBounds(50, 110, 200, 30);
        bottomPanel.add(passLabel);

        RoundedPasswordField password = new RoundedPasswordField(20, 30);
        password.setBounds(50, 140, 360, 40);
        bottomPanel.add(password);

        ImageIcon showIcon = new ImageIcon("showPass.PNG");
        ImageIcon hideIcon = new ImageIcon("hidePass.PNG");

        JCheckBox showPassword = new JCheckBox();
        showPassword.setBounds(420, 145, 30, 30);
        showPassword.setIcon(showIcon);
        showPassword.setBackground(bottomPanel.getBackground());

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                password.setEchoChar((char) 0);
                showPassword.setIcon(hideIcon);
            } else {
                password.setEchoChar('*');
                showPassword.setIcon(showIcon);
            }
        });
        bottomPanel.add(showPassword);

        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setBounds(50, 200, 180, 40);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // JOptionPane.showMessageDialog(null, "Login Successful!");
                if (isIDCorrect(user, password)){
               new Dashboard();}
               else 
               JOptionPane.showMessageDialog(null, "Incorrect User Name or Password");
            }
        });
        bottomPanel.add(loginButton);

        RoundedButton backButton = new RoundedButton("Back");
        backButton.setBounds(270, 200, 180, 40);
        backButton.addActionListener(e -> {
            setVisible(false);
            new LoginFrame();
        });
        bottomPanel.add(backButton);
        RoundedButton forgot=new RoundedButton("Forgot Password");
        forgot.setBounds(160,250,180,40);
        forgot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(null, "Enter your PIN:", "Reset Your Password", JOptionPane.QUESTION_MESSAGE);
                
                if (input != null && input.equals(mpin)) {
                    String newUserName = JOptionPane.showInputDialog(null, "Enter new Username:", "Reset Username", JOptionPane.QUESTION_MESSAGE);
                    String newPassword = JOptionPane.showInputDialog(null, "Enter new Password:", "Reset Password", JOptionPane.QUESTION_MESSAGE);
        
                    if (newUserName != null && !newUserName.isEmpty() && newPassword != null && !newPassword.isEmpty()) {
                        userName = newUserName;
                        pasword = newPassword;
                        JOptionPane.showMessageDialog(null, "Username and Password updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Username or Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect PIN!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        bottomPanel.add(forgot);

        layeredPane.add(bottomPanel, Integer.valueOf(3));
        add(layeredPane);
        setVisible(true);
    }
    private boolean isIDCorrect(JTextField u, JPasswordField p) {
        return u.getText().equals(userName) && new String(p.getPassword()).equals(pasword);
    }
public static void main(String arr[]){
    new AdminLogin();
}    
   
}