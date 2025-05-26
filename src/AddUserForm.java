package hms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddUserForm extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox adminCheckbox;
    private Connection conn;
    private AdminUserManagement parent;

    public AddUserForm(AdminUserManagement parent) {
        super(parent, "Add User", true);
        this.parent = parent;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(4, 2, 10, 10));

        connectToDatabase();

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Administrator:"));
        adminCheckbox = new JCheckBox();
        add(adminCheckbox);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addUser());
        add(addButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void addUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean isAdmin = adminCheckbox.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "INSERT INTO user (username, password, administrator) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setBoolean(3, isAdmin);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully!");
            parent.loadUserData("All");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
