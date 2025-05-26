package hms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class SignUp extends JFrame {

    public SignUp() {
        setTitle("Sign Up - Hotel Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Image Panel 
        JPanel imagePanel = new JPanel() {
            private Image image = new ImageIcon("src/images/signup.png").getImage(); 
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        imagePanel.setPreferredSize(new Dimension(600, getHeight()));
        add(imagePanel, BorderLayout.WEST);

        // Form Panel 
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 228, 232)); // Light pink
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);

        // Title
        JLabel titleLabel = new JLabel("Sign Up");
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

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Sign Up Button
        JButton signUpBtn = new JButton("Sign Up");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        signUpBtn.setFont(new Font("Arial", Font.BOLD, 18));
        formPanel.add(signUpBtn, gbc);

        // Clickable login text
        JLabel loginLabel = new JLabel("<HTML><U>Already have an account? Log in</U></HTML>");
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        formPanel.add(loginLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Click event for login
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose(); 
                new Login().setVisible(true);
            }
        });

        // Sign Up Logic
        signUpBtn.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotelmanagementsystem", "root", "");
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (username, password, administrator) VALUES (?, ?, false)")) {

                    stmt.setString(1, username);
                    stmt.setString(2, password);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Sign Up Successful!");
                        dispose();
                        new UserDashboard(username).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Sign Up Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp().setVisible(true));
    }
}
