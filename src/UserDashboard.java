package hms;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    private JPanel sidebarPanel;

    public UserDashboard(String username) {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("User Dashboard");

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Sidebar setup
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(44, 47, 51));
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));

        addSidebarButton("View Bookings", () -> new UserBookings(username).setVisible(true));

        // Spacer to push logout button to the bottom
        sidebarPanel.add(Box.createVerticalGlue());

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 45));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logoutButton.addActionListener(e -> {
            dispose();
            new WelcomeScreen().setVisible(true);
        });

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebarPanel.add(logoutButton);

        // Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(255, 228, 235));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(30, 30, 30));
        gbc.gridy = 0;
        contentPanel.add(welcomeLabel, gbc);

        // Offers header
        JLabel offersHeader = new JLabel("Our Offers");
        offersHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        offersHeader.setForeground(new Color(30, 30, 30));
        gbc.gridy = 1;
        contentPanel.add(offersHeader, gbc);

        // Offers panel
        JPanel offersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        offersPanel.setBackground(new Color(255, 228, 235));

        // Only "Single" and "Double" options
        String[] roomOptions = {"Single", "Double"};
        for (String roomType : roomOptions) {
            JButton roomButton = new JButton(roomType + " Bed");
            roomButton.setPreferredSize(new Dimension(160, 50));
            roomButton.setBackground(Color.BLACK);
            roomButton.setForeground(Color.WHITE);
            roomButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            roomButton.setFocusPainted(false);
            roomButton.setOpaque(true);
            roomButton.setContentAreaFilled(true);
            roomButton.setBorderPainted(false);
            roomButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            roomButton.addActionListener(e -> new BookRoomForm(username, roomType).setVisible(true));
            offersPanel.add(roomButton);
        }

        gbc.gridy = 2;
        contentPanel.add(offersPanel, gbc);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard("John Doe"));
    }
}
