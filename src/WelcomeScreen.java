package hms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WelcomeScreen extends JFrame {

    public WelcomeScreen() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load background image
        ImageIcon bgIcon = new ImageIcon("src/images/first.jpg"); 
        Image bgImage = bgIcon.getImage();

        // Panel with background image
        JPanel backgroundPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Semi-transparent container
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(0, 0, 0, 180)); // Black with transparency
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to Coo Hotel Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Intro text
        JTextArea introText = new JTextArea(
            "Manage your hotel's daily operations seamlessly. From room bookings, customer records,\n" +
            "and staff management to secure payments — all in one place.\n\n" +
            "Please choose an option below to get started."
        );
        introText.setFont(new Font("Arial", Font.PLAIN, 18));
        introText.setForeground(Color.WHITE);
        introText.setOpaque(false);
        introText.setEditable(false);
        introText.setFocusable(false);
        introText.setHighlighter(null);
        introText.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");

        loginBtn.setFont(new Font("Arial", Font.PLAIN, 20));
        signupBtn.setFont(new Font("Arial", Font.PLAIN, 20));
        loginBtn.setPreferredSize(new Dimension(150, 50));
        signupBtn.setPreferredSize(new Dimension(150, 50));

        buttonPanel.add(loginBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(signupBtn);

        // Button actions
        loginBtn.addActionListener((ActionEvent e) -> {
            dispose();
            new Login().setVisible(true);
        });

        signupBtn.addActionListener((ActionEvent e) -> {
            dispose();
            new SignUp().setVisible(true);
        });

        // Add components to content panel
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(introText);
        contentPanel.add(buttonPanel);

        // Center content panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundPanel.add(contentPanel, gbc);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomeScreen::new);
    }
}
