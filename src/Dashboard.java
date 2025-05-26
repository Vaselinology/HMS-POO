package hms;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private JPanel sidebarPanel;

    public Dashboard() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Admin Dashboard");

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Sidebar setup
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(44, 47, 51));
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));

        // Main nav buttons
        addSidebarButton("Rooms", () -> new AdminRoomManagement().setVisible(true));
        addSidebarButton("Employees", () -> new AdminEmployeeManagement().setVisible(true));
        addSidebarButton("Customers", () -> new AdminCustomerManagement().setVisible(true));
        addSidebarButton("Users", () -> new AdminUserManagement().setVisible(true));

        // Spacer to push logout button to the bottom
        sidebarPanel.add(Box.createVerticalGlue());

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 45));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(220, 53, 69)); // Bootstrap red
        logoutButton.setOpaque(true); // Required to paint background
        logoutButton.setContentAreaFilled(true); // Ensure content area is filled
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        logoutButton.addActionListener(e -> {
            dispose(); // Close dashboard
            new WelcomeScreen().setVisible(true); // Open welcome screen
        });

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(logoutButton);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 228, 235)); // Light pink
        JLabel welcomeLabel = new JLabel("Welcome to the Admin Dashboard", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(30, 30, 30));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void addSidebarButton(String text, Runnable onClick) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(64, 68, 75));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> onClick.run());
        button.setMargin(new Insets(10, 20, 10, 20));

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(button);
    }
}
