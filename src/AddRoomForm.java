package hms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddRoomForm extends JFrame {

    private JTextField roomNumberField, priceField;
    private JComboBox<String> availabilityBox, cleaningStatusBox, bedTypeBox;
    private Connection conn;
    private AdminRoomManagement parent; // reference to refresh room list

    public AddRoomForm(AdminRoomManagement parent) {
        this.parent = parent;

        setTitle("Add New Room");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Connect to DB
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Connection Failed", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Form components
        add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        add(roomNumberField);

        add(new JLabel("Availability:"));
        availabilityBox = new JComboBox<>(new String[]{"Available", "Occupied"});
        add(availabilityBox);

        add(new JLabel("Cleaning Status:"));
        cleaningStatusBox = new JComboBox<>(new String[]{"Clean", "Dirty"});
        add(cleaningStatusBox);

        add(new JLabel("Price:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("Bed Type:"));
        bedTypeBox = new JComboBox<>(new String[]{"Single", "Double", "Queen", "King"});
        add(bedTypeBox);

        JButton saveButton = new JButton("Save");
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        add(cancelButton);

        // Save Action
        saveButton.addActionListener(e -> saveRoom());

        // Cancel Action
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveRoom() {
        String roomNumber = roomNumberField.getText();
        String availability = (String) availabilityBox.getSelectedItem();
        String cleaningStatus = (String) cleaningStatusBox.getSelectedItem();
        String bedType = (String) bedTypeBox.getSelectedItem();
        String priceText = priceField.getText();

        if (roomNumber.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            String sql = "INSERT INTO room (roomnumber, availability, cleaning_status, price, bed_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, roomNumber);
            stmt.setString(2, availability);
            stmt.setString(3, cleaningStatus);
            stmt.setDouble(4, price);
            stmt.setString(5, bedType);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Room added successfully!");
                parent.refreshRoomTable(); // Refresh table
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add room", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "SQL Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
