package hms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EditCustomerForm extends JFrame {
    private JTextField nameField, contactField;
    private JComboBox<String> genderBox;
    private int bookingId;
    private AdminCustomerManagement parent;
    private Connection conn;

    public EditCustomerForm(AdminCustomerManagement parent, int bookingId, String name, String gender, String contact) {
        this.parent = parent;
        this.bookingId = bookingId;
        setTitle("Edit Customer");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        connectToDatabase();

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Name:"));
        nameField = new JTextField(name);
        panel.add(nameField);

        panel.add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setSelectedItem(gender);
        panel.add(genderBox);

        panel.add(new JLabel("Contact:"));
        contactField = new JTextField(contact);
        panel.add(contactField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        panel.add(saveButton);
        panel.add(cancelButton);

        add(panel);

        saveButton.addActionListener(e -> updateCustomer());
        cancelButton.addActionListener(e -> dispose());
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        String name = nameField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE customer SET name = ?, gender = ?, contact = ? WHERE bookingid = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setString(3, contact);
            pstmt.setInt(4, bookingId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.loadCustomerData();  // Refresh table
                dispose(); // Close the window
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
