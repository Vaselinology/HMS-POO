package hms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EditUserForm extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox adminCheckbox;
    private Connection conn;
    private AdminUserManagement parent;
    private String originalUsername;

    public EditUserForm(AdminUserManagement parent, String username, String password, boolean isAdmin) {
        super(parent, "Edit User", true);
        this.parent = parent;
        this.originalUsername = username;

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(4, 2, 10, 10));

        connectToDatabase();

        add(new JLabel("Username:"));
        usernameField = new JTextField(username);
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField(password);
        add(passwordField);

        add(new JLabel("Administrator:"));
        adminCheckbox = new JCheckBox();
        adminCheckbox.setSelected(isAdmin);
        add(adminCheckbox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> updateUser());
        add(saveButton);

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

    private void updateUser() {
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());
        boolean isAdmin = adminCheckbox.isSelected();

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE user SET username = ?, password = ?, administrator = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newUsername);
            stmt.setString(2, newPassword);
            stmt.setBoolean(3, isAdmin);
            stmt.setString(4, originalUsername);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            parent.loadUserData("All");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
