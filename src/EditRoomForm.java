package hms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EditRoomForm extends JDialog {

    private JTextField roomNumberField;
    private JComboBox<String> availabilityBox, cleaningStatusBox, bedTypeBox;
    private JTextField priceField;

    private Connection conn;
    private AdminRoomManagement parent;
    private String originalRoomNumber;

    public EditRoomForm(AdminRoomManagement parent, String roomNumber, String availability, String cleaningStatus, double price, String bedType) {
        super(parent, "Edit Room", true);
        this.parent = parent;
        this.originalRoomNumber = roomNumber;

        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));

        connectToDatabase();

        add(new JLabel("Room Number:"));
        roomNumberField = new JTextField(roomNumber);
        add(roomNumberField);

        add(new JLabel("Availability:"));
        availabilityBox = new JComboBox<>(new String[]{"Available", "Occupied"});
        availabilityBox.setSelectedItem(availability);
        add(availabilityBox);

        add(new JLabel("Cleaning Status:"));
        cleaningStatusBox = new JComboBox<>(new String[]{"Clean", "Dirty"});
        cleaningStatusBox.setSelectedItem(cleaningStatus);
        add(cleaningStatusBox);

        add(new JLabel("Price:"));
        priceField = new JTextField(String.valueOf(price));
        add(priceField);

        add(new JLabel("Bed Type:"));
        bedTypeBox = new JComboBox<>(new String[]{"Single", "Double"});
        bedTypeBox.setSelectedItem(bedType);
        add(bedTypeBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> updateRoom());
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
            JOptionPane.showMessageDialog(this, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void updateRoom() {
        String newRoomNumber = roomNumberField.getText();
        String availability = (String) availabilityBox.getSelectedItem();
        String cleaningStatus = (String) cleaningStatusBox.getSelectedItem();
        String bedType = (String) bedTypeBox.getSelectedItem();
        double price;

        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE room SET roomnumber = ?, availability = ?, cleaning_status = ?, price = ?, bed_type = ? WHERE roomnumber = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newRoomNumber);
            stmt.setString(2, availability);
            stmt.setString(3, cleaningStatus);
            stmt.setDouble(4, price);
            stmt.setString(5, bedType);
            stmt.setString(6, originalRoomNumber);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room updated successfully!");
            parent.refreshRoomTable(); // Refresh table
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating room: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
