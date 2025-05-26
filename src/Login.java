package hms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {

    public Login() {
        setTitle("Login - Hotel Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Image panel 
        JPanel imagePanel = new JPanel() {
            private Image image = new ImageIcon("src/images/signup.png").getImage(); 
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        imagePanel.setPreferredSize(new Dimension(600, getHeight()));
        add(imagePanel, BorderLayout.WEST);


        // Form panel 
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 228, 232)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        // Title
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);

        // Sign-up link
        JLabel signUpLabel = new JLabel("<HTML><U>Don't have an account? Sign up</U></HTML>");
        signUpLabel.setForeground(Color.BLUE);
        signUpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        formPanel.add(signUpLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Login action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelmanagementsystem", "root", "");
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?")) {

                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    boolean isAdmin = rs.getBoolean("administrator");

                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    dispose();

                    if (isAdmin) {
                        new Dashboard().setVisible(true);
                    } else {
                        new UserDashboard(username).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sign-up redirect
        signUpLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new SignUp().setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
